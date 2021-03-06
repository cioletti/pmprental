package bean
{
	import mx.collections.ArrayCollection;

	[RemoteClass(alias="com.pmprental.bean.AgendamentoBean")]
	public class AgendamentoBean
	{
		private var _id:Number;
		private var _idContrato:Number;
		private var _numSerie:String;
		private var _horimetro:Number;
		private var _horasPendentes:Number;
		private var _codigoCliente:String;
		private var _modelo:String;
		private var _idStatusAgendamento:Number;
		private var _idContHorasStandard:Number;
		private var _idFuncionario:String;
		private var _idConfOperacional:Number;
		private var _horasRevisao:Number;
		private var _dataAgendamento:String;
		private var _dataAgendamentoFinal:String;
		private var _siglaStatus:String;
		private var _numOs:String;
		private var _agendamentoList:ArrayCollection;
		private var _local:String;
		private var _contato:String;
		private var _numContrato:String;
		private var _telefone:String;
		private var _statusAgendamento:String;
		private var _standardJob:String;
		private var _filial:String;
		private var _filialDestino:String;
		private var _siglaTipoContrato:String;
		private var _pecasList:ArrayCollection;
		private var _dataAtualizacaoHorimetro:String;
		private var _razaoSocial:String;
		private var _numDoc:String;
		private var _msg:String;
		private var _codErroOsDbs:String;
		private var _codErroDocDbs:String;
		private var _idOsOperacional:Number;
		private var _isFindTecnico:String;
		private var _obs:String;
		private var _totalRegistros:Number;
		private var _dataFaturamento:String;
		private var _obsOs:String;
		private var _idAjudante:String;
		private var _nomeAjudante:String;
		private var _horasTrabalhadas:String;
		private var _obsCheckList:String;
		private var _mediaDiasProximaRevisao:String;
		private var _idEquipamento:String;
		private var _make:String;
		private var _urlStatus:String;

		
		public function get idOsOperacional():Number
		{
			return _idOsOperacional;
		}

		public function set idOsOperacional(value:Number):void
		{
			_idOsOperacional = value;
		}

		public function get codErroDocDbs():String
		{
			return _codErroDocDbs;
		}

		public function set codErroDocDbs(value:String):void
		{
			_codErroDocDbs = value;
		}

		public function get codErroOsDbs():String
		{
			return _codErroOsDbs;
		}

		public function set codErroOsDbs(value:String):void
		{
			_codErroOsDbs = value;
		}

		public function get msg():String
		{
			return _msg;
		}

		public function set msg(value:String):void
		{
			_msg = value;
		}

		public function get numDoc():String
		{
			return _numDoc;
		}

		public function set numDoc(value:String):void
		{
			_numDoc = value;
		}

		public function get id(): Number{return _id};
		public function set id(id: Number): void{this._id = id}; 
		
		public function get idContrato(): Number{return _idContrato};
		public function set idContrato(idContrato: Number): void{this._idContrato = idContrato}; 

		public function get numSerie(): String{return _numSerie};
		public function set numSerie(numSerie: String): void{this._numSerie = numSerie}; 

		public function get horimetro(): Number{return _horimetro};
		public function set horimetro(horimetro: Number): void{this._horimetro = horimetro}; 

		public function get horasPendentes(): Number{return _horasPendentes};
		public function set horasPendentes(horasPendentes: Number): void{this._horasPendentes = horasPendentes};	

		public function get codigoCliente(): String{return _codigoCliente};
		public function set codigoCliente(codigoCliente: String): void{this._codigoCliente = codigoCliente};	

		public function get idStatusAgendamento(): Number{return _idStatusAgendamento};
		public function set idStatusAgendamento(idStatusAgendamento: Number): void{this._idStatusAgendamento = idStatusAgendamento}; 

		public function get idContHorasStandard(): Number{return _idContHorasStandard};
		public function set idContHorasStandard(idContHorasStandard: Number): void{this._idContHorasStandard = idContHorasStandard}; 
		
		public function get idFuncionario(): String{return _idFuncionario};
		public function set idFuncionario(idFuncionario: String): void{this._idFuncionario = idFuncionario}; 

		public function get idConfOperacional(): Number{return _idConfOperacional};
		public function set idConfOperacional(idConfOperacional: Number): void{this._idConfOperacional = idConfOperacional}; 

		public function get horasRevisao(): Number{return _horasRevisao};
		public function set horasRevisao(horasRevisao: Number): void{this._horasRevisao = horasRevisao}; 
		
		public function get dataAgendamento(): String{return _dataAgendamento};
		public function set dataAgendamento(dataAgendamento: String): void{this._dataAgendamento = dataAgendamento}; 

		public function get dataAgendamentoFinal(): String{return _dataAgendamentoFinal};
		public function set dataAgendamentoFinal(dataAgendamentoFinal: String): void{this._dataAgendamentoFinal = dataAgendamentoFinal}; 

		public function get siglaStatus(): String{return _siglaStatus};
		public function set siglaStatus(siglaStatus: String): void{this._siglaStatus = siglaStatus}; 

		public function get numOs(): String{return _numOs};
		public function set numOs(numOs: String): void{this._numOs = numOs}; 

		public function get local(): String{return _local};
		public function set local(local: String): void{this._local = local}; 

		public function get contato(): String{return _contato};
		public function set contato(contato: String): void{this._contato = contato}; 

		public function get numContrato(): String{return _numContrato};
		public function set numContrato(numContrato: String): void{this._numContrato = numContrato}; 

		public function get telefone(): String{return _telefone};
		public function set telefone(telefone: String): void{this._telefone = telefone}; 

		public function get modelo(): String{return _modelo};
		public function set modelo(modelo: String): void{this._modelo = modelo}; 

		public function get statusAgendamento(): String{return _statusAgendamento};
		public function set statusAgendamento(statusAgendamento: String): void{this._statusAgendamento = statusAgendamento}; 

		public function get dataAtualizacaoHorimetro(): String{return _dataAtualizacaoHorimetro};
		public function set dataAtualizacaoHorimetro(dataAtualizacaoHorimetro: String): void{this._dataAtualizacaoHorimetro = dataAtualizacaoHorimetro}; 

		public function get standardJob(): String{return _standardJob};
		public function set standardJob(standardJob: String): void{this._standardJob = standardJob}; 

		public function get filial(): String{return _filial};
		public function set filial(filial: String): void{this._filial = filial}; 
		
		public function get filialDestino(): String{return _filialDestino};
		public function set filialDestino(filialDestino: String): void{this._filialDestino = filialDestino}; 


		public function get siglaTipoContrato(): String{return _siglaTipoContrato};
		public function set siglaTipoContrato(siglaTipoContrato: String): void{this._siglaTipoContrato = siglaTipoContrato}; 

		public function get agendamentoList(): ArrayCollection{return _agendamentoList};
		public function set agendamentoList(agendamentoList: ArrayCollection): void{this._agendamentoList = agendamentoList}; 

		public function get pecasList(): ArrayCollection{return _pecasList};
		public function set pecasList(pecasList: ArrayCollection): void{this._pecasList = pecasList}; 

		public function get razaoSocial(): String{return _razaoSocial};
		public function set razaoSocial(razaoSocial: String): void{this._razaoSocial = razaoSocial};
		
		public function get isFindTecnico():String
		{
			return _isFindTecnico;
		}

		public function set isFindTecnico(value:String):void
		{
			_isFindTecnico = value;
		}		

		public function get obs():String
		{
			return _obs;
		}

		public function set obs(value:String):void
		{
			_obs = value;
		}

		public function get totalRegistros():Number
		{
			return _totalRegistros;
		}

		public function set totalRegistros(value:Number):void
		{
			_totalRegistros = value;
		}
	
		public function get dataFaturamento():String
		{
			return _dataFaturamento;
		}
		
		public function set dataFaturamento(value:String):void
		{
			_dataFaturamento = value;
		}

		public function get obsOs():String
		{
			return _obsOs;
		}

		public function set obsOs(value:String):void
		{
			_obsOs = value;
		}

		public function get idAjudante():String
		{
			return _idAjudante;
		}

		public function set idAjudante(value:String):void
		{
			_idAjudante = value;
		}

		public function get nomeAjudante():String
		{
			return _nomeAjudante;
		}

		public function set nomeAjudante(value:String):void
		{
			_nomeAjudante = value;
		}

		public function get horasTrabalhadas():String
		{
			return _horasTrabalhadas;
		}

		public function set horasTrabalhadas(value:String):void
		{
			_horasTrabalhadas = value;
		}

		public function get obsCheckList():String
		{
			return _obsCheckList;
		}

		public function set obsCheckList(value:String):void
		{
			_obsCheckList = value;
		}

		public function get mediaDiasProximaRevisao():String
		{
			return _mediaDiasProximaRevisao;
		}

		public function set mediaDiasProximaRevisao(value:String):void
		{
			_mediaDiasProximaRevisao = value;
		}

		public function get idEquipamento():String
		{
			return _idEquipamento;
		}

		public function set idEquipamento(value:String):void
		{
			_idEquipamento = value;
		}

		public function get make():String
		{
			return _make;
		}

		public function set make(value:String):void
		{
			_make = value;
		}

		public function get urlStatus():String
		{
			return _urlStatus;
		}

		public function set urlStatus(value:String):void
		{
			_urlStatus = value;
		}

		
		public function AgendamentoBean()
		{
		}
	}
}