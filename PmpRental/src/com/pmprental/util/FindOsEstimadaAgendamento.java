package com.pmprental.util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.pmprental.bean.UsuarioBean;
import com.pmprental.business.OsBusiness;
import com.pmprental.entity.PmpOsOperacional;

public class FindOsEstimadaAgendamento implements Job {

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		Connection con = null;
        Statement prstmt = null;
        Statement prstmtSegmento = null;
        EntityManager manager = null;
        ResultSet rs = null;
        try {
        	con = com.pmprental.util.ConectionDbs.getConnecton(); 
        	prstmt = con.createStatement();
        	prstmtSegmento = con.createStatement();
        	manager = JpaUtil.getInstance();

        	Query query = manager.createQuery("From PmpOsOperacional where codErroOsDbs in ('100', '99')");
        	List<PmpOsOperacional> osOperacionalList = query.getResultList();
        	String pair = "";
        	for (PmpOsOperacional operacional : osOperacionalList) {
				pair += "'"+operacional.getId().toString()+"-S',";
			}
        	pair = (pair.length() > 0)?pair.substring(0, pair.length()-1):"''";
        	String SQL = "select TRIM(DESCERR) DESCERR, TRIM(CODERR) CODERR, TRIM(WONO) WONO, TRIM(WONOSM) WONOSM from "+IConstantAccess.AMBIENTE_DBS+".USPWOSM0 where LENGTH(TRIM(CODERR)) > 0 and TRIM(WONOSM) in("+pair+")";
        	rs = prstmt.executeQuery(SQL);
        	while (rs.next()){		            	 
        		String CODERR = rs.getString("CODERR").trim();				
        		String WONO = rs.getString("WONO").trim();				
        		String DESCERR = rs.getString("DESCERR").trim();
        		String [] aux = rs.getString("WONOSM").trim().split("-");
        		if(aux.length > 1){
        			String PEDSM = aux[0];
        			manager.getTransaction().begin();
        			PmpOsOperacional osOperacional = manager.find(PmpOsOperacional.class, Long.valueOf(PEDSM));
        		       		        		
        			if(osOperacional != null){
        				
        				if(CODERR.equals("00")){
        					//Coloca a OS como Open
        					UsuarioBean bean = new UsuarioBean();
        					bean.setFilial(osOperacional.getFilial().toString());
        					
        					//osOperacional.setMsg("OS Criada com sucesso!");
        					osOperacional.setCodErroOsDbs("00");
        					osOperacional.setNumOs(WONO);
        					query = manager.createNativeQuery("select sum(cast(FRSDHR as decimal(18,2))) from ren_pmp_hora h"+
        							" where h.cptcd = '"+osOperacional.getCompCode()+"'"+
        							" and h.bgrp = '"+osOperacional.getIdConfigOperacional().getIdContrato().getBgrp()+"'"+
        							" and substring(h.beqmsn,1,4) = '"+osOperacional.getIdConfigOperacional().getIdContrato().getPrefixo()+"'"+
        							" and (h.beqmsn = '"+osOperacional.getIdConfigOperacional().getIdContrato().getBeginRanger()+"' or h.beqmsn = '"+osOperacional.getIdConfigOperacional().getIdContrato().getEndRanger()+"')");

        					BigDecimal totalHHManutencao = BigDecimal.ZERO;
        					BigDecimal valorMO = BigDecimal.ZERO;
        					if(query.getResultList().size() > 0){
        						totalHHManutencao = (BigDecimal)query.getSingleResult();
        						if(totalHHManutencao != null){
        							valorMO =  osOperacional.getIdConfigOperacional().getIdContrato().getIdConfigManutencao().getIdConfiguracaoPreco().getHhPmp().multiply(totalHHManutencao);//valor de hh
        							valorMO = valorMO.add(osOperacional.getIdConfigOperacional().getIdContrato().getIdConfigManutencao().getIdConfiguracaoPreco().getValorKmPmp().multiply(BigDecimal.valueOf(osOperacional.getIdConfigOperacional().getIdContrato().getIdConfigManutencao().getIdConfiguracaoPreco().getKmPmp().doubleValue())));
        						}else{
        							totalHHManutencao = BigDecimal.ZERO;
        						}
        					}
        					
        					if(osOperacional.getIdConfigOperacional().getIdContrato().getIsSpot() != null && osOperacional.getIdConfigOperacional().getIdContrato().getIsSpot().equals("S")){
    							valorMO = osOperacional.getIdContHorasStandard().getCustoMo();
    						}
        					
        					int decimalPlace = 2;
        					valorMO = valorMO.setScale(decimalPlace,BigDecimal.ROUND_HALF_UP);
        					String totalMO = valorMO.toString().replace(".", "");
        					for (int i = totalMO.length();  i < 13; i++) {
        						totalMO = "0"+totalMO;
        					}
        					
        					String horas = totalHHManutencao.toString().replace(".", "");
//        					horas = new Integer(Integer.valueOf(horas) * segmentoBean.getQtdComp()).toString();
        					if(horas.length() == 3){
        						horas = "00"+horas;
        					}else if(horas.length() == 4){
        						horas = "0"+horas;
        					}else if(horas.length() == 1){
        						horas = "0000"+horas;
        					}else if(horas.length() == 2){
        						horas = "000"+horas;
        					}
        					
        					//Enviar os segmentos para o DBS
        					pair = "'"+osOperacional.getId().toString()+"-S','"+osOperacional.getNumeroSegmento()+"','"+osOperacional.getCscc()+"','"+osOperacional.getJobCode()+"','"+osOperacional.getCompCode()+"','"+osOperacional.getInd()+"','F','"+horas+"','F','"+totalMO+"','00001'";
        					//con = DriverManager.getConnection(url, user, password);  
        					try {
        						SQL = "insert into "+IConstantAccess.AMBIENTE_DBS+".USPSGSM0 (wonosm, wosgno, cscc, jbcd, cptcd, ind, shpfld, frsthr, lbrate, lbamt, qty) values("+pair+")";
        						//prstmt_ = con.prepareStatement(SQL);
        						prstmtSegmento.executeUpdate(SQL);

        						prstmtSegmento = con.createStatement();
        						Thread.sleep(10000);
        						//Envia as peças para o DBS
        						new OsBusiness(bean).sendPecasDbs(osOperacional, manager, prstmtSegmento);
        						osOperacional.setMsg("Aguarde o retorno da cotação!");
        						osOperacional.setCodErroDocDbs("100");
        						new OsBusiness(bean).openOs(osOperacional.getId(), WONO, "S");//S = Schedule
        					} catch (Exception e) {
        						e.printStackTrace();
        					}
        					
        				}else{
        					osOperacional.setMsg(DESCERR);
        					osOperacional.setNumOs(DESCERR);
        					//osOperacional.setNumOs(DESCERR);
        					osOperacional.setCodErroOsDbs("99");
        				}	
        			} else {
        				manager.getTransaction().commit();
        				continue;   				
        			}
        			manager.getTransaction().commit();
        		
        		}
        	}
        } catch (Exception e) {
        	if(manager != null && manager.getTransaction().isActive()){
        		manager.getTransaction().rollback();
        	}
        	EmailHelper emailHelper = new EmailHelper();
        	emailHelper.sendSimpleMail("Erro ao recuperar OS de Agendamento!", "Erro ao Buscar OS Agendamento", "rodrigo@rdrsistemas.com.br");
        	e.printStackTrace();
        	
        }finally{
        	try {
        		if(manager != null && manager.isOpen()){
        			manager.close();
        		}
        		if(rs != null){
        			rs.close();
        		}
				if(prstmtSegmento != null){
					prstmtSegmento.close();
				}
				if(prstmt != null){
					prstmt.close();
				}
				if(con != null){
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
        	
        }
		
	}

}
