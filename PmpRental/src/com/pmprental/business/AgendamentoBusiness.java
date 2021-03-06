package com.pmprental.business;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.pmprental.bean.AgendamentoBean;
import com.pmprental.bean.DataHeaderBean;
import com.pmprental.bean.PecasDbsBean;
import com.pmprental.bean.StatusAgendamentoBean;
import com.pmprental.bean.UsuarioBean;
import com.pmprental.entity.PmpAgendamento;
import com.pmprental.entity.PmpConfigOperacional;
import com.pmprental.entity.PmpContHorasStandard;
import com.pmprental.entity.PmpContrato;
import com.pmprental.entity.PmpFluxoDatas;
import com.pmprental.entity.PmpOsOperacional;
import com.pmprental.entity.PmpPecas;
import com.pmprental.entity.PmpStatusAgendamento;
import com.pmprental.entity.TwFuncionario;
import com.pmprental.util.ConectionDbs;
import com.pmprental.util.DateHelper;
import com.pmprental.util.EmailHelper;
import com.pmprental.util.IConstantAccess;
import com.pmprental.util.JpaUtil;

public class AgendamentoBusiness {
	private String ID_FUNCIONARIO;
	private String FILIAL;
	private UsuarioBean usuarioBean;
	String semana[] = {"Domingo","Segunda-feira","Terça-feira","Quarta-feira","Quinta-feira","Sexta-feira","Sábado","Domingo"}; 
	
	public AgendamentoBusiness() {
	}
	
	public AgendamentoBusiness(UsuarioBean bean) {
		ID_FUNCIONARIO = bean.getMatricula();
		FILIAL = bean.getFilial();
		this.usuarioBean = bean;
	}
	
