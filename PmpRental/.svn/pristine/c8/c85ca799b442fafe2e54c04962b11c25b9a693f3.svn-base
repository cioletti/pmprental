package com.pmprental.util;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import flex.messaging.io.ArrayList;

public class JobKillProcess implements Job {
	private static List<Short> spidLockList = new ArrayList();
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
        EntityManager manager = null;
        try {
        	manager = JpaUtil.getInstance();
        	List<Short> spidLockAux = new ArrayList();
        	Query query = manager.createNativeQuery("SELECT spid FROM MASTER.DBO.SYSPROCESSES BLOCKING WHERE BLOCKING.BLOCKED = 0 and LOGINAME = 'control_dse'   AND EXISTS "+ 
       											 " (SELECT 1 FROM MASTER.DBO.SYSPROCESSES BLOCKED WHERE BLOCKED.BLOCKED = BLOCKING.SPID )");
        	List<Short> spidList = query.getResultList();
        	for (Short spid : spidList) {
        		spidLockAux.add(spid);
        		try {
        			for (Short spidLock : spidLockList) {
        				if(spidLock.equals(spid)){
        					manager.getTransaction().begin();
        					query = manager.createNativeQuery("kill "+spid);
        					query.executeUpdate();
        					manager.getTransaction().commit();
        					spidList.remove(spidLock);
        					continue;
        				}
        			}
        			spidLockAux.add(spid);
        		} catch (Exception e) {
        		}
        	}
        	spidLockList.clear();
        	spidLockList.addAll(spidLockAux);
        } catch (Exception e) {
        	if(manager != null && manager.getTransaction().isActive()){
        		manager.getTransaction().rollback();
        	}
        	EmailHelper emailHelper = new EmailHelper();
        	emailHelper.sendSimpleMail("Erro ao matar processo!", "Erro ao matar processo!", "rodrigo@rdrsistemas.com.br");
        	e.printStackTrace();

        }finally{
        	if(manager != null && manager.isOpen()){
        		manager.close();
        	}

        }

	}

}
