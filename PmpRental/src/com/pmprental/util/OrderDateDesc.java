package com.pmprental.util;

import java.util.Comparator;

import com.pmprental.bean.OperacionalBean;

public class OrderDateDesc implements Comparator{

	public int compare(Object o1, Object o2) {
		return (((OperacionalBean)o2).getDataAtualizacaoHori()).compareTo(((OperacionalBean)o1).getDataAtualizacaoHori());
	}

	

}
