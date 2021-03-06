package com.pmprental.business;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.pmprental.bean.ComponenteCodeBean;
import com.pmprental.bean.FilialBean;
import com.pmprental.bean.JobCodeBean;
import com.pmprental.bean.JobControlBean;
import com.pmprental.bean.MaquinaPlBean;
import com.pmprental.bean.OsEstimada;
import com.pmprental.bean.TotalAgendamentoChartBean;
import com.pmprental.bean.UsuarioBean;
import com.pmprental.entity.PmpConfigOperacional;
import com.pmprental.entity.PmpContHorasStandard;
import com.pmprental.entity.PmpContrato;
import com.pmprental.entity.PmpFamilia;
import com.pmprental.entity.PmpOsOperacional;
import com.pmprental.entity.PmpPecasConfigOperacional;
import com.pmprental.entity.PmpPecasOsOperacional;
import com.pmprental.entity.TwFilial;
import com.pmprental.util.ConectionDbs;
import com.pmprental.util.IConstantAccess;
import com.pmprental.util.JpaUtil;
import com.pmprental.util.ValorMonetarioHelper;

public class OsBusiness {

	private String FILIAL;
	private static String HQL_FIND_ALL_FILIAIS = "FROM TwFilial ORDER BY stnm";
	private SimpleDateFormat dateFormatHorimetroDbs = new SimpleDateFormat("ddMMyy");
	
    public OsBusiness(UsuarioBean bean) {
		FILIAL = bean.getFilial();
	}

