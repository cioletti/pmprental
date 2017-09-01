package com.pmprental.util;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import webservices.framew2.com.br.InterfaceExternaServicePortTypeProxy;
import br.com.framew2.webservices.xsd.Evento;
import br.com.framew2.webservices.xsd.RetornoInterfaceExterna;

import com.pmprental.entity.AdmLocalizacaoVeiculo;

public class JobFindLocalizacaoVeiculo implements Job{
	final int id = 78; // colocar 0 id - 15 = Teste
	final String pass = "872b578e9e7c8c3b9601681950e161c3"; // colocar a senha
	final static SimpleDateFormat sdfF = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", new Locale("pt", "BR"));
	final static SimpleDateFormat sdfP = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", new Locale("pt", "BR"));
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		EntityManager manager = null;
		Query query = null;

		try {
			manager = JpaUtil.getInstance();

			InterfaceExternaServicePortTypeProxy proxy = new InterfaceExternaServicePortTypeProxy(); 
			sdfP.setTimeZone(TimeZone.getTimeZone("GMT-0"));
			sdfF.setTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"));
			Long count = 0L;

			try
			{
				RetornoInterfaceExterna ie = proxy.obterUltimaPosicao(id, pass);
				Evento[] evts = ie.getEventos();
				if ( evts != null && evts.length != 0 ){
					manager.getTransaction().begin();
					query = manager.createNativeQuery("delete from ADM_LOCALIZACAO_VEICULO");
					query.executeUpdate();
					for ( Evento evt : evts ){
						count++;
						if ( evt == null ){
							//System.out.println(count + " --->> vazio...");
							continue;
						}
						try{
							Date dataequipamento = sdfP.parse(evt.getDataEquipamento());
							// gravar em algum lugar
							AdmLocalizacaoVeiculo admLocalizacaoVeiculo = new AdmLocalizacaoVeiculo();
							admLocalizacaoVeiculo.setDataReport(sdfF.format(dataequipamento));
							admLocalizacaoVeiculo.setId(count);
							admLocalizacaoVeiculo.setLatitude(evt.getLatitude().toString());
							admLocalizacaoVeiculo.setLongitude(evt.getLongitude().toString());
							admLocalizacaoVeiculo.setLocalizacao(evt.getLocalizacao());
							admLocalizacaoVeiculo.setPlaca(evt.getPlaca());
							admLocalizacaoVeiculo.setVelocidade(evt.getVelocidade().longValue());
							manager.persist(admLocalizacaoVeiculo);
							// as datas est‹o em UTC
							//System.out.println(count + " --->> " + evt.getPlaca() + "," + sdfF.format(dataequipamento) + "," + evt.getVelocidade()+"."+evt.getLatitude());
						}catch(Exception ex){
							ex.printStackTrace();
						}
					}
					manager.getTransaction().commit();
				}
			}
			catch (RemoteException e)
			{
				EmailHelper emailHelper = new EmailHelper();
	        	emailHelper.sendSimpleMail("Erro ao executar rotina de localizar veículo!", "Erro rotina de localizar veículo", "rodrigo@rdrsistemas.com.br");
				e.printStackTrace();
			}

		}catch (Exception e) {
			if(manager != null && manager.getTransaction().isActive()){
				manager.getTransaction().rollback();
			}
			EmailHelper emailHelper = new EmailHelper();
        	emailHelper.sendSimpleMail("Erro ao executar rotina de localizar veículo!", "Erro rotina de localizar veículo", "rodrigo@rdrsistemas.com.br");
			e.printStackTrace();
		} finally{
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
	}
}
