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

import com.pmprental.entity.PmpConfigOperacional;
import com.pmprental.entity.PmpContrato;

public class JobFindLocalMaquinaCliente implements Job{

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		EntityManager manager = null;
//		InputStream in = getClass().getClassLoader().getResourceAsStream(IConstantAccess.CONF_FILE);
//		Properties prop = new Properties();
		Connection con = null;
		Statement prstmt = null;
		Statement prstmt2 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		try {
			con = ConectionDbs.getConnecton();
			prstmt = con.createStatement();
			prstmt2 = con.createStatement();
			manager = JpaUtil.getInstance();
			Query query = manager.createQuery("from PmpContrato c, PmpConfigOperacional op where c.isAtivo is null and c.dataRejeicao is null and c.idStatusContrato.id = (select s.id from PmpStatusContrato s where s.sigla = 'CA') and op.idContrato.id = c.id" +
					" and c.numeroSerie not in ('FG20/25N/M','AF17D30824',"+
					"	'GP25ZNT','T3550488',"+
					"	'FG30NM','AF13FP0158',"+
					"	'GP25NM','AT17DT0307',"+
					"	'FG20/25N/M','AF17DR0351',"+
					"	'FG20/25N/M','AF17DT0125',"+
					"	'FG20/25N/M','AF17DT0212',"+
					"	'FG20/25N/M','AF17DT0409',"+
					"	'FG20/25N/M','AF17DT0210',"+
					"	'FG25ZNT','F3551222',"+
					"	'FG25ZNT','F3551280',"+
					"	'GP25ZNT','T3550489',"+
					"	'H40-70FT','A977Y18067M',"+
					"	'H40-70FT','A977Y14969K',"+
					"	'H40-70FT','A977Y17673L',"+
					"	'GP25NM','AT17DT0378',"+
					"	'GP15NM','AT34T0117',"+
					"	'H40-70FT','A977Y15737L',"+
					"	'H40-70FT','A977Y15005K',"+
					"	'FG20/25N/M','AF17D30813',"+
					"	'FG20/25N/M','AF17D30812',"+
					"	'FG20/25N/M','AF17D30823',"+
					"	'FG20/25N/M','AF17D30815',"+
					"	'FG20/25N/M','AF17DT0163')");
			List<Object[]> pmpContratoList = query.getResultList();
			String SQL = "";
			try {
				for (Object[] pair : pmpContratoList) {
					PmpContrato contrato = (PmpContrato)pair[0];
					PmpConfigOperacional configOperacional = (PmpConfigOperacional)pair[1];
					
					
					
					SQL = "select e.eqmfs2,e.LCUNO cliente, e.LOC1 codigoAlocacao  from "+IConstantAccess.LIB_DBS+".EMPEQPD0 e where e.eqmfs2 = '"+contrato.getNumeroSerie().replaceAll("TTT1", "")+"'";				
					rs = prstmt.executeQuery(SQL);
					if(rs.next()){
						String codigoCliente = rs.getString("cliente");
						String codigoAlocacao = rs.getString("codigoAlocacao");
						if(codigoCliente == null || codigoCliente.trim().equals("")){
							manager.getTransaction().begin();
							contrato.setMediaHorasMes(0L);
							codigoCliente = "0900629";
							manager.getTransaction().commit();
							
						}
//					query = manager.createNativeQuery("select STNO from TW_FILIAL where SIGLA_RENTAL = '"+codigoAlocacao+"'");
//					BigDecimal filial = (BigDecimal)query.getSingleResult();
						
						SQL = "select c.STN1, c.FLGDLI,c.CUNM CLCHAVE, c.CUNO,c.CUADD2 END2, c.CUADD3 BAIRRO,c.CUCYST CID,c.CUSTE EST, trim(SUBstring(c.TELXNO,0,15)) CPF, c.ZIPCD9 CEP from "+IConstantAccess.LIB_DBS+".CIPNAME0 c where c.cuno = '"+codigoCliente+"'";
						rs2 = prstmt2.executeQuery(SQL);
						manager.getTransaction().begin();
						if(rs2.next()){
							contrato.setCodigoCliente(codigoCliente);
							contrato.setRazaoSocial(rs2.getString("CLCHAVE"));
							contrato.setEndereco(rs2.getString("END2"));
							contrato.setBairro(rs2.getString("BAIRRO"));
							contrato.setCidade(rs2.getString("CID"));
							contrato.setUf(rs2.getString("EST"));
							contrato.setCnpj(rs2.getString("CPF"));
							contrato.setCep(rs2.getString("CEP"));
							
						}
						//configOperacional.setFilial(filial.longValue());
						manager.getTransaction().commit();
					}
				}
				EmailHelper emailHelper = new EmailHelper();
				emailHelper.sendSimpleMail("Rotina de normalização de cliente executada com sucesso!", "Sucesso normalizar cliente", "rodrigo@rdrsistemas.com.br");
			} catch (Exception e) {
				if(manager != null && manager.getTransaction().isActive()){
					manager.getTransaction().rollback();
				}
				EmailHelper emailHelper = new EmailHelper();
				emailHelper.sendSimpleMail("Erro ao executar rotina de normalização de cliente!", "Erro normalizar cliente", "rodrigo@rdrsistemas.com.br");
				e.printStackTrace();
			}
			
		
		}catch (Exception e) {
			if(manager != null && manager.getTransaction().isActive()){
				manager.getTransaction().rollback();
			}
			EmailHelper emailHelper = new EmailHelper();
        	emailHelper.sendSimpleMail("Erro ao executar rotina de normalização de cliente!", "Erro normalizar cliente", "rodrigo@rdrsistemas.com.br");
			e.printStackTrace();
		}finally{
			try {
				con.close();
				prstmt.close();
				prstmt2.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if(manager != null && manager.isOpen()){
				manager.close();
			}
			
		}
	}
	public static void main(String[] args) {
		System.out.println("TTT1180893".replaceAll("TTT1", ""));
	}

}
