package com.pmprental.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.pmprental.bean.FilialBean;
import com.pmprental.bean.PlMaquinaBean;
import com.pmprental.bean.UsuarioBean;
import com.pmprental.business.LocalizacaoMaquinaBusiness;
import com.pmprental.entity.PmpConfigOperacional;
import com.pmprental.entity.PmpContHorasStandard;
import com.pmprental.entity.PmpContrato;
import com.pmprental.entity.PmpMotivosNaoFecContrato;
import com.pmprental.entity.PmpStatusContrato;

public class JobSendMail implements Job {

	private static final String MANUTENCAO_PMP = "Manutenção PMP RENTAL";
	private static int HORAS_PROXIMA_REVISAO = 50;
	
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		InputStream in = getClass().getClassLoader().getResourceAsStream(IConstantAccess.CONF_FILE);
		Properties prop = new Properties();
		EntityManager manager = null;

		try{
			prop.load(in);
			String url = prop.getProperty("cancelar.url");
			//boolean isConnection = true;
			//while(isConnection){
				try {
					manager = JpaUtil.getInstance();
					EmailHelper emailHelper = new EmailHelper();
					//envia e-mail informando que a próxima revisão está próxima ou por vencimento de horímetro ou por tempo
					Query query = manager.createNativeQuery("select STNO from tw_filial");
					List<BigDecimal> filiais = query.getResultList();
					String filiaisIn = "";
					for (BigDecimal string : filiais) {
						filiaisIn += string+",";
					}
					if(filiaisIn.length() > 0){
						filiaisIn = filiaisIn.substring(0, filiaisIn.length() -1);
					}
					UsuarioBean usuarioBean = new UsuarioBean();
					usuarioBean.setFilial(filiaisIn);
					LocalizacaoMaquinaBusiness business = new LocalizacaoMaquinaBusiness(usuarioBean);
					Map map =  business.findAllMaquinaPlServico("CRITICO", null);
					List<PlMaquinaBean> plMaquinaBeans = (List<PlMaquinaBean>)map.get("PlMaquinaBean");
					for (PlMaquinaBean plMaquinaBean : plMaquinaBeans) {
						query = manager.createQuery("from PmpContrato where id =:id");
						query.setParameter("id", plMaquinaBean.getIdContrato());
						PmpContrato contrato = (PmpContrato)query.getSingleResult();
						try {
							query = manager.createNativeQuery("select EPLSNM, EMAIL  from tw_funcionario usu, adm_perfil_sistema_usuario psu"+
									" where psu.id_sistema = (select sis.id from adm_sistema sis where sis.sigla = 'RENPMP')"+
									" and psu.id_perfil = (select p.id from adm_perfil p where p.sigla = 'OPER' and p.tipo_sistema = 'RENPMP')"+
									" and usu.epidno = psu.id_tw_usuario"+
							" and usu.stn1 =:filial" );
							query.setParameter("filial", contrato.getFilial());
							List<Object[]> objList = query.getResultList();
							query = manager.createNativeQuery("select min(s.horas_manutencao) as horas_manutencao"+ 
															  "	from ren_pmp_cont_horas_standard s "+
															  "	where s.is_executado = 'N'"+
															  "	and s.id_Contrato =:id"+
															  "	group by  s.id_Contrato");
							query.setParameter("id", contrato.getId());
							//System.out.println(contrato.getId());
							if(query.getResultList().size() == 0){
								continue;
							}
							BigDecimal horasManutencao = (BigDecimal)query.getSingleResult();
							for (Object[] pair : objList) {
								String msg = ((String)pair[0])+" informamos que o equipamento de serie "+contrato.getNumeroSerie()+", contrato "+contrato.getNumeroContrato()+" entrou no período crítico favor realizar o agendamento da mesma, para realizar a revisao de "+horasManutencao+" horas\n" +
								" por favor efetue o agendamento!";
								emailHelper.sendSimpleMail(msg, MANUTENCAO_PMP, (String)pair[1]);
							}
						} catch (Exception e) {
							StringWriter erro = new StringWriter();
							e.printStackTrace(new PrintWriter(erro));
							emailHelper.sendSimpleMail(contrato.getId()+" Erro "+erro.toString(), MANUTENCAO_PMP, "rodrigo@rdrsistemas.com.br");
						}
						
					}
					
					query = manager.createNativeQuery("select distinct pl.numero_serie, chs.id_contrato, chs.horas_manutencao, (select chs.horas_manutencao - (max(plFrom.horimetro) )  from ren_pmp_maquina_pl plFrom where plFrom.Numero_Serie = pl.numero_serie) revisao"+
							" from ren_pmp_contrato c, ren_pmp_maquina_pl pl,"+
							" (select s.id_Contrato, min(s.horas_manutencao) as horas_manutencao from ren_pmp_cont_horas_standard s where s.is_executado = 'N'"+ 
							" group by  s.id_Contrato) chs"+
							" where c.numero_serie = pl.numero_serie"+
							" and chs.id_contrato = c.id" +
							" and c.data_aceite is not null" +
					" and c.has_send_email = 'S'" +
					" and c.id in (select op.id_contrato from ren_pmp_config_operacional op)");
					List<Object[]> list = query.getResultList();
					for (Object[] objects : list) {
						try {
							String numero_serie = (String)objects[0];
							Long idContrato = ((BigDecimal)objects[1]).longValue();
							BigDecimal horas_manutencao = (BigDecimal)objects[2];
							//Long revisao = ((BigDecimal)objects[3]).longValue();
							query = manager.createQuery("From PmpConfigOperacional where idContrato.id =:idContrato");
							query.setParameter("idContrato", idContrato);
							PmpConfigOperacional operacional = (PmpConfigOperacional)query.getSingleResult();
							//contratos que a próxma revisão é menor que 50 horas
//							if(revisao <= HORAS_PROXIMA_REVISAO){
//								query = manager.createNativeQuery("select EPLSNM, EMAIL  from tw_funcionario usu, adm_perfil_sistema_usuario psu"+
//										" where psu.id_sistema = (select sis.id from adm_sistema sis where sis.sigla = 'PMP')"+
//										" and psu.id_perfil = (select p.id from adm_perfil p where p.sigla = 'OPER' and p.tipo_sistema = 'PMP')"+
//										" and usu.epidno = psu.id_tw_usuario"+
//								" and usu.stn1 =:filial" );
//								query.setParameter("filial", operacional.getFilial());
//								List<Object[]> objList = query.getResultList();
//								for (Object[] pair : objList) {
//									String msg = ((String)pair[0])+" informamos que o equipamento de serie "+numero_serie+", contrato "+operacional.getIdContrato().getNumeroContrato()+" falta menos que 50 horas para a revisao de "+horas_manutencao+" horas\n" +
//									" por favor efetue o agendamento!";
//									emailHelper.sendSimpleMail(msg, MANUTENCAO_PMP, (String)pair[1]);
//								}
//							}
							query = manager.createQuery("From PmpContHorasStandard where idContrato.id =:idContrato and isExecutado = 'N'");
							query.setParameter("idContrato", idContrato);
							//Contratos que faltam apenas uma revisão
							List<PmpContHorasStandard> chsList = query.getResultList();
							if(chsList.size() == 1){
								query = manager.createNativeQuery("select EPLSNM, EMAIL, p.sigla   from tw_funcionario usu, adm_perfil_sistema_usuario psu, adm_perfil p"+
										" where psu.id_sistema = (select sis.id from adm_sistema sis where sis.sigla = 'RENPMP')"+
										" and psu.id_perfil in (select p.id from adm_perfil p where p.sigla = 'OPER' or p.sigla = 'CON'  and p.tipo_sistema = 'RENPMP')"+
										" and usu.EPIDNO = psu.id_tw_usuario"+
										" and usu.stn1 =:filial" +
								" and p.id = psu.id_perfil");
								query.setParameter("filial", operacional.getFilial());
								List<Object[]> objList = query.getResultList();
								for (Object[] pair : objList) {
									String msg = "";
									if(((String)pair[2]).equals("CON")){
										msg = ((String)pair[0])+" informamos que o equipamento de serie "+numero_serie+", modelo,"+operacional.getIdContrato().getModelo()+" contrato "+operacional.getIdContrato().getNumeroContrato()+" falta apenas a revisao de "+horas_manutencao+" horas" +
										" para terminar.Por favor entre em contato com o cliente "+operacional.getIdContrato().getRazaoSocial()+" para renovar. Para nao receber mais esse e-mail, click no link abaixo.\n" +
										url+"?idContrato="+idContrato;
									}else{
										msg = ((String)pair[0])+" informamos que o equipamento de serie "+numero_serie+", contrato "+operacional.getIdContrato().getNumeroContrato()+" falta apenas a revisao de "+horas_manutencao+" horas\n" +
										" para terminar.Por favor entre em contato com o cliente para renovar.";
									}
									emailHelper.sendSimpleMail(msg, MANUTENCAO_PMP, (String)pair[1]);
								}
							}
						} catch (Exception e) {
							File file = File.createTempFile("log", ".txt", new File("."));
							 try {  
						            PrintStream ps = new PrintStream(file);  
						            e.printStackTrace(ps);  
						        } catch (FileNotFoundException e1) {  
						            e1.printStackTrace();  
						        }  
							emailHelper.sendSimpleMail("Erro ao executar o monitoramento do sistema de PMP", "ERRO MONITORAMENTO RENTAL", "rodrigo@rdrsistemas.com.br", file);
						}
					}
					//enviar e-mail para os horímetros com mais de sete dias sem atualizar
					query = manager.createNativeQuery("select DATEDIFF( DD,max(m.data_atualizacao),GETDATE()) as dias  , c.id, c.id_status_contrato, op.filial, m.numero_serie, c.numero_contrato from "+
							" ren_pmp_maquina_pl m, ren_pmp_contrato c, ren_pmp_config_operacional op" +
							" where c.numero_serie = m.numero_serie"+
							" and c.id_status_contrato = (select id from ren_pmp_status_contrato where sigla = 'CA')"+
							" and c.id in (select distinct hs.id_contrato from ren_pmp_cont_horas_standard hs where hs.is_executado = 'N' and c.id = hs.id_contrato)"+
							" and c.id = op.id_contrato" +
							" group by m.numero_serie, c.id, c.id_status_contrato, op.filial, m.numero_serie, c.numero_contrato" +
					" order by max(m.data_atualizacao)");
					List<Object[]> result = query.getResultList();
					for (Object[] objects : result) {
						try {
							if(((Integer)objects[0]).intValue() >= 7){
								query = manager.createNativeQuery("select EPLSNM, EMAIL, p.sigla   from tw_funcionario usu, adm_perfil_sistema_usuario psu, adm_perfil p"+
										" where psu.id_sistema = (select sis.id from adm_sistema sis where sis.sigla = 'RENPMP')"+
										" and psu.id_perfil in (select p.id from adm_perfil p where p.sigla = 'OPER' and p.tipo_sistema = 'RENPMP')"+
										" and usu.EPIDNO = psu.id_tw_usuario"+
										" and usu.stn1 =:filial" +
								" and p.id = psu.id_perfil");
								query.setParameter("filial", ((BigDecimal)objects[3]).intValue());
								List<Object[]> objList = query.getResultList();
								for (Object[] pair : objList) {
									String msg = "";

									msg = ((String)pair[0])+" informamos que o equipamento de serie "+(String)objects[4]+", contrato "+(String)objects[5]+" ja tem mais sete dias que o seu horimetro\n" +
									" foi atualizado.";
									emailHelper.sendSimpleMail(msg, MANUTENCAO_PMP, (String)pair[1]);

								}
							}
						} catch (Exception e) {
							File file = File.createTempFile("log", ".txt", new File("."));
							 try {  
						            PrintStream ps = new PrintStream(file);  
						            e.printStackTrace(ps);  
						        } catch (FileNotFoundException e1) {  
						            e1.printStackTrace();  
						        }  
							emailHelper.sendSimpleMail("Erro ao executar o monitoramento do sistema de PMP", "ERRO MONITORAMENTO PMP RENTAL", "rodrigo@rdrsistemas.com.br", file);
						}
					}
					
					//enviar e-mail notificando os operacionais para entrar em contato com os clientes informando o que foi realizado no serviço
					query = manager.createNativeQuery("select p.cliente, p.numero_os, p.modelo, p.serie, p.smu, p.tecnico, convert(int,p.filial) filial, convert(varchar(10), p.emissao,103) emissao, p.contato, p.telefone, p.equipamento from ren_pmp_os_palm p"+
													"  where DATEDIFF( DD, p.emissao,GETDATE()) = 2");
					List<Object[]> resultMobile = query.getResultList();
					for (Object[] objects : resultMobile) {
						
							try {
								query = manager.createNativeQuery("select EPLSNM, EMAIL, p.sigla   from tw_funcionario usu, adm_perfil_sistema_usuario psu, adm_perfil p"+
										" where psu.id_sistema = (select sis.id from adm_sistema sis where sis.sigla = 'RENPMP')"+
										" and psu.id_perfil in (select p.id from adm_perfil p where p.sigla = 'OPER' and p.tipo_sistema = 'RENPMP')"+
										" and usu.EPIDNO = psu.id_tw_usuario"+
										" and usu.stn1 =:filial" +
								" and p.id = psu.id_perfil");
								query.setParameter("filial", ((Integer)objects[6]).intValue());
								List<Object[]> objList = query.getResultList();
								for (Object[] pair : objList) {
									String msg = "";

									msg = ((String)pair[0])+", favor entrar em contato com o cliente "+(String)objects[0]+", telefone "+(String)objects[9]+" para informar que os serviços que foram realizados\n" +
									" no seu equipamento de modelo"+(String)objects[2]+", serie "+(String)objects[3]+", horimetro "+(String)objects[4]+", na data de "+(String)objects[7]+", pelo tecnico "+(String)objects[5]+".";
									emailHelper.sendSimpleMail(msg, MANUTENCAO_PMP, (String)pair[1]);

								}
							} catch (Exception e) {
								File file = File.createTempFile("log", ".txt", new File("."));
								 try {  
							            PrintStream ps = new PrintStream(file);  
							            e.printStackTrace(ps);  
							        } catch (FileNotFoundException e1) {  
							            e1.printStackTrace();  
							        }  
								emailHelper.sendSimpleMail("Erro ao executar o monitoramento do sistema de PMP", "ERRO MONITORAMENTO RENTAL", "rodrigo@rdrsistemas.com.br", file);
							}
						}
					
					
					query = manager.createNativeQuery("select distinct  c.id, tw.eplsnm, tw.email, p.validade_contrato, c.numero_contrato, c.numero_serie   " +
							" from ren_pmp_contrato c, tw_funcionario tw, ren_pmp_configuracao_precos p, ren_pmp_config_manutencao m"+
							" where c.id_status_contrato = (select s.id from ren_pmp_status_contrato s where s.sigla = 'CEN')"+
							" and tw.epidno = c.id_funcionario"+
							" and c.id_config_manutencao = m.id" +
							" and m.ID_CONFIGURACAO_PRECO = p.id"+
					" and DATEDIFF( DD, c.data_criacao,GETDATE()) >= p.validade_contrato");
					result = query.getResultList();
					manager.getTransaction().begin();
					for (Object[] objects : result) {
						query = manager.createQuery("From PmpStatusContrato where sigla = 'CNA'");
						PmpStatusContrato pmpStatusContrato = (PmpStatusContrato)query.getSingleResult();
						query = manager.createQuery("From PmpMotivosNaoFecContrato where sigla = 'VA'");
						PmpMotivosNaoFecContrato fecContrato = (PmpMotivosNaoFecContrato)query.getSingleResult();
						PmpContrato contrato = manager.find(PmpContrato.class, ((BigDecimal)objects[0]).longValue());
						contrato.setDataRejeicao(new Date());
						contrato.setIdMotivoNaoFecContrato(fecContrato);
						contrato.setIdStatusContrato(pmpStatusContrato);
						String msg = ((String)objects[1])+" informamos que o equipamento de serie "+(String)objects[5]+", proposta "+(String)objects[4]+" foi cancelado pelo motivo de validade da mesma.";
						emailHelper.sendSimpleMail(msg, MANUTENCAO_PMP, (String)objects[2]);
					}
					manager.getTransaction().commit();
					//isConnection = false;
					emailHelper.sendSimpleMail("Serviço de envio de e-mail executado", MANUTENCAO_PMP , "rodrigo@rdrsistemas.com.br");
				} catch (Exception e1) {
					File file = File.createTempFile("log", ".txt", new File("."));
					 try {  
				            PrintStream ps = new PrintStream(file);  
				            e1.printStackTrace(ps);  
				        } catch (FileNotFoundException e) {  
				            e.printStackTrace();  
				        }  
				        EmailHelper emailHelper = new EmailHelper();
					//emailHelper.sendSimpleMail("Erro ao executar o monitoramento do sistema de PMP", "ERRO MONITORAMENTO", "ti.monitoramento@marcosa.com.br");
					emailHelper.sendSimpleMail("Erro ao executar o monitoramento do sistema de PMP", "ERRO MONITORAMENTO RENTAL", "rodrigo@rdrsistemas.com.br", file);
				}
			//}
		}catch (Exception e) {
			e.printStackTrace();
		}
		manager.close();
	}


}
