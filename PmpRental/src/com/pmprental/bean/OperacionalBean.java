package com.pmprental.bean;

import java.util.Date;


public class OperacionalBean implements Comparable{
    
	private Long contrato_id;
	private String modelo;
	private String numeroSerie;
	private Integer horimetro;
	private String horasPendentes;
	private String proximaRevisao;
	private String dataAtualizacaoHorimetro;
	private String numOs;
	private String numeroContrato;
	private String dataContrato;
	private String situacao;
	private String situacaoImagem;
	private String codigoCliente;
	private String valorContrato;
	private String numeroParcela;
	private String nomeCliente;
	private String revPendentes;
	private String consultor;
	private String origem;
	private String destino;
	private String plAtivo;
	private String siglaTipoContrato;
	private String filial;
	private String ta;
	private Date dataAtualizacaoHori;
	private String codErroDbs;
	private Long numLinhas;
	private String diasProximaRevisao;
	private String mediaDiasProximaRevisao;
	private String idEquipamento;
	private Integer mediaHorasMes;

	
	public Long getContratoId() {
		return contrato_id;
	}
	public void setContratoId(Long contrato_id) {
		this.contrato_id = contrato_id;
	}	
	public String getModelo() {
		return modelo;
	}
	public void setModelo(String modelo) {
		this.modelo = modelo;
	}
	public String getNumeroSerie() {
		return numeroSerie;
	}
	public void setNumeroSerie(String  numeroSerie) {
		this.numeroSerie = numeroSerie;
	}
	public Integer getHorimetro() {
		return horimetro;
	}
	public void setHorimetro(Integer horimetro) {
		this.horimetro = horimetro;
	}
	public String getHorasPendentes() {
		return horasPendentes;
	}
	public void setHorasPendentes(String horasPendentes) {
		this.horasPendentes = horasPendentes;
	}
	public String getProximaRevisao() {
		return proximaRevisao;
	}
	public void setProximaRevisao(String proximaRevisao) {
		this.proximaRevisao = proximaRevisao;
	}
	public String getDataAtualizacaoHorimetro() {
		return dataAtualizacaoHorimetro;
	}
	public void setDataAtualizacaoHorimetro(String dataAtualizacaoHorimetro) {
		this.dataAtualizacaoHorimetro = dataAtualizacaoHorimetro;
	}
	public String getNumOs() {
		return numOs;
	}
	public void setNumOs(String numOs) {
		this.numOs = numOs;
	}
	public String getNumeroContrato() {
		return numeroContrato;
	}
	public void setNumeroContrato(String numeroContrato) {
		this.numeroContrato = numeroContrato;
	}
	public String getDataContrato() {
		return dataContrato;
	}
	public void setDataContrato(String dataContrato) {
		this.dataContrato = dataContrato;
	}	
	public String getSituacao() {
		return situacao;
	}
	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}	
	public String getSituacaoImagem() {
		return situacaoImagem;
	}
	public void setSituacaoImagem(String situacaoImagem) {
		this.situacaoImagem = situacaoImagem;

	}
	public String getCodigoCliente() {
		return codigoCliente;
	}
	public void setCodigoCliente(String codigoCliente) {
		this.codigoCliente = codigoCliente;

	}
	public Long getContrato_id() {
		return contrato_id;
	}
	public void setContrato_id(Long contrato_id) {
		this.contrato_id = contrato_id;
	}
	public String getValorContrato() {
		return valorContrato;
	}
	public void setValorContrato(String valorContrato) {
		this.valorContrato = valorContrato;
	}
	public String getNumeroParcela() {
		return numeroParcela;
	}
	public void setNumeroParcela(String numeroParcela) {
		this.numeroParcela = numeroParcela;

	}
	public String getNomeCliente() {
		return nomeCliente;
	}
	public void setNomeCliente(String nomeCliente) {
		this.nomeCliente = nomeCliente;
	}
	public String getRevPendentes() {
		return revPendentes;
	}
	public void setRevPendentes(String revPendentes) {
		this.revPendentes = revPendentes;
	}
	public String getConsultor() {
		return consultor;
	}
	public void setConsultor(String consultor) {
		this.consultor = consultor;
	}
	public String getOrigem() {
		return origem;
	}
	public void setOrigem(String origem) {
		this.origem = origem;
	}
	public String getDestino() {
		return destino;
	}
	public void setDestino(String destino) {
		this.destino = destino;
	}
	public String getPlAtivo() {
		return plAtivo;
	}
	public void setPlAtivo(String plAtivo) {
		this.plAtivo = plAtivo;
	}
	public String getFilial() {
		return filial;
	}
	public void setFilial(String filial) {
		this.filial = filial;
	}
	
	public String getSiglaTipoContrato() {
		return siglaTipoContrato;
	}
	public void setSiglaTipoContrato(String siglaTipoContrato) {
		this.siglaTipoContrato = siglaTipoContrato;
	}
	public String getTa() {
		return ta;
	}
	public void setTa(String ta) {
		this.ta = ta;
	}
	public Date getDataAtualizacaoHori() {
		return dataAtualizacaoHori;
	}
	public void setDataAtualizacaoHori(Date dataAtualizacaoHori) {
		this.dataAtualizacaoHori = dataAtualizacaoHori;
	}
	public String getCodErroDbs() {
		return codErroDbs;
	}
	public void setCodErroDbs(String codErroDbs) {
		this.codErroDbs = codErroDbs;
	}
	public Long getNumLinhas() {
		return numLinhas;
	}
	public void setNumLinhas(Long numLinhas) {
		this.numLinhas = numLinhas;
	}
	
	public String getDiasProximaRevisao() {
		return diasProximaRevisao;
	}
	public void setDiasProximaRevisao(String diasProximaRevisao) {
		this.diasProximaRevisao = diasProximaRevisao;
	}
	public String getMediaDiasProximaRevisao() {
		return mediaDiasProximaRevisao;
	}
	public void setMediaDiasProximaRevisao(String mediaDiasProximaRevisao) {
		this.mediaDiasProximaRevisao = mediaDiasProximaRevisao;
	}
	public String getIdEquipamento() {
		return idEquipamento;
	}
	public void setIdEquipamento(String idEquipamento) {
		this.idEquipamento = idEquipamento;
	}
	public Integer getMediaHorasMes() {
		return mediaHorasMes;
	}
	public void setMediaHorasMes(Integer mediaHorasMes) {
		this.mediaHorasMes = mediaHorasMes;
	}
	public int compareTo(Object o) {
		long hpObj = Long.valueOf(((OperacionalBean)o).getHorasPendentes());
		long hpCurrent = Long.valueOf(this.getHorasPendentes());
		if(hpObj > hpCurrent){
			return -1;
		}else if(hpObj == hpCurrent){
			return 0;
		}else{
			return 1;
		}
	}	
}