	public List<DataHeaderBean> findAllHeaderList(String data){
		List<DataHeaderBean> result = new ArrayList<DataHeaderBean>();
		try {
			Calendar calendar = new GregorianCalendar();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			if(data == null){
				calendar.setTime(new Date());
			}else{
				try {
					calendar.setTime(sdf.parse(data));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			for(int i = 0; i < 7; i++){
				DataHeaderBean bean = new DataHeaderBean();
				bean.setData(calendar.getTime());
				bean.setDescricao(semana[calendar.get(Calendar.DAY_OF_WEEK) - 1]);
				bean.setDateString(sdf.format(calendar.getTime()));
				result.add(bean);
				calendar.add(Calendar.DAY_OF_MONTH, 1);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public List<UsuarioBean> findAllTecnico(List<DataHeaderBean> dataHeaderList){
		List<UsuarioBean> usuList = new ArrayList<UsuarioBean>();
		EntityManager manager = null;
		try {
			manager = JpaUtil.getInstance();

			Query query = manager.createNativeQuery("select EPLSNM, EPIDNO, login from tw_funcionario t, adm_perfil_sistema_usuario ps"+
												" where t.epidno = ps.id_tw_usuario"+
												" and t.stn1 =:filial"+
												" and ps.id_sistema = (select sis.id from adm_sistema sis where sis.sigla = 'RENPMP')"+
												" and ps.id_perfil = (select p.id from adm_perfil p where p.sigla = 'USUTEC' and p.tipo_sistema = 'RENPMP') order by EPLSNM");
			query.setParameter("filial", Integer.valueOf(FILIAL));
			List<Object[]> result = query.getResultList();
			for (Object [] pair : result) {
				UsuarioBean bean = new UsuarioBean();
				bean.setNome((((String)pair[0]).length() > 25)?(((String)pair[0])) .substring(0,25):(((String)pair[0])));
				bean.setNome(bean.getNome()+"/"+((String)pair[2]));
				bean.setMatricula((String)pair[1]);
				List<AgendamentoBean> agendamentoList = new ArrayList<AgendamentoBean>();
				for (DataHeaderBean dataHeaderBean : dataHeaderList) {
					query = manager.createNativeQuery("select a.id, a.id_status_agendamento, a.id_cong_operacional, a.id_funcionario, CONVERT(varchar(10), a.data_agendamento, 103) as dataAgendamento, a.horas_revisao, s.sigla, a.num_os, a.id_cont_horas_standard, CONVERT(varchar(1000), a.obs) as obs, a.data_faturamento, a.id_ajudante," +
							" (select EPLSNM from TW_FUNCIONARIO where EPIDNO = a.ID_AJUDANTE) as nome_ajudante, (select cast(palm.obs as varchar(255)) from ren_pmp_os_palm palm where palm.id_agendamento = a.id) as obsCheckList " +
							" from ren_pmp_agendamento a, ren_pmp_status_agendamento s" +
							" where CONVERT(varchar(10), a.data_agendamento, 103) = '"+dataHeaderBean.getDateString()+"' " +
							" and a.id_funcionario = :id_funcionario " +
							" and a.id_status_agendamento = s.id " +
							" and a.filial ='"+FILIAL+"'");
					query.setParameter("id_funcionario", (String)pair[1]);
					List<Object[]> objectList = (List<Object[]>)query.getResultList();
					AgendamentoBean agendamentoBeanInterno = new AgendamentoBean();
					for (Object[] objects : objectList) {
						AgendamentoBean agendamentoBean = new AgendamentoBean();
						
						if(objects[2] != null){							
							PmpConfigOperacional operacional = manager.find(PmpConfigOperacional.class, ((BigDecimal)objects[2]).longValue());
							agendamentoBean.setObsOs(operacional.getObs());
							agendamentoBean.setNumSerie(operacional.getIdContrato().getNumeroSerie());
							agendamentoBean.setContato(operacional.getContato());
							agendamentoBean.setLocal(operacional.getLocal());
							agendamentoBean.setTelefone(operacional.getTelefoneContato());
							agendamentoBean.setRazaoSocial(operacional.getIdContrato().getRazaoSocial());
							agendamentoBean.setIdConfOperacional((objects[2] != null)?((BigDecimal)objects[2]).longValue():null);
							agendamentoBean.setHorasRevisao((objects[5] != null)? ((BigDecimal)objects[5]).longValue(): null);
							PmpContHorasStandard standard = manager.find(PmpContHorasStandard.class, ((BigDecimal)objects[8]).longValue());
							agendamentoBean.setIdContHorasStandard(standard.getId());
							query = manager.createNativeQuery("select CODIGO, NOME_PECA, NUM_PECA, QTD  From Pmp_Pecas where id_Agendamento = :idAgendamento");
							query.setParameter("idAgendamento", ((BigDecimal)objects[0]).longValue());
							List<Object[]> pecas = query.getResultList();
							List<PecasDbsBean> pecasList = new ArrayList<PecasDbsBean>();
							for (Object[] pmpPecas : pecas) {
								PecasDbsBean dbsBean = new PecasDbsBean();
								dbsBean.setCodigo((String)pmpPecas[0]);
								dbsBean.setNomePeca((String)pmpPecas[1]);
								dbsBean.setNumPeca((String)pmpPecas[2]);
								dbsBean.setQtd(((BigDecimal)pmpPecas[3]).intValue());
								pecasList.add(dbsBean);
							}
							agendamentoBean.setPecasList(pecasList);
							//System.out.println(standard.getId());
							agendamentoBean.setHorasTrabalhadas(this.findHorasRevisao(standard.getId()).doubleValue());
						}
						
						agendamentoBean.setId(((BigDecimal)objects[0]).longValue());
						agendamentoBean.setIdStatusAgendamento(((BigDecimal)objects[1]).longValue());
						agendamentoBean.setIdFuncionario((String)objects[3]);
						agendamentoBean.setDataAgendamento((String)objects[4]);
						agendamentoBean.setSiglaStatus((String)objects[6]);
						agendamentoBean.setNumOs((String)objects[7]);
						agendamentoBean.setObs((String)objects[9]);
						if((String)objects[10] != null){
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
							SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							
							Date d = sdf2.parse((String)objects[10]);
							String dataFaturamento = sdf.format(d);
							agendamentoBean.setDataFaturamento(dataFaturamento);			
							
						}
						agendamentoBean.setIdAjudante((String)objects[11]);
						agendamentoBean.setNomeAjudante((String)objects[12]);
						agendamentoBean.setObsCheckList((String)objects[13]);
						agendamentoBeanInterno.getAgendamentoList().add(agendamentoBean);
					}
					agendamentoList.add(agendamentoBeanInterno);
				}
				bean.setAgendamentoList(agendamentoList);
				usuList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return usuList;
	}
	
	public synchronized List<UsuarioBean> saveOrUpdate(AgendamentoBean bean, List<DataHeaderBean> dataHeaderList){
		EntityManager manager = null;
		Connection con = null;
		Statement prstmtDataUpdate = null;
		ResultSet rs = null;
		try{
			con = com.pmprental.util.ConectionDbs.getConnecton(); 
			manager = JpaUtil.getInstance();
			manager.getTransaction().begin();
			PmpConfigOperacional operacional = manager.find(PmpConfigOperacional.class, bean.getIdConfOperacional());
			operacional.setContato(bean.getContato()!=null?bean.getContato().toUpperCase():null);
			operacional.setLocal(bean.getLocal()!= null?bean.getLocal().toUpperCase():null);
			operacional.setTelefoneContato(bean.getTelefone());
			PmpStatusAgendamento statusAgendamento = manager.find(PmpStatusAgendamento.class, bean.getIdStatusAgendamento());
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//			if(!DateHelper.verificarDatasVencimento(sdf.parse(bean.getDataAgendamento()))){
//				return null;
//			}		
			// Sun Jul 07 00:00:00 BRT 2013
			Calendar calBegin = new GregorianCalendar();
			calBegin.setTime(sdf.parse(bean.getDataAgendamento()));
					
			
			PmpAgendamento agendamento = new PmpAgendamento();
			if(bean.getId() != null && bean.getId() > 0){
				agendamento = manager.find(PmpAgendamento.class, bean.getId());
				Query query = manager.createNativeQuery("delete from ren_pmp_pecas where id_agendamento = :id_agendamento");
				query.setParameter("id_agendamento", bean.getId());
				query.executeUpdate();
			}
			agendamento.setIdAjudante(bean.getIdAjudante());
			agendamento.setHorasAgendadas(BigDecimal.valueOf(Double.valueOf(bean.getHorasTrabalhadas())));
			Calendar calEnd = new GregorianCalendar();
			calEnd.setTime(sdf.parse(bean.getDataAgendamentoFinal()));
			//System.out.println(calBegin.getTime());
			PmpContHorasStandard standard = manager.find(PmpContHorasStandard.class, bean.getIdContHorasStandard());
			
			if(standard !=null && standard.getIdContrato() != null){
				PmpContrato contrato = manager.find(PmpContrato.class, standard.getIdContrato().getId());
				
				Query query = manager.createNativeQuery("select COUNT(*) from REN_PMP_CONTRATO c, REN_PMP_CONFIG_OPERACIONAL op, REN_PMP_CONT_HORAS_STANDARD hs"+
						" where c.id = "+standard.getIdContrato().getId()+
						" and c.ID = op.ID_CONTRATO"+
						" and hs.ID_CONTRATO = c.id"+
						" and c.is_ativo is null"+
						" and IS_EXECUTADO = 'N'");
				
				Integer result = (Integer)query.getSingleResult();
				
				if(result == 1){
					contrato.setIsAtivo("I");
					manager.merge(contrato);					
				}					
			}
			
			String complemento = "";
			if(standard.getIdContrato().getIdConfigTracao() != null){
				complemento = " and (ocptmd  not in (select SIGLA_AC from REN_PMP_SIGLA_AC where ID_CONFIG_TRACAO = "+standard.getIdContrato().getIdConfigTracao().getId()+") or ocptmd is null)";
				complemento += " and (JWKAPP  not in (select SIGLA_AC from PMP_SIGLA_AC where ID_CONFIG_TRACAO = "+standard.getIdContrato().getIdConfigTracao().getId()+") or JWKAPP is null)"
							+ " and (ojbloc not in (select SIGLA_TRACAO from REN_PMP_SIGLA_TRACAO where ID_CONFIG_TRACAO = "+standard.getIdContrato().getIdConfigTracao().getId()+") or ojbloc is null)";
			}
			
			Query query = manager.createNativeQuery("select m.dlrqty, m.PANO20, m.DS18 from ren_pmp_manutencao m"+
					" where m.cptcd = '"+standard.getStandardJobCptcd()+"'"+
					" and m.bgrp = 'PM'"+
					" and substring(m.beqmsn,1,4) = '"+standard.getIdContrato().getPrefixo()+"'"+
					complemento+
					" and (m.beqmsn = '"+standard.getIdContrato().getBeginRanger()+"' or m.beqmsn = '"+standard.getIdContrato().getEndRanger()+"')");
			
			List<Object[]> pecasList = query.getResultList();
			
			if(pecasList.size() == bean.getPecasList().size()){
				for (int i = 0; i < bean.getPecasList().size(); i++) {
					PecasDbsBean pecasBean = (PecasDbsBean)bean.getPecasList().get(i);
					boolean isEquals = false;
					for (Object[] pair : pecasList) {
						if(pecasBean.getQtd().intValue() == ((BigDecimal)pair[0]).intValue()  && pecasBean.getNumPeca().trim().equals((String)pair[1]) ){
							isEquals = true;
						}
					}
					if(!isEquals){
						new EmailHelper().sendSimpleMail("Verifique urgente a "+bean.getNumOs()+" da revisao de "+bean.getHorasRevisao()+", pois, existe diferenca entre as pecas da revisao do Stander Job\n e as da OS", "Urgente PMP", "rodrigo@rdrsistemas.com.br");
						//new EmailHelper().sendSimpleMail("Verifique urgente a "+bean.getNumOs()+" da revisao de "+bean.getHorasRevisao()+", pois, existe diferenca entre as pecas da revisao do Stander Job\n e as da OS", "Urgente PMP", "costa_flademir@pesa.com.br");
						//new EmailHelper().sendSimpleMail("Verifique urgente a "+bean.getNumOs()+" da revisao de "+bean.getHorasRevisao()+", pois, existe diferenca entre as pecas da revisao do Stander Job\n e as da OS", "Urgente PMP", "salomao.freitas@marcosa.com.br");
						break;
					}
				}
			}else{
				new EmailHelper().sendSimpleMail("Verifique urgente a "+bean.getNumOs()+" da revisao de"+bean.getHorasRevisao()+", pois, existe diferenca entre as pecas da revisao do Stander Job\n e as da OS", "Urgente PMP", "rodrigo@rdrsistemas.com.br");
				//new EmailHelper().sendSimpleMail("Verifique urgente a "+bean.getNumOs()+" da revisao de"+bean.getHorasRevisao()+", pois, existe diferenca entre as pecas da revisao do Stander Job\n e as da OS", "Urgente PMP", "costa_flademir@pesa.com.br");
				//new EmailHelper().sendSimpleMail("Verifique urgente a "+bean.getNumOs()+" da revisao de"+bean.getHorasRevisao()+", pois, existe diferenca entre as pecas da revisao do Stander Job\n e as da OS", "Urgente PMP", "salomao.freitas@marcosa.com.br");
			}
			
			while(calBegin.getTimeInMillis() <= calEnd.getTimeInMillis()){
				agendamento.setDataAgendamento(calBegin.getTime());
				agendamento.setIdCongOperacional(operacional);
				agendamento.setIdFuncSupervisor(ID_FUNCIONARIO);
				agendamento.setIdFuncionario(bean.getIdFuncionario());
				agendamento.setIdStatusAgendamento(statusAgendamento);
				agendamento.setTipoPm(standard.getTipoPm());
				if(statusAgendamento.getSigla().equals("FIN")){
					standard.setIsExecutado("S");
				}else{
					standard.setIsExecutado("N");
				}
				agendamento.setNumOs(bean.getNumOs());
				agendamento.setHorasRevisao(bean.getHorasRevisao());
				agendamento.setFilial(Long.valueOf(FILIAL));
				agendamento.setIdContHorasStandard(standard);
				if(bean.getId() != null && bean.getId() > 0){
					manager.merge(agendamento);
				}else{
					manager.persist(agendamento);
				}
				for (int i = 0; i < bean.getPecasList().size(); i++) {
					PecasDbsBean pecasBean = (PecasDbsBean)bean.getPecasList().get(i);
					PmpPecas pecas = new PmpPecas();
					pecas.setCodigo(pecasBean.getCodigo());
					pecas.setIdAgendamento(agendamento);
					pecas.setNomePeca(pecasBean.getNomePeca());
					pecas.setNumPeca(pecasBean.getNumPeca());
					pecas.setQtd(pecasBean.getQtd().longValue());
					manager.persist(pecas);
				}
				calBegin.add(Calendar.DAY_OF_MONTH, 1);

				//manager.persist(agendamento);
			}
			//bean.setId(agendamento.getId().longValue());
			
			query = manager.createQuery("From PmpOsOperacional where numOs =:numOs");
			query.setParameter("numOs", bean.getNumOs());
			PmpOsOperacional osOperacional = (PmpOsOperacional)query.getSingleResult();
			try {
				String SQL = "select lbamt, frsthr from " +IConstantAccess.AMBIENTE_DBS+".USPSGSM0 where wonosm = '"+osOperacional.getId().toString()+"-S'";
				prstmtDataUpdate = con.createStatement();
				rs = prstmtDataUpdate.executeQuery(SQL);
				rs.next();
				String totalMO = rs.getString("lbamt");
				String horas = rs.getString("frsthr");
				String pair = "'"+osOperacional.getId().toString()+"-S','"+osOperacional.getNumeroSegmento()+"','"+osOperacional.getCscc()+"','"+osOperacional.getJobCode()+"','"+osOperacional.getCompCode()+"','"+osOperacional.getInd()+"','F','"+horas+"','F','"+totalMO+"','00001'";
				
				 SQL = "delete from " +IConstantAccess.AMBIENTE_DBS+".USPSGSM0 where wonosm = '"+osOperacional.getId().toString()+"-S'";
				 prstmtDataUpdate = con.createStatement();
				 prstmtDataUpdate.executeUpdate(SQL);
				
					TwFuncionario funcionario = manager.find(TwFuncionario.class, bean.getIdFuncionario());
					pair += ",'"+funcionario.getLogin()+"'";
					SQL = "insert into "+IConstantAccess.AMBIENTE_DBS+".USPSGSM0 (wonosm, wosgno, cscc, jbcd, cptcd, ind, shpfld, frsthr, lbrate, lbamt, qty, mecr) values("+pair+")";
					
					prstmtDataUpdate = con.createStatement();
					prstmtDataUpdate.executeUpdate(SQL);
				
			} catch (Exception e) {
				StringWriter writer = new StringWriter();
				e.printStackTrace(new PrintWriter(writer));
				EmailHelper emailHelper = new EmailHelper();
	        	emailHelper.sendSimpleMail("OS "+bean.getNumOs()+" Erro ao enviar mecanico responsavel! "+writer.toString(), "Erro ao inserir MECÂNICO RESPONSÁVEL "+osOperacional.getId().toString(), "rodrigo@rdrsistemas.com.br");
				e.printStackTrace();
			}
			
			

			if(statusAgendamento.getSigla().equals("EA") || statusAgendamento.getSigla().equals("FIN")){
				PmpFluxoDatas fluxoDatas = new PmpFluxoDatas(); 
				String data = null;
				String coluna = null;

				if(statusAgendamento.getSigla().equals("EA")){
					data = sdf.format(agendamento.getDataAgendamento());
					coluna = "005";
				}else{					
					Date dateAux = new Date();					
					data = sdf.format(dateAux);
					coluna = "006";					
				}
				fluxoDatas.setColuna(coluna);
				fluxoDatas.setData(data);
				fluxoDatas.setIdAgendamento(agendamento);
				manager.merge(fluxoDatas);
				
//				query = manager.createNativeQuery("select ESTIMATEBY from tw_funcionario where EPIDNO = '"+agendamento.getIdFuncionario()+"'");
//
//				String estimateBy = (String)query.getSingleResult();			
//
//				prstmtDataUpdate = setDateFluxoOSDBS(data, con, agendamento.getNumOs(), coluna, estimateBy);

			}
			
			manager.getTransaction().commit();
			
		}catch (Exception e) {
			if(manager != null && manager.getTransaction().isActive()){
				manager.getTransaction().rollback();
			}
			e.printStackTrace();
		} finally {
			try{
				if(manager != null && manager.isOpen()){
					manager.close();
				}
				if(prstmtDataUpdate != null){					
					prstmtDataUpdate.close();
				}
				if(con != null){
					con.close();
				}

			}catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return this.findAllTecnico(dataHeaderList);
	}
		
	public PreparedStatement setDateFluxoOSDBS(String dataPrevisao,
			Connection con, String numeroOs, String coluna, String estimateBy) throws SQLException {
		PreparedStatement prstmt_ = null;
		ResultSet rs = null;;
		try {
			String SQL = "select TRIM(W.NTDA) NTDA, substring(W.NTDA, 0, 15) as campoReplace from "+IConstantAccess.LIB_DBS+".WOPNOTE0 W where TRIM(W.WONO) = '"+numeroOs+"' AND TRIM(W.NTLNO1) = '"+coluna+"' AND TRIM(W.WOSGNO) = ''";
			prstmt_ = con.prepareStatement(SQL);
			rs = prstmt_.executeQuery();
			if(rs.next()){
				String NTDA = rs.getString("NTDA");
				String campoReplace = rs.getString("campoReplace");
				String dataAux = NTDA.replace(campoReplace, dataPrevisao+" "+((estimateBy != null) ? estimateBy : "   "));
				SQL = "update "+IConstantAccess.LIB_DBS+".WOPNOTE0 W set W.NTDA = '"+dataAux+"' where TRIM(W.WONO) = '"+numeroOs+"' AND TRIM(W.NTLNO1) = '"+coluna+"' AND TRIM(W.WOSGNO) = ''";
				prstmt_ = con.prepareStatement(SQL);
				prstmt_.executeUpdate();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			rs.close();
			prstmt_.close();
		}
		return prstmt_;
	}	
	
	public List<AgendamentoBean> findAllOsDisponiveis(){
		EntityManager manager = null;
		List<AgendamentoBean> result = new ArrayList<AgendamentoBean>();
		try{
			manager = JpaUtil.getInstance();
			Query query = manager.createNativeQuery("select op.num_os, co.contato, co.local, co.telefone_contato, co.id_contrato, co.id, CONVERT(varchar(1000),co.obs)," +
													"  (select cast(palm.OBS as varchar(255)) from REN_PMP_AGENDAMENTO ag, REN_PMP_OS_PALM palm where palm.NUMERO_OS = op.num_os and palm.ID_AGENDAMENTO = ag.ID) as obsCheckList, c.numero_serie" +
													"   from ren_pmp_cont_horas_standard hs,  ren_pmp_os_operacional op, ren_pmp_config_operacional co, ren_pmp_contrato c" +
													"   where hs.is_executado = 'N' "+
													"	and hs.id_os_operacional is not null" +
													"	and co.id_contrato = c.id" +
													//"   and hs.id not in (select ag.id_cont_horas_standard from pmp_agendamento ag )" +
													"   and hs.id_os_operacional = op.id" +
													"   and co.id_contrato = hs.id_contrato" +
													"   and op.filial =:filial" +
													"   order by op.num_os");
			query.setParameter("filial", Integer.valueOf(FILIAL));
			List<Object[]> pairs = (List<Object[]>)query.getResultList();
			for (Object[] objects : pairs) {
				AgendamentoBean bean = new AgendamentoBean();
				bean.setNumOs((String)objects[0]);
				bean.setContato((String)objects[1]);
				bean.setLocal((String)objects[2]);
				bean.setTelefone((String)objects[3]);
				bean.setIdConfOperacional(((BigDecimal)objects[5]).longValue());
				bean.setObsOs((String)objects[6]);
				bean.setNumSerie((String)objects[8]);
				Map<String, Double> map = this.findHorasProximaRevisao(((BigDecimal)objects[4]).longValue(), manager);
				bean.setHorasRevisao(map.get("horas").longValue());
				bean.setIdContHorasStandard(map.get("id").longValue());
				bean.setHorasTrabalhadas(map.get("horasTrabalhadas"));
				bean.setObsCheckList((String)objects[7]);
				result.add(bean);
			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return result;
	}
	
	private Map<String, Double> findHorasProximaRevisao(Long idContrato,EntityManager manager){
		Query query = manager.createNativeQuery("select hs.horas_manutencao, hs.id from ren_pmp_contrato con, ren_pmp_cont_horas_standard hs"+
				"	where con.id = :idContrato"+
				"	and hs.id_contrato = con.id"+
				"	and  hs.is_executado = 'N'"+
				"	order by hs.horas_manutencao");
		query.setParameter("idContrato", idContrato);
		List<Object[]> result = (List<Object[]>)query.getResultList();
		Map<String, Double> map = new HashMap<String, Double>();
		if(result.size() > 0){
			Object[] pair = result.get(0);
			map.put("horas", ((BigDecimal)pair[0]).doubleValue());
			map.put("id", ((BigDecimal)pair[1]).doubleValue());
			map.put("horasTrabalhadas", this.findHorasRevisao(((BigDecimal)pair[1]).longValue()).doubleValue());
		}
		return map;
	}
	
	/**
	 * Recupera as horas necessárias para realizar a revisão;
	 * @param idAgendamento
	 * @return
	 */
	public BigDecimal findHorasRevisao(Long idContHorasStandard){
			
			EntityManager manager = null;
			BigDecimal totalHHManutencao = BigDecimal.ZERO;
			try {
				manager = JpaUtil.getInstance();
				PmpContHorasStandard hs = manager.find(PmpContHorasStandard.class, idContHorasStandard);
				//total horas para a manutenção
				Query query = manager.createNativeQuery("select sum(cast(FRSDHR as decimal(18,2))) from ren_pmp_hora h"+
						" where h.cptcd = '"+hs.getStandardJobCptcd()+"'"+
						" and h.bgrp = '"+hs.getIdContrato().getBgrp()+"'"+
						" and substring(h.beqmsn,1,4) = '"+hs.getIdContrato().getPrefixo()+"'"+
						" and (h.beqmsn = '"+hs.getIdContrato().getBeginRanger()+"' or h.beqmsn = '"+hs.getIdContrato().getEndRanger()+"')");
				totalHHManutencao = (BigDecimal)query.getSingleResult();
				if(totalHHManutencao == null){
					totalHHManutencao = BigDecimal.ZERO;
				}
			} catch (Exception e) {
				totalHHManutencao = BigDecimal.ZERO;
				e.printStackTrace();
			}finally {
				if(manager != null && manager.isOpen()){
					manager.close();
				}
			}
			return totalHHManutencao;
		}
	
	public List<PecasDbsBean> findPecas(String numberOs){
		ResultSet rs = null;
		PreparedStatement prstmt_ = null;
		Connection con = null;

//		InputStream in = getClass().getClassLoader().getResourceAsStream(IConstantAccess.CONF_FILE);
//		Properties prop = new Properties();
		List<PecasDbsBean> pecasList = new ArrayList<PecasDbsBean>();
		try {
//			prop.load(in);
//			String url = prop.getProperty("dbs.url");
//			String user = prop.getProperty("dbs.user");
//			String password =prop.getProperty("dbs.password");
//			Class.forName(prop.getProperty("dbs.driver")).newInstance();

			//						String url = "jdbc:as400://192.168.128.146";
			//						String user = "XUPU15PSS";
			//						String password = "marcosa";
			//						Class.forName("com.ibm.as400.access.AS400JDBCDriver").newInstance();
			//con = DriverManager.getConnection(url, user, password); 
			con = ConectionDbs.getConnecton();
			String SQL  = "SELECT   a.pano20, a.ds18, a.rfdcno, a.orqy"+
			" FROM     "+IConstantAccess.LIB_DBS+".woppart0 a"+
			" WHERE    wono='"+numberOs+"'  ";
			prstmt_ = con.prepareStatement(SQL);
			rs = prstmt_.executeQuery();

			while(rs.next()){
				PecasDbsBean bean = new PecasDbsBean();
				bean.setCodigo(rs.getString("rfdcno"));
				bean.setNomePeca(rs.getString("ds18"));
				bean.setNumPeca(rs.getString("pano20"));
				bean.setQtd(rs.getInt("orqy"));


				pecasList.add(bean);

			}	

		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(con != null){
					con.close();
				}
				if(prstmt_ != null){
					prstmt_.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return pecasList;
	}
	
	public List<StatusAgendamentoBean> findAllStatusAgendamento(){
		EntityManager manager = null;
		List<StatusAgendamentoBean> result = new ArrayList<StatusAgendamentoBean>();
		try{
			manager = JpaUtil.getInstance();
			Query query = manager.createQuery("From PmpStatusAgendamento where sigla <> 'OBS'");
			List<PmpStatusAgendamento> list = query.getResultList();
			for (PmpStatusAgendamento pmpStatusAgendamento : list) {
				StatusAgendamentoBean bean = new StatusAgendamentoBean();
				bean.setId(pmpStatusAgendamento.getId().longValue());
				bean.setDescricao(pmpStatusAgendamento.getDescricao());
				bean.setSigla(pmpStatusAgendamento.getSigla());
				result.add(bean);
			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return result;
	}
	
	public List<UsuarioBean> remover(Long idAgendamento, List<DataHeaderBean> dataHeaderList){
		EntityManager manager = null;
		try{
			manager = JpaUtil.getInstance();
			manager.getTransaction().begin();
			PmpAgendamento agendamento = manager.find(PmpAgendamento.class, idAgendamento);
			
			if(agendamento.getIsFindTecnico() != null && agendamento.getIsFindTecnico().equals("S")){
				return null;
			}
			
			PmpContHorasStandard standard = manager.find(PmpContHorasStandard.class, agendamento.getIdContHorasStandard().getId());
			standard.setIsExecutado("N");
			manager.merge(standard);
			manager.remove(agendamento);
			manager.getTransaction().commit();
		}catch (Exception e) {
			if(manager != null && manager.getTransaction().isActive()){
				manager.getTransaction().rollback();
			}
		   e.printStackTrace();
		} finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return this.findAllTecnico(dataHeaderList);
	}
	
	public synchronized List<AgendamentoBean> findAllAgendamentosPendentes(Integer inicial, Integer numRegistros, String tipoAgendamento, String campoPesquisa){
		EntityManager manager = null;
		List<AgendamentoBean> result = new ArrayList<AgendamentoBean>();
		List<AgendamentoBean> resultNull = new ArrayList<AgendamentoBean>();
		try{
			manager = JpaUtil.getInstance();
//			Query query = manager.createNativeQuery("select C.id,C.Numero_Contrato, C.modelo, C.numero_Serie as serie, " +
//					" ( select op.num_os from pmp_os_operacional op where  op.ID_CONT_HORAS_STANDARD = (select st.id from pmp_cont_horas_standard st where st.horas_manutencao = (select  min(horas_Manutencao) from Pmp_Cont_Horas_Standard  where id_Contrato = C.Id and is_Executado = 'N') and id_Contrato = C.id))  as num_os, " +
//					//" op.num_os,"+
//					" (select horimetro from pmp_maquina_pl pl where pl.numero_serie = C.Numero_Serie and pl.horimetro is not null and pl.id = (select max(id) from pmp_maquina_pl where numero_serie = pl.numero_serie and horimetro is not null and horimetro > 0) ) as horimetro, " +
//					"  (select  min(horas_Manutencao) from Pmp_Cont_Horas_Standard  where id_Contrato = C.id and is_Executado = 'N') as horas_manutencao," +
//					" ((select  min(horas_Manutencao) from Pmp_Cont_Horas_Standard  where id_Contrato = C.id and is_Executado = 'N')  - (select max(horimetro) from pmp_maquina_pl pl where pl.numero_serie = C.Numero_Serie " +
//					"  and pl.horimetro is not null and pl.data_atualizacao = (select max(data_atualizacao) " +
//					"   from pmp_maquina_pl" +
//					" where numero_serie = pl.numero_serie and horimetro is not null and HORIMETRO > 0) )) horas_pendentes, c.codigo_cliente," +
//					" ( select st.id from pmp_cont_horas_standard st where st.horas_manutencao = (select  min(horas_Manutencao) from Pmp_Cont_Horas_Standard  where id_Contrato = C.Id and is_Executado = 'N') and id_Contrato = C.Id)  as idContOperacional," +
//					" ( select count(ag.id) from Pmp_Agendamento ag where  ag.id_cont_horas_standard = (select st.id from pmp_cont_horas_standard st where st.horas_manutencao = (select  min(horas_Manutencao) from Pmp_Cont_Horas_Standard  where id_Contrato = C.id and is_Executado = 'N') and id_Contrato = C.id)) statusAgendamento," +
//					" (select convert(varchar(10),max(pl.data_atualizacao),103) from pmp_maquina_pl pl where pl.numero_serie = c.numero_serie)," +
//					" (select hs.standard_job_cptcd from Pmp_Cont_Horas_Standard hs"+
//					" where horas_manutencao = (select  min(horas_manutencao) from Pmp_Cont_Horas_Standard  where id_Contrato = C.id and is_Executado = 'N')"+
//					" and hs.id_contrato = C.id) standard_job_cptcd, " +
//					" c.filial origem," +
//					" (select t.sigla from pmp_tipo_contrato t where t.id = c.id_tipo_contrato), co.filial destino, c.razao_social, co.local," +
//					" ( select op.num_doc from pmp_os_operacional op where  op.id = (select st.id_os_operacional from pmp_cont_horas_standard st where st.horas_manutencao = (select  min(horas_Manutencao) from Pmp_Cont_Horas_Standard  where id_Contrato = C.Id and is_Executado = 'N') and id_Contrato = C.id))  as num_doc," +
//					" ( select op.msg from pmp_os_operacional op where  op.id = (select st.id_os_operacional from pmp_cont_horas_standard st where st.horas_manutencao = (select  min(horas_Manutencao) from Pmp_Cont_Horas_Standard  where id_Contrato = C.Id and is_Executado = 'N') and id_Contrato = C.id))  as msg," +
//					" ( select op.COD_ERRO_OS_DBS from pmp_os_operacional op where  op.ID_CONT_HORAS_STANDARD = (select st.id from pmp_cont_horas_standard st where st.horas_manutencao = (select  min(horas_Manutencao) from Pmp_Cont_Horas_Standard  where id_Contrato = C.Id and is_Executado = 'N') and id_Contrato = C.id))  as COD_ERRO_OS_DBS,"+
//					" ( select op.COD_ERRO_DOC_DBS from pmp_os_operacional op where  op.ID_CONT_HORAS_STANDARD = (select st.id from pmp_cont_horas_standard st where st.horas_manutencao = (select  min(horas_Manutencao) from Pmp_Cont_Horas_Standard  where id_Contrato = C.Id and is_Executado = 'N') and id_Contrato = C.id))  as COD_ERRO_DOC_DBS,"+
//					" ( select op.id from pmp_os_operacional op where  op.ID_CONT_HORAS_STANDARD = (select st.id from pmp_cont_horas_standard st where st.horas_manutencao = (select  min(horas_Manutencao) from Pmp_Cont_Horas_Standard  where id_Contrato = C.Id and is_Executado = 'N') and id_Contrato = C.id))  as idOsOperacional," +
//					"  tc.sigla as sigla, convert(varchar(4000), co.obs) as obs"+
//					"  from pmp_contrato c, pmp_config_operacional co, pmp_tipo_contrato tc" +
//					" where c.id_Status_Contrato = (select s.id from Pmp_Status_Contrato s where s.sigla = 'CA') " +
//					" and  c.id in((select  distinct id_Contrato  from Pmp_Cont_Horas_Standard hs where id_Contrato = c.id and is_Executado = 'N')) " +
//					" and co.num_os is not null " +
//					" and co.id_contrato = c.id" +
//					" and c.id_tipo_contrato = tc.id" +
//					//" and op.id_Config_Operacional = co.id" +
//					" and co.filial = "+Integer.valueOf(FILIAL)+
//					//Caso a OS puder ser agendada mais de uma vez comentar essa linha
//					//" and co.id not in (select ag.id_cong_operacional from pmp_agendamento ag)"+
//					" order by horas_pendentes");
			
			String SQL = "select C.id,"+//0
														" c.Numero_Contrato,"+//1 
														" c.modelo, " +//2
														" c.numero_Serie as serie,"+//3 
														" c.codigo_cliente,"+//4				  
														" c.filial origem,"+//5
														" tc.sigla,"+//6
														" co.filial destino,"+//7 
														" c.razao_social,"+ //8
														" co.local,"+//9		
														" tc.sigla as sigla,"+//10 
														" convert(varchar(4000), co.obs) as obs," +//11
														" c.media_horas_mes," +//12
														" c.id_equipamento, " +//13
														"  ((select  min(horas_Manutencao) from Ren_Pmp_Cont_Horas_Standard  where id_Contrato = C.id and is_Executado = 'N')  - (select max(horimetro) from ren_pmp_maquina_pl pl where pl.numero_serie = C.Numero_Serie"+ 
														"  and pl.horimetro is not null and pl.id = (select max(id)"+ 
														"   from ren_pmp_maquina_pl"+
														" where numero_serie = pl.numero_serie and horimetro is not null and HORIMETRO > 0) )) horas_pendentes," +//14
														" c.make,"+//15
														" (select  min(horas_Manutencao) from ren_Pmp_Cont_Horas_Standard  where id_Contrato = c.id and is_Executado = 'N') horas_Manutencao,"+//16
														" (select horimetro from ren_pmp_maquina_pl pl where pl.numero_serie = C.Numero_Serie"+
														" and pl.horimetro is not null and pl.id = (select max(id)"+
														"  from ren_pmp_maquina_pl"+
														"   where numero_serie = pl.numero_serie and horimetro is not null and HORIMETRO > 0) ) horimetro"+//17
														" from ren_pmp_contrato c, ren_pmp_config_operacional co, ren_pmp_tipo_contrato tc"+
														" where c.id_Status_Contrato = (select s.id from Ren_Pmp_Status_Contrato s where s.sigla = 'CA')"+ 
														" and  c.id in((select  distinct id_Contrato  from Ren_Pmp_Cont_Horas_Standard hs where id_Contrato = c.id and is_Executado = 'N'))"+ 
														" and co.num_os is not null "+
														" and co.id_contrato = c.id"+
														" and c.id_tipo_contrato = tc.id"+
														" and co.filial = "+Integer.valueOf(FILIAL);
			if(tipoAgendamento.equals("TA")){
				SQL += " and  (select count(ag.id) from Ren_Pmp_Agendamento ag where ag.ID_STATUS_AGENDAMENTO in (select ID from REN_PMP_STATUS_AGENDAMENTO where SIGLA in ('EA','AT')) and  ag.id_cont_horas_standard = (select st.id from ren_pmp_cont_horas_standard st where st.horas_manutencao = (select  min(horas_Manutencao) from ren_Pmp_Cont_Horas_Standard  where id_Contrato = c.id and is_Executado = 'N') and id_Contrato = c.id)) > 0";
			}
			if(tipoAgendamento.equals("TNA")){
				SQL += " and  (select count(ag.id) from ren_Pmp_Agendamento ag where  ag.id_cont_horas_standard = (select st.id from ren_pmp_cont_horas_standard st where st.horas_manutencao = (select  min(horas_Manutencao) from ren_Pmp_Cont_Horas_Standard  where id_Contrato = c.id and is_Executado = 'N') and id_Contrato = c.id)) = 0";
			}
			if(campoPesquisa.trim().length() > 0){
				SQL += " and (C.numero_Serie like '%"+campoPesquisa.toUpperCase()+"%' or C.modelo like '%"+campoPesquisa.toUpperCase()+"%' or C.Numero_Contrato like '%"+campoPesquisa.toUpperCase()+"%' or C.numero_Serie like '%"+campoPesquisa.toUpperCase()+"%' or c.razao_social like '%"+campoPesquisa.toUpperCase()+"%' or c.id_equipamento like '%"+campoPesquisa.toUpperCase()+"%')";
			}

			SQL += " order by horas_pendentes";
			Query query = manager.createNativeQuery(SQL);
			List<Object[]> resultQuery = query.getResultList();
			Integer tamanhoTotalLista = resultQuery.size();
			//query.setFirstResult(inicial);
			//setMaxResults(numRegistros);

			
			int tamanho = inicial + numRegistros;
			if(tamanho > resultQuery.size()){
				tamanho = resultQuery.size();
			}
			for (int i = inicial; i < tamanho; i++){
			//for (Object[] objects : resultQuery) {
				Object[] objects = resultQuery.get(i);
			
				
//				query = manager.createNativeQuery("select  min(horas_Manutencao) from ren_Pmp_Cont_Horas_Standard  where id_Contrato = "+((BigDecimal)objects[0]).longValue()+" and is_Executado = 'N'");
//				BigDecimal horasManutencao = (BigDecimal)query.getSingleResult(); 
				BigDecimal horasManutencao = (BigDecimal)objects[16]; 
				
				
				
				query = manager.createNativeQuery("select st.id, st.standard_job_cptcd from ren_pmp_cont_horas_standard st where st.horas_manutencao = "+horasManutencao+" and id_Contrato = "+((BigDecimal)objects[0]).longValue());
				BigDecimal idContHorasStandard = BigDecimal.ZERO;
				String standerJob = null;
				if(query.getResultList().size() > 0){
					Object[] pair = (Object[])query.getSingleResult();
					idContHorasStandard = (BigDecimal)pair[0];
					standerJob = (String)pair[1];
				}
				
//				query = manager.createNativeQuery("select op.num_os from ren_pmp_os_operacional op where  op.ID_CONT_HORAS_STANDARD = "+idContHorasStandard);
//				String numeroOs = null;
//				if(query.getResultList().size() > 0){
//					numeroOs = (String)query.getSingleResult();
//				}
				//query = manager.createNativeQuery("select horimetro from ren_pmp_maquina_pl pl where pl.numero_serie = '"+(String)objects[3]+"'  and pl.horimetro is not null and pl.id = (select max(id) from ren_pmp_maquina_pl where numero_serie = pl.numero_serie and horimetro is not null and horimetro > 0)");
				BigDecimal horimetro = BigDecimal.ZERO;
				if(objects[17] != null){
					horimetro = (BigDecimal)objects[17];;
				}
				
				query = manager.createNativeQuery(" select CONVERT(varchar(10), ag.data_agendamento, 103) from ren_Pmp_Agendamento ag where ag.ID_STATUS_AGENDAMENTO in (select ID from REN_PMP_STATUS_AGENDAMENTO where SIGLA in ('EA','AT'))  and  ag.id_cont_horas_standard = "+idContHorasStandard);
				String dataAgendamento = "";
				if(query.getResultList().size() > 0){
					List<String> strings = (List<String>)query.getResultList();
					for (String data : strings) {
						dataAgendamento += data+" ";
					}
					
				}

				//query = manager.createNativeQuery("select convert(varchar(10),max(pl.data_atualizacao),103) from pmp_maquina_pl pl where pl.numero_serie = '"+(String)objects[3]+"' and pl.horimetro is not null and pl.horimetro > 0");
//				String dataAtualizacaoHorimentro = null;
//				if(query.getResultList().size() > 0){
//					dataAtualizacaoHorimentro = (String)query.getSingleResult();
//				}

//				query = manager.createNativeQuery("select hs.standard_job_cptcd from ren_Pmp_Cont_Horas_Standard hs  where id = "+idContHorasStandard);
//				String standerJob = null;
//				if(query.getResultList().size() > 0){
//					standerJob = (String)query.getSingleResult();
//				}
				query = manager.createNativeQuery("select op.num_doc, op.msg, op.COD_ERRO_OS_DBS, op.COD_ERRO_DOC_DBS, op.id, op.num_os from ren_pmp_os_operacional op where  op.ID_CONT_HORAS_STANDARD = "+idContHorasStandard);
				String numDoc = null;
				String msg = null;
				String codErroOsDbs = null;
				String codErroDocDbs = null;
				BigDecimal idOsOperacional = null;
				String numeroOs = null;
				if(query.getResultList().size() > 0){
					Object [] pair = (Object [])query.getSingleResult();
					numDoc = (String)pair[0];
					msg = (String)pair[1];
					codErroOsDbs = (String)pair[2];
					codErroDocDbs = (String)pair[3];
					idOsOperacional = (BigDecimal)pair[4];
					numeroOs = (String)pair[5];
				}
//				query = manager.createNativeQuery("select op.msg from ren_pmp_os_operacional op where  op.ID_CONT_HORAS_STANDARD = "+idContHorasStandard);
//				String msg = null;
//				if(query.getResultList().size() > 0){
//					msg = (String)query.getSingleResult();
//				}
//				query = manager.createNativeQuery("select op.COD_ERRO_OS_DBS from ren_pmp_os_operacional op where  op.ID_CONT_HORAS_STANDARD = "+idContHorasStandard);
//				String codErroOsDbs = null;
//				if(query.getResultList().size() > 0){
//					codErroOsDbs = (String)query.getSingleResult();
//				}
//				query = manager.createNativeQuery("select op.COD_ERRO_DOC_DBS from ren_pmp_os_operacional op where  op.ID_CONT_HORAS_STANDARD = "+idContHorasStandard);
//				String codErroDocDbs = null;
//				if(query.getResultList().size() > 0){
//					codErroDocDbs = (String)query.getSingleResult();
//				}
//				query = manager.createNativeQuery("select id from ren_pmp_os_operacional op where  op.ID_CONT_HORAS_STANDARD = "+idContHorasStandard);
//				BigDecimal idOsOperacional = null;
//				if(query.getResultList().size() > 0){
//					idOsOperacional = (BigDecimal)query.getSingleResult();
//				}
				AgendamentoBean bean = new AgendamentoBean();
				bean.setIdContrato(((BigDecimal)objects[0]).longValue());
				bean.setNumContrato((String)objects[1]);
				bean.setModelo((String)objects[2]);
				bean.setNumSerie((String)objects[3]);
				bean.setNumOs(numeroOs);
				if(horimetro != null){
					bean.setHorimetro(horimetro.longValue());
				}
				try {
					//bean.setHorasRevisao(((BigDecimal)objects[6]).longValue());
					bean.setHorasRevisao(horasManutencao.longValue());
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					if(horasManutencao != null && horimetro != null){
						bean.setHorasPendentes(horasManutencao.longValue() - horimetro.longValue());
						bean.setUrlStatus("img/CE.png");
						if(bean.getHorasPendentes() <= 50){
							bean.setUrlStatus("img/CN.png");
						}
//						Double mediaDiasProximaRevisao = ((horasManutencao.doubleValue() - horimetro.doubleValue())/((BigDecimal)objects[12]).doubleValue())*30;
//						query = manager.createNativeQuery(" select DATEDIFF ( day , max(palm.EMISSAO), GETDATE())   from ren_Pmp_Agendamento ag, ren_pmp_os_palm palm where  ag.id_cont_horas_standard in (select st.id from ren_pmp_cont_horas_standard st where id_Contrato = "+((BigDecimal)objects[0]).longValue()+") and palm.ID_AGENDAMENTO = ag.ID");
//						if(query.getResultList().size() > 0 && query.getResultList().get(0) != null){
//							Integer dias = (Integer)query.getSingleResult();
//							mediaDiasProximaRevisao = mediaDiasProximaRevisao - dias;
//						}else{
//							query = manager.createNativeQuery(" select DATEDIFF ( day , max(c.data_aceite), GETDATE())   from ren_Pmp_contrato  c where  c.id = "+((BigDecimal)objects[0]).longValue());
//							Integer dias = (Integer)query.getSingleResult();
//							mediaDiasProximaRevisao = mediaDiasProximaRevisao -dias;
//						}
//						Long diasProximaRevisao = Math.round(mediaDiasProximaRevisao);
//						bean.setMediaDiasProximaRevisao(diasProximaRevisao.toString());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				bean.setCodigoCliente((String)objects[4]);
				bean.setIdContHorasStandard(idContHorasStandard.longValue());
				bean.setStatusAgendamento((dataAgendamento.equals(""))?"Não Agendado":dataAgendamento);
				//bean.setDataAtualizacaoHorimetro(dataAtualizacaoHorimentro);
				bean.setStandardJob(standerJob);
				bean.setFilial(((BigDecimal)objects[5]).toString());
				bean.setSiglaTipoContrato((String)objects[6]);
				bean.setFilialDestino(((BigDecimal)objects[7]).toString());
				bean.setRazaoSocial((String)objects[8]);
				bean.setLocal((String)objects[9]);
				bean.setNumDoc(numDoc);
				bean.setMsg(msg);
				bean.setCodErroOsDbs(codErroOsDbs);
				bean.setCodErroDocDbs(codErroDocDbs);
				if(idOsOperacional != null){
					bean.setIdOsOperacional(idOsOperacional.longValue());
				}
				//bean.setSiglaTipoContrato((String)objects[23]);
				bean.setTotalRegistros(tamanhoTotalLista);
				bean.setObsOs((String)objects[10]);
				bean.setIdEquipamento((String)objects[13]);
				bean.setMake((String)objects[15]);
//				if(bean.getHorimetro() == null || bean.getHorimetro() == 0){
//					resultNull.add(bean);
//				}else{
					result.add(bean);
				//}
			}
			//result.addAll(resultNull);
		}catch (Exception e) {
		   e.printStackTrace();
		} finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return result;
	}
	
	public Boolean saveObsAgendamento(AgendamentoBean bean) {
		
		EntityManager manager = null;
		
		try {
			manager = JpaUtil.getInstance();
			manager.getTransaction().begin();
			
			PmpAgendamento agendamento = null;
			
			if(bean.getId() == null || bean.getId() == 0){				
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Calendar calBegin = new GregorianCalendar();
			calBegin.setTime(sdf.parse(bean.getDataAgendamento()));
			
			agendamento = new PmpAgendamento();
			
			Query query = manager.createQuery("FROM PmpStatusAgendamento WHERE sigla = 'OBS'");
			
			PmpStatusAgendamento statusAgendamento = (PmpStatusAgendamento)query.getSingleResult();
			
			agendamento.setIdStatusAgendamento(statusAgendamento);
			agendamento.setIdFuncSupervisor(ID_FUNCIONARIO);
			agendamento.setIdFuncionario(bean.getIdFuncionario());
			agendamento.setDataAgendamento(calBegin.getTime());
			agendamento.setFilial(Long.valueOf(FILIAL));
			agendamento.setNumOs("OBSERVAÇÃO");
			agendamento.setObs(bean.getObs());
			manager.persist(agendamento);			
			}else{
				agendamento = manager.find(PmpAgendamento.class, bean.getId());
				agendamento.setObs(bean.getObs());
				manager.merge(agendamento);
			}
			
			manager.getTransaction().commit();
			
			return true;
			
		} catch (Exception e) {
			if(manager != null && manager.getTransaction().isActive()){
				manager.getTransaction().rollback();
			}
		   e.printStackTrace();
		}finally{
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}		
		return false;
	}
	
	public Boolean removerAgendamentoObs(AgendamentoBean bean) {

		EntityManager manager = null;

		try {
			manager = JpaUtil.getInstance();
			manager.getTransaction().begin();
			manager.remove(manager.find(PmpAgendamento.class, bean.getId()));
			manager.getTransaction().commit();
			
			return true;

		} catch (Exception e) {
			if(manager != null && manager.getTransaction().isActive()){
				manager.getTransaction().rollback();
			}
			e.printStackTrace();
		}finally{
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return false;
	}
	
	public Boolean saveDataFaturamento(AgendamentoBean bean) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		EntityManager manager = null;
		Connection con = null;
		Statement prstmtDataUpdate = null;
		
		try {
			
			manager = JpaUtil.getInstance();
			manager.getTransaction().begin();
			con = ConectionDbs.getConnecton();
			
			PmpAgendamento agendamento = manager.find(PmpAgendamento.class, bean.getId());
			agendamento.setDataFaturamento(sdf.parse(bean.getDataFaturamento()));
			
			manager.merge(agendamento);
			
			manager.getTransaction().commit();
								
			//Query query = manager.createNativeQuery("SELECT ESTIMATEBY FROM tw_funcionario WHERE EPIDNO = '"+agendamento.getIdFuncionario()+"'");
			
			String estimateBy = this.usuarioBean.getEstimateBy();
			
			prstmtDataUpdate = setDateFluxoOSDBS(bean.getDataFaturamento(), con, bean.getNumOs(), "007", estimateBy);
			
			return true;
			
		} catch (Exception e) {
			if(manager != null && manager.getTransaction().isActive()){
				manager.getTransaction().rollback();
			}
		   e.printStackTrace();
		}finally{
			if(manager != null && manager.isOpen()){
				manager.close();
			}
			try {
				if(prstmtDataUpdate != null){
					prstmtDataUpdate.close();
				}
				if(con != null){
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}		
		return false;
	}
	public List<AgendamentoBean> pesquisarOsNaoRealizada(String numOs){
		List<AgendamentoBean> result = new ArrayList<AgendamentoBean>();

		EntityManager manager = null;
		
		try {
			manager = JpaUtil.getInstance();
			
			String SQL = "select os.NUM_OS, hs.HORAS_MANUTENCAO from REN_PMP_OS_OPERACIONAL os, REN_PMP_CONT_HORAS_STANDARD hs "+
					" where NUM_OS like '%"+numOs+"%'"+
					" and os.ID = hs.ID_OS_OPERACIONAL"+
					" and hs.IS_EXECUTADO = 'N'";
			
			
			Query query = manager.createNativeQuery(SQL);
			List<Object[]> list = query.getResultList();
			for (Object[] pmpAgendamento : list) {
				AgendamentoBean bean = new AgendamentoBean();
				bean.setNumOs((String)pmpAgendamento[0]);
				bean.setHorasRevisao(((BigDecimal)pmpAgendamento[1]).longValue());
				result.add(bean);
			}

		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return result;
	}
	
	public Boolean removerAgendamento(AgendamentoBean bean){
		EntityManager manager = null;
		try{
			manager = JpaUtil.getInstance();
			manager.getTransaction().begin();
			String SQL = "update "+ 
					" REN_PMP_CONT_HORAS_STANDARD "+ 
					" set ID_OS_OPERACIONAL = null "+
					" where ID_OS_OPERACIONAL = (select id from REN_PMP_OS_OPERACIONAL where NUM_OS =:numOs)";
			
            Query query = manager.createNativeQuery(SQL);
            query.setParameter("numOs", bean.getNumOs());
            query.executeUpdate();

            SQL ="delete from REN_PMP_AGENDAMENTO where NUM_OS =:numOs ";
            query = manager.createNativeQuery(SQL);
            query.setParameter("numOs", bean.getNumOs());
            query.executeUpdate();
            
            SQL ="delete from REN_PMP_OS_OPERACIONAL where NUM_OS =:numOs ";
            query = manager.createNativeQuery(SQL);
            query.setParameter("numOs", bean.getNumOs());
            query.executeUpdate();
            
			manager.getTransaction().commit();
			return true;
		}catch (Exception e) {
			if(manager != null && manager.getTransaction().isActive()){
				manager.getTransaction().rollback();
			}
		   e.printStackTrace();
		} finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return false;
	}
	
	public static void main(String[] args) {
		Calendar calendar = Calendar.getInstance();
		System.out.println(calendar.get(calendar.MONTH));
	}
	
}

