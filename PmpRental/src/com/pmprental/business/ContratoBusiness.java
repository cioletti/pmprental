package com.pmprental.business;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.beanutils.BeanAccessLanguageException;

import com.pmprental.bean.BusinessGroupBean;
import com.pmprental.bean.ClienteBean;
import com.pmprental.bean.ConfigManutencaoBean;
import com.pmprental.bean.ConfigManutencaoHorasBean;
import com.pmprental.bean.ConfigManutencaoMesesBean;
import com.pmprental.bean.ConfigurarCustomizacaoBean;
import com.pmprental.bean.ContratoComercialBean;
import com.pmprental.bean.ModeloBean;
import com.pmprental.bean.MotNaoFecContratoBean;
import com.pmprental.bean.OperacionalBean;
import com.pmprental.bean.PrecoBean;
import com.pmprental.bean.PrefixoBean;
import com.pmprental.bean.RangerBean;
import com.pmprental.bean.StatusContratoBean;
import com.pmprental.bean.TipoContratoBean;
import com.pmprental.bean.TipoCustomizacaoBean;
import com.pmprental.bean.UsuarioBean;
import com.pmprental.entity.PmpAgendamento;
import com.pmprental.entity.PmpConfigHorasStandard;
import com.pmprental.entity.PmpConfigManutencao;
import com.pmprental.entity.PmpConfigOperacional;
import com.pmprental.entity.PmpConfigTracao;
import com.pmprental.entity.PmpConfiguracaoPrecos;
import com.pmprental.entity.PmpContHorasStandard;
import com.pmprental.entity.PmpContMesesStandard;
import com.pmprental.entity.PmpContrato;
import com.pmprental.entity.PmpContratoCustomizacao;
import com.pmprental.entity.PmpFamilia;
import com.pmprental.entity.PmpHora;
import com.pmprental.entity.PmpManutencao;
import com.pmprental.entity.PmpManutencaoPrecoCusto;
import com.pmprental.entity.PmpMaquinaPl;
import com.pmprental.entity.PmpMotivosNaoFecContrato;
import com.pmprental.entity.PmpRange;
import com.pmprental.entity.PmpStatusContrato;
import com.pmprental.entity.PmpTipoContrato;
import com.pmprental.entity.PmpTipoCustomizacao;
import com.pmprental.entity.RentPmpHoraPK;
import com.pmprental.entity.RentPmpRangePK;
import com.pmprental.entity.TwFilial;
import com.pmprental.util.ConectionDbs;
import com.pmprental.util.EmailHelper;
import com.pmprental.util.IConstantAccess;
import com.pmprental.util.JpaUtil;
import com.pmprental.util.ValorMonetarioHelper;

public class ContratoBusiness {

	private final String SQL_FIND_ALL_PMP = "SELECT TRIM(HO.BGRP) as BGRP, TRIM(HO.BEQMSN) as BEQMSN, TRIM(UNCS) as UNCS, TRIM(UNLS) as UNLS, TRIM(DLRQTY) as DLRQTY, TRIM(MD5.PANO20)as PANO20, TRIM(BECTYC) as BECTYC, TRIM(CPTCD) as CPTCD, TRIM(PTO.DS18) as DS18, TRIM(PTO.SOS1) SOS1," +
	" TRIM(HO.OCPTMD) as OCPTMD, TRIM(HO.OJBLOC) as OJBLOC, TRIM(HO.OWKAPP) as JWKAPP "+
	" FROM "+IConstantAccess.LIB_DBS+".PCPPIPT0 PTO, "+IConstantAccess.LIB_DBS+".SHLRBMD5 MD5 ,"+IConstantAccess.LIB_DBS+".SHLRBMH0 HO"+
	" WHERE PTO.PANO20 = MD5.PANO20"+
	" AND PTO.SOS1 = MD5.SOS1"+
	" AND MD5.RLTDHS = HO.RLTDHS"+
	" AND TRIM(HO.BGRP) IN ('PM')" +
	" AND MD5.DLRPCT <> '0.00000'";
	//" and substring(TRIM(HO.BEQMSN), 0, 5) = '0CBD'";

	private final String SQL_FIND_ALL_PMP_POR_FILIAL = "SELECT TRIM(HO.BGRP) as BGRP, TRIM(HO.BEQMSN) as BEQMSN, TRIM(PP.LANDCS) as LANDCS, TRIM(DLRQTY) as DLRQTY, TRIM(MD5.PANO20)as PANO20, TRIM(BECTYC) as BECTYC, TRIM(CPTCD) as CPTCD, TRIM(PTO.DS18) as DS18, TRIM(PP.STNO) as  STNO,"+
	" TRIM(HO.OCPTMD) as OCPTMD, TRIM(HO.OJBLOC) as OJBLOC, TRIM(HO.OWKAPP) as JWKAPP "+
	" FROM "+IConstantAccess.LIB_DBS+".PCPPIPT0 PTO, "+IConstantAccess.LIB_DBS+".SHLRBMD5 MD5 ,"+IConstantAccess.LIB_DBS+".SHLRBMH0 HO,"+IConstantAccess.LIB_DBS+".PCPPIST0 PP"+
	" WHERE PP.PANO20 = PTO.PANO20" +
	" AND PP.SOS1 = PTO.SOS1" +
	" AND PTO.PANO20 = MD5.PANO20" +
	" AND TRIM(PP.STNO) = '0'"+
	" AND PTO.SOS1 = MD5.SOS1"+
	" AND MD5.RLTDHS = HO.RLTDHS"+
	" AND TRIM(HO.BGRP) IN ('PM')" +
	" AND MD5.DLRPCT <> '0.00000'";
	//" and substring(TRIM(HO.BEQMSN), 0, 5) = '0CBD'";
	
	
	
	
	private String SQL_HORAS_PMP = " SELECT TRIM(SH2.JBCD) as JBCD , TRIM(SH2.CPTCD) as CPTCD, TRIM(SH2.EQMFCD) as EQMFCD, TRIM(SH2.BEQMSN) as BEQMSN, TRIM(SH2.FRSDHR) as FRSDHR, TRIM(SH2.EQMFMD) as EQMFMD, TRIM(BGRP) as BGRP,trim(cptcd.CPTCDD) as TIPO_PM  FROM "+IConstantAccess.LIB_DBS+".SHLSTAN2 SH2, "+IConstantAccess.LIB_DBS+".SHLCMPC0 cptcd WHERE TRIM(SH2.BGRP)  IN ('PM') AND cptcd.cptcd = SH2.CPTCD";// and TRIM(SH2.EQMFMD) = '416E'";

	private String SQL_RANGE = "SELECT TRIM(SHL.EQMFCD) as EQMFCD, TRIM(SHL.BEGSN2) as BEGSN2, TRIM(SHL.ENDSN2) as ENDSN2, TRIM(SHL.EQMFM2) as EQMFM2, TRIM(SHL.BSERNO) as BSERNO from "+IConstantAccess.LIB_DBS+".SHLBSSN0 SHL WHERE TRIM(SHL.EQMFM2) <> ''";// and TRIM(SHL.EQMFM2) = '416E'";
	
	private String SQL_FIND_CUSTOMER_MACHINE = "SELECT TRIM(EMPEQPD0.EQMFS2) EQMFS2, TRIM(CIPNAME0.CUNO) CUNO, TRIM(CIPNAME0.CUNM) CUNM, TRIM(CIPNAME0.STN1) STN1"+
											    " FROM "+IConstantAccess.LIB_DBS+".CIPNAME0 CIPNAME0, "+IConstantAccess.LIB_DBS+".EMPEQPD0 EMPEQPD0"+
												" WHERE CIPNAME0.CUNO = EMPEQPD0.CUNO AND ((EMPEQPD0.EQMFCD='AA')) ";
	private String SQL_FIND_FILIAL = "SELECT WOLSTSQ0.STNO STNO, WOLSTSQ0.STNM STNM"+
									 " FROM B108F034.LIBU15.WOLSTSQ0 WOLSTSQ0"+
									 " WHERE (WOLSTSQ0.SQNOPF Like 'M%') ";
	private String ID_FUNCIONARIO;
	private String FILIAL;
	private UsuarioBean usuarioBean;
	
