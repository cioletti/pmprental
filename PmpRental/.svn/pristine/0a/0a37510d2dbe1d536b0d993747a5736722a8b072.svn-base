package com.pmprental.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;

import com.pmprental.bean.AgendamentoBean;
import com.pmprental.bean.FilialBean;
import com.pmprental.bean.UsuarioBean;
import com.pmprental.business.OsBusiness;
import com.pmprental.util.Connection;

/**
 * Servlet implementation class RelatorioSituacaoOS
 */
public class RelatorioSituacaoOS extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RelatorioSituacaoOS() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String beginDate = request.getParameter("beginDate");
		String endDate = request.getParameter("endDate");
		String idFilial = request.getParameter("idFilial");
		java.sql.Connection con = null;
		try {
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd");

			Date date = format.parse(beginDate);
			beginDate = format2.format(date);
			date = format.parse(endDate);
			endDate = format2.format(date);

			//EntityManager manager = null;
			JasperReport jasperReport = null;  
			JasperPrint pdfInspecao = null;    
			OsBusiness business = new OsBusiness((UsuarioBean)request.getSession().getAttribute("usuario"));
			List<BigDecimal> listFilial = new ArrayList<BigDecimal>();
			
			if(!idFilial.equals("-1")) { //Se foi selecionada uma filial específica
				listFilial.add(BigDecimal.valueOf(Long.valueOf(idFilial)));
			} else { //Se foi selecionadas todas filiais
				for (FilialBean filial : business.findAllFiliais()) {
					listFilial.add(BigDecimal.valueOf(filial.getStno())); //Adiciona à lista o ID da filial
				}
			}
			
			//Obtem o caminho do .jasper  
			ServletContext servletContext = super.getServletContext();  
			String caminhoJasper = servletContext.getRealPath("WEB-INF/relatorios/RelatorioSituacaoOS.jasper"); 
			String pathSubreport = servletContext.getRealPath("WEB-INF/relatorios/")+"/"; 

			//Carrega o arquivo com o jasperReport  
			try {  
				jasperReport = (JasperReport) JRLoader.loadObject( caminhoJasper );  
			} catch (Exception jre) {  
				jre.printStackTrace();  
			}  
			

			con = Connection.getConnection();
			Map parametros = new HashMap();  
			parametros.put("SUBREPORT_DIR", pathSubreport);  
			parametros.put("beginDate", beginDate);  
			parametros.put("endDate", endDate);  
			parametros.put("FILIAL", listFilial);  

			//Gera o excel para exibicao..
			byte[] bytes = null;
			try {  
				pdfInspecao = JasperFillManager.fillReport(jasperReport, parametros, con);  
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
			response.setHeader("content-disposition", "inline; filename=RelatorioSitucaoOS.xls");   

			//Envia para o navegador o pdf..  
			ServletOutputStream servletOutputStream = response.getOutputStream();  
			servletOutputStream.write( bytes );  
			servletOutputStream.flush();  
			servletOutputStream.close();

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(con != null){
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
