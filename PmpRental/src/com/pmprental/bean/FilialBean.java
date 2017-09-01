package com.pmprental.bean;

import java.io.Serializable;

import com.pmprental.entity.TwFilial;

public class FilialBean implements Serializable {

	private static final long serialVersionUID = 2155358431269549665L;
	private Long stno;
	private String stnm;
	private String descricao;

	public Long getStno() {
		return stno;
	}

	public void setStno(Long stno) {
		this.stno = stno;
	}

	public String getStnm() {
		return stnm;
	}

	public void setStnm(String stnm) {
		this.stnm = stnm;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void fromBean(TwFilial entity) {
		setStno(entity.getStno());
		setStnm(entity.getStnm());
	}

	public TwFilial toBean() {
		TwFilial fil = new TwFilial();
		fil.setStnm(getStnm());
		return fil;
	}
}
