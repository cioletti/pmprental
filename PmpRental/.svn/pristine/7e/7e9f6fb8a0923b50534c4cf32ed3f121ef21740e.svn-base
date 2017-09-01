package com.pmprental.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.sql.Insert;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;

import com.pmprental.bean.AgendamentoBean;
import com.pmprental.bean.EquipamentosPmpBean;
import com.pmprental.util.JpaUtil;

/**
 * Servlet implementation class ReportMaquinasSemContrato
 */
public class ReportMaquinasSemContrato extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReportMaquinasSemContrato() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
			
			
			//EntityManager manager = null;
			JasperReport jasperReport = null;  
			JasperPrint pdfInspecao = null;    

			//Obtem o caminho do .jasper  
			ServletContext servletContext = super.getServletContext();  
			String caminhoJasper = servletContext.getRealPath("WEB-INF/relatorios/MaquinasSemContrato.jasper"); 
			String pathSubreport = servletContext.getRealPath("WEB-INF/relatorios/")+"/"; 

			//Carrega o arquivo com o jasperReport  
			try {  
				jasperReport = (JasperReport) JRLoader.loadObject( caminhoJasper );  
			} catch (Exception jre) {  
				jre.printStackTrace();  
			}  

			try {
				Map parametros = new HashMap();  
				parametros.put("SUBREPORT_DIR", pathSubreport);  
				List<AgendamentoBean> maquinasList = findMaquinasSemContrato();
				
				JRBeanCollectionDataSource result =  new JRBeanCollectionDataSource(maquinasList);
				//Gera o excel para exibicao..
				byte[] bytes = null;
				try {  
					pdfInspecao = JasperFillManager.fillReport(jasperReport, parametros, result);  
					JRXlsExporter exporter = new JRXlsExporter();     
					ByteArrayOutputStream xlsReport = new ByteArrayOutputStream();     
					exporter.setParameter(JRXlsExporterParameter.JASPER_PRINT, pdfInspecao);    
					exporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, xlsReport);     
					exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);     
					exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.TRUE);     
					exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.TRUE); 
					exporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE); 
					//exporter.setParameter(JRXlsExporterParameter.OUTPUT_FILE_NAME, "c:/relatorio.xls");  


					exporter.exportReport();     
					bytes = xlsReport.toByteArray();     
				} catch (Exception jre) {  
					jre.printStackTrace();  
				}  

				//Parametros para nao fazer cache e o que será exibido..  
				response.setContentType( "application/vnd.ms-excel" );  
				//			response.setHeader("Content-disposition",
				//			"attachment; filename=transferencia" ); 
				response.setHeader("content-disposition", "inline; filename=gestaEquipamentos.xls");   

				//Envia para o navegador o pdf..  
				ServletOutputStream servletOutputStream = response.getOutputStream();  
				servletOutputStream.write( bytes );  
				servletOutputStream.flush();  
				servletOutputStream.close();

			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				//manager.close();
			}
		}


	public List<AgendamentoBean> findMaquinasSemContrato() {
		PreparedStatement prstmt_ = null;
		EntityManager manager = null;

		Connection con = null;

		List<AgendamentoBean> listaMaquina = new ArrayList<AgendamentoBean>();
		try {
			manager = JpaUtil.getInstance();

			
		    
		    		
			String SQL = " select id_maquina, serie_maquina from REN_PMP_MAQUINA_SEM_CONTRATO WHERE  serie_maquina not  in("+
					" select c.NUMERO_SERIE from PMP_CONTRATO c"+
					" where c.ID_STATUS_CONTRATO = (select id from PMP_STATUS_CONTRATO where SIGLA = 'CA')"+
					" and c.IS_ATIVO is null"+
					" union "+
					" select c1.NUMERO_SERIE from REN_PMP_CONTRATO c1"+
					" where c1.ID_STATUS_CONTRATO = (select id from PMP_STATUS_CONTRATO where SIGLA = 'CA')"+
					" and c1.IS_ATIVO is null)";

			
			Query query= manager.createNativeQuery(SQL);
			List<Object[]> list = query.getResultList();
			//manager.getTransaction().begin();

			for (Object[] pair : list) {


				AgendamentoBean maquina = new AgendamentoBean();
				maquina.setIdEquipamento((String)pair[0]);
				maquina.setNumSerie((String)pair[1]);
				listaMaquina.add(maquina);


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
				if(manager != null && manager.isOpen()){
					manager.close();
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return listaMaquina;
	}
}
