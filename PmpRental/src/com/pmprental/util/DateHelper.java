package com.pmprental.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {

	public static boolean verificarDatasVencimento(Date data) throws ParseException {  
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");        
		Date dataAtual = format.parse(format.format(new Date()));  
		if(data == null)  
			return false;  
		if(data.compareTo(dataAtual) == -1){
			return false;
		}
		return true;
			  
	}

	public static String converteHoraMinuto(float valorFinalEmHoras){  
	    float tempoM= valorFinalEmHoras*60;  
	    int hora=0;  
	    int minutos=0;  
	    String hora_minutos="00:00";  
	    while(tempoM>=60){  
	        hora++;  
	        tempoM=tempoM-60;           
	    }  
	    minutos=Math.round(tempoM);  
	    hora_minutos = hora+":"+((minutos < 10)?"0"+minutos:minutos);  
	    return hora_minutos.replace(":", "");  
	}  
}
