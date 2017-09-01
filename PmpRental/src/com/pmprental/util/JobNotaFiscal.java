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

public class JobNotaFiscal implements Job {

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		EntityManager manager = null;
		Connection conn = null;
		Statement prstmt = null;
		ResultSet rs = null;
		try {
			manager = JpaUtil.getInstance();
			conn = com.pmprental.util.ConectionDbs.getConnecton();
			prstmt = conn.createStatement();

			Query query = manager.createNativeQuery("select op.NUM_OS, c.ID from REN_PMP_CONTRATO c, REN_PMP_CONFIG_OPERACIONAL op"+
													" where c.DATA_ACEITE is not null and c.VALOR_NOTA is null"+
													" and c.ID = op.ID_CONTRATO"+
													" and c.ID_TIPO_CONTRATO in (select ID from REN_PMP_TIPO_CONTRATO where SIGLA not in('VEN', 'CAN', 'REN'))"+
													" and op.NUM_OS is not null");
			
			List<Object[]> result = query.getResultList();
			for (Object[] objects : result) {
				rs = prstmt.executeQuery("select nf.VLRNTF  from PESA200ARQ.NF020F nf, LIBU17.WOPHDRS0 wo"+ 
										 "	where nf.NRODOC = wo.WONO"+
										 "	and wo.wono = '"+objects[0]+"'"+
										 "	and nf.VLRNTF is not null");
				if(rs.next()){
					manager.getTransaction().begin();
					query = manager.createNativeQuery("update PMP_CONTRATO set VALOR_NOTA = '"+rs.getDouble("VLRNTF")+"' where ID = "+(BigDecimal)objects[1]);
					query.executeUpdate();
					manager.getTransaction().commit();
				}
			}
				
		}catch (Exception e) {
			if(manager != null && manager.getTransaction().isActive()){
				manager.getTransaction().rollback();
			}
			e.printStackTrace();
		}finally{
			if(manager != null && manager.isOpen()){
				manager.close();
			}
			if(conn != null){
				try {
					conn.close();
					rs.close();
					prstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
