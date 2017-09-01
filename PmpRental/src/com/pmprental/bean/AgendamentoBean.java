package com.pmprental.bean;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AgendamentoBean implements Comparator{ 
	private Long id;
	private Long idContrato;
	private String numSerie;
	private Long horimetro;
	private Long horasPendentes;
	private String codigoCliente;
	private String modelo;
	private Long idStatusAgendamento;
	private String idFuncionario;
	private Long idConfOperacional;
	private Long horasRevisao;
	private String dataAgendamento;
	private String dataAgendamentoFinal;
	private String siglaStatus;
	private String numOs;
	private String local;
	private String contato;
	private String numContrato;
	private String telefone;
	private String statusAgendamento;
	private Long idContHorasStandard;
	private String dataAtualizacaoHorimetro;
	private String standardJob;
	private String filial;
	private String filialDestino;
	private String siglaTipoContrato;
	private String razaoSocial;
	private List<PecasDbsBean> pecasList;
	private List<AgendamentoBean> agendamentoList;
	private String numDoc;
	private String msg;
	private String codErroOsDbs;
	private String codErroDocDbs;
	private Long idOsOperacional;
	private String isFindTecnico;
	private String obs;
	private String obsOs;
	private Integer totalRegistros;
	private String dataFaturamento;
	private String idAjudante;
	private String nomeAjudante;
	private Double horasTrabalhadas;
	private String obsCheckList;
	private String mediaDiasProximaRevisao;
	private String idEquipamento;
	private String make;
	private String urlStatus;
	
	public Integer getTotalRegistros() {
		return totalRegistros;
	}
	public void setTotalRegistros(Integer totalRegistros) {
		this.totalRegistros = totalRegistros;
	}
	public AgendamentoBean() {
		agendamentoList = new ArrayList<AgendamentoBean>();
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getIdStatusAgendamento() {
		return idStatusAgendamento;
	}
	public void setIdStatusAgendamento(Long idStatusAgendamento) {
		this.idStatusAgendamento = idStatusAgendamento;
	}
	public String getIdFuncionario() {
		return idFuncionario;
	}
	public void setIdFuncionario(String idFuncionario) {
		this.idFuncionario = idFuncionario;
	}
	public String getDataAgendamento() {
		return dataAgendamento;
	}
	public void setDataAgendamento(String dataAgendamento) {
		this.dataAgendamento = dataAgendamento;
	}
	public String getSiglaStatus() {
		return siglaStatus;
	}
	public void setSiglaStatus(String siglaStatus) {
		this.siglaStatus = siglaStatus;
	}
	public Long getIdConfOperacional() {
		return idConfOperacional;
	}
	public void setIdConfOperacional(Long idConfOperacional) {
		this.idConfOperacional = idConfOperacional;
	}
	public Long getHorasRevisao() {
		return horasRevisao;
	}
	public void setHorasRevisao(Long horasRevisao) {
		this.horasRevisao = horasRevisao;
	}
	public String getNumOs() {
		return numOs;
	}
	public void setNumOs(String numOs) {
		this.numOs = numOs;
	}
	public List<AgendamentoBean> getAgendamentoList() {
		return agendamentoList;
	}
	public void setAgendamentoList(List<AgendamentoBean> agendamentoList) {
		this.agendamentoList = agendamentoList;
	}
	public String getLocal() {
		return local;
	}
	public void setLocal(String local) {
		this.local = local;
	}
	public String getDataAgendamentoFinal() {
		return dataAgendamentoFinal;
	}
	public void setDataAgendamentoFinal(String dataAgendamentoFinal) {
		this.dataAgendamentoFinal = dataAgendamentoFinal;
	}
	public List<PecasDbsBean> getPecasList() {
		return pecasList;
	}
	public void setPecasList(List<PecasDbsBean> pecasList) {
		this.pecasList = pecasList;
	}
	public String getContato() {
		return contato;
	}
	public void setContato(String contato) {
		this.contato = contato;
	}
	public String getTelefone() {
		return telefone;
	}
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
	public Long getIdContHorasStandard() {
		return idContHorasStandard;
	}
	public void setIdContHorasStandard(Long idContHorasStandard) {
		this.idContHorasStandard = idContHorasStandard;
	}
	public Long getIdContrato() {
		return idContrato;
	}
	public void setIdContrato(Long idContrato) {
		this.idContrato = idContrato;
	}
	public String getNumSerie() {
		return numSerie;
	}
	public void setNumSerie(String numSerie) {
		this.numSerie = numSerie;
	}
	public Long getHorimetro() {
		return horimetro;
	}
	public void setHorimetro(Long horimetro) {
		this.horimetro = horimetro;
	}
	public Long getHorasPendentes() {
		return horasPendentes;
	}
	public void setHorasPendentes(Long horasPendentes) {
		this.horasPendentes = horasPendentes;
	}
	public String getCodigoCliente() {
		return codigoCliente;
	}
	public void setCodigoCliente(String codigoCliente) {
		this.codigoCliente = codigoCliente;
	}
	public String getNumContrato() {
		return numContrato;
	}
	public void setNumContrato(String numContrato) {
		this.numContrato = numContrato;
	}
	public String getModelo() {
		return modelo;
	}
	public void setModelo(String modelo) {
		this.modelo = modelo;
	}
	public String getStatusAgendamento() {
		return statusAgendamento;
	}
	public void setStatusAgendamento(String statusAgendamento) {
		this.statusAgendamento = statusAgendamento;
	}
	public String getDataAtualizacaoHorimetro() {
		return dataAtualizacaoHorimetro;
	}
	public void setDataAtualizacaoHorimetro(String dataAtualizacaoHorimetro) {
		this.dataAtualizacaoHorimetro = dataAtualizacaoHorimetro;
	}
	public String getStandardJob() {
		return standardJob;
	}
	public void setStandardJob(String standardJob) {
		this.standardJob = standardJob;
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
	public String getFilialDestino() {
		return filialDestino;
	}
	public void setFilialDestino(String filialDestino) {
		this.filialDestino = filialDestino;
	}
	public String getRazaoSocial() {
		return razaoSocial;
	}
	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}
	public String getNumDoc() {
		return numDoc;
	}
	public void setNumDoc(String numDoc) {
		this.numDoc = numDoc;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getCodErroOsDbs() {
		return codErroOsDbs;
	}
	public void setCodErroOsDbs(String codErroOsDbs) {
		this.codErroOsDbs = codErroOsDbs;
	}
	public String getCodErroDocDbs() {
		return codErroDocDbs;
	}
	public void setCodErroDocDbs(String codErroDocDbs) {
		this.codErroDocDbs = codErroDocDbs;
	}
	public Long getIdOsOperacional() {
		return idOsOperacional;
	}
	public void setIdOsOperacional(Long idOsOperacional) {
		this.idOsOperacional = idOsOperacional;
	}
	public String getIsFindTecnico() {
		return isFindTecnico;
	}
	public void setIsFindTecnico(String isFindTecnico) {
		this.isFindTecnico = isFindTecnico;
	}
	public String getObs() {
		return obs;
	}
	public void setObs(String obs) {
		this.obs = obs;
	}
	public String getDataFaturamento() {
		return dataFaturamento;
	}
	public void setDataFaturamento(String dataFaturamento) {
		this.dataFaturamento = dataFaturamento;
	}
	public String getObsOs() {
		return obsOs;
	}
	public void setObsOs(String obsOs) {
		this.obsOs = obsOs;
	}
	public String getIdAjudante() {
		return idAjudante;
	}
	public void setIdAjudante(String idAjudante) {
		this.idAjudante = idAjudante;
	}
	public String getNomeAjudante() {
		return nomeAjudante;
	}
	public void setNomeAjudante(String nomeAjudante) {
		this.nomeAjudante = nomeAjudante;
	}
	public Double getHorasTrabalhadas() {
		return horasTrabalhadas;
	}
	public void setHorasTrabalhadas(Double horasTrabalhadas) {
		this.horasTrabalhadas = horasTrabalhadas;
	}
	public String getObsCheckList() {
		return obsCheckList;
	}
	public void setObsCheckList(String obsCheckList) {
		this.obsCheckList = obsCheckList;
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
	public String getMake() {
		return make;
	}
	public void setMake(String make) {
		this.make = make;
	}
	public String getUrlStatus() {
		return urlStatus;
	}
	public void setUrlStatus(String urlStatus) {
		this.urlStatus = urlStatus;
	}
	public int compare(Object o1, Object o2) {
		return ((AgendamentoBean)o1).getHorasPendentes().compareTo(((AgendamentoBean)o2).getHorasPendentes());
	}

}