	public ContratoBusiness() {
		
	}
	public ContratoBusiness(UsuarioBean bean) {
		this.usuarioBean = bean;
		ID_FUNCIONARIO = bean.getMatricula();
		FILIAL = bean.getFilial();
	}
	public List<ModeloBean> findAllModelosContrato(String contExcessao, Long idFamilia, String isGerador){
		EntityManager manager = null;
		List<ModeloBean> modeloList = new ArrayList<ModeloBean>();
		try {
			manager = JpaUtil.getInstance();
			//String PMP_CONFIG_MANUTENCAO_SQL = "select distinct modelo from PmpConfigManutencao where contExcessao=:contExcessao and idFamilia.id = :id ";
			String PMP_CONFIG_MANUTENCAO_SQL_NEW = "select distinct MODELO, arv.ID_ARV from REN_PMP_CONFIG_MANUTENCAO cm, REN_PMP_ARV_INSPECAO arv where cm.ID_FAMILIA =:id and arv.DESCRICAO = cm.modelo and cont_Excessao=:contExcessao";
			if(isGerador != null){
				PMP_CONFIG_MANUTENCAO_SQL_NEW += "  and is_Gerador_Standby = '"+isGerador+"' order by modelo";
			}else{
				PMP_CONFIG_MANUTENCAO_SQL_NEW += " order by modelo";
				
			}
			Query query = manager.createNativeQuery(PMP_CONFIG_MANUTENCAO_SQL_NEW);
			query.setParameter("contExcessao", contExcessao);
			query.setParameter("id", idFamilia);
			List<Object[]> modelos = query.getResultList();
			for (Object[] pair : modelos) {
				ModeloBean bean = new ModeloBean();
				bean.setDescricao((String)pair[0]);
				bean.setIdModelo(((BigDecimal)pair[1]).longValue());
				modeloList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return modeloList;
	}
	
	public List<PrefixoBean> findAllPrefixosContrato(String modelo, String contExcessao){
		EntityManager manager = null;
		List<PrefixoBean> prefixoList = new ArrayList<PrefixoBean>();
		try {
			manager = JpaUtil.getInstance();
			Query query = manager.createQuery("select distinct prefixo from PmpConfigManutencao where modelo = :modelo and contExcessao =:contExcessao order by prefixo");
			query.setParameter("modelo", modelo);
			query.setParameter("contExcessao", contExcessao);
			
			List<String> prefixos = query.getResultList();
			for (String string : prefixos) {
				PrefixoBean bean = new PrefixoBean();
				bean.setDescricao(string);
				prefixoList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return prefixoList;
	}
	public List<ConfigManutencaoBean> findAllPrecoEspecial(String modelo, String prefixo, String contExcessao, Long idFamilia){
		EntityManager manager = null;
		List<ConfigManutencaoBean> precoList = new ArrayList<ConfigManutencaoBean>();
		try {
			manager = JpaUtil.getInstance();
			Query query = manager.createNativeQuery("select distinct  p.ID, p.DESCRICAO from REN_PMP_CONFIG_MANUTENCAO m, REN_PMP_CONFIGURACAO_PRECOS p where m.MODELO = '"+modelo+"' and m.PREFIXO = '"+prefixo+"' and m.CONT_EXCESSAO = '"+contExcessao+"' and ID_FAMILIA = "+idFamilia+" and p.ID = m.ID_CONFIGURACAO_PRECO ");
			
			List<Object[]> precos = query.getResultList();
			for (Object [] result : precos) {
				ConfigManutencaoBean bean = new ConfigManutencaoBean();
				bean.setIdPrecoConfig(((BigDecimal)result[0]).longValue());
				bean.setDescTipoPreco((String)result[1]);
				precoList.add(bean);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return precoList;
	}
	
	public List<BusinessGroupBean> findAllBusinessGroupContrato(){
		EntityManager manager = null;
		List<BusinessGroupBean> bgList = new ArrayList<BusinessGroupBean>();
		try {
			manager = JpaUtil.getInstance();
			Query query = manager.createQuery("select distinct bgrp from PmpConfigManutencao order by bgrp");
			List<String> bgs = query.getResultList();
			for (String string : bgs) {
				BusinessGroupBean bean = new BusinessGroupBean();
				bean.setDescricao(string);
				bgList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return bgList;
	}
	public List<RangerBean> findAllRangerContrato(String modelo, String prefixo, String contExcessao, Long idConfiguracaoPreco){
		EntityManager manager = null;
		List<RangerBean> rangerList = new ArrayList<RangerBean>();
		try {
			manager = JpaUtil.getInstance();
			String SQL = "select distinct beginrange, endrange from PmpConfigManutencao where modelo=:modelo and contExcessao=:contExcessao and prefixo=:prefixo ";
			if(idConfiguracaoPreco != null){
				SQL+= " and idConfiguracaoPreco.id="+idConfiguracaoPreco;
			}
			Query query = manager.createQuery(SQL);
			query.setParameter("modelo", modelo);
			query.setParameter("prefixo", prefixo);
			query.setParameter("contExcessao", contExcessao);
			List<Object[]> rangers = query.getResultList();
			for (Object [] string : rangers) {
				RangerBean bean = new RangerBean();
				bean.setBeginRanger((String)string[0]);
				bean.setEndRanger((String)string[1]);
				bean.setDescricao(bean.getBeginRanger()+" - "+bean.getEndRanger());
				rangerList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return rangerList;
	}
	public List<ConfigurarCustomizacaoBean> findAllTipoCustomizacao(String modelo, Long idFamilia, Long idContrato){
		EntityManager manager = null;
		List<ConfigurarCustomizacaoBean> customizacaoList = new ArrayList<ConfigurarCustomizacaoBean>();
		try {
			manager = JpaUtil.getInstance();
			Query query = manager.createNativeQuery("select * from REN_PMP_CONTRATO_CUSTOMIZACAO where ID_CONTRATO =:ID_CONTRATO");
			query.setParameter("ID_CONTRATO", idContrato);
			if(query.getResultList().size() == 0){
				return null;
			}
			
			query = manager.createNativeQuery("select ID_ARV from REN_PMP_ARV_INSPECAO where ID_FAMILIA =:ID_FAMILIA and DESCRICAO =:MODELO");
			query.setParameter("MODELO", modelo);
			query.setParameter("ID_FAMILIA", idFamilia);
			BigDecimal ID_ARV = (BigDecimal)query.getSingleResult();
			query = manager.createNativeQuery(" select cc.ID_CONTRATO, tc.ID, tc.DESCRICAO, tc.ID_MODELO,cc.id id_contrato_customizacao from REN_PMP_CONTRATO_CUSTOMIZACAO cc right join REN_PMP_TIPO_CUSTOMIZACAO tc on cc.ID_TIPO_CUSTOMIZACAO = tc.ID and cc.ID_CONTRATO =:ID_CONTRATO" +
					" where tc.ID_MODELO =:ID_ARV  " +
					"order by tc.descricao");
			query.setParameter("ID_ARV", ID_ARV);
			query.setParameter("ID_CONTRATO", idContrato);
			
			List<Object[]> rangers = query.getResultList();
			for (Object [] string : rangers) {
				ConfigurarCustomizacaoBean bean = new ConfigurarCustomizacaoBean();
				bean.setIsSelected(false);
				if(string[4] != null){
					bean.setIsSelected(true);
				}
				bean.setIdTipoCustomizacao(((BigDecimal)string[1]).longValue());
				bean.setIdModelo(((BigDecimal)string[3]).longValue());
				bean.setDescricao((String)string[2]);
				bean.setIdFamilia(idFamilia);
				customizacaoList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return customizacaoList;
	}
	
	public List<TipoContratoBean> findAllTipoContrato(){
		EntityManager manager = null;
		List<TipoContratoBean> tipoContratoList = new ArrayList<TipoContratoBean>();
		try {
			manager = JpaUtil.getInstance();
			Query query = manager.createQuery("from PmpTipoContrato where sigla not in ('REN', 'CAN')");
			List<PmpTipoContrato> tcList = query.getResultList();
			for (PmpTipoContrato tc : tcList) {
				TipoContratoBean bean = new TipoContratoBean();
				bean.setDescricao(tc.getDescricao());
				bean.setId(tc.getId().longValue());
				bean.setSigla(tc.getSigla());
				tipoContratoList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return tipoContratoList;
	}
	
	public List<TipoContratoBean> findAllTipoContratoRental(){
		EntityManager manager = null;
		List<TipoContratoBean> tipoContratoList = new ArrayList<TipoContratoBean>();
		try {
			manager = JpaUtil.getInstance();
			Query query = manager.createQuery("from PmpTipoContrato where sigla = 'REN'");
			List<PmpTipoContrato> tcList = query.getResultList();
			for (PmpTipoContrato tc : tcList) {
				TipoContratoBean bean = new TipoContratoBean();
				bean.setDescricao(tc.getDescricao());
				bean.setId(tc.getId().longValue());
				bean.setSigla(tc.getSigla());
				tipoContratoList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return tipoContratoList;
	}
	
	public List<TipoContratoBean> findAllTipoContratoAntigo(){
		EntityManager manager = null;
		List<TipoContratoBean> tipoContratoList = new ArrayList<TipoContratoBean>();
		try {
			manager = JpaUtil.getInstance();
			Query query = manager.createQuery("from PmpTipoContrato");
			List<PmpTipoContrato> tcList = query.getResultList();
			for (PmpTipoContrato tc : tcList) {
				TipoContratoBean bean = new TipoContratoBean();
				bean.setDescricao(tc.getDescricao());
				bean.setId(tc.getId().longValue());
				bean.setSigla(tc.getSigla());
				tipoContratoList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return tipoContratoList;
	}
	
	public List<StatusContratoBean> findAllStatusContrato(){
		EntityManager manager = null;
		List<StatusContratoBean> statusContratoList = new ArrayList<StatusContratoBean>();
		try {
			manager = JpaUtil.getInstance();
			Query query = manager.createQuery("from PmpStatusContrato");
			List<PmpStatusContrato> scList = query.getResultList();
			for (PmpStatusContrato tc : scList) {
				StatusContratoBean bean = new StatusContratoBean();
				bean.setDescricao(tc.getDescricao());
				bean.setId(tc.getId().longValue());
				bean.setSigla(tc.getSigla());
				statusContratoList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return statusContratoList;
	}
	
	public ClienteBean findDataCliente(String cuno){
		ResultSet rs = null;
		PreparedStatement prstmt_ = null;

		Connection con = null;

//		InputStream in = getClass().getClassLoader().getResourceAsStream(IConstantAccess.CONF_FILE);
//		Properties prop = new Properties();
		ClienteBean bean = new ClienteBean();
		try {
//			prop.load(in);
//			String url = prop.getProperty("dbs.url");
//			String user = prop.getProperty("dbs.user");
//			String password =prop.getProperty("dbs.password");
//			Class.forName(prop.getProperty("dbs.driver")).newInstance();

			con = ConectionDbs.getConnecton(); 

			String SQL = "select c.FLGDLI,c.CUNM CLCHAVE, " +
			"c.CUNO,c.CUADD2 END2, c.CUADD3 BAIRRO,c.CUCYST CID,c.CUSTE EST, trim(SUBstring(c.TELXNO,0,15)) CPF, c.ZIPCD9 CEP " +
			"FROM "+com.pmprental.util.IConstantAccess.LIB_DBS+".CIPNAME0 c "+
			" where c.CUNO = '"+cuno+"'";
			prstmt_ = con.prepareStatement(SQL);
			rs = prstmt_.executeQuery();
			if(rs.next()){
				if( rs.getString("FLGDLI").equals("Y")){
					bean.setMsg("O código do cliente não pode ser usuado!");
					return bean;
				}
				bean.setRAZSOC(rs.getString("CLCHAVE").trim());
				bean.setCLCHAVE(rs.getString("CUNO").trim());
				bean.setEND(rs.getString("END2").trim());
				bean.setBAIRRO(rs.getString("BAIRRO").trim());
				bean.setCID(rs.getString("CID").trim());
				bean.setEST(rs.getString("EST").trim());				
				//bean.setINSCEST(rs.getString("INSCEST").trim());
				//bean.setINSCMUN(rs.getString("INSCMUN").trim());
				//bean.setINDCONT(rs.getString("INDCONT").trim());
				bean.setEST(rs.getString("EST").trim());
				bean.setCEP(rs.getString("CEP").trim());
				bean.setCNPJ(rs.getString("CPF").trim());
				bean.setCNPJ(rs.getString("CPF").trim());
//				if(rs.getString("CONFIS").equals("J")){
//					String primeiraCasa = rs.getString("CGCNUM");
//					if(9 - primeiraCasa.length() > 0){
//						int pc = (9 - primeiraCasa.length());
//						for(int i = 0; i < pc ; i++){
//							primeiraCasa = "0"+primeiraCasa;
//						}
//					}			    	
//					String segundaCasa = rs.getString("CGCFIL");
//					if(4 - segundaCasa.length() > 0){
//						int sc = (4 - segundaCasa.length());
//						for(int i = 0; i < sc ; i++){
//							segundaCasa = "0"+segundaCasa;
//						}
//					}
//
//					String terceiraCasa = rs.getString("CGCDIG");
//					if(2 - terceiraCasa.length() > 0){
//						int tc = (2 - terceiraCasa.length());
//						for(int i = 0; i < tc ; i++){
//							terceiraCasa = "0"+terceiraCasa;
//						}
//					}
//					bean.setCNPJ(primeiraCasa+"/"+segundaCasa+"-"+terceiraCasa);
//				}else{
//					bean.setCNPJ(rs.getString("CPF").trim());
//				}
			}

	
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				if(con != null){
					prstmt_.close();
					rs.close();
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return bean;
	}
	
	public String findFabricante (String prefixo){
		ResultSet rs = null;
		PreparedStatement prstmt_ = null;
		Connection con = null;
		String result = "";
		EntityManager manager = null;
		try {
			manager = JpaUtil.getInstance();
			con = ConectionDbs.getConnecton(); 
			
			String SQL = "select distinct trim(s.ds4) ||'-'|| trim(e.eqmfcd) from "+IConstantAccess.LIB_DBS+".SCPCODE0 s, "+IConstantAccess.LIB_DBS+".EMPEQPD0 e"+ 
					" where s.keyda1 = e.EQMFCD"+ 
					" and s.rcdcd = 'F'"+
					" and substring(e.EQMFS2,0,5 ) = '"+prefixo+"'";
			prstmt_ = con.prepareStatement(SQL);
			rs = prstmt_.executeQuery();
			
			if(rs.next()){
				result = rs.getString(1).trim();
			}
			
			Query query = manager.createQuery("from PmpContrato where prefixo =:prefixo");
			query.setParameter("prefixo", prefixo);
			
			List<PmpContrato> listContratos = (List<PmpContrato>)query.getResultList();
			if(listContratos.size() > 0){
				PmpContrato contrato = (PmpContrato)query.getResultList().get(0);
				PmpFamilia familia = manager.find(PmpFamilia.class, contrato.getIdFamilia());
				if(familia.getDescricao().equals("REBOCADORES")){
					SQL = "select distinct trim(s.ds4) ||'-'|| trim(e.eqmfcd) from "+IConstantAccess.LIB_DBS+".SCPCODE0 s, "+IConstantAccess.LIB_DBS+".EMPEQPD0 e"+ 
					" where s.keyda1 = e.EQMFCD"+ 
					" and s.rcdcd = 'F'"+
					" and substring(e.EQMFS2,0,5 ) = '"+contrato.getNumeroSerie().substring(3)+"'";
					prstmt_ = con.prepareStatement(SQL);
					rs = prstmt_.executeQuery();

					if(rs.next()){
						result = rs.getString(1).trim();
					}
				}
			}

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(manager != null && manager.isOpen()){
				manager.close();
			}
			try {
				if(con != null){
					prstmt_.close();
					rs.close();
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
		
	}
	
	public List<ClienteBean> findDataNomeCliente(String nomeCliente){
		ResultSet rs = null;
		PreparedStatement prstmt_ = null;

		Connection con = null;

//		InputStream in = getClass().getClassLoader().getResourceAsStream(IConstantAccess.CONF_FILE);
//		Properties prop = new Properties();

		List<ClienteBean> clienteList = new ArrayList<ClienteBean>();
		try {
//			prop.load(in);
//			String url = prop.getProperty("dbs.url");
//			String user = prop.getProperty("dbs.user");
//			String password =prop.getProperty("dbs.password");
//			Class.forName(prop.getProperty("dbs.driver")).newInstance();

			con = ConectionDbs.getConnecton();  

			String SQL = "select c.FLGDLI,c.CUNM CLCHAVE, " +
			"c.CUNO,c.CUADD2 END2, c.CUADD3 BAIRRO,c.CUCYST CID,c.CUSTE EST, trim(SUBstring(c.TELXNO,0,15)) CPF, c.ZIPCD9 CEP " +
			"FROM "+com.pmprental.util.IConstantAccess.LIB_DBS+".CIPNAME0 c "+
			" where c.CUNM like '"+nomeCliente.toUpperCase()+"%'";
			//" and c.FLGDLI <> 'Y'";
			prstmt_ = con.prepareStatement(SQL);
			rs = prstmt_.executeQuery();
			while(rs.next()){
				ClienteBean bean = new ClienteBean();
				bean.setRAZSOC(rs.getString("CLCHAVE").trim());
				bean.setCLCHAVE(rs.getString("CUNO").trim());
				bean.setEND(rs.getString("END2").trim());
				bean.setBAIRRO(rs.getString("BAIRRO").trim());
				bean.setCID(rs.getString("CID").trim());
				bean.setEST(rs.getString("EST").trim());				
				//bean.setINSCEST(rs.getString("INSCEST").trim());
				//bean.setINSCMUN(rs.getString("INSCMUN").trim());
				//bean.setINDCONT(rs.getString("INDCONT").trim());
				bean.setEST(rs.getString("EST").trim());
				bean.setCEP(rs.getString("CEP").trim());
				bean.setCNPJ(rs.getString("CPF").trim());
				bean.setCNPJ(rs.getString("CPF").trim());
				bean.setFLAGDELETE(rs.getString("FLGDLI").trim());
//				if(rs.getString("CONFIS").equals("J")){
//					String primeiraCasa = rs.getString("CGCNUM");
//					if(9 - primeiraCasa.length() > 0){
//						int pc = (9 - primeiraCasa.length());
//						for(int i = 0; i < pc ; i++){
//							primeiraCasa = "0"+primeiraCasa;
//						}
//					}			    	
//					String segundaCasa = rs.getString("CGCFIL");
//					if(4 - segundaCasa.length() > 0){
//						int sc = (4 - segundaCasa.length());
//						for(int i = 0; i < sc ; i++){
//							segundaCasa = "0"+segundaCasa;
//						}
//					}
//
//					String terceiraCasa = rs.getString("CGCDIG");
//					if(2 - terceiraCasa.length() > 0){
//						int tc = (2 - terceiraCasa.length());
//						for(int i = 0; i < tc ; i++){
//							terceiraCasa = "0"+terceiraCasa;
//						}
//					}
//					bean.setCNPJ(primeiraCasa+"/"+segundaCasa+"-"+terceiraCasa);
//				}else{
//					bean.setCNPJ(rs.getString("CPF").trim());
//				}
				clienteList.add(bean);
			}


	
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(con != null){
					prstmt_.close();
					rs.close();
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return clienteList;
	}
	
	public List<ConfigManutencaoHorasBean> findAllManutencaoHoras(String modelo, String prefixo, String beginrange, String endrange, String bgrp, String contExcessao, Long idConfigPreco){
		EntityManager manager = null;
		List<ConfigManutencaoHorasBean> result = new ArrayList<ConfigManutencaoHorasBean>();
		try {
			manager = JpaUtil.getInstance();
			String SQL = "select h from PmpConfigManutencao m, PmpConfigHorasStandard h"+
				" where m.modelo = '"+modelo+"'"+
				" and m.prefixo = '"+prefixo+"'"+
				" and m.beginrange = '"+beginrange+"'"+
				" and m.endrange = '"+endrange+"'"+
				" and m.bgrp = '"+bgrp+"'"+
				" and m.id = h.idConfigManutencao.id " +
				" and m.isGeradorStandby = 'N' " +
				" and m.contExcessao = '"+contExcessao+"'";
			if(idConfigPreco != null){
				SQL+= " and m.idConfiguracaoPreco.id="+idConfigPreco;
			}
				
			SQL+=" order by h.horas";
			Query query = manager.createQuery(SQL);
			List<PmpConfigHorasStandard> list = query.getResultList();
			Long frequencia = 0l;
			Long frequenciaUltima = 0l;
			int cont = 0;
			while(cont < 48000){
				for (int i = 0; i < list.size(); i++) {
					PmpConfigHorasStandard horas = list.get(i);
					ConfigManutencaoHorasBean bean = new ConfigManutencaoHorasBean();
					
					if(i == 0 || i == 1 || i == 2){
						if((horas.getHoras()+cont) == 12200	|| (horas.getHoras()+cont) == 12400 || (horas.getHoras()+cont) == 12600
									|| (horas.getHoras()+cont) == 24200 || (horas.getHoras()+cont) == 24400 || (horas.getHoras()+cont) == 24600
									|| (horas.getHoras()+cont) == 36200 || (horas.getHoras()+cont) == 36400 || (horas.getHoras()+cont) == 36600
									|| (horas.getHoras()+cont) == 48200 || (horas.getHoras()+cont) == 48400 || (horas.getHoras()+cont) == 48600){
							continue;
						}
					}
					if(i == 0){
						if((horas.getHoras()+cont) == 12800	
									|| (horas.getHoras()+cont) == 25600 
									|| (horas.getHoras()+cont) == 38400 
									|| (horas.getHoras()+cont) == 51200){
							if(cont == 12000 || cont == 24800 || cont == 37600){
								cont = cont + 800;
							}
							continue;
						}
					}
					
					System.out.println("i " +i+" "+(horas.getHoras()+cont)+" - "+horas.getStandardJobCptcd());
					
					if(i == 0 || i == 1 || (i == 2 && horas.getHoras() == 500)){
						PmpConfigHorasStandard horasProximo = list.get(i+1);
						frequencia = horasProximo.getHoras() - horas.getHoras();
						
						PmpConfigHorasStandard horasFinal = list.get(list.size()-1);
						PmpConfigHorasStandard horasPenultima = list.get(list.size()-2);
						frequenciaUltima = horasFinal.getHoras() - horasPenultima.getHoras();
						
						if((cont == 12000 || cont == 24000 || cont == 36000) && frequencia.longValue() != frequenciaUltima.longValue()){
							continue;
						}
						if(cont == 12000 || cont == 24000 || cont == 36000){
							if(horas.getHoras() == 500 || horas.getHoras() == 250){
								continue;
							}
						}
						
					}else{
						PmpConfigHorasStandard horasFinal = list.get(list.size()-1);
						PmpConfigHorasStandard horasPenultima = list.get(list.size()-2);
						frequencia = horasFinal.getHoras() - horasPenultima.getHoras();
					}
					
					bean.setId(horas.getId());
					bean.setHoras(horas.getHoras());
					bean.setHorasManutencao(horas.getHoras()+cont);
					bean.setStandardJob(horas.getStandardJobCptcd());
					bean.setIdConfigManutencao(horas.getIdConfigManutencao().getId());
					bean.setIsExecutado("N");
					bean.setFrequencia(frequencia);
					bean.setHorasRevisao(horas.getHorasRevisao());
					result.add(bean);
				}
				cont = cont + 12000;
			}
			return result;
		} catch (Exception e) {			
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return result;
	}
	
	public List<ConfigManutencaoMesesBean> findAllManutencaoMeses(String modelo, String prefixo, String beginrange, String endrange, String bgrp){
		EntityManager manager = null;
		List<ConfigManutencaoMesesBean> result = new ArrayList<ConfigManutencaoMesesBean>();
		try {
			manager = JpaUtil.getInstance();
			Query query = manager.createQuery("select h from PmpConfigManutencao m, PmpConfigHorasStandard h"+
					" where m.modelo = '"+modelo+"'"+
					" and m.prefixo = '"+prefixo+"'"+
					" and m.beginrange = '"+beginrange+"'"+
					" and m.endrange = '"+endrange+"'"+
					" and m.bgrp = '"+bgrp+"'"+
					" and m.id = h.idConfigManutencao.id and m.isGeradorStandby = 'S' order by h.horas");
			List<PmpConfigHorasStandard> list = query.getResultList();
			
			Long frequencia = 30L;	//Em dias
			for (int i = 0; i < list.size(); i++) {
				PmpConfigHorasStandard horas = list.get(i);
				ConfigManutencaoMesesBean bean = new ConfigManutencaoMesesBean();
				
				bean.setId(horas.getId());
				bean.setMeses(horas.getHoras());
				bean.setMesManutencao(horas.getHoras());
				bean.setStandardJob(horas.getStandardJobCptcd());
				bean.setIdConfigManutencao(horas.getIdConfigManutencao().getId());
				bean.setIsExecutado("N");
				bean.setFrequencia(frequencia);
				result.add(bean);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return result;
	}
	
	public ContratoComercialBean validarRenovacaoContrato(ContratoComercialBean contratoComercialBean){
		EntityManager manager = null;
		//SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		//SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd");

		try {
			manager = JpaUtil.getInstance();
			String msg = validarRenovacaoContrato(contratoComercialBean, manager);
			if(msg != null){
				contratoComercialBean.setMsg(msg);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(manager != null && manager.isOpen()){
				manager.close();		
			}
		}
		return contratoComercialBean;
	}
	
	public synchronized ContratoComercialBean saveOrUpdate(ContratoComercialBean contratoComercialBean, String isSaveHorasPosPago){
		EntityManager manager = null;
		Connection con = null;
		Statement prstmt = null;
		try {
			manager = JpaUtil.getInstance();
			con = ConectionDbs.getConnecton();
			
			PmpTipoContrato pmpTipoContrato = manager.find(PmpTipoContrato.class, contratoComercialBean.getIdTipoContrato());
			PmpStatusContrato statusContrato = manager.find(PmpStatusContrato.class, contratoComercialBean.getStatusContrato());
			TwFilial filial = manager.find(TwFilial.class, Long.valueOf(FILIAL));
			PmpConfigTracao pmpConfigTracao = null;
			if(contratoComercialBean.getIdConfigTracao() != null && contratoComercialBean.getIdConfigTracao() != 0){
				pmpConfigTracao = manager.find(PmpConfigTracao.class, contratoComercialBean.getIdConfigTracao());
			}
			
			
			String SQL = "From PmpConfigManutencao where modelo =:modelo and prefixo =:prefixo and bgrp =:bgrp and beginrange =:beginrange and endrange =:endrange and contExcessao=:contExcessao";
			if(contratoComercialBean.getIdConfigPreco() != null && contratoComercialBean.getIdConfigPreco() > 0){
				SQL += " and idConfiguracaoPreco.id = "+contratoComercialBean.getIdConfigPreco();
			}
			Query query = manager.createQuery(SQL);
			query.setParameter("modelo", contratoComercialBean.getModelo());
			query.setParameter("prefixo", contratoComercialBean.getPrefixo());
			query.setParameter("bgrp", contratoComercialBean.getBusinessGroup());
			query.setParameter("beginrange", contratoComercialBean.getBeginRanger());
			query.setParameter("endrange", contratoComercialBean.getEndRanger());
			query.setParameter("contExcessao", contratoComercialBean.getContExcessao());
			PmpConfigManutencao manutencao = (PmpConfigManutencao)query.getSingleResult();
			//contrato.setIdConfigManutencao(manutencao);
			
			PmpContrato contrato = null;
			PmpConfigOperacional operacional = null;
			
			if(contratoComercialBean.getId() == null || contratoComercialBean.getId() == 0){
				contrato = new PmpContrato();
				contrato.setIdTipoContrato(pmpTipoContrato);
				contrato.setIdStatusContrato(statusContrato);
				contrato.setIdFuncionario(ID_FUNCIONARIO);
				contrato.setFilial(Integer.valueOf(FILIAL));
				contrato.setIdConfigTracao(pmpConfigTracao);
				if(filial.getRegional() != null){
					contrato.setRegional(filial.getRegional().toString());
				}
				contratoComercialBean.toBean(contrato);
				contrato.setNumeroSerie(contratoComercialBean.getPrefixo()+contratoComercialBean.getNumeroSerie());
				
				if (contratoComercialBean.getDistanciaGerador() != null) {
					contrato.setDistanciaGerador(BigDecimal.valueOf(Double.valueOf(contratoComercialBean.getDistanciaGerador())));
				}
				
				if (statusContrato.getSigla().equals("CA")) {
					contrato.setDataAceite(new Date());
					contrato.setHasSendEmail("S");
				} else if (statusContrato.getSigla().equals("CNA")) {
					contrato.setDataRejeicao(new Date());
					contrato.setIdMotivoNaoFecContrato(manager.find(PmpMotivosNaoFecContrato.class, contratoComercialBean.getIdMotNaoFecContrato()));
				}
				
				SimpleDateFormat fmt = new SimpleDateFormat("yyyy/dd/MM");
				String data = fmt.format(new Date());
				String tipoContrato = "G";
				if(pmpTipoContrato.getSigla().equals("REN")){
					tipoContrato = "R";
				}
				if(contratoComercialBean.getIsSpot() != null && contratoComercialBean.getIsSpot().equals("S")){
					tipoContrato = "S";
				}
//				String SQL = "From PmpConfigManutencao where modelo =:modelo and prefixo =:prefixo and bgrp =:bgrp and beginrange =:beginrange and endrange =:endrange and contExcessao=:contExcessao";
//				if(contratoComercialBean.getIdConfigPreco() != null && contratoComercialBean.getIdConfigPreco() > 0){
//					SQL += " and idConfiguracaoPreco.id = "+contratoComercialBean.getIdConfigPreco();
//				}
//				Query query = manager.createQuery(SQL);
//				query.setParameter("modelo", contratoComercialBean.getModelo());
//				query.setParameter("prefixo", contratoComercialBean.getPrefixo());
//				query.setParameter("bgrp", contratoComercialBean.getBusinessGroup());
//				query.setParameter("beginrange", contratoComercialBean.getBeginRanger());
//				query.setParameter("endrange", contratoComercialBean.getEndRanger());
//				query.setParameter("contExcessao", contratoComercialBean.getContExcessao());
//				PmpConfigManutencao manutencao = (PmpConfigManutencao)query.getSingleResult();
				contrato.setIdConfigManutencao(manutencao);
				manager.getTransaction().begin();
				manager.persist(contrato);
				manager.getTransaction().commit();
				contrato.setNumeroContrato(data.substring(2,4)+(Integer.valueOf(FILIAL) < 10?"0"+FILIAL:FILIAL)+tipoContrato+contrato.getId());
				if(statusContrato.getSigla().equals("CA")){
					operacional = new PmpConfigOperacional();
					operacional.setContato(contrato.getContatoComercial());
					operacional.setLocal(contrato.getBairro()+", "+contrato.getCidade()+", "+contrato.getEndereco());
					operacional.setIdFuncSupervisor(ID_FUNCIONARIO);
					operacional.setIdContrato(contrato);
					operacional.setTelefoneContato(contrato.getTelefoneComercial());
					operacional.setFilial(Long.valueOf(FILIAL));
					if(contratoComercialBean.getIdFilialDestino() != null && contratoComercialBean.getIdFilialDestino() > 0){
						operacional.setFilial(contratoComercialBean.getIdFilialDestino());
					}
					manager.getTransaction().begin();
					manager.persist(operacional);
					manager.getTransaction().commit();
				}
				
			}else{
				contrato = manager.find(PmpContrato.class, contratoComercialBean.getId());
				contrato.setIdConfigManutencao(manutencao);
				contrato.setIdTipoContrato(pmpTipoContrato);
				contrato.setIdStatusContrato(statusContrato);
				contrato.setIdConfigTracao(pmpConfigTracao);
				if(filial.getRegional() != null){
					contrato.setRegional(filial.getRegional().toString());
				}
				//contrato.setIdFuncionario(ID_FUNCIONARIO);
				//contrato.setFilial(Integer.valueOf(FILIAL));
				contratoComercialBean.toBean(contrato);
				contrato.setNumeroSerie(contratoComercialBean.getPrefixo() + contratoComercialBean.getNumeroSerie());
				
				if (contratoComercialBean.getDistanciaGerador() != null) {
					contrato.setDistanciaGerador(BigDecimal.valueOf(Double.valueOf(contratoComercialBean.getDistanciaGerador())));
				}
				
				if(statusContrato.getSigla().equals("CA")){
					contrato.setDataAceite(new Date());
					contrato.setHasSendEmail("S");
					query = manager.createQuery("from PmpConfigOperacional where idContrato.id = :id_contrato");
					query.setParameter("id_contrato", contrato.getId());
					List<PmpConfigOperacional> list = query.getResultList();
					if(list.size() > 0){
						operacional = list.get(0);
					}else{
						operacional = new PmpConfigOperacional();
					}
					operacional.setContato(contrato.getContatoComercial());
					operacional.setLocal(contrato.getBairro()+", "+contrato.getCidade()+", "+contrato.getEndereco());
					operacional.setIdFuncSupervisor(ID_FUNCIONARIO);
					operacional.setIdContrato(contrato);
					operacional.setTelefoneContato(contrato.getTelefoneComercial());
					operacional.setFilial(Long.valueOf(FILIAL));
					if(contratoComercialBean.getIdFilialDestino() != null && contratoComercialBean.getIdFilialDestino() > 0){
						operacional.setFilial(contratoComercialBean.getIdFilialDestino());
					}
					manager.getTransaction().begin();
					manager.merge(operacional);
					manager.getTransaction().commit();
					
					if(pmpTipoContrato.getSigla().equals("CON") || pmpTipoContrato.getSigla().equals("VEN") || pmpTipoContrato.getSigla().equals("REN")){//concessão ou contrato pós-pago (VEN)
						query = manager.createQuery("from PmpConfigOperacional where idContrato.id =:idContrato");
						query.setParameter("idContrato", contrato.getId());
						PmpConfigOperacional configOperacional = new PmpConfigOperacional();
						if(query.getResultList().size() > 0){
							configOperacional = (PmpConfigOperacional)query.getSingleResult();
						}
						configOperacional.setIdContrato(contrato);
						configOperacional.setNumOs(contrato.getNumeroContrato());
						configOperacional.setIdFuncSupervisor(contrato.getIdFuncionario());
						configOperacional.setContato(contrato.getContatoServicos());
						configOperacional.setLocal(contrato.getEndereco());
						configOperacional.setTelefoneContato(contrato.getTelefoneServicos());
						configOperacional.setFilial(contrato.getFilial().longValue());
						configOperacional.setCodErroDbs("00");
						configOperacional.setCompCode("8898");
						configOperacional.setJobCode("041");
						configOperacional.setCscc("CL");
						configOperacional.setInd("E");
						configOperacional.setNumeroSegmento("01");
						if(pmpTipoContrato.getSigla().equals("REN")){
							configOperacional.setFilial(contratoComercialBean.getIdFilialDestino());
						}
						manager.getTransaction().begin();
						manager.merge(configOperacional);
						manager.getTransaction().commit();
					}
					
					
				}else if(statusContrato.getSigla().equals("CNA")){
					contrato.setDataRejeicao(new Date());
					contrato.setIdMotivoNaoFecContrato(manager.find(PmpMotivosNaoFecContrato.class, contratoComercialBean.getIdMotNaoFecContrato()));
				}
				manager.getTransaction().begin();
				manager.merge(contrato);
				manager.getTransaction().commit();
			}
			manager.getTransaction().begin();
			if(statusContrato.getSigla().equals("CA")){
				OperacionalBean bean = new OperacionalBean();
				bean.setContratoId(contrato.getId());
				bean.setNumeroSerie(contratoComercialBean.getPrefixo() + contratoComercialBean.getNumeroSerie());
				if(contratoComercialBean.getIdEquipamento() == null){
					contrato.setIdEquipamento(this.findIdEquipamento(bean));
				}
			}

			//Remove o tipo de customização
			String siglaCustomizacao = "";
			query = manager.createNativeQuery("delete from REN_PMP_CONTRATO_CUSTOMIZACAO where id_contrato =:id_contrato");
			query.setParameter("id_contrato", contrato.getId());
			query.executeUpdate();
			if(contratoComercialBean.getTipoCustomizacaoList() != null){
				for (ConfigurarCustomizacaoBean bean : contratoComercialBean.getTipoCustomizacaoList()) {
					if(bean.getIsSelected()){
						PmpContratoCustomizacao contratoCustomizacao = new PmpContratoCustomizacao();
						contratoCustomizacao.setIdContrato(contrato);
						contratoCustomizacao.setIdTipoCustomizacao(manager.find(PmpTipoCustomizacao.class, bean.getIdTipoCustomizacao()));
						manager.persist(contratoCustomizacao);
						query = manager.createNativeQuery("select sc.sigla_customizacao from REN_PMP_SIGLA_CUSTOMIZACAO sc, REN_PMP_CONFIG_CUSTOMIZACAO cc"+
								" where cc.ID_TIPO_CUSTOMIZACAO =:ID_TIPO_CUSTOMIZACAO"+
								" and sc.id_config_customizacao = cc.id");
						query.setParameter("ID_TIPO_CUSTOMIZACAO", bean.getIdTipoCustomizacao());
						List<String> sgTipoCustList = query.getResultList();
						for (String string : sgTipoCustList) {
							siglaCustomizacao += "'"+string+"',";
						}
					}
				}
			}
			if(siglaCustomizacao.length() > 0){
				siglaCustomizacao = siglaCustomizacao.substring(0, siglaCustomizacao.length() - 1);
			}else{
				siglaCustomizacao = "'-null'";
			}
			if(contratoComercialBean.getFuncionarioIndicado() != null && contratoComercialBean.getMatriculaIndicado() != null){
				contrato.setIdFuncionarioIndicacao(contratoComercialBean.getMatriculaIndicado());
				contrato.setNomeIndicacao(contratoComercialBean.getFuncionarioIndicado());
				contrato.setComissaoindicacao(contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getComissaoIndicacao());
			}else{
				contrato.setIdFuncionarioIndicacao(null);
				contrato.setNomeIndicacao(null);
				contrato.setComissaoConsultor(contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getComissaoConsultor());
				//contrato.setComissaoConsultor(null);
				contrato.setComissaoindicacao(null);
			}
			//contrato.setIsFindSubTributaria(contratoComercialBean.getIsFindSubstuicaoTributaria());
			//contrato.setCodErroDbs(contratoComercialBean.getCodErroDbs());
			//contrato.setMsgErroDbs(contratoComercialBean.getMsgErroDbs());
			//contrato.setNumDocDbs(contratoComercialBean.getNumDocDbs());
			if(!contratoComercialBean.getIsGeradorStandby()) {
				this.insertManutHoras(contratoComercialBean.getConfigManutencaoHorasBeanList(), contrato, manager, isSaveHorasPosPago, siglaCustomizacao);
				contratoComercialBean.setPrintRevisaoPosPago(contrato.getPrintRevisaoPosPago());
			} else {
				this.insertManutMeses(contratoComercialBean.getConfigManutencaoMesesBeanList(), contrato, manager);
			}
			
			if(contratoComercialBean.getIsFindSubstuicaoTributaria() != null && "P".equals(contratoComercialBean.getIsFindSubstuicaoTributaria())){
				contrato.setIsFindSubTributaria("P");
				//envia as peças para gerar nova cotação
				prstmt = con.createStatement();
				String inCptcd = "";
				for (ConfigManutencaoHorasBean configManutHorasBean : contratoComercialBean.getConfigManutencaoHorasBeanList()) {
					inCptcd += "'"+configManutHorasBean.getStandardJob()+"',";
				}
				if(inCptcd.length() > 1){
					inCptcd = inCptcd.substring(0,inCptcd.length()-1);
				}
				new OsBusiness(this.usuarioBean).sendTotalPecasPecasDbs(contrato, manager, prstmt, inCptcd, siglaCustomizacao);
				contrato.setIsFindSubTributaria("P");
				contrato.setCodErroDbs("100");
				contrato.setNumDocDbs(null);
				contrato.setValoContrato(null);
				contratoComercialBean.setCodErroDbs("100");
				contrato.setMsgErroDbs("Aguarde o resultado da Cotação!");
				contratoComercialBean.setMsgErroDbs("Aguarde o resultado da Cotação!");

			}else if("R".equals(contratoComercialBean.getIsFindSubstuicaoTributaria())){//retira  a substituição tributária
				contrato.setIsFindSubTributaria(null);
				contrato.setCodErroDbs(null);
				contratoComercialBean.setCodErroDbs(null);
				contrato.setMsgErroDbs(null);
				contrato.setNumDocDbs(null);

			}
			
			
			PmpMaquinaPl pl = new PmpMaquinaPl();
			pl.setDataAtualizacao(new Date());
			pl.setHorimetro(contratoComercialBean.getHorimetro());
			pl.setNumeroSerie(contratoComercialBean.getPrefixo()+contratoComercialBean.getNumeroSerie());
			manager.persist(pl);
			
			manager.getTransaction().commit();
			contratoComercialBean.setNumeroContrato(contrato.getNumeroContrato());
			contratoComercialBean.setId(contrato.getId().longValue());
			if(contrato.getIdTipoContrato().getSigla().equals("VEN")){
				List<PrecoBean> result = new ArrayList<PrecoBean>();
				if(contrato.getBgrp().equals("PM") || contrato.getBgrp().equals("SEV")){
					result = this.findAllParcelasPMPKIT3Custo(contrato.getId(), siglaCustomizacao);
				}else if(contrato.getBgrp().equals("KIT2")){
					result = this.findAllParcelasKIT2Custo(contrato.getId(), siglaCustomizacao);
				}
				PrecoBean bean = result.get(0);
				manager.getTransaction().begin();
				contrato = manager.find(PmpContrato.class, contrato.getId());
				contrato.setValorCusto((BigDecimal.valueOf(Double.valueOf(bean.getPreco().replace(".", "").replace(",", ".").replace("R$", "")))));
				manager.merge(contrato);
				manager.getTransaction().commit();
			}
			
			return contratoComercialBean;
		} catch (Exception e) {
			if(manager != null && manager.getTransaction().isActive()){
				manager.getTransaction().rollback();
			}
			e.printStackTrace();
		}finally{
			try {
				
			if(manager != null && manager.isOpen()){
				manager.close();
			}
			if(con != null){
				con.close();
			}
			if(prstmt != null){
				prstmt.close();
			}
			} catch (Exception e2) {
				e2.printStackTrace();
			}			
		}
		return null;
	}
	private String validarRenovacaoContrato(ContratoComercialBean contratoComercialBean, EntityManager manager) {
		
		contratoComercialBean.setMsg(null);
		String SQL = "select count(c.id) from ren_pmp_contrato c"+
					 "	where c.id_status_contrato = ( select s.id from ren_pmp_status_contrato s where s.sigla = 'CA')"+
					 "	and c.id = (select distinct hs.id_contrato from ren_pmp_cont_horas_standard hs where  hs.is_executado = 'N' and hs.id_contrato = c.id)"+
					// "	and c.data_aceite between to_date('01/01/2011 00:00:00', 'DD/MM/YYYY HH24:MI:SS') and  to_date('01/01/2013 23:59:59', 'DD/MM/YYYY HH24:MI:SS')"+
					 "	and c.filial = "+ Integer.valueOf(usuarioBean.getFilial())+
					 "	and c.numero_serie = '"+contratoComercialBean.getPrefixo()+contratoComercialBean.getNumeroSerie()+"'";
		Query query =  manager.createNativeQuery(SQL);
		Integer totalContratos = (Integer)query.getSingleResult();
		if(totalContratos.intValue() > 0){
			SQL = "select count( hs.id_contrato) from ren_pmp_cont_horas_standard hs where  hs.is_executado = 'N' and hs.id_contrato = (select min(c.id) from ren_pmp_contrato c where c.numero_serie = '"+contratoComercialBean.getPrefixo()+contratoComercialBean.getNumeroSerie()+"' and c.filial = "+Integer.valueOf(usuarioBean.getFilial())+")";
			query =  manager.createNativeQuery(SQL);
			Integer totalManutencao = (Integer)query.getSingleResult();
			return "O equipamento já possui um contrato aberto com "+totalManutencao.intValue()+" manutenções aberta(s).\nTem certeza que deseja renová-lo ou fazer uma proposta";
		}
		return null;
	}
	
	public ContratoComercialBean saveOrUpdateContratoAntigo(ContratoComercialBean contratoComercialBean){
		EntityManager manager = null;
		try {
			manager = JpaUtil.getInstance();
			manager.getTransaction().begin();
			
			PmpTipoContrato pmpTipoContrato = manager.find(PmpTipoContrato.class, contratoComercialBean.getIdTipoContrato());
			PmpStatusContrato statusContrato = manager.find(PmpStatusContrato.class, contratoComercialBean.getStatusContrato());
			TwFilial filial = manager.find(TwFilial.class, Long.valueOf(FILIAL));
			PmpConfigTracao pmpConfigTracao = null;
			if(contratoComercialBean.getIdConfigTracao() != null && contratoComercialBean.getIdConfigTracao() != 0){
				pmpConfigTracao = manager.find(PmpConfigTracao.class, contratoComercialBean.getIdConfigTracao());
			}
			
			
			PmpContrato contrato = null;
			
			if(contratoComercialBean.getId() == null || contratoComercialBean.getId() == 0){
				contrato = new PmpContrato();
				contrato.setIdTipoContrato(pmpTipoContrato);
				contrato.setIdStatusContrato(statusContrato);
				contrato.setIdFuncionario(ID_FUNCIONARIO);
				contrato.setFilial(Integer.valueOf(FILIAL));
				contrato.setIdConfigTracao(pmpConfigTracao);
				if(filial.getRegional() != null){
					contrato.setRegional(filial.getRegional().toString());
				}
				contratoComercialBean.toBean(contrato);
				contrato.setNumeroSerie(contratoComercialBean.getPrefixo()+contratoComercialBean.getNumeroSerie());
				if(statusContrato.getSigla().equals("CA")){
					contrato.setDataAceite(new Date());
					contrato.setHasSendEmail("S");
				}else if(statusContrato.getSigla().equals("CNA")){
					contrato.setDataRejeicao(new Date());
					contrato.setIdMotivoNaoFecContrato(manager.find(PmpMotivosNaoFecContrato.class, contratoComercialBean.getIdMotNaoFecContrato()));
				}
				SimpleDateFormat fmt = new SimpleDateFormat("yyyy/dd/MM");
				String data = fmt.format(new Date());
				String tipoContrato = "G";
				if(pmpTipoContrato.getSigla().equals("REN")){
					tipoContrato = "R";
				}
				
				String SQL = "From PmpConfigManutencao where modelo =:modelo and prefixo =:prefixo and bgrp =:bgrp and beginrange =:beginrange and endrange =:endrange and contExcessao=:contExcessao";
				if(contratoComercialBean.getIdConfigPreco() != null && contratoComercialBean.getIdConfigPreco() > 0){
					SQL += " and idConfiguracaoPreco.id = "+contratoComercialBean.getIdConfigPreco();
				}
				Query query = manager.createQuery(SQL);
				query.setParameter("modelo", contratoComercialBean.getModelo());
				query.setParameter("prefixo", contratoComercialBean.getPrefixo());
				query.setParameter("bgrp", contratoComercialBean.getBusinessGroup());
				query.setParameter("beginrange", contratoComercialBean.getBeginRanger());
				query.setParameter("endrange", contratoComercialBean.getEndRanger());
				query.setParameter("contExcessao", contratoComercialBean.getContExcessao());
				PmpConfigManutencao manutencao = (PmpConfigManutencao)query.getSingleResult();
				contrato.setIdConfigManutencao(manutencao);
				manager.persist(contrato);
				contrato.setNumeroContrato(data.substring(2,4)+(Integer.valueOf(FILIAL) < 10?"0"+FILIAL:FILIAL)+tipoContrato+contrato.getId());
				if(statusContrato.getSigla().equals("CA")){
					PmpConfigOperacional operacional = new PmpConfigOperacional();
					operacional.setContato(contrato.getContatoComercial());
					operacional.setLocal(contrato.getBairro()+", "+contrato.getCidade()+", "+contrato.getEndereco());
					operacional.setIdFuncSupervisor(ID_FUNCIONARIO);
					operacional.setIdContrato(contrato);
					operacional.setNumOs(contratoComercialBean.getNumOs().toUpperCase());
					operacional.setTelefoneContato(contrato.getTelefoneComercial());
					operacional.setFilial(Long.valueOf(FILIAL));
					manager.persist(operacional);
				}
				
			}else{
				contrato = manager.find(PmpContrato.class, contratoComercialBean.getId());
				contrato.setIdTipoContrato(pmpTipoContrato);
				contrato.setIdStatusContrato(statusContrato);
				contrato.setRegional(filial.getRegional().toString());
				contrato.setIdConfigTracao(pmpConfigTracao);
				//contrato.setIdFuncionario(ID_FUNCIONARIO);
				//contrato.setFilial(Integer.valueOf(FILIAL));
				contratoComercialBean.toBean(contrato);
				contrato.setNumeroSerie(contratoComercialBean.getPrefixo()+contratoComercialBean.getNumeroSerie());
				if(statusContrato.getSigla().equals("CA")){
					contrato.setDataAceite(new Date());
					contrato.setHasSendEmail("S");
					Query query = manager.createQuery("from PmpConfigOperacional where idContrato.id = :id_contrato");
					query.setParameter("id_contrato", contrato.getId());
					List<PmpConfigOperacional> list = query.getResultList();
					PmpConfigOperacional operacional = null;
					if(list.size() > 0){
						operacional = list.get(0);
					}else{
						operacional = new PmpConfigOperacional();
					}
					operacional.setContato(contrato.getContatoComercial());
					operacional.setLocal(contrato.getBairro()+", "+contrato.getCidade()+", "+contrato.getEndereco());
					operacional.setIdFuncSupervisor(ID_FUNCIONARIO);
					operacional.setIdContrato(contrato);
					operacional.setNumOs(contratoComercialBean.getNumOs().toUpperCase());
					operacional.setTelefoneContato(contrato.getTelefoneComercial());
					operacional.setFilial(Long.valueOf(FILIAL));
					manager.merge(operacional);
				}else if(statusContrato.getSigla().equals("CNA")){
					contrato.setDataRejeicao(new Date());
					contrato.setIdMotivoNaoFecContrato(manager.find(PmpMotivosNaoFecContrato.class, contratoComercialBean.getIdMotNaoFecContrato()));
				}
				manager.merge(contrato);
			}
			if(statusContrato.getSigla().equals("CA")){
				OperacionalBean bean = new OperacionalBean();
				bean.setContratoId(contrato.getId());
				bean.setNumeroSerie(contratoComercialBean.getPrefixo() + contratoComercialBean.getNumeroSerie());
				contrato.setIdEquipamento(this.findIdEquipamento(bean));
			}
			//Remove o tipo de customização
			Query query = manager.createNativeQuery("delete from REN_PMP_CONTRATO_CUSTOMIZACAO where id_contrato =:id_contrato");
			query.setParameter("id_contrato", contrato.getId());
			query.executeUpdate();
			String siglaCustomizacao = "";
			if(contratoComercialBean.getTipoCustomizacaoList() != null){
				for (ConfigurarCustomizacaoBean bean : contratoComercialBean.getTipoCustomizacaoList()) {
					if(bean.getIsSelected()){
						PmpContratoCustomizacao contratoCustomizacao = new PmpContratoCustomizacao();
						contratoCustomizacao.setIdContrato(contrato);
						contratoCustomizacao.setIdTipoCustomizacao(manager.find(PmpTipoCustomizacao.class, bean.getIdTipoCustomizacao()));
						manager.persist(contratoCustomizacao);
						query = manager.createNativeQuery("select sc.sigla_customizacao from REN_PMP_SIGLA_CUSTOMIZACAO sc, REN_PMP_CONFIG_CUSTOMIZACAO cc"+
													" where cc.ID_TIPO_CUSTOMIZACAO =:ID_TIPO_CUSTOMIZACAO"+
													" and sc.id_config_customizacao = cc.id");
						query.setParameter("ID_TIPO_CUSTOMIZACAO", bean.getIdTipoCustomizacao());
						List<String> sgTipoCustList = query.getResultList();
						for (String string : sgTipoCustList) {
							siglaCustomizacao += "'"+string+"',";
						}
					}
				}
			}
			if(siglaCustomizacao.length() > 0){
				siglaCustomizacao = siglaCustomizacao.substring(0, siglaCustomizacao.length() - 1);
			}else{
				siglaCustomizacao = "'-null'";
			}
			this.insertManutHoras(contratoComercialBean.getConfigManutencaoHorasBeanList(), contrato, manager, "N", siglaCustomizacao);
			PmpMaquinaPl pl = new PmpMaquinaPl();
			pl.setDataAtualizacao(new Date());
			pl.setHorimetro(contratoComercialBean.getHorimetro());
			pl.setNumeroSerie(contratoComercialBean.getPrefixo()+contratoComercialBean.getNumeroSerie());
			manager.persist(pl);
			
			manager.getTransaction().commit();
			contratoComercialBean.setNumeroContrato(contrato.getNumeroContrato());
			contratoComercialBean.setId(contrato.getId().longValue());
			if(contrato.getIdTipoContrato().getSigla().equals("VEN")){
				List<PrecoBean> result = new ArrayList<PrecoBean>();
				if(contrato.getBgrp().equals("PM") || contrato.getBgrp().equals("KIT3")){
					result = this.findAllParcelasPMPKIT3Custo(contrato.getId(), siglaCustomizacao);
				}else if(contrato.getBgrp().equals("KIT2")){
					result = this.findAllParcelasKIT2Custo(contrato.getId(), siglaCustomizacao);
				}
				PrecoBean bean = result.get(0);
				manager.getTransaction().begin();
				contrato = manager.find(PmpContrato.class, contrato.getId());
				contrato.setValorCusto((BigDecimal.valueOf(Double.valueOf(bean.getPreco().replace(".", "").replace(",", ".").replace("R$", "")))));
				manager.merge(contrato);
				manager.getTransaction().commit();
			}
			
			return contratoComercialBean;
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
		return null;
	}
	
	public void insertManutHoras(List<ConfigManutencaoHorasBean> beanList, PmpContrato contrato, EntityManager manager, String isSaveHorasPosPago, String siglaCustomizacao) throws Exception{
		Query query = manager.createNativeQuery("delete from REN_PMP_CONT_HORAS_STANDARD where id_contrato = "+contrato.getId().longValue());
		query.executeUpdate();
		if(contrato.getBgrp().equals("KIT2")){
			query = manager.createNativeQuery("select distinct(frequencia) from ren_pmp_cont_horas_standard where id_contrato = "+contrato.getId().longValue());
			Long frequencia = (Long)query.getSingleResult();
			Long primeiraManutencao = 0l;
			while(primeiraManutencao < contrato.getHorimetro().longValue()){
				primeiraManutencao = primeiraManutencao + frequencia;
			}
			if(frequencia.equals(500)){
				for (int i = 1; i < 5; i++) {
					PmpContHorasStandard standard = new PmpContHorasStandard();
					String standardJob = null;
					switch (frequencia.intValue()*i) {
					case 500:
						standardJob = "7502";
						break;
					case 1000:
						standardJob = "7503";
						break;
					case 1500:
						standardJob = "7502";
						break;
					case 2000:
						standardJob = "7504";
						break;

					default:
						break;
					}
					standard.setHoras(frequencia*i);
					if(i > 1){
						primeiraManutencao = primeiraManutencao + frequencia;
					}
					standard.setHorasManutencao(primeiraManutencao);
					standard.setStandardJobCptcd(standardJob);
					standard.setIdContrato(contrato);
					standard.setFrequencia(frequencia);
					manager.persist(standard);
				}
		}else{
			for (int i = 1; i < 9; i++) {
				PmpContHorasStandard standard = new PmpContHorasStandard();
				String standardJob = null;
				switch (frequencia.intValue()*i) {
				case 250:
					standardJob = "7501";
					break;
				case 500:
					standardJob = "7502";
					break;
				case 750:
					standardJob = "7501";
					break;
				case 1000:
					standardJob = "7503";
					break;
				case 1250:
					standardJob = "7501";
					break;
				case 1500:
					standardJob = "7502";
					break;
				case 1750:
					standardJob = "7501";
					break;
				case 2000:
					standardJob = "7504";
					break;

				default:
					break;
				}
				standard.setHoras(frequencia*i);
				if(i > 1){
					primeiraManutencao = primeiraManutencao + frequencia;
				}
				standard.setHorasManutencao(primeiraManutencao);
				standard.setStandardJobCptcd(standardJob);
				standard.setIdContrato(contrato);
				standard.setFrequencia(frequencia);
				manager.persist(standard);
			}
		}
		}else{
			Double custo = 0D;
			Double valorParcelas = 0D;
			if(contrato.getValorSugerido() != null && contrato.getValorSugerido().doubleValue() < contrato.getValoContrato().doubleValue()){
				valorParcelas = contrato.getValoContrato().doubleValue() - contrato.getValorSugerido().doubleValue();
				valorParcelas = valorParcelas / beanList.size();
			}
			for (int i = 0; i < beanList.size(); i++) {
				ConfigManutencaoHorasBean bean = beanList.get(i);
				PmpContHorasStandard standard = new PmpContHorasStandard();
				if(bean.getIsSelected() && "S".equals(isSaveHorasPosPago)){
					contrato.setPrintRevisaoPosPago(BigDecimal.valueOf(bean.getHorasManutencao()));
				}
				//System.out.println(bean.getStandardJob()+" "+bean.getHorasManutencao());
				standard.setHoras(bean.getHoras());
				standard.setHorasManutencao(bean.getHorasManutencao());
				//System.out.println(bean.getStandardJob());
				standard.setStandardJobCptcd(bean.getStandardJob());
				standard.setIdContrato(contrato);
				standard.setIsExecutado(bean.getIsExecutado());
				standard.setFrequencia(bean.getFrequencia());
				standard.setHorasRevisao(bean.getHorasRevisao());
				if(contrato.getIsFindSubTributaria() != null && contrato.getIsFindSubTributaria().equals("S")){
					custo = this.calcularValorRevisaoSubTributaria(contrato, standard, manager);
					if(custo != null){
						standard.setCusto(BigDecimal.valueOf(custo + valorParcelas));
					}else{
						throw new Exception();
					}
				}else if(contrato.getIsFindSubTributaria() == null || contrato.getIsFindSubTributaria().equals("N")){
					custo = this.calcularValorRevisao(contrato, standard, manager, siglaCustomizacao);
					if(custo != null){
						standard.setCusto(BigDecimal.valueOf(custo + valorParcelas));
					}else{
						throw new Exception();
					}
				}
				manager.persist(standard);
			}
		}
		
	}
	
	private Double calcularValorRevisao(PmpContrato contrato, PmpContHorasStandard standard, EntityManager manager, String siglaCustomizacao){

		Double custoManutencao = 0D;
		try {
			String complemento = "";
			String complementoSigla="";
			if(contrato.getIdConfigTracao() != null){
				complemento = " and (ocptmd  not in (select SIGLA_AC from REN_PMP_SIGLA_AC where ID_CONFIG_TRACAO = "+contrato.getIdConfigTracao().getId()+") or ocptmd is null)";
				complemento += " and (JWKAPP  not in (select SIGLA_AC from PMP_SIGLA_AC where ID_CONFIG_TRACAO = "+standard.getIdContrato().getIdConfigTracao().getId()+") or JWKAPP is null)"
				+ " and (ojbloc not in (select SIGLA_TRACAO from REN_PMP_SIGLA_TRACAO where ID_CONFIG_TRACAO = "+contrato.getIdConfigTracao().getId()+") or ojbloc is null)";
			}
			if(siglaCustomizacao.length() > 0){
				complementoSigla =  " and ojbloc not in ("+siglaCustomizacao+")";
				complementoSigla +=  " and ocptmd not in ("+siglaCustomizacao+")";
				complementoSigla +=  " and ocptmd not in ("+siglaCustomizacao+")";
			}
		
			if(contrato.getIdTipoContrato().getSigla().equals("REN")){
					Query query = manager.createNativeQuery("select sum(m.landcs * m.dlrqty) from ren_pmp_manutencao_preco_custo m"+
							" where m.cptcd = '"+standard.getStandardJobCptcd()+"'"+
							" and m.bgrp = '"+contrato.getBgrp()+"'"+
							" and substring(m.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
							complemento + complementoSigla +
							" and (m.beqmsn = '"+standard.getIdContrato().getBeginRanger()+"' or m.beqmsn = '"+standard.getIdContrato().getEndRanger()+"')");
					BigDecimal custoPecas = (BigDecimal)query.getSingleResult();
					if(custoPecas != null){
						custoManutencao = custoPecas.doubleValue();
						standard.setCustoPecas(custoPecas);
					}else{
						standard.setCustoPecas(BigDecimal.ZERO);
					}

					//			if(contrato.getTa().equals("S")){
					//				Double taCusto = (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getTaHoras().doubleValue() * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getHhTa().doubleValue());
					//				taCusto = (totalHorasManutencao.intValue() / 2000) * taCusto;// total de revisoes de 2000 horas * custo do TA
					//				custoManutencao = custoManutencao + taCusto;
					//			}


					//tipo manutenção
					query = manager.createNativeQuery("select TIPO_PM from ren_pmp_hora h"+
							" where h.cptcd = '"+standard.getStandardJobCptcd()+"'"+
							" and h.bgrp = '"+contrato.getBgrp()+"'"+
							" and substring(h.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
							" and (h.beqmsn = '"+standard.getIdContrato().getBeginRanger()+"' or h.beqmsn = '"+standard.getIdContrato().getEndRanger()+"')");
							//" and substring(h.beqmsn,5,10) between '"+contrato.getBeginRanger().substring(4, 9)+"' and '"+contrato.getEndRanger().substring(4, 9)+"'");
					String tipoPm = (String)query.getSingleResult();	
					standard.setTipoPm(tipoPm);
					//total horas para a manutenção
					query = manager.createNativeQuery("select sum(cast(FRSDHR as decimal(18,2))) from ren_pmp_hora h"+
							" where h.cptcd = '"+standard.getStandardJobCptcd()+"'"+
							" and h.bgrp = '"+contrato.getBgrp()+"'"+
							" and substring(h.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
							" and (h.beqmsn = '"+standard.getIdContrato().getBeginRanger()+"' or h.beqmsn = '"+standard.getIdContrato().getEndRanger()+"')");
							//" and substring(h.beqmsn,5,10) between '"+contrato.getBeginRanger().substring(4, 9)+"' and '"+contrato.getEndRanger().substring(4, 9)+"'");
					BigDecimal totalHHManutencao = (BigDecimal)query.getSingleResult();
					standard.setCustoMo((BigDecimal.valueOf(contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getHhRental().doubleValue() * totalHHManutencao.doubleValue())).add(BigDecimal.valueOf(contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getKmRental().doubleValue() * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getValorKmRental().doubleValue())));
					custoManutencao = custoManutencao + (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getHhRental().doubleValue() * totalHHManutencao.doubleValue());//valor de hh
					custoManutencao = custoManutencao + (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getKmRental().doubleValue() * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getValorKmRental().doubleValue());//valor de km vezes a quantidade de manutenções

					return custoManutencao;

			}else if(contrato.getIdTipoContrato().getSigla().equals("VEN") || contrato.getIdTipoContrato().getSigla().equals("VPG") || contrato.getIdTipoContrato().getSigla().equals("VEPM") || contrato.getIdTipoContrato().getSigla().equals("CON") || contrato.getIdTipoContrato().getSigla().equals("CAN")){
				
				Query query = manager.createNativeQuery("select sum(m.unls * m.dlrqty) from ren_pmp_manutencao m"+
							" where m.cptcd = '"+standard.getStandardJobCptcd()+"'"+
							" and m.bgrp = '"+contrato.getBgrp()+"'"+
							" and substring(m.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
							" and (m.beqmsn = '"+standard.getIdContrato().getBeginRanger()+"' or m.beqmsn = '"+standard.getIdContrato().getEndRanger()+"') "+
							complemento + complementoSigla+
					" and m.bectyc in (select pdp.descricao from ren_pmp_desconto_pdp pdp)");
					BigDecimal custoPecas = (BigDecimal)query.getSingleResult();
					if(custoPecas != null){
						Double custoTotal = custoPecas.doubleValue();
						//custoNordesteTotal =  custoPecas.doubleValue() + ((custoPecas.doubleValue() * custoNordeste.doubleValue())/100);//aplicar custo nordeste
						custoTotal = custoTotal - ((custoTotal * (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getDescPdp().doubleValue()))/100);//desconto PDP
						if(contrato.getIsSpot() == null || contrato.getIsSpot().equals("N")){
							custoTotal = custoTotal - ((custoTotal * (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getDescPdp().doubleValue()))/100);//desconto PDP
						}else if(contrato.getIsSpot() != null && contrato.getIsSpot().equals("S")){
							custoTotal = custoTotal - ((custoTotal * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getPdpSpot().doubleValue())/100);
						}
						custoManutencao = custoManutencao + custoTotal;
					}
					//Query sem desconto pdp
					query = manager.createNativeQuery("select sum(m.unls * m.dlrqty) from ren_pmp_manutencao m"+
							" where m.cptcd = '"+standard.getStandardJobCptcd()+"'"+
							" and m.bgrp = '"+contrato.getBgrp()+"'"+
							" and substring(m.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
							" and (m.beqmsn = '"+standard.getIdContrato().getBeginRanger()+"' or m.beqmsn = '"+standard.getIdContrato().getEndRanger()+"') "+
							complemento + complementoSigla +
					" and (m.bectyc not in (select pdp.descricao from ren_pmp_desconto_pdp pdp)"+
							" or m.bectyc is null)");
					custoPecas = (BigDecimal)query.getSingleResult();
					//custoNordesteTotal = custoPecas.doubleValue() + ((custoPecas.doubleValue() * custoNordeste.doubleValue())/100);//aplicar custo nordeste	
					
					if(custoPecas != null){
						if(contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getDescPecas() != null && contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getDescPecas().doubleValue() > 0){
							Double custoTotal = custoPecas.doubleValue();
							if(contrato.getIsSpot() == null || contrato.getIsSpot().equals("N")){
								custoTotal = custoTotal - ((custoTotal * (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getDescPecas().doubleValue()))/100);//desconto PDP
							}
							custoManutencao = custoManutencao + custoTotal;
						}else{
							custoManutencao = custoManutencao + custoPecas.doubleValue();
						}
					}
					if(custoManutencao != null){
						BigDecimal custP = BigDecimal.valueOf(custoManutencao);
						standard.setCustoPecas(custP);
					}
				
//				//custo do TA
//				if(contrato.getTa().equals("S")){
//					Double taCusto = (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getTaHoras().doubleValue() * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getHhTa().doubleValue());
//					taCusto = (totalHorasManutencao.intValue() / 2000) * taCusto;// total de revisoes de 2000 horas * custo do TA
//					custoManutencao = custoManutencao + taCusto;
//				}
				
					
					//tipo manutenção
					query = manager.createNativeQuery("select TIPO_PM from ren_pmp_hora h"+
							" where h.cptcd = '"+standard.getStandardJobCptcd()+"'"+
							" and h.bgrp = '"+contrato.getBgrp()+"'"+
							" and substring(h.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
							" and (h.beqmsn = '"+standard.getIdContrato().getBeginRanger()+"' or h.beqmsn = '"+standard.getIdContrato().getEndRanger()+"') ");
					String tipoPm = (String)query.getSingleResult();	
					standard.setTipoPm(tipoPm);
				
				//total horas para a manutenção
				query = manager.createNativeQuery("select sum(cast(FRSDHR as decimal(18,2))) from ren_pmp_hora h"+
						" where h.cptcd = '"+standard.getStandardJobCptcd()+"'"+
						" and h.bgrp = '"+contrato.getBgrp()+"'"+
						" and substring(h.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
						" and (h.beqmsn = '"+standard.getIdContrato().getBeginRanger()+"' or h.beqmsn = '"+standard.getIdContrato().getEndRanger()+"')");
				BigDecimal totalHHManutencao = (BigDecimal)query.getSingleResult();
//				standard.setCustoMo((BigDecimal.valueOf(contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getHhPmp().doubleValue() * totalHHManutencao.doubleValue())).add(BigDecimal.valueOf(contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getKmPmp().doubleValue() * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getValorKmPmp().doubleValue())));
//				custoManutencao = custoManutencao + (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getHhPmp().doubleValue() * totalHHManutencao.doubleValue());//valor de hh
//				custoManutencao = custoManutencao + (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getKmPmp().doubleValue() * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getValorKmPmp().doubleValue());//valor de km vezes a quantidade de manutenções
				
				
				if(contrato.getIsSpot() != null && contrato.getIsSpot().equals("S")){
					Double valorHH = contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getHhPmp().doubleValue() * totalHHManutencao.doubleValue();
					valorHH = valorHH + ( (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getValorSpot().doubleValue() * valorHH) /100 );
					standard.setCustoMo((BigDecimal.valueOf(valorHH)).add(BigDecimal.valueOf(contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getKmPmpSpot().doubleValue() * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getValorKmPmp().doubleValue())));
					custoManutencao = custoManutencao + valorHH;//valor de hh
					custoManutencao = custoManutencao + (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getKmPmpSpot().doubleValue() * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getValorKmPmp().doubleValue());//valor de km
				}else{

					standard.setCustoMo((BigDecimal.valueOf(contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getHhPmp().doubleValue() * totalHHManutencao.doubleValue())).add(BigDecimal.valueOf(contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getKmPmp().doubleValue() * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getValorKmPmp().doubleValue())));
					custoManutencao = custoManutencao + (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getHhPmp().doubleValue() * totalHHManutencao.doubleValue());//valor de hh
					custoManutencao = custoManutencao + (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getKmPmp().doubleValue() * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getValorKmPmp().doubleValue());//valor de km
				}

				
				
				
				if(contrato.getIdTipoContrato().getSigla().equals("VPG") && (contrato.getIsSpot() == null || contrato.getIsSpot().equals("N"))){
					custoManutencao = custoManutencao - ((custoManutencao * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getDescontoPrePago().doubleValue())/100);
				}
				return custoManutencao;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private Double calcularValorRevisaoSubTributaria(PmpContrato contrato, PmpContHorasStandard standard, EntityManager manager){
		Double custoManutencao = 0d;
		BigDecimal custoPecas = BigDecimal.ZERO;
		
		try{
			manager = JpaUtil.getInstance();
			
				Query query = manager.createNativeQuery("select sum(m.VLSUB) from REN_PMP_PECAS_CONFIG_OPERACIONAL m"+
						" where ID_CONTRATO = "+contrato.getId()+
						" and m.cptcd = '"+standard.getStandardJobCptcd()+"'"+
				" and m.bectyc in (select pdp.descricao from ren_pmp_desconto_pdp pdp)");
				custoPecas = (BigDecimal)query.getSingleResult();
//				if(custoPecas != null){
//					Double custoPecasTotal = custoPecas.doubleValue();
//					custoPecasTotal = custoPecasTotal - ((custoPecasTotal * (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getDescPdp().doubleValue()))/100);//desconto PDP
//					custoManutencao = custoManutencao + custoPecasTotal;
//				}
				
				if(custoPecas != null){
					Double custoPecasTotal = custoPecas.doubleValue();
					if(contrato.getIsSpot() == null || contrato.getIsSpot().equals("N")){
						custoPecasTotal = custoPecasTotal - ((custoPecasTotal * (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getDescPdp().doubleValue()))/100);//desconto PDP
					}else if(contrato.getIsSpot() != null && contrato.getIsSpot().equals("S")){
						custoPecasTotal = custoPecasTotal - ((custoPecasTotal * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getPdpSpot().doubleValue())/100);
					}
					custoManutencao = custoManutencao + custoPecasTotal;
				}
				
				
				//Query sem desconto pdp
				query = manager.createNativeQuery("select sum(m.VLSUB) from REN_PMP_PECAS_CONFIG_OPERACIONAL m"+
						" where ID_CONTRATO = "+contrato.getId()+
						" and m.cptcd = '"+standard.getStandardJobCptcd()+"'"+
						" and (m.bectyc not in (select pdp.descricao from ren_pmp_desconto_pdp pdp)"+
				" or m.bectyc is null)");
				custoPecas = (BigDecimal)query.getSingleResult();
				custoManutencao = custoManutencao + custoPecas.doubleValue();
			
				//tipo manutenção
				query = manager.createNativeQuery("select TIPO_PM from ren_pmp_hora h"+
						" where h.cptcd = '"+standard.getStandardJobCptcd()+"'"+
						" and h.bgrp = '"+contrato.getBgrp()+"'"+
						" and substring(h.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
						" and (h.beqmsn = '"+standard.getIdContrato().getBeginRanger()+"' or h.beqmsn = '"+standard.getIdContrato().getEndRanger()+"')");
				String tipoPm = (String)query.getSingleResult();	
				standard.setTipoPm(tipoPm);
				
				
			//total horas para a manutenção
			query = manager.createNativeQuery("select sum(cast(FRSDHR as decimal(18,2))) from ren_pmp_hora h"+
					" where h.cptcd  = '"+standard.getStandardJobCptcd()+"'"+
					" and h.bgrp = '"+contrato.getBgrp()+"'"+
					" and substring(h.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
					" and (h.beqmsn = '"+standard.getIdContrato().getBeginRanger()+"' or h.beqmsn = '"+standard.getIdContrato().getEndRanger()+"')");
			BigDecimal totalHHManutencao = (BigDecimal)query.getSingleResult();
//			custoManutencao = custoManutencao + (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getHhPmp().doubleValue() * totalHHManutencao.doubleValue());//valor de hh
//			custoManutencao = custoManutencao + (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getKmPmp().doubleValue() * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getValorKmPmp().doubleValue());//valor de km vezes a quantidade de manutenções
//			
//			if(contrato.getIdTipoContrato().getSigla().equals("VPG")){
//				custoManutencao = custoManutencao - ((custoManutencao * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getDescontoPrePago().doubleValue())/100);
//			}
			
			if(contrato.getIsSpot() != null && contrato.getIsSpot().equals("S")){
				Double valorHH = contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getHhPmp().doubleValue() * totalHHManutencao.doubleValue();
				valorHH = valorHH + ( (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getValorSpot().doubleValue() * valorHH) /100 );
				//standard.setCustoMo((BigDecimal.valueOf(valorHH)).add(BigDecimal.valueOf(contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getKmPmp().doubleValue() * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getValorKmPmp().doubleValue())));
				custoManutencao = custoManutencao + valorHH;//valor de hh
				custoManutencao = custoManutencao + (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getKmPmpSpot().doubleValue() * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getValorKmPmp().doubleValue());//valor de km vezes a quantidade de manutenções
			}else{
				custoManutencao = custoManutencao + (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getHhPmp().doubleValue() * totalHHManutencao.doubleValue());//valor de hh
				custoManutencao = custoManutencao + (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getKmPmp().doubleValue() * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getValorKmPmp().doubleValue());//valor de km vezes a quantidade de manutenções
			}
			if(contrato.getIdTipoContrato().getSigla().equals("VPG") && (contrato.getIsSpot() == null || contrato.getIsSpot().equals("N"))){
				custoManutencao = custoManutencao - ((custoManutencao * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getDescontoPrePago().doubleValue())/100);
			}
			
			
			return custoManutencao;		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void insertManutMeses(List<ConfigManutencaoMesesBean> beanList, PmpContrato contrato, EntityManager manager){
		Query query = manager.createNativeQuery("DELETE FROM ren_pmp_cont_meses_standard WHERE id_contrato = " + contrato.getId().longValue());
		query.executeUpdate();
		
		for (int i = 0; i < beanList.size(); i++) {
			ConfigManutencaoMesesBean bean = beanList.get(i);
			PmpContMesesStandard standard = new PmpContMesesStandard();

			standard.setMes(bean.getMeses());
			standard.setMesManutencao(bean.getMesManutencao());
			standard.setStandardJobCptcd(bean.getStandardJob());
			standard.setIdContrato(contrato);
			standard.setIsExecutado(bean.getIsExecutado());
			standard.setFrequencia(bean.getFrequencia());

			manager.persist(standard);
		}
	}

	public List<ContratoComercialBean> findAllContratoComercial(String nomeCliente, Long idStatusContrato, String isGerador, String contExcessao){
		EntityManager manager = null;
		List<ContratoComercialBean> result = new ArrayList<ContratoComercialBean>();
		try {
			manager = JpaUtil.getInstance();
			
			String sql = "SELECT DISTINCT c FROM PmpContrato c, PmpContHorasStandard chs " +
					" WHERE c.id = chs.idContrato AND (c.razaoSocial LIKE '" + nomeCliente.toUpperCase() + "%' or c.numeroSerie LIKE '" + nomeCliente.toUpperCase() + "%' or c.modelo LIKE '" + nomeCliente.toUpperCase() + "%') AND c.idFuncionario =:idFuncionario AND c.idTipoContrato.sigla NOT IN ('REN', 'CAN') and c.contExcessao =:contExcessao and (c.isSpot <> 'S' or c.isSpot is null)";
			if(usuarioBean.getIsAdm()){
				sql = " SELECT DISTINCT c FROM PmpContrato c, PmpContHorasStandard chs " +
						" WHERE c.id = chs.idContrato AND (c.razaoSocial LIKE '" + nomeCliente.toUpperCase() + "%' or c.numeroSerie LIKE '" + nomeCliente.toUpperCase() + "%' or c.modelo LIKE '" + nomeCliente.toUpperCase() + "%') AND c.idTipoContrato.sigla NOT IN ('REN', 'CAN') and c.contExcessao =:contExcessao and (c.isSpot <> 'S' or c.isSpot is null)";
			}
			if(idStatusContrato > 0){
				sql += " AND c.idStatusContrato.id ="+idStatusContrato;
			}
			Query query = manager.createQuery(sql);
			if(!usuarioBean.getIsAdm()){
				query.setParameter("idFuncionario", ID_FUNCIONARIO);
			}
			query.setParameter("contExcessao", contExcessao);
			List<PmpContrato> contratoList = query.getResultList();
			TracaoBusiness business = new TracaoBusiness();
			for (int i = 0; i < contratoList.size(); i++) {
				PmpContrato contrato = contratoList.get(i);
				ContratoComercialBean bean = new ContratoComercialBean();
				query = manager.createQuery("from PmpConfigOperacional where idContrato.id =:idContrato");
				query.setParameter("idContrato", contrato.getId());
				if(query.getResultList().size() > 0){
					PmpConfigOperacional configOperacional = (PmpConfigOperacional)query.getSingleResult();
					bean.setIdFilialDestino(configOperacional.getFilial());
				}
				bean.fromBean(contrato, usuarioBean);
				bean.setNumeroSerie(contrato.getNumeroSerie());
				bean.setModeloList(this.findAllModelosContrato(contrato.getContExcessao(), contrato.getIdConfigManutencao().getIdFamilia().getId(), isGerador));
				bean.setPrefixoList(this.findAllPrefixosContrato(contrato.getModelo(),contrato.getIdConfigManutencao().getContExcessao()));
				bean.setRangerList(this.findAllRangerContrato(contrato.getModelo(), contrato.getPrefixo(), contrato.getIdConfigManutencao().getContExcessao(), contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getId()));
				bean.setTipoCustomizacaoList(this.findAllTipoCustomizacao(contrato.getModelo(), contrato.getIdFamilia(), contrato.getId()));
				bean.setPrecoEspecialList(this.findAllPrecoEspecial(contrato.getModelo(), contrato.getPrefixo(),contrato.getContExcessao(),contrato.getIdFamilia()));
				bean.setFuncionarioIndicado(contrato.getNomeIndicacao());
				bean.setMatriculaIndicado(contrato.getIdFuncionarioIndicacao());
				
				if(contrato.getIdConfigTracao() != null){
					query = manager.createNativeQuery("select ID_ARV from REN_PMP_ARV_INSPECAO where DESCRICAO =:modelo");
					query.setParameter("modelo", contrato.getModelo());
					BigDecimal idModelo = (BigDecimal)query.getResultList().get(0);
					bean.setConfigTracaoList(business.findAllConfigTracao(idModelo.longValue()));
				}
				query = manager.createNativeQuery("select hm.horas ,hm.horas_manutencao, hm.standard_job_cptcd, 'true', hm.is_executado, hm.horas_revisao from ren_pmp_cont_horas_standard hm"+
						" where hm.id_contrato = "+contrato.getId()+
						" union"+
						" select h.horas,h.horas, h.standard_job_cptcd, 'false', 'N', h.horas_revisao from  ren_pmp_config_horas_standard h " +
						" where h.id_config_manutencao ="+contrato.getIdConfigManutencao().getId()+
						" and h.horas not in (select hm.horas_manutencao from ren_pmp_cont_horas_standard hm"+
						" where hm.id_contrato =  "+contrato.getId()+")"+
						" union"+
						" select h.horas,h.horas + 12000, h.standard_job_cptcd, 'false', 'N', h.horas_revisao from  ren_pmp_config_horas_standard h " +
						" where h.id_config_manutencao ="+contrato.getIdConfigManutencao().getId()+
						" and (h.horas + 12000) not in (select hm.horas_manutencao from ren_pmp_cont_horas_standard hm"+
						" where hm.id_contrato =  "+contrato.getId()+")" +
						" and h.horas <> 500" +
						" and h.horas <> 250" +
						" and h.horas <> 100" +
						" union"+
						" select h.horas,h.horas + 24000, h.standard_job_cptcd, 'false', 'N', h.horas_revisao from  ren_pmp_config_horas_standard h " +
						" where h.id_config_manutencao ="+contrato.getIdConfigManutencao().getId()+
						" and (h.horas + 24000) not in (select hm.horas_manutencao from ren_pmp_cont_horas_standard hm"+
						" where hm.id_contrato =  "+contrato.getId()+")" +
						" and h.horas <> 500" +
						" and h.horas <> 250" +
						" and h.horas <> 100" +
						" union"+
						" select h.horas,h.horas + 36000, h.standard_job_cptcd, 'false', 'N', h.horas_revisao from  ren_pmp_config_horas_standard h " +
						" where h.id_config_manutencao ="+contrato.getIdConfigManutencao().getId()+
						" and (h.horas + 36000) not in (select hm.horas_manutencao from ren_pmp_cont_horas_standard hm"+
						" where hm.id_contrato =  "+contrato.getId()+")" +
						" and h.horas <> 500" +
						" and h.horas <> 250" +
						" and h.horas <> 100" +
						" order by 2 ");
				
				
			   List<ConfigManutencaoHorasBean> horasList = new ArrayList<ConfigManutencaoHorasBean>();
			   List<Object[]> manutHorasList = query.getResultList();
			   Long frequencia = 0l;
			   for (int j = 0; j < manutHorasList.size(); j++){ 
				   Object[] objects  = (Object[]) manutHorasList.get(j);
				   if(j == 0){
					   Object[] objectsProximo  = (Object[]) manutHorasList.get(j+1);
					   frequencia = ((BigDecimal)objectsProximo[1]).longValue() - ((BigDecimal)objects[1]).longValue();
				   }else{
					   Object[] objectsPenultimo  = (Object[]) manutHorasList.get(manutHorasList.size()-2);
					   Object[] objectsUltimo  = (Object[]) manutHorasList.get(manutHorasList.size()-1);
					   frequencia = ((BigDecimal)objectsUltimo[1]).longValue() - ((BigDecimal)objectsPenultimo[1]).longValue();
				   }
				   if(((BigDecimal)objects[1]).longValue() == 12000 || ((BigDecimal)objects[1]).longValue() == 24000 || ((BigDecimal)objects[1]).longValue() == 36000){
					   int k = j;
					   Object[] objectsProximo  = (Object[]) manutHorasList.get(j+1);
					   while(((BigDecimal)objectsProximo[1]).longValue() != (((BigDecimal)objects[1]).longValue() + frequencia)){
						   manutHorasList.remove(k+1);
						   objectsProximo  = (Object[]) manutHorasList.get(k+1);
					   }
					}
				   ConfigManutencaoHorasBean horasBean = new ConfigManutencaoHorasBean();
				   horasBean.setHoras(((BigDecimal)objects[0]).longValue());
				   horasBean.setHorasManutencao(((BigDecimal)objects[1]).longValue());
				   horasBean.setStandardJob((String)objects[2]);
				   horasBean.setIsSelected(Boolean.valueOf((String)objects[3]));
				   horasBean.setIsExecutado((String)objects[4]);
				   horasBean.setFrequencia(frequencia);
				   if(objects[5] != null){
					   horasBean.setHorasRevisao(((BigDecimal)objects[5]).longValue());
				   }
				   horasList.add(horasBean);
			   }
			   Collections.sort(horasList);
			   bean.setConfigManutencaoHorasBeanList(horasList);
			   result.add(bean);
			}			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		
		return result;
	}

	
	public List<ContratoComercialBean> findAllContratoComercialSpot(String nomeCliente, Long idStatusContrato, String isGerador, String contExcessao){
		EntityManager manager = null;
		List<ContratoComercialBean> result = new ArrayList<ContratoComercialBean>();
		try {
			manager = JpaUtil.getInstance();
			
			String sql = "SELECT DISTINCT c FROM PmpContrato c, PmpContHorasStandard chs " +
					" WHERE c.id = chs.idContrato AND (c.razaoSocial LIKE '" + nomeCliente.toUpperCase() + "%' or c.numeroSerie LIKE '" + nomeCliente.toUpperCase() + "%' or c.modelo LIKE '" + nomeCliente.toUpperCase() + "%' or c.numeroContrato LIKE '" + nomeCliente.toUpperCase() + "%') AND c.idFuncionario =:idFuncionario AND c.idTipoContrato.sigla NOT IN ('REN', 'CAN') and c.contExcessao =:contExcessao and c.isSpot = 'S'";
			if(usuarioBean.getIsAdm()){
				sql = " SELECT DISTINCT c FROM PmpContrato c, PmpContHorasStandard chs " +
						" WHERE c.id = chs.idContrato AND (c.razaoSocial LIKE '" + nomeCliente.toUpperCase() + "%' or c.numeroSerie LIKE '" + nomeCliente.toUpperCase() + "%' or c.modelo LIKE '" + nomeCliente.toUpperCase() + "%' or c.numeroContrato LIKE '" + nomeCliente.toUpperCase() + "%') AND c.idTipoContrato.sigla NOT IN ('REN', 'CAN') and c.contExcessao =:contExcessao and c.isSpot = 'S'";
			}
			if(idStatusContrato > 0){
				sql += " AND c.idStatusContrato.id ="+idStatusContrato;
			}
			Query query = manager.createQuery(sql);
			if(!usuarioBean.getIsAdm()){
				query.setParameter("idFuncionario", ID_FUNCIONARIO);
			}
			query.setParameter("contExcessao", contExcessao);
			List<PmpContrato> contratoList = query.getResultList();
			TracaoBusiness business = new TracaoBusiness();
			for (int i = 0; i < contratoList.size(); i++) {
				PmpContrato contrato = contratoList.get(i);
				ContratoComercialBean bean = new ContratoComercialBean();
				query = manager.createQuery("from PmpConfigOperacional where idContrato.id =:idContrato");
				query.setParameter("idContrato", contrato.getId());
				if(query.getResultList().size() > 0){
					PmpConfigOperacional configOperacional = (PmpConfigOperacional)query.getSingleResult();
					bean.setIdFilialDestino(configOperacional.getFilial());
				}
				bean.fromBean(contrato, usuarioBean);
				bean.setNumeroSerie(contrato.getNumeroSerie());
				bean.setModeloList(this.findAllModelosContrato(contrato.getContExcessao(), contrato.getIdConfigManutencao().getIdFamilia().getId(), isGerador));
				bean.setPrefixoList(this.findAllPrefixosContrato(contrato.getModelo(),contrato.getIdConfigManutencao().getContExcessao()));
				bean.setRangerList(this.findAllRangerContrato(contrato.getModelo(), contrato.getPrefixo(), contrato.getIdConfigManutencao().getContExcessao(), contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getId()));
				bean.setTipoCustomizacaoList(this.findAllTipoCustomizacao(contrato.getModelo(), contrato.getIdFamilia(), contrato.getId()));
				bean.setPrecoEspecialList(this.findAllPrecoEspecial(contrato.getModelo(), contrato.getPrefixo(),contrato.getContExcessao(),contrato.getIdFamilia()));
				bean.setFuncionarioIndicado(contrato.getNomeIndicacao());
				bean.setMatriculaIndicado(contrato.getIdFuncionarioIndicacao());
				
				if(contrato.getIdConfigTracao() != null){
					query = manager.createNativeQuery("select ID_ARV from REN_PMP_ARV_INSPECAO where DESCRICAO =:modelo");
					query.setParameter("modelo", contrato.getModelo());
					BigDecimal idModelo = (BigDecimal)query.getResultList().get(0);
					bean.setConfigTracaoList(business.findAllConfigTracao(idModelo.longValue()));
				}
				query = manager.createNativeQuery("select hm.horas ,hm.horas_manutencao, hm.standard_job_cptcd, 'true', hm.is_executado, hm.horas_revisao from ren_pmp_cont_horas_standard hm"+
						" where hm.id_contrato = "+contrato.getId()+
						" union"+
						" select h.horas,h.horas, h.standard_job_cptcd, 'false', 'N', h.horas_revisao from  ren_pmp_config_horas_standard h " +
						" where h.id_config_manutencao ="+contrato.getIdConfigManutencao().getId()+
						" and h.horas not in (select hm.horas_manutencao from ren_pmp_cont_horas_standard hm"+
						" where hm.id_contrato =  "+contrato.getId()+")"+
						" union"+
						" select h.horas,h.horas + 12000, h.standard_job_cptcd, 'false', 'N', h.horas_revisao from  ren_pmp_config_horas_standard h " +
						" where h.id_config_manutencao ="+contrato.getIdConfigManutencao().getId()+
						" and (h.horas + 12000) not in (select hm.horas_manutencao from ren_pmp_cont_horas_standard hm"+
						" where hm.id_contrato =  "+contrato.getId()+")" +
						" and h.horas <> 500" +
						" and h.horas <> 250" +
						" and h.horas <> 100" +
						" union"+
						" select h.horas,h.horas + 24000, h.standard_job_cptcd, 'false', 'N', h.horas_revisao from  ren_pmp_config_horas_standard h " +
						" where h.id_config_manutencao ="+contrato.getIdConfigManutencao().getId()+
						" and (h.horas + 24000) not in (select hm.horas_manutencao from ren_pmp_cont_horas_standard hm"+
						" where hm.id_contrato =  "+contrato.getId()+")" +
						" and h.horas <> 500" +
						" and h.horas <> 250" +
						" and h.horas <> 100" +
						" union"+
						" select h.horas,h.horas + 36000, h.standard_job_cptcd, 'false', 'N', h.horas_revisao from  ren_pmp_config_horas_standard h " +
						" where h.id_config_manutencao ="+contrato.getIdConfigManutencao().getId()+
						" and (h.horas + 36000) not in (select hm.horas_manutencao from ren_pmp_cont_horas_standard hm"+
						" where hm.id_contrato =  "+contrato.getId()+")" +
						" and h.horas <> 500" +
						" and h.horas <> 250" +
						" and h.horas <> 100" +
						" order by 2 ");
				
				
			   List<ConfigManutencaoHorasBean> horasList = new ArrayList<ConfigManutencaoHorasBean>();
			   List<Object[]> manutHorasList = query.getResultList();
			   Long frequencia = 0l;
			   for (int j = 0; j < manutHorasList.size(); j++){ 
				   Object[] objects  = (Object[]) manutHorasList.get(j);
				   if(j == 0){
					   Object[] objectsProximo  = (Object[]) manutHorasList.get(j+1);
					   frequencia = ((BigDecimal)objectsProximo[1]).longValue() - ((BigDecimal)objects[1]).longValue();
				   }else{
					   Object[] objectsPenultimo  = (Object[]) manutHorasList.get(manutHorasList.size()-2);
					   Object[] objectsUltimo  = (Object[]) manutHorasList.get(manutHorasList.size()-1);
					   frequencia = ((BigDecimal)objectsUltimo[1]).longValue() - ((BigDecimal)objectsPenultimo[1]).longValue();
				   }
				   if(((BigDecimal)objects[1]).longValue() == 12000 || ((BigDecimal)objects[1]).longValue() == 24000 || ((BigDecimal)objects[1]).longValue() == 36000){
					   int k = j;
					   Object[] objectsProximo  = (Object[]) manutHorasList.get(j+1);
					   while(((BigDecimal)objectsProximo[1]).longValue() != (((BigDecimal)objects[1]).longValue() + frequencia)){
						   manutHorasList.remove(k+1);
						   objectsProximo  = (Object[]) manutHorasList.get(k+1);
					   }
					}
				   ConfigManutencaoHorasBean horasBean = new ConfigManutencaoHorasBean();
				   horasBean.setHoras(((BigDecimal)objects[0]).longValue());
				   horasBean.setHorasManutencao(((BigDecimal)objects[1]).longValue());
				   horasBean.setStandardJob((String)objects[2]);
				   horasBean.setIsSelected(Boolean.valueOf((String)objects[3]));
				   horasBean.setIsExecutado((String)objects[4]);
				   horasBean.setFrequencia(frequencia);
				   if(objects[5] != null){
					   horasBean.setHorasRevisao(((BigDecimal)objects[5]).longValue());
				   }
				   horasList.add(horasBean);
			   }
			   Collections.sort(horasList);
			   bean.setConfigManutencaoHorasBeanList(horasList);
			   result.add(bean);
			}			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		
		return result;
	}
	
	
	public ContratoComercialBean findAllContratoComercial(Long idContrato){
		EntityManager manager = null;
		ContratoComercialBean bean = new ContratoComercialBean();
		try {
			manager = JpaUtil.getInstance();
			String sql = "SELECT DISTINCT c FROM PmpContrato c " +
			" WHERE  c.id = "+idContrato;

			Query query = manager.createQuery(sql);
			PmpContrato contrato = (PmpContrato)query.getSingleResult();
			if(contrato.getIdTipoContrato().getSigla().equals("CAN")){
				query = manager.createQuery("from PmpConfigOperacional where idContrato.id =:idContrato");
				query.setParameter("idContrato", contrato.getId());
				PmpConfigOperacional operacional = (PmpConfigOperacional)query.getSingleResult();
				//bean.setIdFilialDestino(operacional.getFilial());
				bean.setNumOs(operacional.getNumOs());
			}
			if(contrato.getIdTipoContrato().getSigla().equals("REN")){
				query = manager.createQuery("from PmpConfigOperacional where idContrato.id =:idContrato");
				query.setParameter("idContrato", contrato.getId());
				PmpConfigOperacional operacional = (PmpConfigOperacional)query.getSingleResult();
				bean.setIdFilialDestino(operacional.getFilial());
			}
			
			
			bean.fromBean(contrato, usuarioBean);
			bean.setNumeroSerie(contrato.getNumeroSerie());
			bean.setModeloList(this.findAllModelosContrato(contrato.getContExcessao(), contrato.getIdConfigManutencao().getIdFamilia().getId(), "N"));
			bean.setPrefixoList(this.findAllPrefixosContrato(contrato.getModelo(),contrato.getIdConfigManutencao().getContExcessao()));
			bean.setRangerList(this.findAllRangerContrato(contrato.getModelo(), contrato.getPrefixo(), contrato.getIdConfigManutencao().getContExcessao(), contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getId()));
			bean.setTipoCustomizacaoList(this.findAllTipoCustomizacao(contrato.getModelo(), contrato.getIdFamilia(), contrato.getId()));
			bean.setFuncionarioIndicado(contrato.getNomeIndicacao());
			bean.setMatriculaIndicado(contrato.getIdFuncionarioIndicacao());

			query = manager.createNativeQuery("select hm.horas ,hm.horas_manutencao, hm.standard_job_cptcd, 'true', hm.is_executado, hm.horas_revisao, hm.FREQUENCIA from ren_pmp_cont_horas_standard hm"+
					" where hm.id_contrato = "+contrato.getId());
//					" union"+
//					" select h.horas,h.horas, h.standard_job_cptcd, 'false', 'N' from  pmp_config_horas_standard h " +
//					" where h.id_config_manutencao ="+contrato.getIdConfigManutencao().getId()+
//					" and h.horas not in (select hm.horas_manutencao from pmp_cont_horas_standard hm"+
//					" where hm.id_contrato =  "+contrato.getId()+")"+
//					" union"+
//					" select h.horas,h.horas + 12000, h.standard_job_cptcd, 'false', 'N' from  pmp_config_horas_standard h " +
//					" where h.id_config_manutencao ="+contrato.getIdConfigManutencao().getId()+
//					" and (h.horas + 12000) not in (select hm.horas_manutencao from pmp_cont_horas_standard hm"+
//					" where hm.id_contrato =  "+contrato.getId()+")" +
//			" order by 2 ");


			List<ConfigManutencaoHorasBean> horasList = new ArrayList<ConfigManutencaoHorasBean>();
			List<Object[]> manutHorasList = query.getResultList();
			Long frequencia = 0l;
			for (int j = 0; j < manutHorasList.size(); j++){ 
				Object[] objects  = (Object[]) manutHorasList.get(j);
				if(manutHorasList.size() > 1){
					if(j == 0){
						Object[] objectsProximo  = (Object[]) manutHorasList.get(j+1);
						frequencia = ((BigDecimal)objectsProximo[1]).longValue() - ((BigDecimal)objects[1]).longValue();
					}else{
						Object[] objectsPenultimo  = (Object[]) manutHorasList.get(manutHorasList.size()-2);
						Object[] objectsUltimo  = (Object[]) manutHorasList.get(manutHorasList.size()-1);
						frequencia = ((BigDecimal)objectsUltimo[1]).longValue() - ((BigDecimal)objectsPenultimo[1]).longValue();
					}
				}else{
					frequencia = ((BigDecimal)objects[6]).longValue();
				}
				ConfigManutencaoHorasBean horasBean = new ConfigManutencaoHorasBean();
				horasBean.setHoras(((BigDecimal)objects[0]).longValue());
				horasBean.setHorasManutencao(((BigDecimal)objects[1]).longValue());
				horasBean.setStandardJob((String)objects[2]);
				horasBean.setIsSelected(Boolean.valueOf((String)objects[3]));
				horasBean.setIsExecutado((String)objects[4]);
				horasBean.setFrequencia(frequencia);
				horasBean.setHorasRevisao(((BigDecimal)objects[5]).longValue());
				horasList.add(horasBean);
			}
			bean.setConfigManutencaoHorasBeanList(horasList);
			return bean;
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}

		return bean;
	}
	public ContratoComercialBean findAllContratoComercialSerie(String serie){
		EntityManager manager = null;
		ContratoComercialBean bean = new ContratoComercialBean();
		try {
			manager = JpaUtil.getInstance();
			String sql = "SELECT DISTINCT c FROM PmpContrato c, PmpContHorasStandard chs " +
					" WHERE c.id = chs.idContrato AND (c.numeroSerie = '"+serie+"' or c.idEquipamento = '"+serie+"')";
			
			Query query = manager.createQuery(sql);
			if(query.getResultList().size() == 0){
				return null;
			}
			
			PmpContrato contrato = (PmpContrato)query.getResultList().get(0);
			if(contrato.getIdTipoContrato().getSigla().equals("CAN")){
				query = manager.createQuery("from PmpConfigOperacional where idContrato.id =:idContrato");
				query.setParameter("idContrato", contrato.getId());
				PmpConfigOperacional operacional = (PmpConfigOperacional)query.getSingleResult();
				//bean.setIdFilialDestino(operacional.getFilial());
				bean.setNumOs(operacional.getNumOs());
			}
			if(contrato.getIdTipoContrato().getSigla().equals("REN")){
				query = manager.createQuery("from PmpConfigOperacional where idContrato.id =:idContrato");
				query.setParameter("idContrato", contrato.getId());
				PmpConfigOperacional operacional = (PmpConfigOperacional)query.getSingleResult();
				bean.setIdFilialDestino(operacional.getFilial());
			}
			
			
			bean.fromBean(contrato, usuarioBean);
			bean.setNumeroSerie(contrato.getNumeroSerie());
			bean.setModeloList(this.findAllModelosContrato(contrato.getContExcessao(), contrato.getIdConfigManutencao().getIdFamilia().getId(), "N"));
			bean.setPrefixoList(this.findAllPrefixosContrato(contrato.getModelo(),contrato.getIdConfigManutencao().getContExcessao()));
			bean.setRangerList(this.findAllRangerContrato(contrato.getModelo(), contrato.getPrefixo(), contrato.getIdConfigManutencao().getContExcessao(), contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getId()));
			bean.setTipoCustomizacaoList(this.findAllTipoCustomizacao(contrato.getModelo(), contrato.getIdFamilia(), contrato.getId()));
			bean.setFuncionarioIndicado(contrato.getNomeIndicacao());
			bean.setMatriculaIndicado(contrato.getIdFuncionarioIndicacao());
			
			query = manager.createNativeQuery("select hm.horas ,hm.horas_manutencao, hm.standard_job_cptcd, 'true', hm.is_executado, hm.horas_revisao, hm.FREQUENCIA from ren_pmp_cont_horas_standard hm"+
					" where hm.id_contrato = "+contrato.getId());
//					" union"+
//					" select h.horas,h.horas, h.standard_job_cptcd, 'false', 'N' from  pmp_config_horas_standard h " +
//					" where h.id_config_manutencao ="+contrato.getIdConfigManutencao().getId()+
//					" and h.horas not in (select hm.horas_manutencao from pmp_cont_horas_standard hm"+
//					" where hm.id_contrato =  "+contrato.getId()+")"+
//					" union"+
//					" select h.horas,h.horas + 12000, h.standard_job_cptcd, 'false', 'N' from  pmp_config_horas_standard h " +
//					" where h.id_config_manutencao ="+contrato.getIdConfigManutencao().getId()+
//					" and (h.horas + 12000) not in (select hm.horas_manutencao from pmp_cont_horas_standard hm"+
//					" where hm.id_contrato =  "+contrato.getId()+")" +
//			" order by 2 ");
			
			
			List<ConfigManutencaoHorasBean> horasList = new ArrayList<ConfigManutencaoHorasBean>();
			List<Object[]> manutHorasList = query.getResultList();
			Long frequencia = 0l;
			for (int j = 0; j < manutHorasList.size(); j++){ 
				Object[] objects  = (Object[]) manutHorasList.get(j);
				if(manutHorasList.size() > 1){
					if(j == 0){
						Object[] objectsProximo  = (Object[]) manutHorasList.get(j+1);
						frequencia = ((BigDecimal)objectsProximo[1]).longValue() - ((BigDecimal)objects[1]).longValue();
					}else{
						Object[] objectsPenultimo  = (Object[]) manutHorasList.get(manutHorasList.size()-2);
						Object[] objectsUltimo  = (Object[]) manutHorasList.get(manutHorasList.size()-1);
						frequencia = ((BigDecimal)objectsUltimo[1]).longValue() - ((BigDecimal)objectsPenultimo[1]).longValue();
					}
				}else{
					frequencia = ((BigDecimal)objects[6]).longValue();
				}
				ConfigManutencaoHorasBean horasBean = new ConfigManutencaoHorasBean();
				horasBean.setHoras(((BigDecimal)objects[0]).longValue());
				horasBean.setHorasManutencao(((BigDecimal)objects[1]).longValue());
				horasBean.setStandardJob((String)objects[2]);
				horasBean.setIsSelected(Boolean.valueOf((String)objects[3]));
				horasBean.setIsExecutado((String)objects[4]);
				horasBean.setFrequencia(frequencia);
				horasBean.setHorasRevisao(((BigDecimal)objects[5]).longValue());
				horasList.add(horasBean);
			}
			bean.setConfigManutencaoHorasBeanList(horasList);
			return bean;
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		
		return bean;
	}
	
	public List<ContratoComercialBean> findAllContratoComercialGerador(String nomeCliente, Long idStatusContrato, String isGerador) {
		EntityManager manager = null;
		List<ContratoComercialBean> result = new ArrayList<ContratoComercialBean>();
		try {
			manager = JpaUtil.getInstance();
			String sql = "SELECT DISTINCT c FROM PmpContrato c, PmpContMesesStandard cms " +
					" WHERE c.id = cms.idContrato AND c.razaoSocial LIKE '"+nomeCliente.toUpperCase()+"%' AND c.idFuncionario =:idFuncionario AND c.idTipoContrato.sigla NOT IN('REN', 'CAN')";
			if(usuarioBean.getIsAdm()){
				sql = "SELECT DISTINCT c FROM PmpContrato c, PmpContMesesStandard cms " +
						" WHERE c.id = cms.idContrato AND c.razaoSocial LIKE '"+nomeCliente.toUpperCase()+"%' AND c.idTipoContrato.sigla NOT IN ('REN', 'CAN')";
			}
			if(idStatusContrato > 0){
				sql += " and idStatusContrato.id ="+idStatusContrato;
			}
			Query query = manager.createQuery(sql);
			if(!usuarioBean.getIsAdm()){
				query.setParameter("idFuncionario", ID_FUNCIONARIO);
			}
			List<PmpContrato> contratoList = query.getResultList();
			for (int i = 0; i < contratoList.size(); i++) {
				PmpContrato contrato = contratoList.get(i);
				ContratoComercialBean bean = new ContratoComercialBean();
				bean.fromBean(contrato, usuarioBean);
				bean.setNumeroSerie(contrato.getNumeroSerie());
				bean.setModeloList(this.findAllModelosContrato(contrato.getContExcessao(), contrato.getIdConfigManutencao().getIdFamilia().getId(), isGerador));
				bean.setPrefixoList(this.findAllPrefixosContrato(contrato.getModelo(),contrato.getIdConfigManutencao().getContExcessao()));
				bean.setRangerList(this.findAllRangerContrato(contrato.getModelo(), contrato.getPrefixo(), contrato.getIdConfigManutencao().getContExcessao(), contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getId()));
				bean.setDistanciaGerador(Double.valueOf(contrato.getDistanciaGerador().doubleValue()));

				query = manager.createNativeQuery("SELECT cms.mes, cms.mes_manutencao, cms.standard_job_cptcd, 'true' AS selected, cms.is_executado FROM ren_pmp_cont_meses_standard cms"+
						" WHERE cms.id_contrato = " + contrato.getId() +
						" UNION "+
						" SELECT cms.horas AS mes, cms.horas AS mes_manutencao, cms.standard_job_cptcd, 'false' AS selected, 'N' FROM ren_pmp_config_horas_standard cms " +
						" WHERE cms.id_config_manutencao =" + contrato.getIdConfigManutencao().getId() +
						" AND cms.horas NOT IN (SELECT chs.mes_manutencao FROM ren_pmp_cont_meses_standard chs"+
						" WHERE chs.id_contrato = " + contrato.getId() + ")"+
						" ORDER BY 2");
				
				
				List<ConfigManutencaoMesesBean> mesesList = new ArrayList<ConfigManutencaoMesesBean>();
				List<Object[]> manutMesesList = query.getResultList();
				Long frequencia = 30l;
				for (int j = 0; j < manutMesesList.size(); j++) {
					Object[] objects  = (Object[]) manutMesesList.get(j);
//					if(j == 0){
//						Object[] objectsProximo  = (Object[]) manutMesesList.get(j+1);
//						//frequencia = ((BigDecimal)objectsProximo[1]).longValue() - ((BigDecimal)objects[1]).longValue();
//					}else{
//						Object[] objectsPenultimo  = (Object[]) manutMesesList.get(manutMesesList.size()-2);
//						Object[] objectsUltimo  = (Object[]) manutMesesList.get(manutMesesList.size()-1);
//						//frequencia = ((BigDecimal)objectsUltimo[1]).longValue() - ((BigDecimal)objectsPenultimo[1]).longValue();
//					}
					ConfigManutencaoMesesBean mesesBean = new ConfigManutencaoMesesBean();
					mesesBean.setMeses(((BigDecimal)objects[0]).longValue());
					mesesBean.setMesManutencao(((BigDecimal)objects[1]).longValue());
					mesesBean.setStandardJob((String)objects[2]);
					mesesBean.setIsSelected(Boolean.valueOf((String)objects[3]));
					mesesBean.setIsExecutado((String)objects[4]);
					mesesBean.setFrequencia(frequencia);
					mesesList.add(mesesBean);
				}
				Collections.sort(mesesList);
				bean.setConfigManutencaoMesesBeanList(mesesList);
				result.add(bean);
			}			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		
		return result;
	}
	
	public List<ContratoComercialBean> findAllContratoComercialAVM(String nomeCliente){
		EntityManager manager = null;
		List<ContratoComercialBean> result = new ArrayList<ContratoComercialBean>();
		try {
			manager = JpaUtil.getInstance();
			//String sql = "FROM PmpContrato WHERE razaoSocial LIKE'"+nomeCliente.toUpperCase()+"%'  AND idFuncionario =:idFuncionario and idTipoContrato.sigla NOT IN('REN', 'CAN')";
			//if(usuarioBean.getIsAdm()){
			String sql = "FROM PmpContrato WHERE razaoSocial LIKE'"+nomeCliente.toUpperCase()+"%' AND idTipoContrato.sigla NOT IN ('REN', 'CAN')";
			//}
			
				sql += " AND idTipoContrato.id in (SELECT id FROM PmpTipoContrato WHERE (sigla = 'VEPM' or sigla = 'CON'))";
			
			Query query = manager.createQuery(sql);
/*			if(!usuarioBean.getIsAdm()){
				query.setParameter("idFuncionario", ID_FUNCIONARIO);
			}*/
			List<PmpContrato> contratoList = query.getResultList();
			for (int i = 0; i < contratoList.size(); i++) {
				PmpContrato contrato = contratoList.get(i);
				ContratoComercialBean bean = new ContratoComercialBean();
				
				
				query = manager.createNativeQuery("select f.eplsnm from tw_funcionario f where f.epidno = '"+contrato.getIdFuncionario()+"'"); 
				
				bean.setNomeFuncionario((String) query.getSingleResult());
				
				bean.fromBean(contrato, usuarioBean);
				bean.setNumeroSerie(contrato.getNumeroSerie());
				bean.setModeloList(this.findAllModelosContrato(contrato.getContExcessao(), contrato.getIdConfigManutencao().getIdFamilia().getId(), null));
				bean.setPrefixoList(this.findAllPrefixosContrato(contrato.getModelo(),contrato.getIdConfigManutencao().getContExcessao()));
				bean.setRangerList(this.findAllRangerContrato(contrato.getModelo(), contrato.getPrefixo(), contrato.getIdConfigManutencao().getContExcessao(), contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getId()));
				
				query = manager.createNativeQuery("select hm.horas ,hm.horas_manutencao, hm.standard_job_cptcd, 'true', hm.is_executado from ren_pmp_cont_horas_standard hm"+
						" where hm.id_contrato = "+contrato.getId()+
						" union"+
						" select h.horas,h.horas, h.standard_job_cptcd, 'false', 'N' from  ren_pmp_config_horas_standard h " +
						" where h.id_config_manutencao ="+contrato.getIdConfigManutencao().getId()+
						" and h.horas not in (select hm.horas_manutencao from ren_pmp_cont_horas_standard hm"+
						" where hm.id_contrato =  "+contrato.getId()+")"+
						" union"+
						" select h.horas,h.horas + 12000, h.standard_job_cptcd, 'false', 'N' from  ren_pmp_config_horas_standard h " +
						" where h.id_config_manutencao ="+contrato.getIdConfigManutencao().getId()+
						" and (h.horas + 12000) not in (select hm.horas_manutencao from ren_pmp_cont_horas_standard hm"+
						" where hm.id_contrato =  "+contrato.getId()+")" +
				" order by 2 ");	
				
				
				List<ConfigManutencaoHorasBean> horasList = new ArrayList<ConfigManutencaoHorasBean>();
				List<Object[]> manutHorasList = query.getResultList();
				Long frequencia = 0l;
				for (int j = 0; j < manutHorasList.size(); j++){ 
					Object[] objects  = (Object[]) manutHorasList.get(j);
					if(j == 0){
						Object[] objectsProximo  = (Object[]) manutHorasList.get(j+1);
						frequencia = ((BigDecimal)objectsProximo[1]).longValue() - ((BigDecimal)objects[1]).longValue();
					}else{
						Object[] objectsPenultimo  = (Object[]) manutHorasList.get(manutHorasList.size()-2);
						Object[] objectsUltimo  = (Object[]) manutHorasList.get(manutHorasList.size()-1);
						frequencia = ((BigDecimal)objectsUltimo[1]).longValue() - ((BigDecimal)objectsPenultimo[1]).longValue();
					}
					ConfigManutencaoHorasBean horasBean = new ConfigManutencaoHorasBean();
					horasBean.setHoras(((BigDecimal)objects[0]).longValue());
					horasBean.setHorasManutencao(((BigDecimal)objects[1]).longValue());
					horasBean.setStandardJob((String)objects[2]);
					horasBean.setIsSelected(Boolean.valueOf((String)objects[3]));
					horasBean.setIsExecutado((String)objects[4]);
					horasBean.setFrequencia(frequencia);
					horasList.add(horasBean);
				}
				Collections.sort(horasList);
				bean.setConfigManutencaoHorasBeanList(horasList);
				result.add(bean);
			}			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		
		return result;
	}
	
	public List<ContratoComercialBean> findAllContratoComercialRental(String nomeCliente, Long idStatusContrato, String contExcessao){
		EntityManager manager = null;
		List<ContratoComercialBean> result = new ArrayList<ContratoComercialBean>();
		try {
			manager = JpaUtil.getInstance();
			String sql = "From PmpContrato where (razaoSocial LIKE '" + nomeCliente.toUpperCase() + "%' or numeroSerie LIKE '" + nomeCliente.toUpperCase() + "%' or modelo LIKE '" + nomeCliente.toUpperCase() + "%') and idTipoContrato.sigla = 'REN' and filial =:filial and contExcessao =:contExcessao";
			if(idStatusContrato > 0){
				sql += " and idStatusContrato.id ="+idStatusContrato;
			}
			Query query = manager.createQuery(sql);
			query.setParameter("filial", Integer.valueOf(usuarioBean.getFilial()));
			query.setParameter("contExcessao", contExcessao);
			List<PmpContrato> contratoList = query.getResultList();
			TracaoBusiness business = new TracaoBusiness();
			for (int i = 0; i < contratoList.size(); i++) {
				PmpContrato contrato = contratoList.get(i);
				ContratoComercialBean bean = new ContratoComercialBean();
				if(contrato.getIdTipoContrato().getSigla().equals("REN")){
					query = manager.createNativeQuery("select id from ren_Pmp_Config_Operacional where id_Contrato =:idContrato");
					query.setParameter("idContrato", contrato.getId());
					if(query.getResultList().size() > 0){
						BigDecimal idConfigOperacional = (BigDecimal)query.getSingleResult();
						PmpConfigOperacional configOperacional = manager.find(PmpConfigOperacional.class, idConfigOperacional.longValue());
						bean.setIdFilialDestino(configOperacional.getFilial());
					}
				}
				bean.fromBean(contrato, usuarioBean);
				bean.setNumeroSerie(contrato.getNumeroSerie());
				bean.setModeloList(this.findAllModelosContrato(contrato.getContExcessao(), contrato.getIdConfigManutencao().getIdFamilia().getId(), "N"));
				bean.setPrefixoList(this.findAllPrefixosContrato(contrato.getModelo(),contrato.getIdConfigManutencao().getContExcessao()));
				bean.setRangerList(this.findAllRangerContrato(contrato.getModelo(), contrato.getPrefixo(), contrato.getIdConfigManutencao().getContExcessao(), contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getId()));
				bean.setTipoCustomizacaoList(this.findAllTipoCustomizacao(contrato.getModelo(), contrato.getIdFamilia(), contrato.getId()));
				if(contrato.getIdConfigTracao() != null){
					query = manager.createNativeQuery("select ID_ARV from REN_PMP_ARV_INSPECAO where DESCRICAO =:modelo");
					query.setParameter("modelo", contrato.getModelo());
					BigDecimal idModelo = (BigDecimal)query.getResultList().get(0);
					bean.setConfigTracaoList(business.findAllConfigTracao(idModelo.longValue()));
				}
				
				query = manager.createNativeQuery("select hm.horas ,hm.horas_manutencao, hm.standard_job_cptcd, 'true', hm.is_executado, hm.horas_revisao from ren_pmp_cont_horas_standard hm"+
						" where hm.id_contrato = "+contrato.getId()+
						" union"+
						" select h.horas,h.horas, h.standard_job_cptcd, 'false', 'N', h.horas_revisao from  ren_pmp_config_horas_standard h " +
						" where h.id_config_manutencao ="+contrato.getIdConfigManutencao().getId()+
						" and h.horas not in (select hm.horas_manutencao from ren_pmp_cont_horas_standard hm"+
						" where hm.id_contrato =  "+contrato.getId()+")"+
						" union"+
						" select h.horas,h.horas + 12000, h.standard_job_cptcd, 'false', 'N', h.horas_revisao from  ren_pmp_config_horas_standard h " +
						" where h.id_config_manutencao ="+contrato.getIdConfigManutencao().getId()+
						" and (h.horas + 12000) not in (select hm.horas_manutencao from ren_pmp_cont_horas_standard hm"+
						" where hm.id_contrato =  "+contrato.getId()+")" +
						" and h.horas <> 500" +
						" and h.horas <> 250" +
						" and h.horas <> 100" +
						" union"+
						" select h.horas,h.horas + 24000, h.standard_job_cptcd, 'false', 'N', h.horas_revisao from  ren_pmp_config_horas_standard h " +
						" where h.id_config_manutencao ="+contrato.getIdConfigManutencao().getId()+
						" and (h.horas + 24000) not in (select hm.horas_manutencao from ren_pmp_cont_horas_standard hm"+
						" where hm.id_contrato =  "+contrato.getId()+")" +
						" and h.horas <> 500" +
						" and h.horas <> 250" +
						" and h.horas <> 100" +
						" union"+
						" select h.horas,h.horas + 36000, h.standard_job_cptcd, 'false', 'N', h.horas_revisao from  ren_pmp_config_horas_standard h " +
						" where h.id_config_manutencao ="+contrato.getIdConfigManutencao().getId()+
						" and (h.horas + 36000) not in (select hm.horas_manutencao from ren_pmp_cont_horas_standard hm"+
						" where hm.id_contrato =  "+contrato.getId()+")" +
						" and h.horas <> 500" +
						" and h.horas <> 250" +
						" and h.horas <> 100" +
						" order by 2 ");	
				
				
				List<ConfigManutencaoHorasBean> horasList = new ArrayList<ConfigManutencaoHorasBean>();
				List<Object[]> manutHorasList = query.getResultList();
				Long frequencia = 0l;
				for (int j = 0; j < manutHorasList.size(); j++){ 
					Object[] objects  = (Object[]) manutHorasList.get(j);
					if(j == 0){
						Object[] objectsProximo  = (Object[]) manutHorasList.get(j+1);
						frequencia = ((BigDecimal)objectsProximo[1]).longValue() - ((BigDecimal)objects[1]).longValue();
					 }else{
						   Object[] objectsPenultimo  = (Object[]) manutHorasList.get(manutHorasList.size()-2);
						   Object[] objectsUltimo  = (Object[]) manutHorasList.get(manutHorasList.size()-1);
						   frequencia = ((BigDecimal)objectsUltimo[1]).longValue() - ((BigDecimal)objectsPenultimo[1]).longValue();
					 }
					if(((BigDecimal)objects[1]).longValue() == 12000 || ((BigDecimal)objects[1]).longValue() == 24000 || ((BigDecimal)objects[1]).longValue() == 36000){
						   int k = j;
						   Object[] objectsProximo  = (Object[]) manutHorasList.get(j+1);
						   while(((BigDecimal)objectsProximo[1]).longValue() != (((BigDecimal)objects[1]).longValue() + frequencia)){
							   manutHorasList.remove(k+1);
							   objectsProximo  = (Object[]) manutHorasList.get(k+1);
						   }
						}
//					if(((BigDecimal)objects[1]).longValue() == 24500){
//						System.out.println(((BigDecimal)objects[1]).longValue());
//					}
					ConfigManutencaoHorasBean horasBean = new ConfigManutencaoHorasBean();
					horasBean.setHoras(((BigDecimal)objects[0]).longValue());
					horasBean.setHorasManutencao(((BigDecimal)objects[1]).longValue());
					horasBean.setStandardJob((String)objects[2]);
					horasBean.setIsSelected(Boolean.valueOf((String)objects[3]));
					horasBean.setIsExecutado((String)objects[4]);
					horasBean.setFrequencia(frequencia);
					if(objects[5] != null){
						horasBean.setHorasRevisao(((BigDecimal)objects[5]).longValue());
					}
					horasList.add(horasBean);
				}
				Collections.sort(horasList);
				bean.setConfigManutencaoHorasBeanList(horasList);
				result.add(bean);
			}			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		
		return result;
	}
	
	public List<ContratoComercialBean> findAllContratoComercialAntigo(String nomeCliente, Long idStatusContrato, String contExcessao){
		EntityManager manager = null;
		List<ContratoComercialBean> result = new ArrayList<ContratoComercialBean>();
		try {
			manager = JpaUtil.getInstance();
			String sql = "From PmpContrato c, PmpConfigOperacional pco  where (c.razaoSocial LIKE '" + nomeCliente.toUpperCase() + "%' or c.numeroSerie LIKE '" + nomeCliente.toUpperCase() + "%' or c.modelo LIKE '" + nomeCliente.toUpperCase() + "%') and c.id = pco.idContrato.id  and c.filial =:filial and c.idTipoContrato.sigla = 'CAN' and c.contExcessao =:contExcessao";
			if(idStatusContrato > 0){
				sql += " and idStatusContrato.id ="+idStatusContrato;
			}
			Query query = manager.createQuery(sql);
			query.setParameter("filial", Integer.valueOf(usuarioBean.getFilial()));
			query.setParameter("contExcessao", contExcessao);
			List<Object[]> contratoList = query.getResultList();
			TracaoBusiness business = new TracaoBusiness();
			for (int i = 0; i < contratoList.size(); i++) {
				Object [] pair = (Object[])contratoList.get(i);
				PmpContrato contrato = (PmpContrato)pair[0];
				PmpConfigOperacional cop = (PmpConfigOperacional)pair[1];
				ContratoComercialBean bean = new ContratoComercialBean();
				
				
//				query = manager.createQuery("from PmpConfigOperacional where idContrato.id =:idContrato");
//				query.setParameter("idContrato", contrato.getId());
//				PmpConfigOperacional configOperacional = (PmpConfigOperacional)query.getSingleResult();
//				bean.setIdFilialDestino(configOperacional.getFilial());
				
				bean.fromBean(contrato, usuarioBean);
				bean.setNumOs(cop.getNumOs());
				bean.setNumeroSerie(contrato.getNumeroSerie());
				bean.setModeloList(this.findAllModelosContrato(contrato.getContExcessao(), contrato.getIdConfigManutencao().getIdFamilia().getId(), "N"));
				bean.setPrefixoList(this.findAllPrefixosContrato(contrato.getModelo(),contrato.getIdConfigManutencao().getContExcessao()));
				bean.setRangerList(this.findAllRangerContrato(contrato.getModelo(), contrato.getPrefixo(), contrato.getIdConfigManutencao().getContExcessao(), contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getId()));
				bean.setTipoCustomizacaoList(this.findAllTipoCustomizacao(contrato.getModelo(), contrato.getIdFamilia(), contrato.getId()));
				
				if(contrato.getIdConfigTracao() != null){
					query = manager.createNativeQuery("select ID_ARV from REN_PMP_ARV_INSPECAO where DESCRICAO =:modelo");
					query.setParameter("modelo", contrato.getModelo());
					BigDecimal idModelo = (BigDecimal)query.getResultList().get(0);
					bean.setConfigTracaoList(business.findAllConfigTracao(idModelo.longValue()));
				}
				
				query = manager.createNativeQuery("select hm.horas ,hm.horas_manutencao, hm.standard_job_cptcd, 'true', hm.is_executado, hm.horas_revisao from ren_pmp_cont_horas_standard hm"+
						" where hm.id_contrato = "+contrato.getId()+
						" union"+
						" select h.horas,h.horas, h.standard_job_cptcd, 'false', 'N', h.horas_revisao from  ren_pmp_config_horas_standard h " +
						" where h.id_config_manutencao ="+contrato.getIdConfigManutencao().getId()+
						" and h.horas not in (select hm.horas_manutencao from ren_pmp_cont_horas_standard hm"+
						" where hm.id_contrato =  "+contrato.getId()+")"+
						" union"+
						" select h.horas,h.horas + 12000, h.standard_job_cptcd, 'false', 'N', h.horas_revisao from  ren_pmp_config_horas_standard h " +
						" where h.id_config_manutencao ="+contrato.getIdConfigManutencao().getId()+
						" and (h.horas + 12000) not in (select hm.horas_manutencao from ren_pmp_cont_horas_standard hm"+
						" where hm.id_contrato =  "+contrato.getId()+")" +
						" and h.horas <> 500" +
						" and h.horas <> 250" +
						" and h.horas <> 100" +
						" union"+
						" select h.horas,h.horas + 24000, h.standard_job_cptcd, 'false', 'N', h.horas_revisao from  ren_pmp_config_horas_standard h " +
						" where h.id_config_manutencao ="+contrato.getIdConfigManutencao().getId()+
						" and (h.horas + 24000) not in (select hm.horas_manutencao from ren_pmp_cont_horas_standard hm"+
						" where hm.id_contrato =  "+contrato.getId()+")" +
						" and h.horas <> 500" +
						" and h.horas <> 250" +
						" and h.horas <> 100" +
						" union"+
						" select h.horas,h.horas + 36000, h.standard_job_cptcd, 'false', 'N', h.horas_revisao from  ren_pmp_config_horas_standard h " +
						" where h.id_config_manutencao ="+contrato.getIdConfigManutencao().getId()+
						" and (h.horas + 36000) not in (select hm.horas_manutencao from ren_pmp_cont_horas_standard hm"+
						" where hm.id_contrato =  "+contrato.getId()+")" +
						" and h.horas <> 500" +
						" and h.horas <> 250" +
						" and h.horas <> 100" +
						" order by 2 ");	
				
				
				List<ConfigManutencaoHorasBean> horasList = new ArrayList<ConfigManutencaoHorasBean>();
				List<Object[]> manutHorasList = query.getResultList();
				Long frequencia = 0l;
				for (int j = 0; j < manutHorasList.size(); j++){ 
					Object[] objects  = (Object[]) manutHorasList.get(j);
					if(j == 0){
						Object[] objectsProximo  = (Object[]) manutHorasList.get(j+1);
						frequencia = ((BigDecimal)objectsProximo[1]).longValue() - ((BigDecimal)objects[1]).longValue();
					 }else{
						   Object[] objectsPenultimo  = (Object[]) manutHorasList.get(manutHorasList.size()-2);
						   Object[] objectsUltimo  = (Object[]) manutHorasList.get(manutHorasList.size()-1);
						   frequencia = ((BigDecimal)objectsUltimo[1]).longValue() - ((BigDecimal)objectsPenultimo[1]).longValue();
					 }
					if(((BigDecimal)objects[1]).longValue() == 12000 || ((BigDecimal)objects[1]).longValue() == 24000 || ((BigDecimal)objects[1]).longValue() == 36000){
						   int k = j;
						   Object[] objectsProximo  = (Object[]) manutHorasList.get(j+1);
						   while(((BigDecimal)objectsProximo[1]).longValue() != (((BigDecimal)objects[1]).longValue() + frequencia)){
							   manutHorasList.remove(k+1);
							   objectsProximo  = (Object[]) manutHorasList.get(k+1);
						   }
						}
					ConfigManutencaoHorasBean horasBean = new ConfigManutencaoHorasBean();
					horasBean.setHoras(((BigDecimal)objects[0]).longValue());
					horasBean.setHorasManutencao(((BigDecimal)objects[1]).longValue());
					horasBean.setStandardJob((String)objects[2]);
					horasBean.setIsSelected(Boolean.valueOf((String)objects[3]));
					horasBean.setIsExecutado((String)objects[4]);
					horasBean.setFrequencia(frequencia);
					if(objects[5] != null){
						horasBean.setHorasRevisao(((BigDecimal)objects[5]).longValue());
					}
					horasList.add(horasBean);
				}
				Collections.sort(horasList);
				bean.setConfigManutencaoHorasBeanList(horasList);
				result.add(bean);
			}			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		
		return result;
	}

	public List<PrecoBean> findAllParcelas(Long idContrato){

		EntityManager manager = null;
		PmpContrato contrato = new PmpContrato();
		List<PrecoBean> result = new ArrayList<PrecoBean>();
		try{
			manager = JpaUtil.getInstance();
			contrato = manager.find(PmpContrato.class, idContrato);
			//Recupera o tipo de customização
			Query query = manager.createNativeQuery("select ID_TIPO_CUSTOMIZACAO from REN_PMP_CONTRATO_CUSTOMIZACAO where id_contrato =:id_contrato");
			query.setParameter("id_contrato", contrato.getId());
			List<BigDecimal> contratoCustList =  query.getResultList();
			String siglaCustomizacao = "";
			
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
			if(contrato != null){
				if(!contrato.getIdTipoContrato().getSigla().equals("REN")){
					if(contrato.getBgrp().equals("PM") || contrato.getBgrp().equals("KIT3")){
						result = this.findAllParcelasPMPKIT3(idContrato, siglaCustomizacao);
//					}else if(contrato.getBgrp().equals("KIT2")){
//						result = this.findAllParcelasKIT2(idContrato);
					}else if(contrato.getBgrp().equals("SEV")){
						result = this.findAllParcelasPMPKIT3(idContrato, siglaCustomizacao);
					}
//				}else if (!contrato.getIdTipoContrato().getSigla().equals("REN")){
//					if(contrato.getBgrp().equals("PM") || contrato.getBgrp().equals("KIT3")){
//						result = this.findAllParcelasPMPKIT3Custo(idContrato);
//					}
//					}else if(contrato.getBgrp().equals("KIT2")){
//						result = this.findAllParcelasKIT2Custo(idContrato);
//					}
				}else{
					if(contrato.getBgrp().equals("PM") || contrato.getBgrp().equals("KIT3")){
						result = this.findAllParcelasRentalCustoPMKIT3(idContrato, siglaCustomizacao);
					}
//					else if(contrato.getBgrp().equals("KIT2")){
//						result = this.findAllParcelasRentalKIT2Custo(idContrato);
//					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return result;
	}
	
	public List<PrecoBean> findAllParcelasSubstituicaoTributaria(Long idContrato){
		
		EntityManager manager = null;
		PmpContrato contrato = new PmpContrato();
		List<PrecoBean> result = new ArrayList<PrecoBean>();
		try{
			manager = JpaUtil.getInstance();
			contrato = manager.find(PmpContrato.class, idContrato);
			if(contrato != null){
				if(!contrato.getIdTipoContrato().getSigla().equals("REN")){
					if(contrato.getBgrp().equals("PM") || contrato.getBgrp().equals("KIT3")){
						result = this.findAllParcelasSubstituicaoTributariaPMPKIT3(idContrato);
//					}else if(contrato.getBgrp().equals("KIT2")){
//						result = this.findAllParcelasKIT2(idContrato);
					}else if(contrato.getBgrp().equals("SEV")){
						result = this.findAllParcelasSubstituicaoTributariaPMPKIT3(idContrato);
					}
//				}else if (!contrato.getIdTipoContrato().getSigla().equals("REN")){
//					if(contrato.getBgrp().equals("PM") || contrato.getBgrp().equals("KIT3")){
//						result = this.findAllParcelasPMPKIT3Custo(idContrato);
//					}
//					}else if(contrato.getBgrp().equals("KIT2")){
//						result = this.findAllParcelasKIT2Custo(idContrato);
//					}
				}else{
					if(contrato.getBgrp().equals("PM") || contrato.getBgrp().equals("KIT3")){
						contrato = manager.find(PmpContrato.class, idContrato);
						//Recupera o tipo de customização
						Query query = manager.createNativeQuery("select ID_TIPO_CUSTOMIZACAO from REN_PMP_CONTRATO_CUSTOMIZACAO where id_contrato =:id_contrato");
						query.setParameter("id_contrato", contrato.getId());
						List<BigDecimal> contratoCustList =  query.getResultList();
						String siglaCustomizacao = "";
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
						result = this.findAllParcelasRentalCustoPMKIT3(idContrato, siglaCustomizacao);
					}
					//					else if(contrato.getBgrp().equals("KIT2")){
					//						result = this.findAllParcelasRentalKIT2Custo(idContrato);
					//					}
				}
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
	
	public List<PrecoBean> findAllParcelasGerador(Long idContrato){
		
		EntityManager manager = null;
		PmpContrato contrato = new PmpContrato();
		List<PrecoBean> result = new ArrayList<PrecoBean>();
		try{
			manager = JpaUtil.getInstance();
			contrato = manager.find(PmpContrato.class, idContrato);
			if(contrato != null){
				if(!contrato.getIdTipoContrato().getSigla().equals("REN")){
					if(contrato.getBgrp().equals("PM") || contrato.getBgrp().equals("KIT3")){
						result = this.findAllParcelasPMPKIT3Gerador(idContrato);
//					}else if(contrato.getBgrp().equals("KIT2")){
//						result = this.findAllParcelasKIT2(idContrato);
					}else if(contrato.getBgrp().equals("SEV")){
						result = this.findAllParcelasPMPKIT3Gerador(idContrato);
					}
//				}else if (!contrato.getIdTipoContrato().getSigla().equals("REN")){
//					if(contrato.getBgrp().equals("PM") || contrato.getBgrp().equals("KIT3")){
//						result = this.findAllParcelasPMPKIT3Custo(idContrato);
//					}
//					}else if(contrato.getBgrp().equals("KIT2")){
//						result = this.findAllParcelasKIT2Custo(idContrato);
//					}
				}else{
					if(contrato.getBgrp().equals("PM") || contrato.getBgrp().equals("KIT3")){
						result = this.findAllParcelasRentalCustoPMKIT3Gerador(idContrato);
					}
//					else if(contrato.getBgrp().equals("KIT2")){
//						result = this.findAllParcelasRentalKIT2Custo(idContrato);
//					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return result;
	}

	public List<PrecoBean> findAllParcelasPMPKIT3(Long idContrato, String siglaCustomizacao){
		
		EntityManager manager = null;
		PmpContrato contrato = new PmpContrato();
		Double custoManutencao = 0d;
		Long totalHorasManutencao = 0l;
		BigDecimal custoPecas = BigDecimal.ZERO;
		BigDecimal totalHHManutencao = BigDecimal.ZERO;
		List<PrecoBean> result = new ArrayList<PrecoBean>();
		try{
			manager = JpaUtil.getInstance();
			contrato = manager.find(PmpContrato.class, idContrato);
			
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
			
			//Long frequencia = 0l;
			
				//BigDecimal custoNordeste = contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getCustoNordeste();
				Query query = manager.createQuery("From PmpContHorasStandard where idContrato.id = :id order by horasManutencao");
				query.setParameter("id", contrato.getId());
				List<PmpContHorasStandard> horasStandardList = query.getResultList();
				for (PmpContHorasStandard pmpContHorasStandard : horasStandardList) {
					totalHorasManutencao += pmpContHorasStandard.getFrequencia();
				}
				int quantidadeManutencoes = 0;
				for (PmpContHorasStandard pmpContHorasStandard : horasStandardList) {
					quantidadeManutencoes++;
					//frequencia = pmpContHorasStandard.getFrequencia();
					//Query para peças com desconto PDP
					query = manager.createNativeQuery("select sum(m.unls * m.dlrqty) from ren_pmp_manutencao m"+
							" where m.cptcd = '"+pmpContHorasStandard.getStandardJobCptcd()+"'"+
							" and m.bgrp = '"+contrato.getBgrp()+"'"+
							" and substring(m.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
							" and (m.beqmsn = '"+contrato.getBeginRanger()+"' or m.beqmsn = '"+contrato.getEndRanger()+"') "+
							complemento + complementoSigla +
					" and m.bectyc in (select pdp.descricao from ren_pmp_desconto_pdp pdp)");
					custoPecas = (BigDecimal)query.getSingleResult();
					if(custoPecas != null){
						Double custoTotal = custoPecas.doubleValue();
						//custoNordesteTotal =  custoPecas.doubleValue() + ((custoPecas.doubleValue() * custoNordeste.doubleValue())/100);//aplicar custo nordeste
						custoTotal = custoTotal - ((custoTotal * (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getDescPdp().doubleValue()))/100);//desconto PDP
						custoManutencao = custoManutencao + custoTotal;
					}
					//Query sem desconto pdp
					query = manager.createNativeQuery("select sum(m.unls * m.dlrqty) from ren_pmp_manutencao m"+
							" where m.cptcd = '"+pmpContHorasStandard.getStandardJobCptcd()+"'"+
							" and m.bgrp = '"+contrato.getBgrp()+"'"+
							" and substring(m.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
							" and (m.beqmsn = '"+contrato.getBeginRanger()+"' or m.beqmsn = '"+contrato.getEndRanger()+"') "+
							complemento + complementoSigla +
					" and (m.bectyc not in (select pdp.descricao from ren_pmp_desconto_pdp pdp)"+
							" or m.bectyc is null)");
					custoPecas = (BigDecimal)query.getSingleResult();
					//custoNordesteTotal = custoPecas.doubleValue() + ((custoPecas.doubleValue() * custoNordeste.doubleValue())/100);//aplicar custo nordeste	
					if(custoPecas != null){
						custoManutencao = custoManutencao + custoPecas.doubleValue();
					}
					query = manager.createNativeQuery("select sum(cast(FRSDHR as decimal(18,2))) from ren_pmp_hora h"+
							" where h.cptcd = '"+pmpContHorasStandard.getStandardJobCptcd()+"'"+
							" and h.bgrp = '"+contrato.getBgrp()+"'"+
							" and substring(h.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
							" and (h.beqmsn = '"+contrato.getBeginRanger()+"' or h.beqmsn = '"+contrato.getEndRanger()+"')");
					totalHHManutencao = totalHHManutencao.add((BigDecimal)query.getSingleResult());
					
					if(contrato.getIdTipoContrato().getSigla().equals("VEN") && contrato.getIdStatusContrato().getSigla().equals("CA")){//contrato pos pago e aberto para na revisão que o consultor marcou
						if(contrato.getPrintRevisaoPosPago() != null  && contrato.getPrintRevisaoPosPago().longValue() <= pmpContHorasStandard.getHorasManutencao().longValue()){
							break;
						}
							
					}
					
				}
				//int totalManutencoes = 0;
				
				//totalHorasManutencao = frequencia * horasStandardList.size();
				
				//custo do TA
				if(contrato.getTa().equals("S")){
					Double taCusto = (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getTaHoras().doubleValue() * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getHhTa().doubleValue());
					taCusto = (totalHorasManutencao.intValue() / 2000) * taCusto;// total de revisoes de 2000 horas * custo do TA
					custoManutencao = custoManutencao + taCusto;
				}
				
				
				//total horas para a manutenção
				//corrigir o erro das horas
//				query = manager.createNativeQuery("select hs.standard_job_cptcd from pmp_cont_horas_standard hs where id_contrato = "+contrato.getId());
//				List<String> cptcdList = query.getResultList();
//				BigDecimal totalHHManutencao = BigDecimal.ZERO;
//				for (String cptcd : cptcdList) {
//
//					query = manager.createNativeQuery("select sum(cast(FRSDHR as decimal(18,2))) from pmp_hora h"+
//							" where h.cptcd = '"+cptcd+"'"+
//							" and h.bgrp = '"+contrato.getBgrp()+"'"+
//							" and substring(h.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
//							" and substring(h.beqmsn,5,10) between '"+contrato.getBeginRanger().substring(4, 9)+"' and '"+contrato.getEndRanger().substring(4, 9)+"'");
//					totalHHManutencao = totalHHManutencao.add((BigDecimal)query.getSingleResult());
//				}
				custoManutencao = custoManutencao + (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getHhPmp().doubleValue() * totalHHManutencao.doubleValue());//valor de hh
				custoManutencao = custoManutencao + ((contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getKmPmp().doubleValue() * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getValorKmPmp().doubleValue())*quantidadeManutencoes);//valor de km vezes a quantidade de manutenções
				
				//Contrato pré-pago da desconto
				if(contrato.getIdTipoContrato().getSigla().equals("VPG")){
					custoManutencao = custoManutencao - ((custoManutencao * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getDescontoPrePago().doubleValue())/100);
				}
				PrecoBean bean = new PrecoBean();
//				bean.setParcela(1);
//				bean.setPreco("R$ "+ValorMonetarioHelper.formata("###,###.00", custoManutencao));
//				result.add(bean);
				PrecoBean precoConcessaoBean = this.findPrecoConcessao(idContrato);
				for(int i = 0; i < 10; i++){
					//custoManutencao = custoManutencao + ((custoManutencao * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getJurosVenda().doubleValue())/100);
					bean = new PrecoBean();
					bean.setParcela(i+1);
					if(i == 0){
						bean.setPrecoConcessao(precoConcessaoBean.getPrecoConcessao());
					}
					bean.setPreco("R$ "+ValorMonetarioHelper.formata("###,###.00", custoManutencao));
					result.add(bean);
					
				}
				return result;
			
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return result;
	}
	
	public List<PrecoBean> findAllParcelasSubstituicaoTributariaPMPKIT3(Long idContrato){
		
		EntityManager manager = null;
		PmpContrato contrato = new PmpContrato();
		Double custoManutencao = 0d;
		Long totalHorasManutencao = 0l;
		BigDecimal custoPecas = BigDecimal.ZERO;
		
		List<PrecoBean> result = new ArrayList<PrecoBean>();
		try{
			manager = JpaUtil.getInstance();
			contrato = manager.find(PmpContrato.class, idContrato);
			
//			Query query = manager.createQuery("From PmpConfigOperacional where idContrato.id = :id");
//			query.setParameter("id", contrato.getId());
//			PmpConfigOperacional configOperacional = (PmpConfigOperacional)query.getSingleResult();
			//Long frequencia = 0l;
			
			//BigDecimal custoNordeste = contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getCustoNordeste();
			Query query = manager.createQuery("From PmpContHorasStandard where idContrato.id = :id");
			query.setParameter("id", contrato.getId());
			List<PmpContHorasStandard> horasStandardList = query.getResultList();
			for (PmpContHorasStandard pmpContHorasStandard : horasStandardList) {
				totalHorasManutencao += pmpContHorasStandard.getFrequencia();
			}
			//for (PmpContHorasStandard pmpContHorasStandard : horasStandardList) {
				//frequencia = pmpContHorasStandard.getFrequencia();
				//Query para peças com desconto PDP
				query = manager.createNativeQuery("select sum(m.VLSUB) from REN_PMP_PECAS_CONFIG_OPERACIONAL m"+
						" where ID_CONTRATO = "+contrato.getId()+
				" and m.bectyc in (select pdp.descricao from ren_pmp_desconto_pdp pdp)");
				custoPecas = (BigDecimal)query.getSingleResult();
				Double custoPecasTotal = 0.0;
				if(custoPecas != null){
					//custoNordesteTotal =  custoPecas.doubleValue() + ((custoPecas.doubleValue() * custoNordeste.doubleValue())/100);//aplicar custo nordeste
					custoPecasTotal = custoPecas.doubleValue();
					custoPecasTotal = custoPecasTotal - ((custoPecasTotal * (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getDescPdp().doubleValue()))/100);//desconto PDP
					custoManutencao = custoManutencao + custoPecasTotal;
				}
				//Query sem desconto pdp
				query = manager.createNativeQuery("select sum(m.VLSUB) from REN_PMP_PECAS_CONFIG_OPERACIONAL m"+
						" where ID_CONTRATO = "+contrato.getId()+
						" and (m.bectyc not in (select pdp.descricao from ren_pmp_desconto_pdp pdp)"+
				" or m.bectyc is null)");
				custoPecas = (BigDecimal)query.getSingleResult();
				//custoNordesteTotal = custoPecas.doubleValue() + ((custoPecas.doubleValue() * custoNordeste.doubleValue())/100);//aplicar custo nordeste	
				custoManutencao = custoManutencao + custoPecas.doubleValue();
			//}
			//int totalManutencoes = 0;
			
			//totalHorasManutencao = frequencia * horasStandardList.size();
			
			//custo do TA
			if(contrato.getTa().equals("S")){
				Double taCusto = (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getTaHoras().doubleValue() * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getHhTa().doubleValue());
				taCusto = (totalHorasManutencao.intValue() / 2000) * taCusto;// total de revisoes de 2000 horas * custo do TA
				custoManutencao = custoManutencao + taCusto;
			}
			
			
			//total horas para a manutenção
			query = manager.createNativeQuery("select hs.standard_job_cptcd from ren_pmp_cont_horas_standard hs where id_contrato = "+contrato.getId());
			List<String> cptcdList = query.getResultList();
			BigDecimal totalHHManutencao = BigDecimal.ZERO;
			for (String cptcd : cptcdList) {

				query = manager.createNativeQuery("select sum(cast(FRSDHR as decimal(18,2))) from ren_pmp_hora h"+
						" where h.cptcd = '"+cptcd+"'"+
						" and h.bgrp = '"+contrato.getBgrp()+"'"+
						" and substring(h.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
						" and (h.beqmsn = '"+contrato.getBeginRanger()+"' or h.beqmsn = '"+contrato.getEndRanger()+"')");
				totalHHManutencao = totalHHManutencao.add((BigDecimal)query.getSingleResult());
			}
			custoManutencao = custoManutencao + (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getHhPmp().doubleValue() * totalHHManutencao.doubleValue());//valor de hh
			custoManutencao = custoManutencao + ((contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getKmPmp().doubleValue() * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getValorKmPmp().doubleValue())*horasStandardList.size());//valor de km vezes a quantidade de manutenções
			
			if(contrato.getIdTipoContrato().getSigla().equals("VPG")){
				custoManutencao = custoManutencao - ((custoManutencao * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getDescontoPrePago().doubleValue())/100);
			}
			PrecoBean bean = new PrecoBean();
//				bean.setParcela(1);
//				bean.setPreco("R$ "+ValorMonetarioHelper.formata("###,###.00", custoManutencao));
//				result.add(bean);
			PrecoBean precoConcessaoBean = this.findPrecoConcessao(idContrato);
			for(int i = 0; i < 10; i++){
				//custoManutencao = custoManutencao + ((custoManutencao * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getJurosVenda().doubleValue())/100);
				bean = new PrecoBean();
				bean.setParcela(i+1);
				if(i == 0){
					bean.setPrecoConcessao(precoConcessaoBean.getPrecoConcessao());
				}
				bean.setPreco("R$ "+ValorMonetarioHelper.formata("###,###.00", custoManutencao));
				result.add(bean);
				
			}
			return result;			
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return result;
	}

	public List<PrecoBean> findAllParcelasPMPKIT3Gerador(Long idContrato){
		
		EntityManager manager = null;
		PmpContrato contrato = new PmpContrato();
		Double custoManutencao = 0d;
		//Long totalHorasManutencao = 0l;
		BigDecimal custoPecas = BigDecimal.ZERO;
		
		List<PrecoBean> result = new ArrayList<PrecoBean>();
		
		try{
			manager = JpaUtil.getInstance();
			contrato = manager.find(PmpContrato.class, idContrato);
			
			//Long frequencia = 0l;
			
			BigDecimal custoNordeste = contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getCustoNordeste();
			Query query = manager.createQuery("From PmpContMesesStandard where idContrato.id = :id");
			query.setParameter("id", contrato.getId());
			List<PmpContMesesStandard> mesesStandardList = query.getResultList();
//			for (PmpContMesesStandard pmpContMesesStandard : mesesStandardList) {
//				totalHorasManutencao += pmpContMesesStandard.getFrequencia();
//			}
			for (PmpContMesesStandard pmpContMesesStandard : mesesStandardList) {
				//frequencia = pmpContHorasStandard.getFrequencia();
				//Query para peças com desconto PDP
				query = manager.createNativeQuery("select sum(m.unls * m.dlrqty) from ren_pmp_manutencao m"+
						" where m.cptcd = '"+pmpContMesesStandard.getStandardJobCptcd()+"'"+
						" and m.bgrp = '"+contrato.getBgrp()+"'"+
						" and substr(m.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
						" and (m.beqmsn = '"+contrato.getBeginRanger()+"' or m.beqmsn = '"+contrato.getEndRanger()+"') "+
						" and m.bectyc in (select pdp.descricao from ren_pmp_desconto_pdp pdp)");
				custoPecas = (BigDecimal)query.getSingleResult();
				Double custoNordesteTotal = 0.0;
				if(custoPecas != null){
					custoNordesteTotal =  custoPecas.doubleValue() + ((custoPecas.doubleValue() * custoNordeste.doubleValue())/100);//aplicar custo nordeste
					custoNordesteTotal = custoNordesteTotal - ((custoNordesteTotal * (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getDescPdp().doubleValue()))/100);//desconto PDP
					custoManutencao = custoManutencao + custoNordesteTotal;
				}
				//Query sem desconto pdp
				query = manager.createNativeQuery("select sum(m.unls * m.dlrqty) from ren_pmp_manutencao m"+
						" where m.cptcd = '"+pmpContMesesStandard.getStandardJobCptcd()+"'"+
						" and m.bgrp = '"+contrato.getBgrp()+"'"+
						" and substr(m.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
						" and (m.beqmsn = '"+contrato.getBeginRanger()+"' or m.beqmsn = '"+contrato.getEndRanger()+"') "+
						" and (m.bectyc not in (select pdp.descricao from ren_pmp_desconto_pdp pdp)"+
						" or m.bectyc is null)");
				custoPecas = (BigDecimal)query.getSingleResult();
				if(custoPecas != null){
					custoNordesteTotal = custoPecas.doubleValue() + ((custoPecas.doubleValue() * custoNordeste.doubleValue())/100);//aplicar custo nordeste	
					custoManutencao = custoManutencao + custoNordesteTotal;
				}
			}
			//int totalManutencoes = 0;
			
			//totalHorasManutencao = frequencia * horasStandardList.size();
			
			//custo do TA
			//if(contrato.getTa().equals("S")){
				Double taCusto = (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getTaHoras().doubleValue() * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getHhTa().doubleValue());
				//taCusto = (totalHorasManutencao.intValue() / 2000) * taCusto;// total de revisoes de 2000 horas * custo do TA
				custoManutencao = custoManutencao + taCusto;
			//}
			
			
			//total horas para a manutenção
			query = manager.createNativeQuery("select sum (to_number(replace(h.frsdhr,'.',','))) from ren_pmp_hora h"+
					" where h.cptcd in (select hs.standard_job_cptcd from ren_pmp_cont_meses_standard hs where id_contrato = "+contrato.getId()+")"+
					" and h.bgrp = '"+contrato.getBgrp()+"'"+
					" and substr(h.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
					" and (h.beqmsn = '"+contrato.getBeginRanger()+"' or h.beqmsn = '"+contrato.getEndRanger()+"') ");
			BigDecimal totalHHManutencao = (BigDecimal)query.getSingleResult();
			
			if (totalHHManutencao != null) {
				custoManutencao = custoManutencao + (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getHhPmp().doubleValue() * totalHHManutencao.doubleValue());//valor de hh
				custoManutencao = custoManutencao + ((contrato.getDistanciaGerador().doubleValue() * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getValorKmPmp().doubleValue())*mesesStandardList.size());//valor de km vezes a quantidade de manutenções
				
				
				PrecoBean bean = new PrecoBean();
	//				bean.setParcela(1);
	//				bean.setPreco("R$ "+ValorMonetarioHelper.formata("###,###.00", custoManutencao));
	//				result.add(bean);
				PrecoBean precoConcessaoBean = this.findPrecoConcessaoGerador(idContrato);
				for(int i = 0; i < 10; i++){
					//custoManutencao = custoManutencao + ((custoManutencao * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getJurosVenda().doubleValue())/100);
					bean = new PrecoBean();
					bean.setParcela(i+1);
					if(i == 0){
						bean.setPrecoConcessao(precoConcessaoBean.getPrecoConcessao());
					}
					bean.setPreco("R$ " + ValorMonetarioHelper.formata("###,###.00", custoManutencao));
					result.add(bean);
					
				}
				return result;
			}
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return result;
	}
	
	public List<ContratoComercialBean> findTabelaPreco(){
		
		EntityManager manager = null;
		
		Long totalHorasManutencao = 2000l;
		
		List<ContratoComercialBean> result = new ArrayList<ContratoComercialBean>();
		try{
			manager = JpaUtil.getInstance();
			
			//Long frequencia = 0l;
			
			Query query = manager.createQuery("select distinct m, p from PmpConfigManutencao m, PmpConfiguracaoPrecos p where m.idConfiguracaoPreco.id = p.id and m.bgrp = 'PM' order by m.modelo, m.prefixo, m.beginrange ");
			
			List<Object[]> list = query.getResultList();
			for (Object[] objects : list) {
				BigDecimal totalHHManutencao = BigDecimal.ZERO;
				Double custoManutencao = 0d;
				BigDecimal custoPecas = BigDecimal.ZERO;
				ContratoComercialBean bean = new ContratoComercialBean();
				String cptcd = "";
				PmpConfigManutencao manutencao = (PmpConfigManutencao)objects[0];
				PmpConfiguracaoPrecos precos = (PmpConfiguracaoPrecos)objects[1];
				bean.setModelo(manutencao.getModelo());
				bean.setPrefixo(manutencao.getPrefixo());
				bean.setQtdParcelas(1);
				bean.setBeginRanger(manutencao.getBeginrange());
				bean.setEndRanger(manutencao.getEndrange());
				//BigDecimal custoNordeste = precos.getCustoNordeste();

				query = manager.createQuery("From  PmpConfigManutencao pc, PmpConfigHorasStandard hs where pc.id = hs.idConfigManutencao.id " +
						" and hs.horas <=:horas" +
						" and pc.bgrp =:bgrp" +
						" and pc.modelo =:modelo" +
						" and pc.prefixo =:prefixo" +
						" and pc.beginrange =:beginrange" +
						" and pc.endrange =:endrange" +
						" and pc.contExcessao = 'N'" +
				" order by hs.horas");
				query.setParameter("horas", 2000L);
				query.setParameter("bgrp", manutencao.getBgrp());
				query.setParameter("modelo", manutencao.getModelo());
				query.setParameter("prefixo", manutencao.getPrefixo());
				query.setParameter("beginrange", manutencao.getBeginrange());
				query.setParameter("endrange", manutencao.getEndrange());
				List<Object[]> manutencaoList = query.getResultList();
				boolean isPreco = true;
				for (Object[] pair : manutencaoList) {
					//PmpConfigManutencao pmpConfigManutencao = (PmpConfigManutencao)pair[0];
					PmpConfigHorasStandard standard = (PmpConfigHorasStandard)pair[1];
					cptcd += "'"+standard.getStandardJobCptcd()+"',";
					//frequencia = pmpContHorasStandard.getFrequencia();
					//Query para peças com desconto PDP
					query = manager.createNativeQuery("select sum(m.unls * m.dlrqty) from ren_pmp_manutencao m"+
							" where m.cptcd = '"+standard.getStandardJobCptcd()+"'"+
							" and m.bgrp = '"+manutencao.getBgrp()+"'"+
							" and substring(m.beqmsn,1,4) = '"+manutencao.getPrefixo()+"'"+
							" and (m.beqmsn = '"+manutencao.getBeginrange()+"' or m.beqmsn = '"+manutencao.getEndrange()+"') "+
					" and m.bectyc in (select pdp.descricao from ren_pmp_desconto_pdp pdp)");
					
					custoPecas = (BigDecimal)query.getSingleResult();
					Double custoNordesteTotal = 0.0;
					if(custoPecas != null){
						//custoNordesteTotal =  custoPecas.doubleValue() + ((custoPecas.doubleValue() * custoNordeste.doubleValue())/100);//aplicar custo nordeste
						custoNordesteTotal = custoPecas.doubleValue();
						custoNordesteTotal = custoNordesteTotal - ((custoNordesteTotal * (precos.getDescPdp().doubleValue()))/100);//desconto PDP
						custoManutencao = custoManutencao + custoNordesteTotal;
					}
					//Query sem desconto pdp
					query = manager.createNativeQuery("select sum(m.unls * m.dlrqty) from ren_pmp_manutencao m"+
							" where m.cptcd = '"+standard.getStandardJobCptcd()+"'"+
							" and m.bgrp = '"+manutencao.getBgrp()+"'"+
							" and substring(m.beqmsn,1,4) = '"+manutencao.getPrefixo()+"'"+
							" and (m.beqmsn = '"+manutencao.getBeginrange()+"' or m.beqmsn = '"+manutencao.getEndrange()+"') "+
							" and (m.bectyc not in (select pdp.descricao from ren_pmp_desconto_pdp pdp)"+
					" or m.bectyc is null)");
					custoPecas = (BigDecimal)query.getSingleResult();
					if(custoPecas != null){
					//custoNordesteTotal = custoPecas.doubleValue() + ((custoPecas.doubleValue() * custoNordeste.doubleValue())/100);//aplicar custo nordeste	
					custoManutencao = custoManutencao + custoPecas.doubleValue();
					}
					
					query = manager.createNativeQuery("select sum(cast(FRSDHR as decimal(18,2))) from ren_pmp_hora h"+
							" where h.cptcd = '"+standard.getStandardJobCptcd()+"'"+
							" and h.bgrp = '"+manutencao.getBgrp()+"'"+
							" and substring(h.beqmsn,1,4) = '"+manutencao.getPrefixo()+"'"+
							" and (h.beqmsn = '"+manutencao.getBeginrange()+"' or h.beqmsn = '"+manutencao.getEndrange()+"')");
					if((BigDecimal)query.getSingleResult() != null){
						totalHHManutencao = totalHHManutencao.add((BigDecimal)query.getSingleResult());
					}else{
						isPreco = false;
					}
				}
				if(isPreco == false){
					continue;
				}
				//int totalManutencoes = 0;

				//totalHorasManutencao = frequencia * horasStandardList.size();

				//custo do TA
				//if(contrato.getTa().equals("S")){
//				Double taCusto = (precos.getTaHoras().doubleValue() * precos.getHhTa().doubleValue());
//				taCusto = (totalHorasManutencao.intValue() / 2000) * taCusto;// total de revisoes de 2000 horas * custo do TA
//				custoManutencao = custoManutencao + taCusto;
				//}


				//total horas para a manutenção
//				query = manager.createNativeQuery("select sum(cast(h.FRSDHR as decimal(18,2))) from pmp_hora h"+
//						" where h.cptcd in ("+cptcd.substring(0,cptcd.length()-1)+")"+
//						" and h.bgrp = '"+manutencao.getBgrp()+"'"+
//						" and substring(h.beqmsn,1,4) = '"+manutencao.getPrefixo()+"'"+
//						" and substring(h.beqmsn,5,10) between '"+manutencao.getBeginrange().substring(4, 9)+"' and '"+manutencao.getEndrange().substring(4, 9)+"'");
//				BigDecimal totalHHManutencao = (BigDecimal)query.getSingleResult();
				if(totalHHManutencao != null){
					custoManutencao = custoManutencao + (precos.getHhPmp().doubleValue() * totalHHManutencao.doubleValue());//valor de hh
					custoManutencao = custoManutencao + ((precos.getKmPmp().doubleValue() * precos.getValorKmPmp().doubleValue())*manutencaoList.size());//valor de km vezes a quantidade de manutenções
				}

				//				bean.setParcela(1);
				//				bean.setPreco("R$ "+ValorMonetarioHelper.formata("###,###.00", custoManutencao));
				//				result.add(bean);


				//custoManutencao = custoManutencao + ((custoManutencao * precos.getJurosVenda().doubleValue())/100);
				if(custoManutencao == null){
					custoManutencao = 0d;
					
				}

				bean.setValorContrato("R$ "+ValorMonetarioHelper.formata("###,###.00", custoManutencao));
				result.add(bean);
			}
			return result;		
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return result;
	}
	
	public PrecoBean findPrecoConcessao(Long idContrato){
		
		EntityManager manager = null;
		PmpContrato contrato = new PmpContrato();
		Double custoManutencao = 0d;
		Long totalHorasManutencao = 0l;
		BigDecimal custoPecas = BigDecimal.ZERO;
		BigDecimal custoPecasFrasco = BigDecimal.ZERO;
		BigDecimal totalHHManutencao = BigDecimal.ZERO;
		
		try{
			manager = JpaUtil.getInstance();
			contrato = manager.find(PmpContrato.class, idContrato);
			
			//Long frequencia = 0l;
			
			BigDecimal custoNordeste = contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getCustoNordeste();
			Query query = manager.createQuery("From PmpContHorasStandard where idContrato.id = :id order by horasManutencao");
			query.setParameter("id", contrato.getId());
			List<PmpContHorasStandard> horasStandardList = query.getResultList();
			for (PmpContHorasStandard pmpContHorasStandard : horasStandardList) {
				totalHorasManutencao += pmpContHorasStandard.getFrequencia();
			}
			int quantidadeManutencoes = 0;
			for (PmpContHorasStandard pmpContHorasStandard : horasStandardList) {
				quantidadeManutencoes++;
				//frequencia = pmpContHorasStandard.getFrequencia();
				//Query para peças com desconto PDP
				query = manager.createNativeQuery("select sum(m.landcs * m.dlrqty) from ren_pmp_manutencao_preco_custo m"+
						" where m.cptcd = '"+pmpContHorasStandard.getStandardJobCptcd()+"'"+
						" and m.bgrp = '"+contrato.getBgrp()+"'"+
						" and substring(m.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
						" and (m.beqmsn = '"+contrato.getBeginRanger()+"' or m.beqmsn = '"+contrato.getEndRanger()+"') ");
						//" and m.stno = "+Integer.valueOf(usuarioBean.getFilial())+
						//" and m.pano20 <> '1Z0212'");
				custoPecas = (BigDecimal)query.getSingleResult(); 
				
				if(custoPecas != null){
					custoPecas = BigDecimal.ZERO;
				}
				
				query = manager.createNativeQuery("select sum(m.uncs * m.dlrqty) from ren_pmp_manutencao m"+
						" where m.cptcd = '"+pmpContHorasStandard.getStandardJobCptcd()+"'"+
						" and m.bgrp = '"+contrato.getBgrp()+"'"+
						" and substring(m.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
						" and (m.beqmsn = '"+contrato.getBeginRanger()+"' or m.beqmsn = '"+contrato.getEndRanger()+"') ");
						//" and m.pano20 = '1Z0212'");
				custoPecasFrasco = (BigDecimal)query.getSingleResult(); 
				
				if(custoPecasFrasco != null){
					custoPecas = custoPecas.add(custoPecasFrasco);
				}
				//Double custoNordesteTotal = 0.0;
				//if(custoPecas != null){
					//custoNordesteTotal =  custoPecas.doubleValue() + ((custoPecas.doubleValue() * custoNordeste.doubleValue())/100);//aplicar custo nordeste
					//custoNordesteTotal = custoNordesteTotal - ((custoNordesteTotal * (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getDescPdp().doubleValue()))/100);//desconto PDP
					custoManutencao = custoManutencao + custoPecas.doubleValue();
				//}
					
					//total horas para a manutenção
					query = manager.createNativeQuery(" select sum(cast(FRSDHR as decimal(18,2))) from ren_pmp_hora h"+
							" where h.cptcd ='"+pmpContHorasStandard.getStandardJobCptcd()+"'"+
							" and h.bgrp = '"+contrato.getBgrp()+"'"+
							" and substring(h.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
							" and (h.beqmsn = '"+contrato.getBeginRanger()+"' or h.beqmsn = '"+contrato.getEndRanger()+"')");
					totalHHManutencao = totalHHManutencao.add((BigDecimal)query.getSingleResult());	
					if(contrato.getIdTipoContrato().getSigla().equals("VEN") && contrato.getIdStatusContrato().getSigla().equals("CA")){//contrato pos pago e aberto para na revisão que o consultor marcou
						if(contrato.getPrintRevisaoPosPago() != null  && contrato.getPrintRevisaoPosPago().longValue() <= pmpContHorasStandard.getHorasManutencao().longValue()){
							break;
						}
							
					}
			}
			//int totalManutencoes = 0;
			
			//totalHorasManutencao = frequencia * horasStandardList.size();
			
			//custo do TA
			if(contrato.getTa().equals("S")){
				Double taCusto = (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getTaHoras().doubleValue() * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getValorHhTaCusto().doubleValue());
				taCusto = (totalHorasManutencao.intValue() / 2000) * taCusto;// total de revisoes de 2000 horas * custo do TA
				custoManutencao = custoManutencao + taCusto;
			}
			
			
//			//total horas para a manutenção
//			query = manager.createNativeQuery(" select sum(cast(FRSDHR as decimal(18,2))) from pmp_hora h"+
//					" where h.cptcd in (select hs.standard_job_cptcd from pmp_cont_horas_standard hs where id_contrato = "+contrato.getId()+")"+
//					" and h.bgrp = '"+contrato.getBgrp()+"'"+
//					" and substring(h.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
//					" and substring(h.beqmsn,5,10) between '"+contrato.getBeginRanger().substring(4, 9)+"' and '"+contrato.getEndRanger().substring(4, 9)+"'");
//			BigDecimal totalHHManutencao = (BigDecimal)query.getSingleResult();
			custoManutencao = custoManutencao + (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getValorHhPmpCusto().doubleValue() * totalHHManutencao.doubleValue());//valor de hh
			custoManutencao = custoManutencao + ((contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getKmPmp().doubleValue() * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getValorKmPmpCusto().doubleValue())*quantidadeManutencoes);//valor de km vezes a quantidade de manutenções
			
			
			PrecoBean bean = new PrecoBean();
//				bean.setParcela(1);
//				bean.setPreco("R$ "+ValorMonetarioHelper.formata("###,###.00", custoManutencao));
//				result.add(bean);
			//for(int i = 0; i < 10; i++){
				//custoManutencao = custoManutencao + ((custoManutencao * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getJurosVenda().doubleValue())/100);
				//bean = new PrecoBean();
				bean.setParcela(1);
				bean.setPrecoConcessao("R$ "+ValorMonetarioHelper.formata("###,###.00", custoManutencao));
				//result.add(bean);
				
			//}
			return bean;
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return null;
	}
	
	public PrecoBean findPrecoConcessaoGerador(Long idContrato){
		
		EntityManager manager = null;
		PmpContrato contrato = new PmpContrato();
		Double custoManutencao = 0d;
		//Long totalMesesManutencao = 0l;
		BigDecimal custoPecas = BigDecimal.ZERO;
		BigDecimal custoPecasFrasco = BigDecimal.ZERO;
		
		try{
			manager = JpaUtil.getInstance();
			contrato = manager.find(PmpContrato.class, idContrato);
			
			//Long frequencia = 0l;
			
			//BigDecimal custoNordeste = contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getCustoNordeste();
			Query query = manager.createQuery("From PmpContMesesStandard where idContrato.id = :id");
			query.setParameter("id", contrato.getId());
			List<PmpContMesesStandard> mesesStandardList = query.getResultList();
//			for (PmpContMesesStandard pmpContMesesStandard : mesesStandardList) {
//				totalMesesManutencao += pmpContMesesStandard.getFrequencia();
//			}
			for (PmpContMesesStandard pmpContHorasStandard : mesesStandardList) {
				//frequencia = pmpContHorasStandard.getFrequencia();
				//Query para peças com desconto PDP
				query = manager.createNativeQuery("select sum(m.landcs * m.dlrqty) from ren_pmp_manutencao_preco_custo m"+
						" where m.cptcd = '"+pmpContHorasStandard.getStandardJobCptcd()+"'"+
						" and m.bgrp = '"+contrato.getBgrp()+"'"+
						" and substr(m.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
						" and (m.beqmsn = '"+contrato.getBeginRanger()+"' or m.beqmsn = '"+contrato.getEndRanger()+"') "+
						//" and m.stno = "+Integer.valueOf(usuarioBean.getFilial())+
						" and m.pano20 <> '1Z0212'");
				custoPecas = (BigDecimal)query.getSingleResult();
				if(custoPecas == null){
					custoPecas = BigDecimal.ZERO;
				}
				query = manager.createNativeQuery("select sum(m.uncs * m.dlrqty) from ren_pmp_manutencao m"+
						" where m.cptcd = '"+pmpContHorasStandard.getStandardJobCptcd()+"'"+
						" and m.bgrp = '"+contrato.getBgrp()+"'"+
						" and substr(m.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
						" and (m.beqmsn = '"+contrato.getBeginRanger()+"' or m.beqmsn = '"+contrato.getEndRanger()+"') "+
						" and m.pano20 = '1Z0212'");
				custoPecasFrasco = (BigDecimal)query.getSingleResult(); 
				if(custoPecasFrasco == null){
					custoPecasFrasco = BigDecimal.ZERO;
				}
				if(custoPecasFrasco != null){
					custoPecas = custoPecas.add(custoPecasFrasco);
				}
				//Double custoNordesteTotal = 0.0;
				//if(custoPecas != null){
				//custoNordesteTotal =  custoPecas.doubleValue() + ((custoPecas.doubleValue() * custoNordeste.doubleValue())/100);//aplicar custo nordeste
				//custoNordesteTotal = custoNordesteTotal - ((custoNordesteTotal * (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getDescPdp().doubleValue()))/100);//desconto PDP
				custoManutencao = custoManutencao + custoPecas.doubleValue();
				//}
			}
			//int totalManutencoes = 0;
			
			//totalHorasManutencao = frequencia * horasStandardList.size();
			
			//custo do TA
			//if(contrato.getTa().equals("S")){
				Double taCusto = (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getTaHoras().doubleValue() * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getValorHhTaCusto().doubleValue());
				//taCusto = (totalMesesManutencao.intValue() / 2000) * taCusto;// total de revisoes de 2000 horas * custo do TA
				custoManutencao = custoManutencao + taCusto;
			//}
			
			
			//total horas para a manutenção
			query = manager.createNativeQuery("select sum (to_number(replace(h.frsdhr,'.',','))) from ren_pmp_hora h"+
					" where h.cptcd in (select hs.standard_job_cptcd from ren_pmp_cont_meses_standard hs where id_contrato = "+contrato.getId()+")"+
					" and h.bgrp = '"+contrato.getBgrp()+"'"+
					" and substr(h.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
					" and (h.beqmsn = '"+contrato.getBeginRanger()+"' or h.beqmsn = '"+contrato.getEndRanger()+"') ");
			BigDecimal totalHHManutencao = (BigDecimal)query.getSingleResult();
			custoManutencao = custoManutencao + (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getValorHhPmpCusto().doubleValue() * totalHHManutencao.doubleValue());//valor de hh
			custoManutencao = custoManutencao + ((contrato.getDistanciaGerador().doubleValue() * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getValorKmPmpCusto().doubleValue())*mesesStandardList.size());//valor de km vezes a quantidade de manutenções
			
			
			PrecoBean bean = new PrecoBean();
//				bean.setParcela(1);
//				bean.setPreco("R$ "+ValorMonetarioHelper.formata("###,###.00", custoManutencao));
//				result.add(bean);
			//for(int i = 0; i < 10; i++){
			//custoManutencao = custoManutencao + ((custoManutencao * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getJurosVenda().doubleValue())/100);
			//bean = new PrecoBean();
			bean.setParcela(1);
			bean.setPrecoConcessao("R$ "+ValorMonetarioHelper.formata("###,###.00", custoManutencao));
			//result.add(bean);
			
			//}
			return bean;
			
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return null;
	}
	
	
	public List<PrecoBean> findAllParcelasPMPKIT3Custo(Long idContrato, String  siglaCustomizacao){
		
		EntityManager manager = null;
		PmpContrato contrato = new PmpContrato();
		Double custoManutencao = 0d;
		Long totalHorasManutencao = 0l;
		BigDecimal custoPecas = BigDecimal.ZERO;
		BigDecimal totalHHManutencao = BigDecimal.ZERO;
		List<PrecoBean> result = new ArrayList<PrecoBean>();
		try{
			manager = JpaUtil.getInstance();
			contrato = manager.find(PmpContrato.class, idContrato);
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
			//Long frequencia = 0l;
			
				//BigDecimal custoNordeste = contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getCustoNordeste();
				Query query = manager.createQuery("From PmpContHorasStandard where idContrato.id = :id order by horasManutencao");
				query.setParameter("id", contrato.getId());
				List<PmpContHorasStandard> horasStandardList = query.getResultList();
				for (PmpContHorasStandard pmpContHorasStandard : horasStandardList) {
					totalHorasManutencao += pmpContHorasStandard.getFrequencia();
				}
				int quantidadeManutencoes = 0;
				for (PmpContHorasStandard pmpContHorasStandard : horasStandardList) {
					quantidadeManutencoes++;
					//frequencia = pmpContHorasStandard.getFrequencia();
					//Query para peças com desconto PDP
					query = manager.createNativeQuery("select sum(m.landcs * m.dlrqty) from ren_pmp_manutencao_preco_custo m"+
							" where m.cptcd = '"+pmpContHorasStandard.getStandardJobCptcd()+"'"+
							" and m.bgrp = '"+contrato.getBgrp()+"'"+
							" and substring(m.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
							complemento + complementoSigla +
							" and (m.beqmsn = '"+contrato.getBeginRanger()+"' or m.beqmsn = '"+contrato.getEndRanger()+"') ");
					//" and m.stno ="+Integer.valueOf(FILIAL));
					custoPecas = (BigDecimal)query.getSingleResult();
					//Double custoNordesteTotal =  custoPecas.doubleValue() + ((custoPecas.doubleValue() * custoNordeste.doubleValue())/100);//aplicar custo nordeste
					//custoNordesteTotal = custoNordesteTotal - ((custoNordesteTotal * (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getDescPdp().doubleValue()))/100);//desconto PDP
					if(custoPecas != null){
						custoManutencao = custoManutencao + custoPecas.doubleValue();
					}
//					//Query sem desconto pdp
//					query = manager.createNativeQuery("select sum(m.unls) from pmp_manutencao_preco_custo m"+
//							" where m.cptcd = '"+pmpContHorasStandard.getStandardJobCptcd()+"'"+
//							" and m.bgrp = '"+contrato.getBgrp()+"'"+
//							" and substr(m.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
//							" and substr(m.beqmsn,5,10) between '"+contrato.getBeginRanger().substring(4, 9)+"' and '"+contrato.getEndRanger().substring(4, 9)+"'"+
//					" and m.bectyc not in (select pdp.descricao from pmp_desconto_pdp pdp)");
//					custoPecas = (BigDecimal)query.getSingleResult();
//					custoNordesteTotal = custoPecas.doubleValue() + ((custoPecas.doubleValue() * custoNordeste.doubleValue())/100);//aplicar custo nordeste	
//					custoManutencao = custoManutencao + custoNordesteTotal;
					
					//total horas para a manutenção
					query = manager.createNativeQuery("select sum(cast(FRSDHR as decimal(18,2))) from ren_pmp_hora h"+
							" where h.cptcd = '"+pmpContHorasStandard.getStandardJobCptcd()+"'"+
							" and h.bgrp = '"+contrato.getBgrp()+"'"+
							" and substring(h.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
							" and (h.beqmsn = '"+contrato.getBeginRanger()+"' or h.beqmsn = '"+contrato.getEndRanger()+"')");
					totalHHManutencao = totalHHManutencao.add((BigDecimal)query.getSingleResult());
					
					
					if(contrato.getIdTipoContrato().getSigla().equals("VEN") && contrato.getIdStatusContrato().getSigla().equals("CA")){//contrato pos pago e aberto para na revisão que o consultor marcou
						if(contrato.getPrintRevisaoPosPago() != null  && contrato.getPrintRevisaoPosPago().longValue() <= pmpContHorasStandard.getHorasManutencao().longValue()){
							break;
						}
							
					}
				}
				//int totalManutencoes = 0;
				
				//totalHorasManutencao = frequencia * horasStandardList.size();
				
				//custo do TA
				if(contrato.getTa().equals("S")){
					Double taCusto = (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getTaHoras().doubleValue() * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getValorHhTaCusto().doubleValue());
					taCusto = (totalHorasManutencao.intValue() / 2000) * taCusto;// total de revisoes de 2000 horas * custo do TA
					custoManutencao = custoManutencao + taCusto;
				}
				
				
				//total horas para a manutenção
//				query = manager.createNativeQuery("select sum(cast(FRSDHR as decimal(18,2))) from pmp_hora h"+
//						" where h.cptcd in (select hs.standard_job_cptcd from pmp_cont_horas_standard hs where id_contrato = "+contrato.getId()+")"+
//						" and h.bgrp = '"+contrato.getBgrp()+"'"+
//						" and substring(h.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
//						" and substring(h.beqmsn,5,10) between '"+contrato.getBeginRanger().substring(4, 9)+"' and '"+contrato.getEndRanger().substring(4, 9)+"'");
//				BigDecimal totalHHManutencao = (BigDecimal)query.getSingleResult();
			
					custoManutencao = custoManutencao + (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getValorHhPmpCusto().doubleValue() * totalHHManutencao.doubleValue());//valor de hh
					custoManutencao = custoManutencao + ((contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getKmPmp().doubleValue() * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getValorKmPmpCusto().doubleValue())*quantidadeManutencoes);//valor de km vezes a quantidade de manutenções
				
				PrecoBean bean = new PrecoBean();
//				bean.setParcela(1);
//				bean.setPreco("R$ "+ValorMonetarioHelper.formata("###,###.00", custoManutencao));
//				result.add(bean);
				//for(int i = 0; i < 10; i++){
					//custoManutencao = custoManutencao + ((custoManutencao * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getJurosVenda().doubleValue())/100);
					bean = new PrecoBean();
					bean.setParcela(1);
					bean.setPreco("R$ "+ValorMonetarioHelper.formata("###,###.00", custoManutencao));
					result.add(bean);
					
				//}
				return result;
			
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return result;
	}
	
	
	public List<PrecoBean> findAllParcelasRentalCustoPMKIT3(Long idContrato, String siglaCustomizacao){
		
		EntityManager manager = null;
		PmpContrato contrato = new PmpContrato();
		Double custoManutencao = 0d;
		Long totalHorasManutencao = 0l;
		BigDecimal custoPecas = BigDecimal.ZERO;
		BigDecimal totalHHManutencao = BigDecimal.ZERO;
		
		List<PrecoBean> result = new ArrayList<PrecoBean>();
		try{
			manager = JpaUtil.getInstance();
			contrato = manager.find(PmpContrato.class, idContrato);
			
			//Long frequencia = 0l;
			String complemento = "";
			String complementoSigla ="";
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
				//BigDecimal custoNordeste = contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getCustoNordeste();
				Query query = manager.createQuery("From PmpContHorasStandard where idContrato.id = :id");
				query.setParameter("id", contrato.getId());
				List<PmpContHorasStandard> horasStandardList = query.getResultList();
				for (PmpContHorasStandard pmpContHorasStandard : horasStandardList) {
					totalHorasManutencao += pmpContHorasStandard.getFrequencia();
				}
				for (PmpContHorasStandard pmpContHorasStandard : horasStandardList) {
					//frequencia = pmpContHorasStandard.getFrequencia();
					//Query para peças com desconto PDP
					query = manager.createNativeQuery("select sum(m.landcs * m.dlrqty) from ren_pmp_manutencao_preco_custo m"+
							" where m.cptcd = '"+pmpContHorasStandard.getStandardJobCptcd()+"'"+
							" and m.bgrp = '"+contrato.getBgrp()+"'"+
							" and substring(m.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
							complemento + complementoSigla +
							" and (m.beqmsn = '"+contrato.getBeginRanger()+"' or m.beqmsn = '"+contrato.getEndRanger()+"') ");
							//" and m.stno ="+Integer.valueOf(FILIAL));
					custoPecas = (BigDecimal)query.getSingleResult();
					//Double custoNordesteTotal =  custoPecas.doubleValue() + ((custoPecas.doubleValue() * custoNordeste.doubleValue())/100);//aplicar custo nordeste
					//custoNordesteTotal = custoNordesteTotal - ((custoNordesteTotal * (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getDescPdp().doubleValue()))/100);//desconto PDP
					custoManutencao = custoManutencao + custoPecas.doubleValue();
					
//					//Query sem desconto pdp
//					query = manager.createNativeQuery("select sum(m.unls) from pmp_manutencao_preco_custo m"+
//							" where m.cptcd = '"+pmpContHorasStandard.getStandardJobCptcd()+"'"+
//							" and m.bgrp = '"+contrato.getBgrp()+"'"+
//							" and substr(m.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
//							" and substr(m.beqmsn,5,10) between '"+contrato.getBeginRanger().substring(4, 9)+"' and '"+contrato.getEndRanger().substring(4, 9)+"'"+
//					" and m.bectyc not in (select pdp.descricao from pmp_desconto_pdp pdp)");
//					custoPecas = (BigDecimal)query.getSingleResult();
//					custoNordesteTotal = custoPecas.doubleValue() + ((custoPecas.doubleValue() * custoNordeste.doubleValue())/100);//aplicar custo nordeste	
//					custoManutencao = custoManutencao + custoNordesteTotal;
					query = manager.createNativeQuery("select sum(cast(FRSDHR as decimal(18,2))) from ren_pmp_hora h"+
							" where h.cptcd = '"+pmpContHorasStandard.getStandardJobCptcd()+"'"+
							" and h.bgrp = '"+contrato.getBgrp()+"'"+
							" and substring(h.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
							" and (h.beqmsn = '"+contrato.getBeginRanger()+"' or h.beqmsn = '"+contrato.getEndRanger()+"')");
					totalHHManutencao = totalHHManutencao.add((BigDecimal)query.getSingleResult());
				}
				//int totalManutencoes = 0;
				
				//totalHorasManutencao = frequencia * horasStandardList.size();
				
				//custo do TA
				if(contrato.getTa().equals("S")){
					Double taCusto = (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getTaHoras().doubleValue() * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getHhTa().doubleValue());
					taCusto = (totalHorasManutencao.intValue() / 2000) * taCusto;// total de revisoes de 2000 horas * custo do TA
					custoManutencao = custoManutencao + taCusto;
				}
				
				
//				//total horas para a manutenção
//				query = manager.createNativeQuery("select sum(cast(FRSDHR as decimal(18,2))) from pmp_hora h"+
//						" where h.cptcd in (select hs.standard_job_cptcd from pmp_cont_horas_standard hs where id_contrato = "+contrato.getId()+")"+
//						" and h.bgrp = '"+contrato.getBgrp()+"'"+
//						" and substring(h.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
//						" and substring(h.beqmsn,5,10) between '"+contrato.getBeginRanger().substring(4, 9)+"' and '"+contrato.getEndRanger().substring(4, 9)+"'");
//				BigDecimal totalHHManutencao = (BigDecimal)query.getSingleResult();
				custoManutencao = custoManutencao + (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getHhRental().doubleValue() * totalHHManutencao.doubleValue());//valor de hh
				custoManutencao = custoManutencao + ((contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getKmRental().doubleValue() * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getValorKmRental().doubleValue())*horasStandardList.size());//valor de km vezes a quantidade de manutenções
				
				
				PrecoBean bean = new PrecoBean();
//				bean.setParcela(1);
//				bean.setPreco("R$ "+ValorMonetarioHelper.formata("###,###.00", custoManutencao));
//				result.add(bean);
			//	for(int i = 0; i < 10; i++){
					//custoManutencao = custoManutencao + ((custoManutencao * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getJurosVenda().doubleValue())/100);
					bean = new PrecoBean();
					bean.setParcela(1);
					bean.setPreco("R$ "+ValorMonetarioHelper.formata("###,###.00", custoManutencao));
					result.add(bean);
					
				//}
				return result;
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return result;
	}

	
	public List<PrecoBean> findAllParcelasRentalCustoPMKIT3Gerador(Long idContrato){
		
		EntityManager manager = null;
		PmpContrato contrato = new PmpContrato();
		Double custoManutencao = 0d;
		Long totalHorasManutencao = 0l;
		BigDecimal custoPecas = BigDecimal.ZERO;
		
		List<PrecoBean> result = new ArrayList<PrecoBean>();
		try{
			manager = JpaUtil.getInstance();
			contrato = manager.find(PmpContrato.class, idContrato);
			
			//Long frequencia = 0l;
			
			//BigDecimal custoNordeste = contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getCustoNordeste();
			Query query = manager.createQuery("From PmpContMesesStandard where idContrato.id = :id");
			query.setParameter("id", contrato.getId());
			List<PmpContMesesStandard> mesesStandardList = query.getResultList();
			for (PmpContMesesStandard pmpContMesesStandard : mesesStandardList) {
				totalHorasManutencao += pmpContMesesStandard.getFrequencia();
			}
			for (PmpContMesesStandard pmpContMesesStandard : mesesStandardList) {
				//frequencia = pmpContHorasStandard.getFrequencia();
				//Query para peças com desconto PDP
				query = manager.createNativeQuery("select sum(m.landcs * m.dlrqty) from ren_pmp_manutencao_preco_custo m"+
						" where m.cptcd = '"+pmpContMesesStandard.getStandardJobCptcd()+"'"+
						" and m.bgrp = '"+contrato.getBgrp()+"'"+
						" and substr(m.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
						" and (m.beqmsn = '"+contrato.getBeginRanger()+"' or m.beqmsn = '"+contrato.getEndRanger()+"') ");
				//" and m.stno ="+Integer.valueOf(FILIAL));
				custoPecas = (BigDecimal)query.getSingleResult();
				//Double custoNordesteTotal =  custoPecas.doubleValue() + ((custoPecas.doubleValue() * custoNordeste.doubleValue())/100);//aplicar custo nordeste
				//custoNordesteTotal = custoNordesteTotal - ((custoNordesteTotal * (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getDescPdp().doubleValue()))/100);//desconto PDP
				custoManutencao = custoManutencao + custoPecas.doubleValue();
				
			}
			
			//custo do TA
			//if(contrato.getTa().equals("S")){
				Double taCusto = (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getTaHoras().doubleValue() * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getHhTa().doubleValue());
				//taCusto = (totalHorasManutencao.intValue() / 2000) * taCusto;// total de revisoes de 2000 horas * custo do TA
				custoManutencao = custoManutencao + taCusto;
			//}
			
			
			//total horas para a manutenção
			query = manager.createNativeQuery("select sum (to_number(replace(h.frsdhr,'.',','))) from ren_pmp_hora h"+
					" where h.cptcd in (select hs.standard_job_cptcd from ren_pmp_cont_meses_standard hs where id_contrato = "+contrato.getId()+")"+
					" and h.bgrp = '"+contrato.getBgrp()+"'"+
					" and substr(h.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
					" and (h.beqmsn = '"+contrato.getBeginRanger()+"' or h.beqmsn = '"+contrato.getEndRanger()+"') ");
			BigDecimal totalHHManutencao = (BigDecimal)query.getSingleResult();
			custoManutencao = custoManutencao + (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getHhRental().doubleValue() * totalHHManutencao.doubleValue());//valor de hh
			custoManutencao = custoManutencao + ((contrato.getDistanciaGerador().doubleValue() * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getValorKmRental().doubleValue())*mesesStandardList.size());//valor de km vezes a quantidade de manutenções
			
			
			PrecoBean bean = new PrecoBean();
			bean = new PrecoBean();
			bean.setParcela(1);
			bean.setPreco("R$ "+ValorMonetarioHelper.formata("###,###.00", custoManutencao));
			result.add(bean);
			
			//}
			return result;
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return result;
	}
	
	public List<PrecoBean> findAllParcelasKIT2(Long idContrato){
		
		EntityManager manager = null;
		PmpContrato contrato = new PmpContrato();
		Double custoManutencao = 0d;
		Long totalHorasManutencao = 0l;
		BigDecimal custoPecas = BigDecimal.ZERO;
		
		List<PrecoBean> result = new ArrayList<PrecoBean>();
		try{
			manager = JpaUtil.getInstance();
			contrato = manager.find(PmpContrato.class, idContrato);
			
			//Long frequencia = 0l;
	
				BigDecimal custoNordeste = contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getCustoNordeste();
				Query query = manager.createQuery("From PmpContHorasStandard where idContrato.id = :id");
				query.setParameter("id", contrato.getId());
				List<PmpContHorasStandard> horasStandardList = query.getResultList();
				for (PmpContHorasStandard pmpContHorasStandard : horasStandardList) {
					totalHorasManutencao += pmpContHorasStandard.getFrequencia();
				}
				for (PmpContHorasStandard pmpContHorasStandard : horasStandardList) {
					//frequencia = pmpContHorasStandard.getFrequencia();
					//Query para peças com desconto PDP
					query = manager.createNativeQuery("select sum(m.unls * m.dlrqty) from ren_pmp_manutencao m"+
							" where m.cptcd = '"+pmpContHorasStandard.getStandardJobCptcd()+"'"+
							" and m.bgrp = '"+contrato.getBgrp()+"'"+
							" and substr(m.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
							" and (m.beqmsn = '"+contrato.getBeginRanger()+"' or m.beqmsn = '"+contrato.getEndRanger()+"') "+
					" and m.bectyc in (select pdp.descricao from ren_pmp_desconto_pdp pdp)");
					custoPecas = (BigDecimal)query.getSingleResult();
					Double custoNordesteTotal = 0.0;
					if(custoPecas != null){
						custoNordesteTotal =  custoPecas.doubleValue() + ((custoPecas.doubleValue() * custoNordeste.doubleValue())/100);//aplicar custo nordeste
						custoNordesteTotal = custoNordesteTotal - ((custoNordesteTotal * (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getDescPdp().doubleValue()))/100);//desconto PDP
						custoManutencao = custoManutencao + custoNordesteTotal;
					}
					//Query sem desconto pdp
					query = manager.createNativeQuery("select sum(m.unls * m.dlrqty) from ren_pmp_manutencao m"+
							" where m.cptcd = '"+pmpContHorasStandard.getStandardJobCptcd()+"'"+
							" and m.bgrp = '"+contrato.getBgrp()+"'"+
							" and substr(m.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
							" and (m.beqmsn = '"+contrato.getBeginRanger()+"' or m.beqmsn = '"+contrato.getEndRanger()+"') "+
							" and (m.bectyc not in (select pdp.descricao from ren_pmp_desconto_pdp pdp)"+
					" or m.bectyc is null)");
					custoPecas = (BigDecimal)query.getSingleResult();
					custoNordesteTotal = custoPecas.doubleValue() + ((custoPecas.doubleValue() * custoNordeste.doubleValue())/100);//aplicar custo nordeste	
					custoManutencao = custoManutencao + custoNordesteTotal;
				}
				//int totalManutencoes = 0;
				
				//totalHorasManutencao = frequencia * horasStandardList.size();
			
				
				//custo do TA
				if(contrato.getTa().equals("S")){
					Double taCusto = (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getTaHoras().doubleValue() * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getHhTa().doubleValue());
					taCusto = (totalHorasManutencao.intValue() / 2000) * taCusto;// total de revisoes de 2000 horas * custo do TA
					custoManutencao = custoManutencao + taCusto;
				}
				
				
				//total horas para a manutenção
				query = manager.createNativeQuery("select sum (to_number(replace(h.frsdhr,'.',','))) from ren_pmp_hora h"+
						" where h.cptcd in (select hs.standard_job_cptcd from ren_pmp_cont_horas_standard hs where id_contrato = "+contrato.getId()+")"+
						" and h.bgrp = '"+contrato.getBgrp()+"'"+
						" and substr(h.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
						" and (h.beqmsn = '"+contrato.getBeginRanger()+"' or h.beqmsn = '"+contrato.getEndRanger()+"') "+
						" and h.cptcd = 7504");
				BigDecimal totalHHManutencao = (BigDecimal)query.getSingleResult();
				custoManutencao = custoManutencao + (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getHhPmp().doubleValue() * totalHHManutencao.doubleValue());//valor de hh
				custoManutencao = custoManutencao + ((contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getKmPmp().doubleValue() * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getValorKmPmp().doubleValue())*1);//valor de km vezes a quantidade de manutenções
				
				
				PrecoBean bean = new PrecoBean();
//				bean.setParcela(1);
//				bean.setPreco("R$ "+ValorMonetarioHelper.formata("###,###.00", custoManutencao));
//				result.add(bean);
				for(int i = 0; i < 10; i++){
					custoManutencao = custoManutencao + ((custoManutencao * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getJurosVenda().doubleValue())/100);
					bean = new PrecoBean();
					bean.setParcela(i+1);
					bean.setPreco("R$ "+ValorMonetarioHelper.formata("###,###.00", custoManutencao));
					result.add(bean);
					
				}
				return result;
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return result;
	}
	public List<PrecoBean> findAllParcelasKIT2Custo(Long idContrato, String siglaCustomizacao){
		
		EntityManager manager = null;
		PmpContrato contrato = new PmpContrato();
		Double custoManutencao = 0d;
		Long totalHorasManutencao = 0l;
		BigDecimal custoPecas = BigDecimal.ZERO;
		
		List<PrecoBean> result = new ArrayList<PrecoBean>();
		try{
			manager = JpaUtil.getInstance();
			contrato = manager.find(PmpContrato.class, idContrato);
			String complemento = "";
			String complementoSigla="";
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
			//Long frequencia = 0l;
			
				//BigDecimal custoNordeste = contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getCustoNordeste();
				Query query = manager.createQuery("From PmpContHorasStandard where idContrato.id = :id order by horasManutencao");
				query.setParameter("id", contrato.getId());
				List<PmpContHorasStandard> horasStandardList = query.getResultList();
				for (PmpContHorasStandard pmpContHorasStandard : horasStandardList) {
					totalHorasManutencao += pmpContHorasStandard.getFrequencia();
				}
				for (PmpContHorasStandard pmpContHorasStandard : horasStandardList) {
					//frequencia = pmpContHorasStandard.getFrequencia();
					//Query para peças com desconto PDP
					query = manager.createNativeQuery("select sum(m.landcs * m.dlrqty) from ren_pmp_manutencao_preco_custo m"+
							" where m.cptcd = '"+pmpContHorasStandard.getStandardJobCptcd()+"'"+
							" and m.bgrp = '"+contrato.getBgrp()+"'"+
							" and substring(m.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
							complemento + complementoSigla +
							" and (m.beqmsn = '"+contrato.getBeginRanger()+"' or m.beqmsn = '"+contrato.getEndRanger()+"') ");
							//" and m.stno ="+Integer.valueOf(FILIAL));
					custoPecas = (BigDecimal)query.getSingleResult();
					//Double custoNordesteTotal =  custoPecas.doubleValue() + ((custoPecas.doubleValue() * custoNordeste.doubleValue())/100);//aplicar custo nordeste
					//custoNordesteTotal = custoNordesteTotal - ((custoNordesteTotal * (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getDescPdp().doubleValue()))/100);//desconto PDP
					custoManutencao = custoManutencao + custoPecas.doubleValue();
					
//					//Query sem desconto pdp
//					query = manager.createNativeQuery("select sum(m.unls) from pmp_manutencao m"+
//							" where m.cptcd = '"+pmpContHorasStandard.getStandardJobCptcd()+"'"+
//							" and m.bgrp = '"+contrato.getBgrp()+"'"+
//							" and substr(m.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
//							" and substr(m.beqmsn,5,10) between '"+contrato.getBeginRanger().substring(4, 9)+"' and '"+contrato.getEndRanger().substring(4, 9)+"'"+
//					" and m.bectyc not in (select pdp.descricao from pmp_desconto_pdp pdp)");
//					custoPecas = (BigDecimal)query.getSingleResult();
//					custoNordesteTotal = custoPecas.doubleValue() + ((custoPecas.doubleValue() * custoNordeste.doubleValue())/100);//aplicar custo nordeste	
//					custoManutencao = custoManutencao + custoNordesteTotal;
				}
				//int totalManutencoes = 0;
				
				//totalHorasManutencao = frequencia * horasStandardList.size();
				
				//custo do TA
				if(contrato.getTa().equals("S")){
					Double taCusto = (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getTaHoras().doubleValue() * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getValorHhTaCusto().doubleValue());
					taCusto = (totalHorasManutencao.intValue() / 2000) * taCusto;// total de revisoes de 2000 horas * custo do TA
					custoManutencao = custoManutencao + taCusto;
				}
				
				
				//total horas para a manutenção
				query = manager.createNativeQuery("select sum (to_number(replace(h.frsdhr,'.',','))) from ren_pmp_hora h"+
						" where h.cptcd in (select hs.standard_job_cptcd from ren_pmp_cont_horas_standard hs where id_contrato = "+contrato.getId()+")"+
						" and h.bgrp = '"+contrato.getBgrp()+"'"+
						" and substring(h.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
						" and (h.beqmsn = '"+contrato.getBeginRanger()+"' or h.beqmsn = '"+contrato.getEndRanger()+"')"+
				" and h.cptcd = 7504");
				BigDecimal totalHHManutencao = (BigDecimal)query.getSingleResult();
				custoManutencao = custoManutencao + (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getValorHhPmpCusto().doubleValue() * totalHHManutencao.doubleValue());//valor de hh
				custoManutencao = custoManutencao + ((contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getKmPmp().doubleValue() * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getValorKmPmpCusto().doubleValue())*1);//valor de km vezes a quantidade de manutenções
				
				
				PrecoBean bean = new PrecoBean();
//				bean.setParcela(1);
//				bean.setPreco("R$ "+ValorMonetarioHelper.formata("###,###.00", custoManutencao));
//				result.add(bean);
				//for(int i = 0; i < 10; i++){
					//custoManutencao = custoManutencao + ((custoManutencao * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getJurosVenda().doubleValue())/100);
					bean = new PrecoBean();
					bean.setParcela(1);
					bean.setPreco("R$ "+ValorMonetarioHelper.formata("###,###.00", custoManutencao));
					result.add(bean);
					
				//}
				return result;
			
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return result;
	}
	
	public List<PrecoBean> findAllParcelasRentalKIT2Custo(Long idContrato){
		
		EntityManager manager = null;
		PmpContrato contrato = new PmpContrato();
		Double custoManutencao = 0d;
		Long totalHorasManutencao = 0l;
		BigDecimal custoPecas = BigDecimal.ZERO;
		
		List<PrecoBean> result = new ArrayList<PrecoBean>();
		try{
			manager = JpaUtil.getInstance();
			contrato = manager.find(PmpContrato.class, idContrato);
			
			//Long frequencia = 0l;
		
				//BigDecimal custoNordeste = contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getCustoNordeste();
				Query query = manager.createQuery("From PmpContHorasStandard where idContrato.id = :id");
				query.setParameter("id", contrato.getId());
				List<PmpContHorasStandard> horasStandardList = query.getResultList();
				for (PmpContHorasStandard pmpContHorasStandard : horasStandardList) {
					totalHorasManutencao += pmpContHorasStandard.getFrequencia();
				}
				for (PmpContHorasStandard pmpContHorasStandard : horasStandardList) {
					//frequencia = pmpContHorasStandard.getFrequencia();
					//Query para peças com desconto PDP
					query = manager.createNativeQuery("select sum(m.landcs * m.dlrqty) from ren_pmp_manutencao_preco_custo m"+
							" where m.cptcd = '"+pmpContHorasStandard.getStandardJobCptcd()+"'"+
							" and m.bgrp = '"+contrato.getBgrp()+"'"+
							" and substr(m.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
							" and (m.beqmsn = '"+contrato.getBeginRanger()+"' or m.beqmsn = '"+contrato.getEndRanger()+"') ");
							//" and m.stno ="+Integer.valueOf(FILIAL));
					custoPecas = (BigDecimal)query.getSingleResult();
					//Double custoNordesteTotal =  custoPecas.doubleValue() + ((custoPecas.doubleValue() * custoNordeste.doubleValue())/100);//aplicar custo nordeste
					//custoNordesteTotal = custoNordesteTotal - ((custoNordesteTotal * (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getDescPdp().doubleValue()))/100);//desconto PDP
					custoManutencao = custoManutencao + custoPecas.doubleValue();
					
//					//Query sem desconto pdp
//					query = manager.createNativeQuery("select sum(m.unls) from pmp_manutencao m"+
//							" where m.cptcd = '"+pmpContHorasStandard.getStandardJobCptcd()+"'"+
//							" and m.bgrp = '"+contrato.getBgrp()+"'"+
//							" and substr(m.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
//							" and substr(m.beqmsn,5,10) between '"+contrato.getBeginRanger().substring(4, 9)+"' and '"+contrato.getEndRanger().substring(4, 9)+"'"+
//					" and m.bectyc not in (select pdp.descricao from pmp_desconto_pdp pdp)");
//					custoPecas = (BigDecimal)query.getSingleResult();
//					custoNordesteTotal = custoPecas.doubleValue() + ((custoPecas.doubleValue() * custoNordeste.doubleValue())/100);//aplicar custo nordeste	
//					custoManutencao = custoManutencao + custoNordesteTotal;
				}
				//int totalManutencoes = 0;
				
				//totalHorasManutencao = frequencia * horasStandardList.size();
				
				//custo do TA
				if(contrato.getTa().equals("S")){
					Double taCusto = (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getTaHoras().doubleValue() * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getHhTa().doubleValue());
					taCusto = (totalHorasManutencao.intValue() / 2000) * taCusto;// total de revisoes de 2000 horas * custo do TA
					custoManutencao = custoManutencao + taCusto;
				}
				
				
				//total horas para a manutenção
				query = manager.createNativeQuery("select sum (to_number(replace(h.frsdhr,'.',','))) from ren_pmp_hora h"+
						" where h.cptcd in (select hs.standard_job_cptcd from ren_pmp_cont_horas_standard hs where id_contrato = "+contrato.getId()+")"+
						" and h.bgrp = '"+contrato.getBgrp()+"'"+
						" and substr(h.beqmsn,1,4) = '"+contrato.getPrefixo()+"'"+
						" and (h.beqmsn = '"+contrato.getBeginRanger()+"' or h.beqmsn = '"+contrato.getEndRanger()+"') "+
				" and h.cptcd = 7504");
				BigDecimal totalHHManutencao = (BigDecimal)query.getSingleResult();
			
				custoManutencao = custoManutencao + (contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getHhRental().doubleValue() * totalHHManutencao.doubleValue());//valor de hh
				custoManutencao = custoManutencao + ((contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getKmRental().doubleValue() * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getValorKmRental().doubleValue())*1);//valor de km vezes a quantidade de manutenções
				
				
				PrecoBean bean = new PrecoBean();
//				bean.setParcela(1);
//				bean.setPreco("R$ "+ValorMonetarioHelper.formata("###,###.00", custoManutencao));
//				result.add(bean);
				//for(int i = 0; i < 10; i++){
					//custoManutencao = custoManutencao + ((custoManutencao * contrato.getIdConfigManutencao().getIdConfiguracaoPreco().getJurosVenda().doubleValue())/100);
					bean = new PrecoBean();
					bean.setParcela(1);
					bean.setPreco("R$ "+ValorMonetarioHelper.formata("###,###.00", custoManutencao));
					result.add(bean);
					
				//}
				return result;
		
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return result;
	}
	
	public List<MotNaoFecContratoBean> findAllMotNaoFecContrato(){
		
		EntityManager manager = null;
		List<MotNaoFecContratoBean> mnfcResultList = new ArrayList<MotNaoFecContratoBean>();
		try {
			manager = JpaUtil.getInstance();
			Query query = manager.createQuery("from PmpMotivosNaoFecContrato");
			List<PmpMotivosNaoFecContrato> mnfcList = query.getResultList();
			for (PmpMotivosNaoFecContrato mnfc : mnfcList) {
				MotNaoFecContratoBean bean = new MotNaoFecContratoBean();
				bean.setDescricao(mnfc.getDescricao());
				bean.setId(mnfc.getId().longValue());
				bean.setSigla(mnfc.getSigla());
				mnfcResultList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return mnfcResultList;
	}

	public List<ContratoComercialBean> 	findAllContratosAbertos(){
		
		EntityManager manager = null;
		List<ContratoComercialBean> result = new ArrayList<ContratoComercialBean>();
		try {
			manager = JpaUtil.getInstance();
			Query query = manager.createQuery("from PmpContrato where idStatusContrato.id in(select id from PmpStatusContrato where sigla = 'CA')");
			List<PmpContrato> mnfcList = query.getResultList();
			for (PmpContrato contrato : mnfcList) {
				ContratoComercialBean bean = new ContratoComercialBean();
				bean.fromBean(contrato, usuarioBean);
				result.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return result;
	}
	
	public Boolean removerContrato(Long idContrato){
		EntityManager manager = null;
		try {
			manager = JpaUtil.getInstance();
			manager.getTransaction().begin();
			manager.remove(manager.find(PmpContrato.class, idContrato));
			manager.getTransaction().commit();
			return true;
		} catch (Exception e) {
			if(manager != null && manager.getTransaction().isActive()){
				manager.getTransaction().rollback();
			}
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return false;
	}
	
	public Boolean buscarStanderJob() {
		ResultSet rs = null;
		PreparedStatement prstmt = null;

		Connection con = null;
		//while(isLoadService){
			try {
//				InputStream in = getClass().getClassLoader().getResourceAsStream(IConstantAccess.CONF_FILE);
//				Properties prop = new Properties();
//				prop.load(in);
//				String url = prop.getProperty("dbs.url");
//				String user = prop.getProperty("dbs.user");
//				String password =prop.getProperty("dbs.password");
//				Class.forName(prop.getProperty("dbs.driver")).newInstance();

				con = com.pmprental.util.ConectionDbs.getConnecton(); 

				prstmt = con.prepareStatement(SQL_FIND_ALL_PMP);
				rs = prstmt.executeQuery();
				EntityManager manager = null;
				//boolean isConnection = true;
				//while(isConnection){
					try {
						manager = JpaUtil.getInstance();

						manager.getTransaction().begin();
						Query query = manager.createNativeQuery("delete from ren_pmp_manutencao");
						query.executeUpdate();
						query = manager.createNativeQuery("delete from ren_pmp_manutencao_preco_custo");
						query.executeUpdate();
						query = manager.createNativeQuery("delete from ren_pmp_hora");
						query.executeUpdate();
						query = manager.createNativeQuery("delete from ren_pmp_range");
						query.executeUpdate();
//						query = manager.createNativeQuery("delete from pmp_cliente_pl");
//						query.executeUpdate();
						//manager.getTransaction().commit();
						while(rs.next()){
							//manager.getTransaction().begin();
							PmpManutencao pManutencao = new PmpManutencao();
					
							pManutencao.setBectyc(rs.getString("BECTYC"));
							pManutencao.setDlrqty(Long.valueOf((rs.getString("DLRQTY"))));
							pManutencao.setDs18(rs.getString("DS18"));
							pManutencao.setUncs(new BigDecimal(rs.getString("UNCS")));
							pManutencao.setUnls(new BigDecimal(rs.getString("UNLS")));
							pManutencao.setSos(rs.getString("SOS1"));
							pManutencao.setOcptmd(rs.getString("OCPTMD"));
							pManutencao.setJwkapp(rs.getString("JWKAPP"));
							pManutencao.setOjbloc(rs.getString("OJBLOC"));
							pManutencao.setBgrp(rs.getString("BGRP"));
							pManutencao.setBeqmsn(rs.getString("BEQMSN"));
							pManutencao.setPano20(rs.getString("PANO20"));
							pManutencao.setCptcd(rs.getString("CPTCD"));
							manager.persist(pManutencao);
							//manager.getTransaction().commit();
						}
//						
						prstmt = con.prepareStatement(SQL_FIND_ALL_PMP_POR_FILIAL);
						rs = prstmt.executeQuery();
						while(rs.next()){
							//manager.getTransaction().begin();
							PmpManutencaoPrecoCusto pManutencao = new PmpManutencaoPrecoCusto();
							pManutencao.setBectyc(rs.getString("BECTYC"));
							pManutencao.setDlrqty(Long.valueOf((rs.getString("DLRQTY"))));
							pManutencao.setDs18(rs.getString("DS18"));
							pManutencao.setLandcs(new BigDecimal(rs.getString("LANDCS")));
							pManutencao.setOcptmd(rs.getString("OCPTMD"));
							pManutencao.setJwkapp(rs.getString("JWKAPP"));
							pManutencao.setOjbloc(rs.getString("OJBLOC"));
							pManutencao.setBgrp(rs.getString("BGRP"));
							pManutencao.setBeqmsn(rs.getString("BEQMSN"));
							pManutencao.setPano20(rs.getString("PANO20"));
							pManutencao.setCptcd(rs.getString("CPTCD"));
							manager.persist(pManutencao);
							//manager.getTransaction().commit();
						}
				
						prstmt = con.prepareStatement(SQL_HORAS_PMP);
						rs = prstmt.executeQuery();
						while(rs.next()){
							//manager.getTransaction().begin();
							PmpHora pmpHora = new PmpHora();
							RentPmpHoraPK pmpHoraPK = new RentPmpHoraPK(rs.getString("JBCD"), rs.getString("CPTCD"),rs.getString("BEQMSN"), rs.getString("BGRP") );
							pmpHora.setEqmfcd(rs.getString("EQMFCD"));
							pmpHora.setEqmfmd(rs.getString("EQMFMD"));
							pmpHora.setFrsdhr(rs.getString("FRSDHR"));
							pmpHora.setTipoPm(rs.getString("TIPO_PM"));
							pmpHora.setPmpHoraPK(pmpHoraPK);
							manager.merge(pmpHora);
							//manager.getTransaction().commit();
						}
						
						prstmt = con.prepareStatement(SQL_RANGE);
						rs = prstmt.executeQuery();
						while(rs.next()){
							//manager.getTransaction().begin();
							PmpRange pmpRange = new PmpRange();
							RentPmpRangePK pmpHoraPK = new RentPmpRangePK(rs.getString("BEGSN2"), rs.getString("ENDSN2"),rs.getString("EQMFM2") );
							pmpRange.setBserno(rs.getString("BSERNO"));
							pmpRange.setEqmfcd(rs.getString("EQMFCD"));
							pmpRange.setPmpRangePK(pmpHoraPK);
							manager.merge(pmpRange);
							//manager.getTransaction().commit();
						}


						
//						prstmt = con.prepareStatement(SQL_FIND_CUSTOMER_MACHINE);
//						rs = prstmt.executeQuery();
//						while(rs.next()){
//							//manager.getTransaction().begin();
//							PmpClientePl clientePl = new PmpClientePl();
//							clientePl.setSerie(rs.getString("EQMFS2"));
//							clientePl.setCodCliente(rs.getString("CUNO"));
//							clientePl.setNomeCliente(rs.getString("CUNM"));
//							clientePl.setFilial(Long.valueOf(rs.getString("STN1")));
//							manager.merge(clientePl);
//							//manager.getTransaction().commit();
//						}
//						prstmt = con.prepareStatement(SQL_FIND_FILIAL);
//						rs = prstmt.executeQuery();
//						while(rs.next()){
//							//manager.getTransaction().begin();
//							TwFilial twFilial = new TwFilial();
//							twFilial.setStno(new Long(rs.getInt("STNO")));
//							twFilial.setStnm(rs.getString("STNM"));
//							try {
//								TwFilial filial = manager.find(TwFilial.class, twFilial.getStno());
//								filial.setStnm(twFilial.getStnm());
//								manager.merge(filial);
//							} catch (Exception e) {
//								e.printStackTrace();
//								manager.merge(twFilial);
//							}
//							//manager.getTransaction().commit();
//						}
						
						manager.getTransaction().commit();
						//isLoadService = false;
						EmailHelper emailHelper = new EmailHelper();
						emailHelper.sendSimpleMail("Serviço de importação de Standard Job executado", "Standard Job", "rodrigo@rdrsistemas.com.br");
						//System.out.println("ImportaÃ§Ã£o realizada com sucesso");
						//isConnection = false;
						return true;
					} catch (Exception e1) {
						if(manager != null && manager.getTransaction().isActive()){
							manager.getTransaction().rollback();
						}
						e1.printStackTrace();
						EmailHelper emailHelper = new EmailHelper();
						//emailHelper.sendSimpleMail("Erro ao executar a busca do STANDARD JOB no DBS", "ERRO STANDARD JOB", "ti.monitoramento@marcosa.com.br");
						emailHelper.sendSimpleMail("Erro ao executar a busca do STANDARD JOB no DBS da PESA", "ERRO STANDARD JOB", "rodrigo@rdrsistemas.com.br");
					} finally {
						if(manager != null && manager.isOpen()){
							manager.close();
						}
					}
				//}
			}catch (Exception e) {
				e.printStackTrace();
				EmailHelper emailHelper = new EmailHelper();
				//emailHelper.sendSimpleMail("Erro ao tentar conectar no DBS (STANDARD JOB)", "ERRO STANDARD JOB", "ti.monitoramento@marcosa.com.br");
				emailHelper.sendSimpleMail("Erro ao tentar conectar no DBS (STANDARD JOB) da PESA", "ERRO STANDARD JOB", "rodrigo@rdrsistemas.com.br");
			}finally{
				
				try {
					rs.close();
					prstmt.close();
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			return false;
	}
	
	public Boolean sincronizarStanderJob(){
		return this.buscarStanderJob();

	}
	
	public Boolean terminarContrato(Long idContrato){
		EntityManager manager = null;
		try {
			manager = JpaUtil.getInstance();
			manager.getTransaction().begin();
			Query query = manager.createNativeQuery("delete from REN_PMP_CONT_HORAS_STANDARD where IS_EXECUTADO = 'N' and ID_OS_OPERACIONAL is null and ID_CONTRATO =:ID_CONTRATO");
			query.setParameter("ID_CONTRATO", idContrato);
			query.executeUpdate();
			PmpContrato contrato = manager.find(PmpContrato.class, idContrato);
			contrato.setIsAtivo("I");
			manager.merge(contrato);
			manager.getTransaction().commit();
			return true;
		} catch (Exception e) {
			if(manager.getTransaction().isActive()){
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
	public String findIdEquipamento(OperacionalBean bean)throws Exception{
		ResultSet rs = null;
		PreparedStatement prstmt = null;

		Connection con = null;
		EntityManager manager = null;
		String idEquipamento = null;
		//while(isLoadService){
			try {
				manager = JpaUtil.getInstance();
				con = com.pmprental.util.ConectionDbs.getConnecton(); 
				String SQL = "select f.IDNO1 from "+IConstantAccess.LIB_DBS+".empeqpd0 f where f.EQMFS2 = '"+bean.getNumeroSerie()+"'";

				prstmt = con.prepareStatement(SQL);
				rs = prstmt.executeQuery();
				if(rs.next()){
					idEquipamento = rs.getString("IDNO1");
					PmpContrato contrato = manager.find(PmpContrato.class, bean.getContratoId());
					manager.getTransaction().begin();
					contrato.setIdEquipamento(idEquipamento);
					manager.merge(contrato);
					manager.getTransaction().commit();
					return idEquipamento;
				}
			}catch (Exception e) {
				if(manager.getTransaction().isActive()){
					manager.getTransaction().rollback();
				}
				e.printStackTrace();
			} finally {
				if(manager != null){
					manager.close();
				}
				prstmt.close();
				con.close();
			}
			return null;
	}
	

}
