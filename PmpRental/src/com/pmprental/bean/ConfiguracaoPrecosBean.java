package com.pmprental.bean;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import com.pmprental.entity.PmpConfiguracaoPrecos;
import com.pmprental.util.ValorMonetarioHelper;

public class ConfiguracaoPrecosBean {

	private Long id;
	private String descricao;
	private String hhRental;
	private String hhPmp;
	private Integer kmRental;
	private Integer kmPmp;
	private String custoNordeste;
	private Integer validadeContrato;
	private String jurosVendaContrato;
	private String valorKmPmp;
	private String valorKmRental;
	private String hhTa;
	private String horasTa;
	private String descPdp;
	private String valorHhPmpCusto;
	private String valorKmPmpCusto;
	private String valorHhTaCusto;
	private String descontoPrePago;
	private String comissaoIndicacao;
	private String comissaoConsultor;
	private String valorSpot;
	private Long kmPmpSpot;
	private String descPdpSpot;
	private String descontoPecas;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getHhRental() {
		return hhRental;
	}
	public void setHhRental(String hhRental) {
		this.hhRental = hhRental;
	}
	public String getHhPmp() {
		return hhPmp;
	}
	public void setHhPmp(String hhPmp) {
		this.hhPmp = hhPmp;
	}
	public Integer getKmRental() {
		return kmRental;
	}
	public void setKmRental(Integer kmRental) {
		this.kmRental = kmRental;
	}
	public Integer getKmPmp() {
		return kmPmp;
	}
	public void setKmPmp(Integer kmPmp) {
		this.kmPmp = kmPmp;
	}
	public String getCustoNordeste() {
		return custoNordeste;
	}
	public void setCustoNordeste(String custoNordeste) {
		this.custoNordeste = custoNordeste;
	}
	public Integer getValidadeContrato() {
		return validadeContrato;
	}
	public void setValidadeContrato(Integer validadeContrato) {
		this.validadeContrato = validadeContrato;
	}
	public String getJurosVendaContrato() {
		return jurosVendaContrato;
	}
	public void setJurosVendaContrato(String jurosVendaContrato) {
		this.jurosVendaContrato = jurosVendaContrato;
	}
	
