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
import com.pmprental.entity.PmpConfigOperacional;
import com.pmprental.entity.PmpContHorasStandard;

public class FindOsEstimadaContrato implements Job {

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

        	Query query = manager.createQuery("From PmpConfigOperacional where codErroDbs = '100'");
        	List<PmpConfigOperacional> osOperacionalList = query.getResultList();
        	String pair = "";
        	for (PmpConfigOperacional operacional : osOperacionalList) {
				pair += "'"+operacional.getId().toString()+"-B',";
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
        			PmpConfigOperacional osOperacional = manager.find(PmpConfigOperacional.class, Long.valueOf(PEDSM));
        		       		        		
        			if(osOperacional != null){
        				
        				if(CODERR.equals("00")){
        					//osOperacional.setMsg("OS Criada com sucesso!");
        					//Coloca a OS como Open
        					UsuarioBean bean = new UsuarioBean();
        					bean.setFilial(osOperacional.getFilial().toString());
        					
        					osOperacional.setCodErroDbs("00");
        					osOperacional.setNumOs(WONO);
        					BigDecimal valorContrato = osOperacional.getIdContrato().getValoContrato();
        					int decimalPlace = 2;
        					valorContrato = valorContrato.setScale(decimalPlace,BigDecimal.ROUND_HALF_UP);
        					String total = valorContrato.toString().replace(".", "");
        					for (int i = total.length();  i < 13; i++) {
        						total = "0"+total;
        					}
        					
        					query = manager.createQuery("From PmpContHorasStandard where idContrato.id =:idContrato");
        					query.setParameter("idContrato", osOperacional.getIdContrato().getId());
        				       				     					
        					List<PmpContHorasStandard> contHorasStandardList = query.getResultList();
        					BigDecimal totalHHManutencao = BigDecimal.ZERO;
        					for (PmpContHorasStandard standard : contHorasStandardList) {
        						query = manager.createNativeQuery("select sum(cast(FRSDHR as decimal(18,2))) from ren_pmp_hora h"+
        								" where h.cptcd = '"+standard.getStandardJobCptcd()+"'"+
        								" and h.bgrp = '"+osOperacional.getIdContrato().getBgrp()+"'"+
        								" and substring(h.beqmsn,1,4) = '"+osOperacional.getIdContrato().getPrefixo()+"'"+
        								" and (h.beqmsn = '"+osOperacional.getIdContrato().getBeginRanger()+"' or h.beqmsn = '"+osOperacional.getIdContrato().getEndRanger()+"') ");
        						totalHHManutencao = totalHHManutencao.add((BigDecimal)query.getSingleResult());
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
        					pair = "'"+osOperacional.getId().toString()+"-B','"+osOperacional.getNumeroSegmento()+"','"+osOperacional.getCscc()+"','"+osOperacional.getJobCode()+"','"+osOperacional.getCompCode()+"','"+osOperacional.getInd()+"','F','F','"+total+"','"+horas+"','00001'";
        					//con = DriverManager.getConnection(url, user, password);  
        					SQL = "insert into "+IConstantAccess.AMBIENTE_DBS+".USPSGSM0 (wonosm, wosgno, cscc, jbcd, cptcd, ind, shpfld, lbrate, lbamt,frsthr ,qty) values("+pair+")";
        					//prstmt_ = con.prepareStatement(SQL);
        					prstmtSegmento.executeUpdate(SQL);
//        					prstmtSegmento = con.createStatement();
//        					//Envia as peças para o DBS
//        					new OsBusiness(null).sendPecasDbs(osOperacional, manager, prstmtSegmento);
//        					osOperacional.setMsg(null);
//        					osOperacional.setCodErroDocDbs("100");
        					Thread.sleep(15000);
        					new OsBusiness(bean).openOs(osOperacional.getId(), WONO, "B");
        					
        				}else{
        					osOperacional.setNumOs(DESCERR);
        					//osOperacional.setNumOs(DESCERR);
        					osOperacional.setCodErroDbs("99");
        				}	
        			} else {
        				manager.getTransaction().commit();
        				continue;   				
        			}
        			manager.getTransaction().commit();
        			if(osOperacional.getIdContrato().getIdTipoContrato().getSigla().equals("VPG")){
        				query = manager.createQuery("from PmpContHorasStandard where idContrato.id =:id");
        				query.setParameter("id", osOperacional.getIdContrato().getId());
        				List<PmpContHorasStandard> horasStandards = query.getResultList();
        				PmpContHorasStandard contHorasStandardRevisao1 = horasStandards.get(0);
        				PmpContHorasStandard contHorasStandardRevisao2 = horasStandards.get(horasStandards.size() - 1);

        				ConectionDbs.setNotesFluxoOSDBS(osOperacional.getIdContrato().getNumeroContrato(), con, osOperacional.getNumOs(), "24").close();
        				ConectionDbs.setNotesFluxoOSDBS(osOperacional.getIdContrato().getModelo(), con, osOperacional.getNumOs(), "25").close();
        				ConectionDbs.setNotesFluxoOSDBS(osOperacional.getIdContrato().getNumeroSerie(), con, osOperacional.getNumOs(), "26").close();
        				ConectionDbs.setNotesFluxoOSDBS(contHorasStandardRevisao1.getHorasManutencao()+" a "+contHorasStandardRevisao2.getHorasManutencao(), con, osOperacional.getNumOs(), "27").close();
        				ConectionDbs.setNotesFluxoOSDBS("Visitas "+horasStandards.size(), con, osOperacional.getNumOs(), "28").close();
        				ConectionDbs.setNotesFluxoOSDBS(osOperacional.getIdContrato().getIdTipoContrato().getDescricao(), con, osOperacional.getNumOs(), "29").close();
        			}
        		
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
