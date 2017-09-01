package com.pmprental.util;

import java.util.Comparator;

import com.pmprental.bean.OperacionalBean;

public class OrderDateAsc implements Comparator{

	public int compare(Object o1, Object o2) {
		return (((OperacionalBean)o1).getDataAtualizacaoHori()).compareTo(((OperacionalBean)o2).getDataAtualizacaoHori());
	}

	

}