	public String getValorKmPmp() {
		return valorKmPmp;
	}
	public void setValorKmPmp(String valorKmPmp) {
		this.valorKmPmp = valorKmPmp;
	}
	public String getValorKmRental() {
		return valorKmRental;
	}
	public void setValorKmRental(String valorKmRental) {
		this.valorKmRental = valorKmRental;
	}
	public String getHhTa() {
		return hhTa;
	}
	public void setHhTa(String hhTa) {
		this.hhTa = hhTa;
	}
	public String getHorasTa() {
		return horasTa;
	}
	public void setHorasTa(String horasTa) {
		this.horasTa = horasTa;
	}
	public String getDescPdp() {
		return descPdp;
	}
	public void setDescPdp(String descPdp) {
		this.descPdp = descPdp;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getValorHhPmpCusto() {
		return valorHhPmpCusto;
	}
	public void setValorHhPmpCusto(String valorHhPmpCusto) {
		this.valorHhPmpCusto = valorHhPmpCusto;
	}
	public String getValorKmPmpCusto() {
		return valorKmPmpCusto;
	}
	public void setValorKmPmpCusto(String valorKmPmpCusto) {
		this.valorKmPmpCusto = valorKmPmpCusto;
	}
	public String getValorHhTaCusto() {
		return valorHhTaCusto;
	}
	public void setValorHhTaCusto(String valorHhTaCusto) {
		this.valorHhTaCusto = valorHhTaCusto;
	}
	public String getDescontoPrePago() {
		return descontoPrePago;
	}
	public void setDescontoPrePago(String descontoPrePago) {
		this.descontoPrePago = descontoPrePago;
	}
	public String getComissaoIndicacao() {
		return comissaoIndicacao;
	}
	public void setComissaoIndicacao(String comissaoIndicacao) {
		this.comissaoIndicacao = comissaoIndicacao;
	}
	public String getComissaoConsultor() {
		return comissaoConsultor;
	}
	public void setComissaoConsultor(String comissaoConsultor) {
		this.comissaoConsultor = comissaoConsultor;
	}
	public String getValorSpot() {
		return valorSpot;
	}
	public void setValorSpot(String valorSpot) {
		this.valorSpot = valorSpot;
	}
	public Long getKmPmpSpot() {
		return kmPmpSpot;
	}
	public void setKmPmpSpot(Long kmPmpSpot) {
		this.kmPmpSpot = kmPmpSpot;
	}
	public String getDescPdpSpot() {
		return descPdpSpot;
	}
	public void setDescPdpSpot(String descPdpSpot) {
		this.descPdpSpot = descPdpSpot;
	}
	public String getDescontoPecas() {
		return descontoPecas;
	}
	public void setDescontoPecas(String descontoPecas) {
		this.descontoPecas = descontoPecas;
	}
	public void toBean(ConfiguracaoPrecosBean bean, PmpConfiguracaoPrecos precos){
		precos.setHhPmp(BigDecimal.valueOf(Double.valueOf(bean.getHhPmp().replace(".", "").replace(",", "."))));
		if(bean.getCustoNordeste() != null){
			precos.setCustoNordeste(BigDecimal.valueOf(Double.valueOf(bean.getCustoNordeste())));
		}
		precos.setHhRental(BigDecimal.valueOf(Double.valueOf(bean.getHhRental().replace(".", "").replace(",", "."))));
		if(bean.getJurosVendaContrato() != null){
			precos.setJurosVenda(BigDecimal.valueOf(Double.valueOf(bean.getJurosVendaContrato())));
		}
		precos.setKmPmp(new BigInteger(bean.getKmPmp().toString()));
		precos.setKmRental(new BigInteger(bean.getKmRental().toString()));
		if(bean.getValidadeContrato() != null){
			precos.setValidadeContrato(new BigInteger(bean.getValidadeContrato().toString()));
		}
		precos.setValorKmPmp(BigDecimal.valueOf(Double.valueOf(bean.getValorKmPmp().replace(".", "").replace(",", "."))));
		precos.setValorKmRental(BigDecimal.valueOf(Double.valueOf(bean.getValorKmRental().replace(".", "").replace(",", "."))));
		if(bean.getHhTa() != null){
			precos.setHhTa(BigDecimal.valueOf(Double.valueOf(bean.getHhTa().replace(".", "").replace(",", "."))));
		}
		precos.setValorHhPmpCusto(BigDecimal.valueOf(Double.valueOf(bean.getValorHhPmpCusto().replace(".", "").replace(",", "."))));
		if(bean.getValorHhTaCusto() != null){
			precos.setValorHhTaCusto(BigDecimal.valueOf(Double.valueOf(bean.getValorHhTaCusto().replace(".", "").replace(",", "."))));
		}
		precos.setValorKmPmpCusto(BigDecimal.valueOf(Double.valueOf(bean.getValorKmPmpCusto().replace(".", "").replace(",", "."))));
		if(bean.getHorasTa() != null){
			precos.setTaHoras(BigDecimal.valueOf(Double.valueOf(bean.getHorasTa())));
		}
		if(bean.getDescontoPrePago() != null){
			precos.setDescontoPrePago(BigDecimal.valueOf(Double.valueOf(bean.getDescontoPrePago().replace(".", "").replace(",", "."))));
		}
		if(bean.getDescPdp() != null){
			precos.setDescPdp(BigDecimal.valueOf(Double.valueOf(bean.getDescPdp().replace(".", "").replace(",", "."))));
		}
		precos.setDateConfiguracao(new Date());
		precos.setDescricao(bean.getDescricao());
		if(bean.getComissaoIndicacao() != null){
			precos.setComissaoIndicacao(BigDecimal.valueOf(Double.valueOf(bean.getComissaoIndicacao().replace(".", "").replace(",", "."))));
		}
		if(bean.getComissaoConsultor() != null){
			precos.setComissaoConsultor(BigDecimal.valueOf(Double.valueOf(bean.getComissaoConsultor().replace(".", "").replace(",", "."))));
		}
		if(bean.getValorSpot() != null){
			precos.setValorSpot(BigDecimal.valueOf(Double.valueOf(bean.getValorSpot().replace(".", "").replace(",", "."))));
		}
		if(bean.getDescPdpSpot() != null){
			precos.setPdpSpot(BigDecimal.valueOf(Double.valueOf(bean.getDescPdpSpot().replace(".", "").replace(",", "."))));
		}
		if(bean.getDescontoPecas() != null){
			precos.setDescPecas(BigDecimal.valueOf(Double.valueOf(bean.getDescontoPecas().replace(".", "").replace(",", "."))));
		}
		precos.setKmPmpSpot(new BigInteger(bean.getKmPmpSpot().toString()));
	}
	
	public void fromBean(PmpConfiguracaoPrecos precos){
		setId(precos.getId().longValue());
		setHhPmp(String.valueOf(ValorMonetarioHelper.formata("###,###,##0.00", Double.valueOf(String.valueOf(precos.getHhPmp())))));
		if(precos.getCustoNordeste() != null){
			setCustoNordeste(precos.getCustoNordeste().toString());
		}
		setHhRental(String.valueOf(ValorMonetarioHelper.formata("###,###,##0.00", Double.valueOf(String.valueOf(precos.getHhRental())))));
		if(precos.getJurosVenda() != null){
			setJurosVendaContrato(precos.getJurosVenda().toString());
		}
		setKmPmp(precos.getKmPmp().intValue());
		setKmRental(precos.getKmRental().intValue());
		if(precos.getValidadeContrato() != null){
			setValidadeContrato(precos.getValidadeContrato().intValue());
		}
		setValorKmPmp(String.valueOf(ValorMonetarioHelper.formata("###,###,##0.00", Double.valueOf(String.valueOf(precos.getValorKmPmp())))));
		setValorKmRental(String.valueOf(ValorMonetarioHelper.formata("###,###,##0.00", Double.valueOf(String.valueOf(precos.getValorKmRental())))));
		if(precos.getHhTa() != null){
			setHhTa(String.valueOf(ValorMonetarioHelper.formata("###,###,##0.00", Double.valueOf(String.valueOf(precos.getHhTa())))));
		}
		setValorHhPmpCusto(String.valueOf(ValorMonetarioHelper.formata("###,###,##0.00", Double.valueOf(String.valueOf(precos.getValorHhPmpCusto())))));
		if(precos.getValorHhTaCusto() != null){
			setValorHhTaCusto(String.valueOf(ValorMonetarioHelper.formata("###,###,##0.00", Double.valueOf(String.valueOf(precos.getValorHhTaCusto())))));
		}
		setValorKmPmpCusto(String.valueOf(ValorMonetarioHelper.formata("###,###,##0.00", Double.valueOf(String.valueOf(precos.getValorKmPmpCusto())))));		
		if(precos.getTaHoras() != null){
			setHorasTa(precos.getTaHoras().toString());
		}
		if(precos.getDescontoPrePago() != null){
			//setDescontoPrePago(precos.getDescontoPrePago().toString());
			setDescontoPrePago(String.valueOf(ValorMonetarioHelper.formata("###,###,##0.00", Double.valueOf(String.valueOf(precos.getDescontoPrePago())))));	
		}else{
			setDescontoPrePago("0");			
		}
		//setDescPdp(String.valueOf(precos.getDescontoPrePago()));		
		if(precos.getDescPdp() != null){
			//setDescPdp(precos.getDescPdp().toString());
			setDescPdp(String.valueOf(ValorMonetarioHelper.formata("###,###,##0.00", Double.valueOf(String.valueOf(precos.getDescPdp())))));
		}
		setDescricao(precos.getDescricao());
		//setDescPdp(String.valueOf(precos.getDescontoPrePago()));		
		if(precos.getComissaoConsultor() != null){
			//setDescPdp(precos.getDescPdp().toString());
			setComissaoConsultor(String.valueOf(ValorMonetarioHelper.formata("###,###,##0.00", Double.valueOf(String.valueOf(precos.getComissaoConsultor())))));
		}
		if(precos.getComissaoIndicacao() != null){
			//setDescPdp(precos.getDescPdp().toString());
			setComissaoIndicacao(String.valueOf(ValorMonetarioHelper.formata("###,###,##0.00", Double.valueOf(String.valueOf(precos.getComissaoIndicacao())))));
		}
		if(precos.getDescPecas() != null){
			//setDescPdp(precos.getDescPdp().toString());
			setDescontoPecas(String.valueOf(ValorMonetarioHelper.formata("###,###,##0.00", Double.valueOf(String.valueOf(precos.getDescPecas())))));
		}
		
	}
}