	public Collection<FilialBean> findAllFiliais() {
		Collection<FilialBean> listForm = new ArrayList<FilialBean>();
		EntityManager manager = null;
		try {
			manager = JpaUtil.getInstance();
			Query query = manager.createQuery(HQL_FIND_ALL_FILIAIS);
			List<TwFilial> list = (List<TwFilial>) query.getResultList();
			for (TwFilial twFil : list) {
				FilialBean filialBean = new FilialBean();
				filialBean.fromBean(twFil);
				listForm.add(filialBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return listForm;
	}
	
	public Collection<JobControlBean> findAllJobControl() {
		Collection<JobControlBean> listForm = new ArrayList<JobControlBean>();
		EntityManager manager = null;
		
		try {
			manager = JpaUtil.getInstance();
			
			Query query = manager.createNativeQuery("select respar from jobcontrol order by respar");
			List<String> list = (List<String>) query.getResultList();
			for (String jbctr : list) {
				JobControlBean bean = new JobControlBean();
				bean.setDescricao(jbctr);
				listForm.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return listForm;
	}
	
	public Collection<JobCodeBean> findAllJobCode() {
		Collection<JobCodeBean> listForm = new ArrayList<JobCodeBean>();
		EntityManager manager = null;
		
		try {
			manager = JpaUtil.getInstance();
			
			Query query = manager.createNativeQuery("select jbcd, jbcdds from jbcd order by jbcd");
			List<Object[]> list = (List<Object[]>) query.getResultList();
			for (Object[] jbcd : list) {
				JobCodeBean bean = new JobCodeBean();
				bean.setId((String)jbcd[0]);
				bean.setDescricao((String)jbcd[1]);
				bean.setLabel((String)jbcd[0]+" - "+(String)jbcd[1]);
				listForm.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return listForm;
	}

	public Collection<ComponenteCodeBean> findAllCompCode(String caracter) {
		Collection<ComponenteCodeBean> listForm = new ArrayList<ComponenteCodeBean>();
		
		EntityManager manager = null;
		try {
			manager = JpaUtil.getInstance();
			
			Query query = manager.createNativeQuery("select cptcd, cptcdd from cptcd where cptcdd like '"+caracter.toUpperCase()+"%'");
			List<Object[]> list = (List<Object[]>) query.getResultList();
			for (Object[] jbcd : list) {
				ComponenteCodeBean bean = new ComponenteCodeBean();
				bean.setId((String)jbcd[0]);
				bean.setDescricao((String)jbcd[1]);
				bean.setLabel((String)jbcd[0]+" - "+(String)jbcd[1]);
				listForm.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return listForm;
	}
	
	 private final Timer timer = new Timer();
	  
	    public void startNewOsEstimada(final OsEstimada bean, final PmpOsOperacional osOperacional, final String prefixo, final String beginRanger, final String endRanger) {
	        timer.schedule(new TimerTask() {
	        	public void run() {
	        		if(createOsEstimadaThread(bean, osOperacional, prefixo, beginRanger, endRanger)){
	        			timer.cancel();
	        		}
	        	}
	        },  30 * 1000);
	    }
	    public void startNewOsEstimadaContrato(final OsEstimada bean, final PmpConfigOperacional operacional, final PmpContrato contrato) {
	    	timer.schedule(new TimerTask() {
	    		public void run() {
	    			if(createOsEstimadaContratoThread(bean, operacional, contrato)){
	    				timer.cancel();
	    			}
	    		}
	    	
	    	},  30 * 1000);
	    }
	
		public Boolean validarNumSerie(String numSerie){
			
			ResultSet rs = null;
			PreparedStatement prstmt_ = null;

			Connection con = null;

//			InputStream in = getClass().getClassLoader().getResourceAsStream(IConstantAccess.CONF_FILE);
//			Properties prop = new Properties();
			EntityManager manager = null;
			try {
				manager = JpaUtil.getInstance();
//				prop.load(in);
//				String url = prop.getProperty("dbs.url");
//				String user = prop.getProperty("dbs.user");
//				String password =prop.getProperty("dbs.password");
//				Class.forName(prop.getProperty("dbs.driver")).newInstance();

//				con = com.pmprental.util.ConectionDbs.getConnecton();
//				String SQL = "select * from "+IConstantAccess.LIB_DBS+".empeqpd0 f where trim(f.eqmfs2) = '"+numSerie+"'";
//				prstmt_ = con.prepareStatement(SQL);
//				rs = prstmt_.executeQuery();
//				if(rs.next()){
//					return true;
//				}
				
				con = com.pmprental.util.ConectionDbs.getConnecton();
				String SQL = "select * from "+IConstantAccess.LIB_DBS+".empeqpd0 f where (trim(f.eqmfs2) = '"+numSerie+"' or trim(f.RDMSR1) = '"+numSerie+"')";
				prstmt_ = con.prepareStatement(SQL);
				rs = prstmt_.executeQuery();
				if(rs.next()){
					return true;
				}
				SQL = "select * from "+IConstantAccess.LIB_DBS+".EMPATCH0 f where trim(f.ATSLN1) = '"+numSerie+"'";
				
				prstmt_ = con.prepareStatement(SQL);
				rs = prstmt_.executeQuery();
				if(rs.next()){
					return true;
				}
				
				SQL = "select * from "+IConstantAccess.LIB_DBS+".EMPORDH0 where eqmfs2 = '"+numSerie+"'";
				prstmt_ = con.prepareStatement(SQL);
				rs = prstmt_.executeQuery();
				if(rs.next()){
					return true;
				}
				
				Query query = manager.createQuery("from PmpContrato where numeroSerie =:numeroSerie");
				query.setParameter("numeroSerie", numSerie);
				
				List<PmpContrato> listContratos = (List<PmpContrato>)query.getResultList();
				if(listContratos.size() > 0){
					PmpContrato contrato = (PmpContrato)query.getResultList().get(0);
					PmpFamilia familia = manager.find(PmpFamilia.class, contrato.getIdFamilia());
					if(familia.getDescricao().equals("REBOCADORES")){

						SQL = "select * from "+IConstantAccess.LIB_DBS+".empeqpd0 f where (trim(f.eqmfs2) = '"+contrato.getNumeroSerie().substring(4)+"' or trim(f.RDMSR1) = '"+numSerie+"')";
						prstmt_ = con.prepareStatement(SQL);
						rs = prstmt_.executeQuery();
						if(rs.next()){
							return true;
						}
						SQL = "select * from "+IConstantAccess.LIB_DBS+".EMPATCH0 f where trim(f.ATSLN1) = '"+contrato.getNumeroSerie().substring(4)+"'";

						prstmt_ = con.prepareStatement(SQL);
						rs = prstmt_.executeQuery();
						if(rs.next()){
							return true;
						}

						SQL = "select * from "+IConstantAccess.LIB_DBS+".EMPORDH0 where eqmfs2 = '"+contrato.getNumeroSerie().substring(4)+"'";
						prstmt_ = con.prepareStatement(SQL);
						rs = prstmt_.executeQuery();
						if(rs.next()){
							return true;
						}
					}
				}
				return false;

			}catch (Exception e) {
				e.printStackTrace();
				return false;
			}finally{
				if(manager != null && manager.isOpen()){
					manager.close();
				}
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
		}
		
		public static void main(String[] args) {
			System.out.println("TTT178432".substring(3));
		}
		
		public Boolean openOs(Long id, String numOs, String letra){
			Connection con = null;
			Statement statement = null;
			try {
				con = ConectionDbs.getConnecton();
				String SQL = "insert into  "+IConstantAccess.AMBIENTE_DBS+".USPPPSM0 (WONOSM, WONO) values('"+id+"-"+letra+"','"+numOs+"')";
				statement = con.createStatement();
				statement.executeUpdate(SQL);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				try {
					if(con != null){
						con.close();
					}
					if(statement != null){
						statement.close();						
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			return false;
		}
		
		public OsEstimada newOsEstimada(OsEstimada bean){
			bean.setMsg("");
			EntityManager manager = null;
			Connection con = null;
			Statement statement = null;
			try {
				manager = JpaUtil.getInstance();
				
				PmpContrato contrato = manager.find(PmpContrato.class, bean.getAgendamentoBean().getIdContrato());
				
				MaquinaPlBusiness maquinaPlBusiness = new MaquinaPlBusiness();
				MaquinaPlBean maquinaPlNovo = new MaquinaPlBean();
				maquinaPlNovo.setNumeroSerie (contrato.getNumeroSerie());
				maquinaPlNovo.setModelo(contrato.getModelo());
				maquinaPlNovo.setHorimetro(bean.getHorimetro());
				//maquinaPlNovo.setIdContrato(contrato.getId());
				maquinaPlBusiness.saveOrUpdate(maquinaPlNovo);
				
				Query query = manager.createQuery("From PmpConfigOperacional where idContrato.id = :id");
				query.setParameter("id", bean.getAgendamentoBean().getIdContrato());
				PmpConfigOperacional operacional = (PmpConfigOperacional)query.getSingleResult();
				
				query = manager.createNativeQuery("select HORIMETRO from  REN_PMP_MAQUINA_PL where NUMERO_SERIE = '"+operacional.getIdContrato().getNumeroSerie()+"' and HORIMETRO is not null"+
						  " and DATA_ATUALIZACAO = (select MAX(DATA_ATUALIZACAO) from REN_PMP_MAQUINA_PL where NUMERO_SERIE = '"+operacional.getIdContrato().getNumeroSerie()+"' and HORIMETRO is not null)");

				BigDecimal smu = (BigDecimal)query.getSingleResult();
				manager.getTransaction().begin();

				PmpOsOperacional osOperacional = new PmpOsOperacional();
				if(bean.getAgendamentoBean().getIdContHorasStandard() != null && bean.getAgendamentoBean().getIdContHorasStandard() > 0){
					query  = manager.createQuery("From PmpOsOperacional where idContHorasStandard.id = :id");
					query.setParameter("id", bean.getAgendamentoBean().getIdContHorasStandard());
					if(query.getResultList().size() > 0){
						osOperacional = (PmpOsOperacional)query.getSingleResult();
						////	    		if(osOperacional.getNumOs() != null && !"".equals(osOperacional.getNumOs())){
						////	    			return bean;
						////	    		}
						//	    		if(this.verifyCreateOs(osOperacional.getId().toString())){
						//	    			this.startNewOsEstimada(bean, osOperacional, osOperacional.getIdConfigOperacional().getIdContrato().getPrefixo(), osOperacional.getIdConfigOperacional().getIdContrato().getBeginRanger(), osOperacional.getIdConfigOperacional().getIdContrato().getEndRanger());
						//	    			return bean;
						//	    		}
						//					if(osOperacional.getCodErroOsDbs() != null && osOperacional.getCodErroOsDbs().equals("100")){
						//						bean.setMsg("Aguarde o retorno do DBS, pois, a OS já foi enviada!");
						//						return bean;
						//					}
					}
				}
				//osOperacional = new PmpOsOperacional();
				osOperacional.setNumOs("Aguarde o retorno do DBS!");
				osOperacional.setIdConfigOperacional(operacional);
				osOperacional.setCodErroOsDbs("100");
				osOperacional.setFilial(Long.valueOf(FILIAL));
				osOperacional.setNumeroSegmento(bean.getSegmento());
				osOperacional.setCscc("CL");
				osOperacional.setJobCode(bean.getJobCode());
				osOperacional.setCompCode(bean.getComponenteCode());
				osOperacional.setInd("E");
				osOperacional.setData(new Date());
				osOperacional.setIdContHorasStandard(manager.find(PmpContHorasStandard.class, bean.getAgendamentoBean().getIdContHorasStandard()));
				manager.persist(osOperacional);
				
				if(this.verifyCreateOsAgendamento(osOperacional.getId().toString())){
					manager.getTransaction().commit();
					if(operacional.getNumOs() != null && !"".equals(operacional.getNumOs())){
						return bean;
					}
					//this.startNewOsEstimadaContrato(bean, operacional, contrato);
					return bean;
				}
				
				
				
				/*Formata Horimetro e data atual para enviar para o DBS*/
				String horimetro = smu.toString();			
				horimetro += "0";		
				
				while (horimetro.length() < 8){
					horimetro = "0"+ horimetro;
				}		
				
				String dataHorimetroDbs = dateFormatHorimetroDbs.format(new Date());
				//Cria uma os estimada no dbs
				if(this.createOsEstimada(osOperacional.getId().toString(), bean.getSegmento(), osOperacional.getCscc(), 
						bean.getJobCode(), bean.getComponenteCode(), osOperacional.getInd(), bean.getJobControl(), (Integer.valueOf(bean.getFilialStore()) < 10)?"0"+bean.getFilialStore():bean.getFilialStore(), bean.getCodigoCliente()
								, bean.getEstimateBy(), bean.getMake(), bean.getNumeroSerie(), null, operacional.getIdContrato().getBgrp(), horimetro, dataHorimetroDbs, "S")){//S = Schedule que é agendamento
					//this.startNewOsEstimada(bean, osOperacional, osOperacional.getIdConfigOperacional().getIdContrato().getPrefixo(), osOperacional.getIdConfigOperacional().getIdContrato().getBeginRanger(), osOperacional.getIdConfigOperacional().getIdContrato().getEndRanger());
					con = ConectionDbs.getConnecton();
					
					ResultSet rs = null;

					String SQL = "SELECT WCLPIPS0.EQMFCD, WCLPIPS0.EQMFSN, WCLPIPS0.PIPNO, WCLPIPS0.OPNDT8"+
					" FROM "+IConstantAccess.LIB_DBS+".WCLPIPS0 WCLPIPS0"+
					" WHERE (WCLPIPS0.OPNDT8=0) "+
					" AND WCLPIPS0.EQMFSN = '"+bean.getNumeroSerie()+"'";
					statement = con.createStatement();
					rs = statement.executeQuery(SQL);
					if(rs.next()){
						bean.setMsg("Os estimada enviada com sucesso.\nPOSSIBILIDADE DE PIP/PSP!");
					}
					SQL = "select trim(t.CUNO) CUNO, trim(t.CRLMT) CRLMT, trim(t.TERMCD) TERMCD from "+IConstantAccess.LIB_DBS+".cipname0 t where t.CUNO = '"+bean.getCodigoCliente()+"'";
					statement = con.createStatement();
					rs = statement.executeQuery(SQL);
					if(rs.next()){
						String msg = "";
						if("1".equals(rs.getString("TERMCD"))){
							msg = "O Pagamento deve ser À VISTA, pois o cliente não possui crédito!";
						}else if("2".equals(rs.getString("TERMCD"))){
							msg = "O cliente possui crédito no valor de "+(String.valueOf(ValorMonetarioHelper.formata("###,###,##0.00", Double.valueOf(String.valueOf(rs.getString("CRLMT"))))))+"!";
						}else if("4".equals(rs.getString("TERMCD"))){
							msg = "O cliente está sem movimentação e possui um crédito de "+(String.valueOf(ValorMonetarioHelper.formata("###,###,##0.00", Double.valueOf(String.valueOf(rs.getString("CRLMT"))))))+"!\nFavor providenciar a atualização do cadastro do cliente!";
						}
						if(bean.getMsg() != null){
							bean.setMsg(bean.getMsg()+"\n"+msg);
						}
					}
					manager.getTransaction().commit();
					return bean;
				}else{
					manager.getTransaction().rollback();
				}
			} catch (Exception e) {
				if(manager != null && manager.getTransaction().isActive()){
					manager.getTransaction().rollback();
				}
				e.printStackTrace();
			}finally{
				if(manager != null && manager.isOpen()){
					manager.close();
				}
				if(con != null){
					try {
						if(statement != null){
							statement.close();
						}
						con.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			bean.setMsg("Não foi possível criar OS estimada!");
			return bean;
	}
		
	private Boolean createOsEstimadaThread(OsEstimada bean, PmpOsOperacional osOperacional, String prefixo, String beginRanger, String endRanger) {
		
		EntityManager manager = null;
		Connection con = ConectionDbs.getConnecton();
		try {
			Statement stmt = con.createStatement();
			manager = JpaUtil.getInstance();
			manager.getTransaction().begin();

			String wono = this.findOsEstimada(osOperacional.getId().toString());
			
			if(wono == null){
				try {
					PmpOsOperacional operacional = manager.find(PmpOsOperacional.class, osOperacional.getId());
					if(operacional.getNumOs() == null || "".equals(operacional.getNumOs())){
						manager.remove(manager.find(PmpOsOperacional.class, osOperacional.getId()));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}
			if("".equals(wono)){
				return false;
			}else{

				osOperacional.setNumOs(wono.trim());			
				manager.merge(osOperacional);
				this.sendPecasDbs(osOperacional,manager, stmt);	
				//Coloca a OS como Open
				//this.openOs(osOperacional.getId(), wono.trim());
				
			}
			manager.getTransaction().commit();	

			if(bean.getVcc().getTipoCliente() != null && !bean.getVcc().getTipoCliente().equals("")){
				if(bean.getVcc().getTipoCliente().equals("INT")){
					if(!this.updateIntoClienteInterOrExcecaoGarantiaDbs(wono,bean.getVcc().getClienteInter(), bean.getVcc().getContaContabilSigla(), bean.getVcc().getCentroDeCustoSigla(),bean.getSegmento(), osOperacional.getId().toString())){
						bean.setMsg("A Ordem de serviço extimada foi criada, mas não foi possível cadastrar centro de custo e conta contábil!");
					}
				}else if(bean.getVcc().getTipoCliente().equals("EXT")){
					if(!this.updateGarantiaClienteExternoDbs(wono, bean.getVcc().getContaContabilSigla(), bean.getVcc().getCentroDeCustoSigla(),bean.getSegmento(),osOperacional.getId().toString())){
						bean.setMsg("A Ordem de serviço extimada foi criada, mas não foi possível cadastrar centro de custo e conta contábil!");
					}
				}
			}else{
				//significa que é garantica com a regra de excessão
				if(!("").equals(bean.getVcc().getClienteInter())){
					if(!this.updateIntoClienteInterOrExcecaoGarantiaDbs(wono,bean.getVcc().getClienteInter(), bean.getVcc().getContaContabilSigla(), bean.getVcc().getCentroDeCustoSigla(),bean.getSegmento(),osOperacional.getId().toString())){
						bean.setMsg("A Ordem de serviço extimada foi criada, mas não foi possível cadastrar centro de custo e conta contábil!");
					}
				}else{//grantia sem regra de excessão
					if(!this.updateGarantiaClienteExternoDbs(wono, bean.getVcc().getContaContabilSigla(), bean.getVcc().getCentroDeCustoSigla(),bean.getSegmento(),osOperacional.getId().toString())){
						bean.setMsg("A Ordem de serviço extimada foi criada, mas não foi possível cadastrar centro de custo e conta contábil!");
					}
				}
			}

		}catch (Exception e) {
			if(manager != null && manager.getTransaction().isActive()){
				manager.getTransaction().rollback();
			}
			e.printStackTrace();
		}finally{
			try {
				if(con != null){
					con.close();					
				}
				if(manager != null && manager.isOpen()){
					manager.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	public Boolean sendPecasDbs(Long idContHorasStandard){
		EntityManager manager = null;
		Connection conn = null;
		Statement stament = null;
		try{
			manager = JpaUtil.getInstance();
			manager.getTransaction().begin();
			PmpContHorasStandard chs = manager.find(PmpContHorasStandard.class, idContHorasStandard);
			
			Query query = manager.createQuery("from PmpOsOperacional where idContHorasStandard.id =:idContHorasStandard");
			query.setParameter("idContHorasStandard", chs.getId());
			
			PmpOsOperacional operacional = (PmpOsOperacional)query.getSingleResult();
			operacional.setCodErroDocDbs("100");
			operacional.setMsg("Aguarde o retorno da cotação!");
			conn = com.pmprental.util.ConectionDbs.getConnecton();
			stament = conn.createStatement();
			this.sendPecasReviewDbs(operacional, manager, stament);
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
				try {
					if(conn != null){
						conn.close();
					}						
					if(stament != null){
						stament.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		
		return false;
	}

	public void sendPecasDbs(PmpOsOperacional osOperacional,
			EntityManager manager, Statement stmt)
			throws SQLException {
		try {
			
			Query query = manager.createNativeQuery("select ID_TIPO_CUSTOMIZACAO from REN_PMP_CONTRATO_CUSTOMIZACAO where id_contrato =:id_contrato");
			query.setParameter("id_contrato", osOperacional.getIdContHorasStandard().getIdContrato().getId());
			List<BigDecimal> contratoCustList =  query.getResultList();
			String siglaCustomizacao = "";
			String complementoSigla = "";
			for (BigDecimal idTipoCustomizacao : contratoCustList) {
						query = manager.createNativeQuery("select sc.sigla_customizacao from REN_PMP_SIGLA_CUSTOMIZACAO sc, REN_PMP_CONFIG_CUSTOMIZACAO cc"+
													" where cc.ID_TIPO_CUSTOMIZACAO =:ID_TIPO_CUSTOMIZACAO"+
													" and sc.id_config_customizacao = cc.id");
						query.setParameter("ID_TIPO_CUSTOMIZACAO", idTipoCustomizacao);
						List<String> sgTipoCustList = query.getResultList();
						for (String string : sgTipoCustList) {
							siglaCustomizacao += "'"+string+"',";
						}
				}
			if(siglaCustomizacao.length() > 0){
				siglaCustomizacao = siglaCustomizacao.substring(0, siglaCustomizacao.length() - 1);
			}else{
				siglaCustomizacao = "'-null'";
			}
			
			String complemento = "";
			if(osOperacional.getIdConfigOperacional().getIdContrato().getIdConfigTracao() != null){
				complemento = " and (ocptmd  not in (select SIGLA_AC from REN_PMP_SIGLA_AC where ID_CONFIG_TRACAO = "+osOperacional.getIdConfigOperacional().getIdContrato().getIdConfigTracao().getId()+") or ocptmd is null)";
				complemento += " and (JWKAPP  not in (select SIGLA_AC from PMP_SIGLA_AC where ID_CONFIG_TRACAO = "+osOperacional.getIdConfigOperacional().getIdContrato().getIdConfigTracao().getId()+") or JWKAPP is null)"
							+ " and (ojbloc not in (select SIGLA_TRACAO from REN_PMP_SIGLA_TRACAO where ID_CONFIG_TRACAO = "+osOperacional.getIdConfigOperacional().getIdContrato().getIdConfigTracao().getId()+") or ojbloc is null)";
			}
			
			if(siglaCustomizacao.length() > 0){
				complementoSigla =  " and ojbloc not in ("+siglaCustomizacao+")";
				complementoSigla +=  " and ocptmd not in ("+siglaCustomizacao+")";
				complementoSigla +=  " and ocptmd not in ("+siglaCustomizacao+")";
			}
			
			PmpContHorasStandard horasStandard = manager.find(PmpContHorasStandard.class, osOperacional.getIdContHorasStandard().getId());
			horasStandard.setIdOsOperacional(osOperacional.getId());
			manager.merge(horasStandard);
			String SQL = "select m.pano20,  m.dlrqty, m.sos, m.ds18  from ren_pmp_manutencao m"+
					" where m.cptcd = '"+osOperacional.getCompCode()+"'"+
					" and m.bgrp = '"+horasStandard.getIdContrato().getBgrp()+"'"+
					" and substring(m.beqmsn,1,4) = '"+osOperacional.getIdConfigOperacional().getIdContrato().getPrefixo()+"'"+
					 complemento + complementoSigla +
					 " and (m.beqmsn = '"+osOperacional.getIdConfigOperacional().getIdContrato().getBeginRanger()+"' or m.beqmsn = '"+osOperacional.getIdConfigOperacional().getIdContrato().getEndRanger()+"') ";
			
			query = manager.createNativeQuery(SQL);
			List<Object[]> list = query.getResultList();
			//manager.getTransaction().begin();
			stmt.executeUpdate("delete from "+IConstantAccess.AMBIENTE_DBS+".USPPLSM0 where PEDSM = '"+osOperacional.getId()+"-S'");
			stmt.executeUpdate("delete from "+IConstantAccess.AMBIENTE_DBS+".USPPWSM0 where PEDSM = '"+osOperacional.getId()+"-S'");
			for (Object[] pair : list) {
				SQL = "insert into "+IConstantAccess.AMBIENTE_DBS+".USPPLSM0 (PEDSM, SOS, PANO20, QTDE) values('"+osOperacional.getId()+"-S', '"+(String)pair[2]+"', '"+((String)pair[0]).replace("-", "")+"', '"+((pair[1] != null)?((BigDecimal)pair[1]).intValue():0)+"')";
				stmt.executeUpdate(SQL);
				PmpPecasOsOperacional operacional = new PmpPecasOsOperacional();
				operacional.setIdOsOperacional(osOperacional);
				operacional.setSos((String)pair[2]);
				operacional.setNomePeca((String)pair[3]);
				operacional.setNumPeca((String)pair[0]);
				operacional.setQtd(((pair[1] != null)?((BigDecimal)pair[1]).longValue():0));
				manager.persist(operacional);
			}
			//manager.getTransaction().commit();
			
			SQL = "insert into "+IConstantAccess.AMBIENTE_DBS+".USPPWSM0 (PEDSM, WONOSM, SGNOSM, OPERSM) values('"+osOperacional.getId()+"-S', '"+osOperacional.getNumOs()+"', '01', '')";
			stmt.executeUpdate(SQL);
		} catch (Exception e) {
//			if(manager != null && manager.getTransaction().isActive()){
//				manager.getTransaction().rollback();
//			}
			e.printStackTrace();
		}
	}

	public void sendTotalPecasPecasDbs(PmpContrato contrato,
			EntityManager manager, Statement stmt, String inCptcd, String siglaCustomizacao)
	throws SQLException {
		try {

			String complemento = "";
			String complementoSigla = "";
			if(contrato.getIdConfigTracao() != null){
				complemento = " and (ocptmd  not in (select SIGLA_AC from REN_PMP_SIGLA_AC where ID_CONFIG_TRACAO = "+contrato.getIdConfigTracao().getId()+") or ocptmd is null)";
				complemento += " and (JWKAPP  not in (select SIGLA_AC from PMP_SIGLA_AC where ID_CONFIG_TRACAO = "+contrato.getIdConfigTracao().getId()+") or JWKAPP is null)"
							+ " and (ojbloc not in (select SIGLA_TRACAO from REN_PMP_SIGLA_TRACAO where ID_CONFIG_TRACAO = "+contrato.getIdConfigTracao().getId()+") or ojbloc is null)";
			}
			if(siglaCustomizacao.length() > 0){
				complementoSigla =  " and ojbloc not in ("+siglaCustomizacao+")";
				complementoSigla +=  " and ocptmd not in ("+siglaCustomizacao+")";
				complementoSigla +=  " and ocptmd not in ("+siglaCustomizacao+")";
			}
			Query query = manager.createNativeQuery("delete from Ren_Pmp_Pecas_Config_Operacional where id_contrato = "+contrato.getId());
			query.executeUpdate();
			String SQL = "select m.pano20,  m.dlrqty, m.sos, m.ds18, m.bectyc, m.cptcd  from ren_pmp_manutencao m"+
			" where m.cptcd in ("+inCptcd+")"+
			" and m.bgrp = '"+contrato.getBgrp()+"'"+
			" and substring(m.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
			  complemento + complementoSigla +
			  " and (m.beqmsn = '"+contrato.getBeginRanger()+"' or m.beqmsn = '"+contrato.getEndRanger()+"') ";
			
			query = manager.createNativeQuery(SQL);
			List<Object[]> list = query.getResultList();
			//manager.getTransaction().begin();
			stmt.executeUpdate("delete from "+IConstantAccess.AMBIENTE_DBS+".USPPLSM0 where PEDSM = 'C-"+contrato.getId()+"'");
			stmt.executeUpdate("delete from "+IConstantAccess.AMBIENTE_DBS+".USPPHSM0 where PEDSM = 'C-"+contrato.getId()+"'");
			for (Object[] pair : list) {
				SQL = "insert into "+IConstantAccess.AMBIENTE_DBS+".USPPLSM0 (PEDSM, SOS, PANO20, QTDE) values('C-"+contrato.getId()+"', '"+(String)pair[2]+"', '"+((String)pair[0]).replace("-", "")+"', '"+((pair[1] != null)?((BigDecimal)pair[1]).intValue():0)+"')";
				stmt.executeUpdate(SQL);
				PmpPecasConfigOperacional operacional = new PmpPecasConfigOperacional();
				operacional.setIdContrato(contrato);
				operacional.setSos((String)pair[2]);
				operacional.setNomePeca((String)pair[3]);
				operacional.setNumPeca((String)pair[0]);
				operacional.setQtd(((pair[1] != null)?((BigDecimal)pair[1]).longValue():0));
				operacional.setBectyc((String)pair[4]);
				operacional.setCptcd((String)pair[5]);
				manager.persist(operacional);
			}
			//manager.getTransaction().commit();
			
			//SQL = "insert into "+IConstantAccess.AMBIENTE_DBS+".USPPWSM0 (PEDSM, WONOSM, SGNOSM, OPERSM) values('"+osOperacional.getId()+"-C', '"+osOperacional.getNumOs()+"', '01', '')";
			SQL = "insert into "+IConstantAccess.AMBIENTE_DBS+".USPPHSM0 (PEDSM, STNSM, CUNOSM) values('C-"+contrato.getId()+"', '"+((contrato.getFilial()< 10)?"0"+contrato.getFilial():contrato.getFilial())+"', '"+contrato.getCodigoCliente()+"')";
			stmt.executeUpdate(SQL);
		} catch (Exception e) {
//			if(manager != null && manager.getTransaction().isActive()){
//				manager.getTransaction().rollback();
//			}
			e.printStackTrace();
		}
	}
	
	public void sendPecasReviewDbs(PmpOsOperacional osOperacional,
			EntityManager manager, Statement stmt)
	throws SQLException {
					
			String SQL = "from PmpPecasOsOperacional where idOsOperacional.id =:idOsOperacional";			
			Query query = manager.createQuery(SQL);
			query.setParameter("idOsOperacional", osOperacional.getId());
			List<PmpPecasOsOperacional> list = query.getResultList();
			//manager.getTransaction().begin();
			stmt.executeUpdate("delete from "+IConstantAccess.AMBIENTE_DBS+".USPPLSM0 where PEDSM = '"+osOperacional.getId()+"-S'");
			stmt.executeUpdate("delete from "+IConstantAccess.AMBIENTE_DBS+".USPPWSM0 where PEDSM = '"+osOperacional.getId()+"-S'");
			for (PmpPecasOsOperacional operacional : list) {
				SQL = "insert into "+IConstantAccess.AMBIENTE_DBS+".USPPLSM0 (PEDSM, SOS, PANO20, QTDE) values('"+osOperacional.getId()+"-S', '"+operacional.getSos()+"', '"+operacional.getNumPeca()+"', '"+operacional.getQtd()+"')";
				stmt.executeUpdate(SQL);
			}
			//manager.getTransaction().commit();
			
			SQL = "insert into "+IConstantAccess.AMBIENTE_DBS+".USPPWSM0 (PEDSM, WONOSM, SGNOSM, OPERSM) values('"+osOperacional.getId()+"-S', '"+osOperacional.getNumOs()+"', '01', '')";
			stmt.executeUpdate(SQL);
		
	}
	
	public OsEstimada newOsEstimadaContrato(OsEstimada bean){
		EntityManager manager = null;
		try {
			bean.setMsg("");
			manager = JpaUtil.getInstance();
			manager.getTransaction().begin();
			Query query = manager.createQuery("From PmpContrato where numeroContrato = '"+bean.getNumContrato()+"'");
			PmpContrato contrato = (PmpContrato)query.getSingleResult();
			query = manager.createQuery("From PmpConfigOperacional where idContrato.id = :id");
			query.setParameter("id", contrato.getId());
			PmpConfigOperacional operacional = (PmpConfigOperacional)query.getSingleResult();
			operacional.setCodErroDbs("100");
			operacional.setNumOs("Aguarde o retorno do DBS!");
			operacional.setNumeroSegmento(bean.getSegmento());
			operacional.setCscc(bean.getJobControl());
			operacional.setJobCode(bean.getJobCode());
			operacional.setCompCode(bean.getComponenteCode());
			operacional.setInd("E");
			
			if(this.verifyCreateOs(operacional.getId().toString())){
				if(operacional.getNumOs() != null && !"".equals(operacional.getNumOs())){
					return bean;
				}
				//this.startNewOsEstimadaContrato(bean, operacional, contrato);
				return bean;
			}

			/*Formata Horimetro e data atual para enviar para o DBS*/
			String horimetro = contrato.getHorimetro().toString();			
			horimetro += "0";		
			
			while (horimetro.length() < 8){
				horimetro = "0"+ horimetro;
			}		
			String dataHorimetroDbs = dateFormatHorimetroDbs.format(new Date());
			//Cria uma os estimada no dbs
			if(this.createOsEstimada(operacional.getId().toString(), bean.getSegmento(),bean.getJobControl(), 
					bean.getJobCode(), bean.getComponenteCode(), "E", bean.getJobControl(), (Integer.valueOf(this.FILIAL) < 10)?"0"+this.FILIAL:this.FILIAL, bean.getCodigoCliente()
							, bean.getEstimateBy(), bean.getMake(), bean.getNumeroSerie(), null, contrato.getBgrp(), horimetro, dataHorimetroDbs, "B")){
				
				//this.startNewOsEstimadaContrato(bean, operacional, contrato);
				Connection con = ConectionDbs.getConnecton();
				Statement statement = null;
				ResultSet rs = null;
				try {
					String SQL = "SELECT WCLPIPS0.EQMFCD, WCLPIPS0.EQMFSN, WCLPIPS0.PIPNO, WCLPIPS0.OPNDT8"+
									" FROM "+IConstantAccess.LIB_DBS+".WCLPIPS0 WCLPIPS0"+
									" WHERE (WCLPIPS0.OPNDT8=0) "+
									" AND WCLPIPS0.EQMFSN = '"+bean.getNumeroSerie()+"'";
					statement = con.createStatement();
					rs = statement.executeQuery(SQL);
					if(rs.next()){
						bean.setMsg("OS enviada com sucesso!\nPOSSIBILIDADE DE PIP/PSP!");
					}
					SQL = "select trim(t.CUNO) CUNO, trim(t.CRLMT) CRLMT, trim(t.TERMCD) TERMCD from "+IConstantAccess.LIB_DBS+".cipname0 t where t.CUNO = '"+bean.getCodigoCliente()+"'";
					statement = con.createStatement();
					rs = statement.executeQuery(SQL);
					if(rs.next()){
						String msg = "";
						if("1".equals(rs.getString("TERMCD"))){
							msg = "O Pagamento deve ser À VISTA, pois o cliente não possui crédito!";
						}else if("2".equals(rs.getString("TERMCD"))){
							msg = "O cliente possui crédito no valor de "+(String.valueOf(ValorMonetarioHelper.formata("###,###,##0.00", Double.valueOf(String.valueOf(rs.getString("CRLMT"))))))+"!";
						}else if("4".equals(rs.getString("TERMCD"))){
							msg = "O cliente está sem movimentação e possui um crédito de "+(String.valueOf(ValorMonetarioHelper.formata("###,###,##0.00", Double.valueOf(String.valueOf(rs.getString("CRLMT"))))))+"!\nFavor providenciar a atualização do cadastro do cliente!";
						}
						if(bean.getMsg() != null){
							bean.setMsg(bean.getMsg()+"\n"+msg);
						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					if(con != null){
						try {
							if(statement != null){
								statement.close();								
							}
							con.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
				manager.getTransaction().commit();
				return bean;
			}else{
				if(manager != null && manager.getTransaction().isActive()){
					manager.getTransaction().rollback();
				}
			}
			
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
    	bean.setMsg("Não foi possível criar OS estimada!");
    	return bean;
		
	}

	private Boolean createOsEstimadaContratoThread(OsEstimada bean, PmpConfigOperacional operacional, PmpContrato contrato) {
		
		EntityManager manager = null;

		try {
			manager = JpaUtil.getInstance();
			manager.getTransaction().begin();
			
	
				String wono = this.findOsEstimada(operacional.getId().toString());
				if(wono == null){
					return true;
				}
				if("".equals(wono.trim())){
					return false;
				}
				
				operacional.setNumOs(wono);
				manager.merge(operacional);
				contrato.setNumOs(wono);
				manager.merge(contrato);
				manager.getTransaction().commit();	
				//Coloca a OS como Open
				//this.openOs(operacional.getId(), wono.trim());
				if(bean.getVcc().getTipoCliente() != null && !bean.getVcc().getTipoCliente().equals("")){
					if(bean.getVcc().getTipoCliente().equals("INT")){
						if(!this.updateIntoClienteInterOrExcecaoGarantiaDbs(wono,bean.getVcc().getClienteInter(), bean.getVcc().getContaContabilSigla(), bean.getVcc().getCentroDeCustoSigla(), bean.getSegmento(), operacional.getId().toString())){
							bean.setMsg("A Ordem de serviço extimada foi criada, mas não foi possível cadastrar centro de custo e conta contábil!");
						}
					}else if(bean.getVcc().getTipoCliente().equals("EXT")){
						if(!this.updateGarantiaClienteExternoDbs(wono, bean.getVcc().getContaContabilSigla(), bean.getVcc().getCentroDeCustoSigla(), bean.getSegmento(), operacional.getId().toString())){
							bean.setMsg("A Ordem de serviço extimada foi criada, mas não foi possível cadastrar centro de custo e conta contábil!");
						}
					}
				}else{
					//significa que é garantica com a regra de excessão
					if(!("").equals(bean.getVcc().getClienteInter())){
						if(!this.updateIntoClienteInterOrExcecaoGarantiaDbs(wono,bean.getVcc().getClienteInter(), bean.getVcc().getContaContabilSigla(), bean.getVcc().getCentroDeCustoSigla(),bean.getSegmento(), operacional.getId().toString())){
							bean.setMsg("A Ordem de serviço extimada foi criada, mas não foi possível cadastrar centro de custo e conta contábil!");
						}
					}else{//grantia sem regra de excessão
						if(!this.updateGarantiaClienteExternoDbs(wono, bean.getVcc().getContaContabilSigla(), bean.getVcc().getCentroDeCustoSigla(), bean.getSegmento(), operacional.getId().toString())){
							bean.setMsg("A Ordem de serviço extimada foi criada, mas não foi possível cadastrar centro de custo e conta contábil!");
						}
					}
				}
			
		}catch (Exception e) {
			if(manager != null && manager.getTransaction().isActive()){
				manager.getTransaction().rollback();
			}
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return true;
	}
	
	private boolean updateIntoClienteInterOrExcecaoGarantiaDbs(String wono, String clienteInter, String contaContabilSigla, String centroDeCustoSigla, String segmento, String wonosm){
		Connection con = null;

//		InputStream in = getClass().getClassLoader().getResourceAsStream(IConstantAccess.CONF_FILE);
//		Properties prop = new Properties();
		try {
//			prop.load(in);
//			String url = prop.getProperty("dbs.url");
//			String user = prop.getProperty("dbs.user");
//			String password =prop.getProperty("dbs.password");
//			Class.forName(prop.getProperty("dbs.driver")).newInstance();

			//			String url = "jdbc:as400://192.168.128.146";
			//			String user = "XUPU15PSS";
			//			String password = "marcosa";
			//			Class.forName("com.ibm.as400.access.AS400JDBCDriver").newInstance();
			con = com.pmprental.util.ConectionDbs.getConnecton();
			Statement statement = con.createStatement();
			String SQL = "insert into LIBU15FTP.USPIFSM0 (LBCUNO, MSCUNO, PTCUNO, PTDOLP, LBDOLP, MSDOLP, WONO, WOSGNO, WONOSM) values('"+clienteInter+"', '"+clienteInter+"', '"+clienteInter+"','100', '100', '100','"+wono+"','"+segmento+"','"+wonosm+"PM')";
							//" where GS.WONO = '"+wono+"'";

			statement.executeUpdate(SQL);
//			statement = con.createStatement();
//			Thread.sleep(30000);
//			statement.executeUpdate("delete from LIBU15FTP.USPIFSM0 where WONOSM = "+wonosm);
			statement = con.createStatement();
			SQL = "insert into LIBU15FTP.USPIFSM0 (CONTA, WONO, WOSGNO, WONOSM) values( '"+contaContabilSigla+"          "+centroDeCustoSigla+"', '"+wono+"','"+segmento+"','"+wonosm+"PMCC')";
					//" where WONO = '"+wono+"'";
			statement.executeUpdate(SQL);	
//			statement = con.createStatement();
//			Thread.sleep(30000);
//			statement.executeUpdate("delete from LIBU15FTP.USPIFSM0 where WONOSM = "+wonosm);		
			return true;
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally{
			try {
				if(con != null){
					con.close();					
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	private boolean updateGarantiaClienteExternoDbs(String wono, String contaContabilSigla, String centroDeCustoSigla, String segmento, String wonosm){
		Connection con = null;
		
//		InputStream in = getClass().getClassLoader().getResourceAsStream(IConstantAccess.CONF_FILE);
//		Properties prop = new Properties();
		try {
//			prop.load(in);
//			String url = prop.getProperty("dbs.url");
//			String user = prop.getProperty("dbs.user");
//			String password =prop.getProperty("dbs.password");
//			Class.forName(prop.getProperty("dbs.driver")).newInstance();
			
			//			String url = "jdbc:as400://192.168.128.146";
			//			String user = "XUPU15PSS";
			//			String password = "marcosa";
			//			Class.forName("com.ibm.as400.access.AS400JDBCDriver").newInstance();
			con = com.pmprental.util.ConectionDbs.getConnecton(); 
			Statement statement = con.createStatement();
		
			String SQL = "insert into LIBU15FTP.USPIFSM0 (CONTA, WONO, WOSGNO, WONOSM) values( '"+contaContabilSigla+"          "+centroDeCustoSigla+"', '"+wono+"','"+segmento+"','"+wonosm+"PM')";;
			//" where WONO like '"+wono+"%'";
			statement.executeUpdate(SQL);
//			statement = con.createStatement();
//			Thread.sleep(30000);
//			statement.executeUpdate("delete from LIBU15FTP.USPIFSM0 where WONOSM = "+wonosm);
			con.commit();
			return true;
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally{
			try {
				if(con != null){
					con.close();					
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Boolean createOsEstimada(String wonosm, String wosgno, String cscc, String jbcd, String cptcd, String ind,
			String respar, String stn1, String cuno, String estby, String eqmfcd, String eqmfsn, String shpfld, String bgrp, String horimetro, String dataHorimetroDbs, String sufixo){

		Connection con = null;

//		InputStream in = getClass().getClassLoader().getResourceAsStream(IConstantAccess.CONF_FILE);
//		Properties prop = new Properties();

		try {
//			prop.load(in);
//			String url = prop.getProperty("dbs.url");
//			String user = prop.getProperty("dbs.user");
//			String password =prop.getProperty("dbs.password");
//			Class.forName(prop.getProperty("dbs.driver")).newInstance();

			String pair = "'"+wonosm+"-"+sufixo+"','"+cuno+"','"+respar+"','"+stn1+"','"+estby+"','"+eqmfcd+"','"+eqmfsn+"','"+horimetro+"','"+dataHorimetroDbs+"'";
			con = com.pmprental.util.ConectionDbs.getConnecton();
			con.setAutoCommit(true);
			Statement statement = con.createStatement();

			String SQL = "delete from "+IConstantAccess.AMBIENTE_DBS+".USPWOSM0 where wonosm = '"+wonosm+"-"+sufixo+"'";// (wonosm, cuno, respar, stn1, estby, eqmfcd, eqmfsn, bgrp) values("+pair+")";
			statement.executeUpdate(SQL);
			
			statement = con.createStatement();
			SQL = "insert into "+IConstantAccess.AMBIENTE_DBS+".USPWOSM0 (wonosm, cuno, respar, stn1, estby, eqmfcd, eqmfsn, smu, datal) values("+pair+")";
			statement.executeUpdate(SQL);
			//prstmt_ = con.prepareStatement(SQL);
			//rs = prstmt_.executeQuery();


//			pair = "'"+wonosm+"-P','"+wosgno+"','"+cscc+"','"+jbcd+"','"+cptcd+"','"+ind+"','F'";
//			//con = DriverManager.getConnection(url, user, password);  
//			SQL = "insert into "+IConstantAccess.AMBIENTE_DBS+".USPSGSM0 (wonosm, wosgno, cscc, jbcd, cptcd, ind, shpfld) values("+pair+")";
//			//prstmt_ = con.prepareStatement(SQL);
//			statement.executeUpdate(SQL);
			return true;
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally{
			try {
				if(con != null){
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private boolean verifyCreateOs(String idAgenda){
		ResultSet rs = null;
		PreparedStatement prstmt_ = null;
//		InputStream in = getClass().getClassLoader().getResourceAsStream(IConstantAccess.CONF_FILE);
//		Properties prop = new Properties();
		Connection con = null;
		try {
//			prop.load(in);
//			String url = prop.getProperty("dbs.url");
//			String user = prop.getProperty("dbs.user");
//			String password =prop.getProperty("dbs.password");
//			Class.forName(prop.getProperty("dbs.driver")).newInstance();

			con = com.pmprental.util.ConectionDbs.getConnecton(); 
			//Class.forName("com.ibm.as400.access.AS400JDBCDriver").newInstance();
			//con = DriverManager.getConnection("jdbc:as400://192.168.128.146", "XUPU15PSS", "marcosa"); 
			String SQL = "select w.wono, w.wonosm, w.descerr, w.coderr from "+IConstantAccess.AMBIENTE_DBS+".USPWOSM0 w where w.wonosm = '"+idAgenda+"-B' and w.wono <> ''";
			prstmt_ = con.prepareStatement(SQL);
			rs = prstmt_.executeQuery();

			if(rs.next()){
				String wono = rs.getString("wono");
				String coderr = rs.getString("coderr");
				if(wono == null || "".equals(wono.trim())){
					if(coderr != null && !"".equals(coderr)){
						return false;
					}
					return true;
				}else{
					return true;
				}
			}
		}catch (Exception e) { 
			e.printStackTrace();
		}finally{
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
		return false;
	}
	private boolean verifyCreateOsAgendamento(String idAgenda){
		ResultSet rs = null;
		PreparedStatement prstmt_ = null;
//		InputStream in = getClass().getClassLoader().getResourceAsStream(IConstantAccess.CONF_FILE);
//		Properties prop = new Properties();
		Connection con = null;
		try {
//			prop.load(in);
//			String url = prop.getProperty("dbs.url");
//			String user = prop.getProperty("dbs.user");
//			String password =prop.getProperty("dbs.password");
//			Class.forName(prop.getProperty("dbs.driver")).newInstance();
			
			con = com.pmprental.util.ConectionDbs.getConnecton(); 
			//Class.forName("com.ibm.as400.access.AS400JDBCDriver").newInstance();
			//con = DriverManager.getConnection("jdbc:as400://192.168.128.146", "XUPU15PSS", "marcosa"); 
			String SQL = "select w.wono, w.wonosm, w.descerr, w.coderr from "+IConstantAccess.AMBIENTE_DBS+".USPWOSM0 w where w.wonosm = '"+idAgenda+"-S' and w.wono <> ''";
			prstmt_ = con.prepareStatement(SQL);
			rs = prstmt_.executeQuery();
			
			if(rs.next()){
				String wono = rs.getString("wono");
				String coderr = rs.getString("coderr");
				if(wono == null || "".equals(wono.trim())){
					if(coderr != null && !"".equals(coderr)){
						return false;
					}
					return true;
				}else{
					return true;
				}
			}
		}catch (Exception e) { 
			e.printStackTrace();
		}finally{
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
		return false;
	}
	
	public String findOsEstimada(String idAgenda) {
		ResultSet rs = null;
		PreparedStatement prstmt_ = null;

		Connection con = null;
//		InputStream in = getClass().getClassLoader().getResourceAsStream(IConstantAccess.CONF_FILE);
//		Properties prop = new Properties();

		String wono = "";
		try {

//			prop.load(in);
//			String url = prop.getProperty("dbs.url");
//			String user = prop.getProperty("dbs.user");
//			String password =prop.getProperty("dbs.password");
//			Class.forName(prop.getProperty("dbs.driver")).newInstance();

			con = com.pmprental.util.ConectionDbs.getConnecton(); 
			//Class.forName("com.ibm.as400.access.AS400JDBCDriver").newInstance();
			//con = DriverManager.getConnection("jdbc:as400://192.168.128.146", "XUPU15PSS", "marcosa"); 
			String SQL = "select w.wono, w.wonosm, descerr from LIBU15FTP.USPWOSM0 w where w.wonosm = "+idAgenda+" and w.bgrp is not null and( w.bgrp = 'PM' or w.bgrp = 'SEV') and w.wono <> ''";
			prstmt_ = con.prepareStatement(SQL);
			rs = prstmt_.executeQuery();
			
			if(rs.next()){
				wono = rs.getString("wono");
				if(wono == null || "".equals(wono.trim())){
					if(!"".equals(rs.getString("descerr"))){
						prstmt_ = con.prepareStatement("delete from LIBU15FTP.USPWOSM0 where wonosm = '"+idAgenda+"'");
						prstmt_.executeUpdate();
						prstmt_ = con.prepareStatement("delete from LIBU15FTP.USPSGSM0 where wonosm = '"+idAgenda+"'");
						prstmt_.executeUpdate();
						return null;
					}
				}
				prstmt_ = con.prepareStatement("delete from LIBU15FTP.USPWOSM0 where wonosm = '"+idAgenda+"'");
				prstmt_.executeUpdate();
				prstmt_ = con.prepareStatement("delete from LIBU15FTP.USPSGSM0 where wonosm = '"+idAgenda+"'");
				prstmt_.executeUpdate();
			}else{
				return "";
			}

		}catch (Exception e) { 
			e.printStackTrace();
		}finally{
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
		return wono.trim();
	}
	
	public Boolean verificaOsFaturada(Integer idContrato) {
//		ResultSet rs = null;
//		PreparedStatement prstmt_ = null;
//		EntityManager manager = null;
//		Connection con = null;
//
//		try {
//			manager = JpaUtil.getInstance();
//			Query query = manager.createNativeQuery("select co.NUM_OS from PMP_CONFIG_OPERACIONAL co, PMP_CONTRATO c"+
//					" where c.ID = co.ID_CONTRATO"+
//					" and c.ID_TIPO_CONTRATO in (select id from PMP_TIPO_CONTRATO where SIGLA in ('VPG','VEPM'))"+
//					//" and co.NUM_OS is not null"+
//					" and c.ID = "+idContrato);
//
//			if(query.getResultList().size() > 0){
//				String numOs = (String) query.getSingleResult();
//				if(numOs == null){
//					return false;
//				}
//				con = com.pmp.util.ConectionDbs.getConnecton(); 
//				String SQL = "select w.ACTI from LIBU17.WOPHDRS0 w where w.WONO = '"+numOs+"' and w.ACTI = 'I'";
//				prstmt_ = con.prepareStatement(SQL);
//				rs = prstmt_.executeQuery();
//				if(rs.next()){
//					return true;
//				}else{
//					return false;
//				}
//
//			}else{
//				return true;
//			}
//		}catch (Exception e) { 
//			e.printStackTrace();
//		}finally{
//			try {
//				if(con != null){
//					con.close();
//				}
//				if(prstmt_ != null){
//					prstmt_.close();
//				}
//				if(manager != null && manager.isOpen()){
//					manager.close();
//				}
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//		return false;
		return true;
	}
	
	
	
	public List<TotalAgendamentoChartBean> findTotalAgendamento(Long filial, String dataInico, String dataFim) {
		SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd");
		EntityManager manager = null;
		List<TotalAgendamentoChartBean> result = new ArrayList<TotalAgendamentoChartBean>();
		try {
			manager = JpaUtil.getInstance();
			
//			String SQL = "select ISNULL(count(op.ID),0) from REN_PMP_OS_OPERACIONAL op"
//					+ " where CONVERT(varchar(10), op.DATA, 112) between '"+format2.format(format1.parse(dataInico))+"' and '"+format2.format(format1.parse(dataFim))+"'";
//			if(filial.intValue() > -1){
//				SQL += " and op.filial = "+filial;
//			}
//
//			Query query = manager.createNativeQuery(SQL);
//			TotalAgendamentoChartBean bean = new TotalAgendamentoChartBean();
//			bean.setDescricao("Total OS Criada");
//			bean.setValor(((Integer)query.getSingleResult()).intValue());
//			result.add(bean);		

			Integer totalFinalizado = 0;
			Integer totalAgendado = 0;

			String SQL = "select ISNULL(count(distinct(ag.id_cong_operacional)),0) from ren_pmp_agendamento ag"+
					" where ag.id_status_agendamento in (select st.id from ren_pmp_status_agendamento st"+
					" where st.sigla in('EA','AT', 'FIN'))"+
					" and CONVERT(varchar(10), ag.DATA_AGENDAMENTO, 112) between '"+format2.format(format1.parse(dataInico))+"' and '"+format2.format(format1.parse(dataFim))+"'";

			if(filial.intValue() > -1){
					SQL += " and ag.filial = "+filial;
			}
			Query query = manager.createNativeQuery(SQL);
			TotalAgendamentoChartBean bean = new TotalAgendamentoChartBean();
			bean.setDescricao("Agendamentos");
			bean.setValor(((Integer)query.getSingleResult()).intValue());
			//bean.setCor("0x336699");
			result.add(bean);
			totalAgendado = bean.getValor();
			
			SQL = "select ISNULL(count(distinct(ag.id_cong_operacional)),0) from ren_pmp_agendamento ag"+
					" where ag.id_status_agendamento in (select st.id from ren_pmp_status_agendamento st"+
					" where st.sigla in('FIN'))"+
					" and CONVERT(varchar(10), ag.DATA_AGENDAMENTO, 112) between '"+format2.format(format1.parse(dataInico))+"' and '"+format2.format(format1.parse(dataFim))+"'";

			if(filial.intValue() > -1){
					SQL += " and ag.filial = "+filial;
			}
			query = manager.createNativeQuery(SQL);
			bean = new TotalAgendamentoChartBean();
			bean.setDescricao("Finalizados");
			bean.setValor(((Integer)query.getSingleResult()).intValue());
			//bean.setCor("#a1f122");
			result.add(bean);
			
			totalFinalizado = bean.getValor();
			
			SQL = "select ISNULL(count(distinct(ag.id_cong_operacional)),0) from ren_pmp_agendamento ag"+
					" where ag.id_status_agendamento in (select st.id from ren_pmp_status_agendamento st"+
					" where st.sigla in('EA','AT'))"+
					" and CONVERT(varchar(10), ag.DATA_AGENDAMENTO, 112) between '"+format2.format(format1.parse(dataInico))+"' and '"+format2.format(format1.parse(dataFim))+"'";

			if(filial.intValue() > -1){
					SQL += " and ag.filial = "+filial;
			}
			query = manager.createNativeQuery(SQL);
			bean = new TotalAgendamentoChartBean();
			bean.setDescricao("Não Finalizados");
			bean.setValor(((Integer)query.getSingleResult()).intValue());
			//bean.setCor("#fb0707");
			result.add(bean);
			
			bean = new TotalAgendamentoChartBean();
			bean.setDescricao("% Eficiência");
			//bean.setCor("#fb0707");
			//Double totalEficiencia = (totalFinalizado.doubleValue()/totalAgendado.doubleValue())/100;
			
			//BigDecimal bd = new BigDecimal(totalEficiencia).setScale(3, RoundingMode.HALF_EVEN);
			if(totalAgendado != null && totalAgendado > 0){
				Double eficiencia = totalFinalizado.doubleValue()/totalAgendado.doubleValue();
				eficiencia = eficiencia * 100;
				Long efi = Math.round(eficiencia);
				bean.setValor(efi.intValue());
			}else{
				bean.setValor(0);
			}
			result.add(bean);
			
//			if(filial.intValue() > -1){
//				SQL = "select count(*) from ren_pmp_os_operacional os, ren_pmp_cont_horas_standard hs"+
//						"	where os.num_os is not null"+
//						"	and hs.id_os_operacional = os.id"+
//						"	and hs.is_executado = 'N'"+
//						"	and os.filial = "+filial+
//						"	and hs.id not in("+
//						"	select ag.id_cont_horas_standard from ren_pmp_agendamento ag"+
//						"	where ag.id_status_agendamento in (select st.id from ren_pmp_status_agendamento st"+
//						"	where st.sigla in ('EA','AT'))"+
//						"	and ag.filial = "+filial+")";
//			}else{
//				SQL = "select count(*) from pmp_os_operacional os, ren_pmp_cont_horas_standard hs"+
//						"	where os.num_os is not null"+
//						"	and hs.id_os_operacional = os.id"+
//						"	and hs.is_executado = 'N'"+
//						"	and hs.id not in("+
//						"	select ag.id_cont_horas_standard from pmp_agendamento ag"+
//						"	where ag.id_status_agendamento in (select st.id from ren_pmp_status_agendamento st"+
//						"	where st.sigla in ('EA','AT'))"+
//						"	)";
//			}
//			query = manager.createNativeQuery(SQL);
//			bean = new TotalAgendamentoChartBean();
//			bean.setDescricao("Não Agendedados com OS");
//			bean.setValor(((Integer)query.getSingleResult()).intValue());
//			result.add(bean);
//			
//			if(filial.intValue() > -1){
////				SQL = "select count(*) from(select "+
////						" ( select op.num_os from pmp_os_operacional op where  op.id = (select st.id_os_operacional from pmp_cont_horas_standard st where st.horas_manutencao = (select  min(horas_Manutencao) from Pmp_Cont_Horas_Standard  where id_Contrato = C.Id and is_Executado = 'N') and id_Contrato = C.id))  as num_os,"+ 
////					
////						" ((select  min(horas_Manutencao) from Pmp_Cont_Horas_Standard  where id_Contrato = C.id and is_Executado = 'N')  - (select max(horimetro) from pmp_maquina_pl pl where pl.numero_serie = C.Numero_Serie "+
////						"  and pl.horimetro is not null and pl.data_atualizacao = (select max(data_atualizacao) "+
////						"   from pmp_maquina_pl"+
////						" where numero_serie = pl.numero_serie and horimetro is not null) )) horas_pendentes"+
////					
////						"  from pmp_contrato c, pmp_config_operacional co"+
////						" where c.id_Status_Contrato = (select s.id from Pmp_Status_Contrato s where s.sigla = 'CA')"+ 
////						" and  c.id in((select  distinct id_Contrato  from Pmp_Cont_Horas_Standard hs where id_Contrato = c.id and is_Executado = 'N'))"+ 
////						" and co.num_os is not null "+
////						" and co.id_contrato = c.id"+
////						" and co.filial = "+filial+") tab"+
////						" where tab.horas_pendentes <= 50"+ 
////						" and tab.num_os is null";
//				SQL = " select count(c.id)"+
//						" from PMP_OS_OPERACIONAL oso, REN_PMP_CONTRATO c, REN_PMP_CONFIG_OPERACIONAL op left join PMP_AGENDAMENTO ag on op.ID = ag.ID_CONG_OPERACIONAL"+ 
//						" where op.ID_CONTRATO = c.ID"+
//						" and ((select  min(horas_Manutencao) from Ren_Pmp_Cont_Horas_Standard  where id_Contrato = C.id and is_Executado = 'N')  -"+ 
//						" (select max(horimetro)  from ren_pmp_maquina_pl pl "+
//						" where pl.numero_serie = C.Numero_Serie   "+
//						" and pl.horimetro is not null "+
//						" and pl.id = (select max(id) from ren_pmp_maquina_pl"+  
//						" where numero_serie = pl.numero_serie and horimetro is not null and HORIMETRO > 0) )) <=50"+
//						" and c.IS_ATIVO is null "+
//						" and oso.ID_CONT_HORAS_STANDARD not in (select s.ID_CONT_HORAS_STANDARD from REN_PMP_AGENDAMENTO s where s.ID_CONG_OPERACIONAL = op.id)"+
//						" and oso.ID_CONFIG_OPERACIONAL = op.id" +
//						" and op.filial = "+filial;
//			}else{
////				SQL = 	" select count(*) from(select  "+
////						  " ( select op.num_os from pmp_os_operacional op where  op.id = (select st.id_os_operacional from pmp_cont_horas_standard st where st.horas_manutencao = (select  min(horas_Manutencao) from Pmp_Cont_Horas_Standard  where id_Contrato = C.Id and is_Executado = 'N') and id_Contrato = C.id))  as num_os,"+
////						  " ((select  min(horas_Manutencao) from Pmp_Cont_Horas_Standard  where id_Contrato = C.id and is_Executado = 'N')  - (select max(horimetro) from pmp_maquina_pl pl where pl.numero_serie = C.Numero_Serie  "+
////						   " and pl.horimetro is not null and pl.data_atualizacao = (select max(data_atualizacao)  "+
////						   " from pmp_maquina_pl "+
////						  " where numero_serie = pl.numero_serie and horimetro is not null) )) horas_pendentes "+
////						  " from pmp_contrato c, pmp_config_operacional co "+
////						  " where c.id_Status_Contrato = (select s.id from Pmp_Status_Contrato s where s.sigla = 'CA')  "+
////						  " and  c.id in((select  distinct id_Contrato  from Pmp_Cont_Horas_Standard hs where id_Contrato = c.id and is_Executado = 'N'))  "+
////						  " and co.num_os is not null  "+
////						  " and co.id_contrato = c.id) tab "+
////						  " where tab.horas_pendentes <= 50  "+
////						  " and tab.num_os is null ";
//				SQL = " select count(c.id)"+
//						" from REN_PMP_OS_OPERACIONAL oso, REN_PMP_CONTRATO c, REN_PMP_CONFIG_OPERACIONAL op left join REN_PMP_AGENDAMENTO ag on op.ID = ag.ID_CONG_OPERACIONAL"+ 
//						" where op.ID_CONTRATO = c.ID"+
//						" and ((select  min(horas_Manutencao) from Ren_Pmp_Cont_Horas_Standard  where id_Contrato = C.id and is_Executado = 'N')  -"+ 
//						" (select max(horimetro)  from ren_pmp_maquina_pl pl "+
//						" where pl.numero_serie = C.Numero_Serie   "+
//						" and pl.horimetro is not null "+
//						" and pl.id = (select max(id) from ren_pmp_maquina_pl"+  
//						" where numero_serie = pl.numero_serie and horimetro is not null and HORIMETRO > 0) )) <=50"+
//						" and c.IS_ATIVO is null "+
//						" and oso.ID_CONT_HORAS_STANDARD not in (select s.ID_CONT_HORAS_STANDARD from REN_PMP_AGENDAMENTO s where s.ID_CONG_OPERACIONAL = op.id)"+
//						" and oso.ID_CONFIG_OPERACIONAL = op.id";
//			}
//			
//
//			query = manager.createNativeQuery(SQL);
//			bean = new TotalAgendamentoChartBean();
//			bean.setDescricao("Não Agendamentos sem OS\n com a prox. manut. menor ou igual\n a 50 horas");
//			bean.setValor(((Integer)query.getSingleResult()).intValue());
//			result.add(bean);
			
		}catch (Exception e) {
			//manager.getTransaction().rollback();
			e.printStackTrace();
		} finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return result;
	}
	
}
