package com.pmprental.business;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.pmprental.bean.MaquinaPlBean;
import com.pmprental.entity.PmpContrato;
import com.pmprental.entity.PmpMaquinaPl;
import com.pmprental.util.JpaUtil;

public class MaquinaPlBusiness {

	public MaquinaPlBean findMaquinaPl(String numeroSerie){
		MaquinaPlBean mp = new MaquinaPlBean();
		EntityManager manager = null;

		try {
			manager = JpaUtil.getInstance();

			Query query = manager.createNativeQuery("select max(MPL.horimetro) , MPL.numero_serie , MPL.modelo" +
					" from ren_pmp_maquina_pl  MPL where MPL.numero_serie = :numeroSerie" +
					" and MPL.id = (select MAX(MPL.id) from REN_PMP_MAQUINA_PL mpl where MPL.numero_serie = :numeroSerie and MPL.HORIMETRO > 0)" +
					" and MPL.HORIMETRO is not null" + 
					" group by MPL.numero_serie, MPL.modelo, MPL.HORIMETRO ");
			query.setParameter("numeroSerie", numeroSerie);
			List<Object[]> result = query.getResultList();
			
			if( result.size() >= 1){
				Object [] par =  result.get(0);
				mp.setHorimetro((Number)par[0]);
				mp.setNumeroSerie(par[1].toString());
				if(par[2] != null){
					mp.setModelo(par[2].toString());					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(manager != null && manager.isOpen()){
				manager.close();
			}
		}
		return mp;
	}
	
	public Boolean fazerUploadSmuXls(byte[] bytes, String nomeArquivo)  {
		//SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH);
		//SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd-MMM-yy hh:mm:ss aaa", Locale.ENGLISH);
		//int pulaLinha = 0;
		InputStream is = null;

		EntityManager manager = null;
		try {
			manager = JpaUtil.getInstance();
			
			is = new ByteArrayInputStream(bytes);
			HSSFWorkbook planilha = new HSSFWorkbook(is); 
			
			//BufferedReader bfReader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
			//Query query = null;
			//String linha = null;
			HSSFSheet sheet = planilha.getSheetAt(0);
			
			for(int i = 0; i <= sheet.getLastRowNum(); i++){
				if(i == 0){
					continue;
				}
				
			
				HSSFRow row = sheet.getRow(i);

				HSSFCell cell = row.getCell(0);
                String idEquipamento = cell.getStringCellValue();
                
                cell = row.getCell(1);
                Double horimetro = cell.getNumericCellValue();
                if(horimetro == null || "".equals(horimetro)){
                	continue;
                }
                
                Query query = manager.createQuery("From PmpContrato where idEquipamento =:idEquipamento");
                query.setParameter("idEquipamento", idEquipamento.trim());
                if(query.getResultList().size() == 0){
                	continue;
                }
                PmpContrato contrato = (PmpContrato)query.getResultList().get(0);
                
                PmpMaquinaPl entity = new PmpMaquinaPl();
				entity.setNumeroSerie(contrato.getNumeroSerie());
				entity.setHorimetro(horimetro.intValue());
				
				entity.setDataAtualizacao(new Date());
				entity.setModelo(contrato.getModelo());
                manager.getTransaction().begin();
                manager.persist(entity);
                manager.getTransaction().commit();             
//
                
			}
			
			
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
	
	public MaquinaPlBean saveOrUpdate(MaquinaPlBean bean){
		EntityManager manager = null;
		try {
			manager = JpaUtil.getInstance();
			manager.getTransaction().begin();
			PmpMaquinaPl maquina = null;
			//if(bean.getId() == null || bean.getId() == 0){
				maquina = new PmpMaquinaPl();
				Date dataAtual = new Date();
				bean.setDataAtualizacao(dataAtual);
				bean.toBean(maquina);
				manager.persist(maquina);
			//}else{
				//maquina = manager.find(PmpMaquinaPl.class, bean.getId());
				//bean.toBean(maquina);
				//manager.merge(maquina);
			//}
			manager.getTransaction().commit();
			bean.setId(maquina.getId());
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
	
	public Boolean remover(MaquinaPlBean bean){
		EntityManager manager = null;
		try {
			manager = JpaUtil.getInstance();
			manager.getTransaction().begin();
			manager.remove(manager.find(PmpMaquinaPl.class, bean.getId()));
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
}
