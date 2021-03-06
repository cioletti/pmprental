package com.pmprental.business;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.pmprental.bean.ConfigOperacionalBean;
import com.pmprental.bean.FilialBean;
import com.pmprental.bean.OperacionalBean;
import com.pmprental.entity.PmpConfigOperacional;
import com.pmprental.entity.PmpContrato;
import com.pmprental.util.EmailHelper;
import com.pmprental.util.JpaUtil;

public class ConfigOperacionalBusiness {

	
	public List<FilialBean> findAllFilial(){
		EntityManager manager = null;
		List<FilialBean> filialList = new ArrayList<FilialBean>();
		try {
			manager = JpaUtil.getInstance();
			Query query = manager.createQuery("select stno, stnm from TwFilial order by stnm");
			List<Object[]> result = query.getResultList();
			for (Object [] filial: result) {
				FilialBean bean = new FilialBean();
				bean.setStno((Long)filial[0]);
				bean.setStnm(filial[1].toString());
				filialList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return filialList;
	}
	
	
	public ConfigOperacionalBean findConfigOperacional(Long idContrato){
		ConfigOperacionalBean co = new ConfigOperacionalBean();
		EntityManager manager = null;

		try {
			manager = JpaUtil.getInstance();

			Query query = manager.createQuery("From PmpConfigOperacional Where idContrato.id = :idContrato");
			query.setParameter("idContrato", idContrato);
			List<PmpConfigOperacional> result = query.getResultList();
			if(result.size() > 0){				
				co.formBean(result.get(0));	
			}					
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return co;
	}
	
	public ConfigOperacionalBean saveOrUpdate(ConfigOperacionalBean bean){
		EntityManager manager = null;
		try {
			manager = JpaUtil.getInstance();
			manager.getTransaction().begin();
			PmpContrato pmpContrato = manager.find(PmpContrato.class,bean.getIdContrato());
			pmpContrato.setEmailContatoServicos(bean.getEmail());
			pmpContrato.setPrefixo(bean.getNumeroSerie().substring(0,4));
			pmpContrato.setNumeroSerie(bean.getNumeroSerie());
			pmpContrato.setEmailChecklist(bean.getEmailCheckList());
			pmpContrato.setFilial(bean.getFilial().intValue());
			PmpConfigOperacional configOperacional = null;
			
			if(bean.getId() == null || bean.getId() == 0){
				configOperacional = new PmpConfigOperacional();
				bean.toBean(configOperacional);
				if(bean.getMediaHorasMes() != null){
					configOperacional.getIdContrato().setMediaHorasMes(bean.getMediaHorasMes().longValue());
				}
				configOperacional.setIdContrato(pmpContrato);
				manager.persist(configOperacional);
			}else{
				configOperacional = manager.find(PmpConfigOperacional.class, bean.getId());
				bean.toBean(configOperacional);
				if(bean.getMediaHorasMes() != null){
					configOperacional.getIdContrato().setMediaHorasMes(bean.getMediaHorasMes().longValue());
				}
				configOperacional.setIdContrato(pmpContrato);
				manager.merge(configOperacional);
			}
			manager.getTransaction().commit();
			bean.setId(configOperacional.getId());
			ContratoBusiness business = new ContratoBusiness();
			OperacionalBean operacionalBean = new OperacionalBean();
			operacionalBean.setContratoId(pmpContrato.getId());
			operacionalBean.setNumeroSerie(pmpContrato.getNumeroSerie());
			business.findIdEquipamento(operacionalBean);
			return bean;
			
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
		return null;
	}
	
	public Boolean enviarObsCliente(String obsEmail, String email) {
		
		try {
			EmailHelper helper = new EmailHelper();
			if(helper.sendSimpleMail(""+",Gostaríamos de informar que:\n"+obsEmail, "Manutenção PMP", email)){
				return true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();			
		}
		
		return false;
	}
	
	public static void main(String[] args) {
		System.out.println("0GCT00714".substring(0,4));
		System.out.println("0GCT00714".substring(4,"0GCT00714".length()));
	}



}
