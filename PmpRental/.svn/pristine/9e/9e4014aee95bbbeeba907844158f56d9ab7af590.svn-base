<%@page import="java.util.ArrayList"%>
<%@page import="com.pmprental.bean.PlMaquinaBean"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@page import="java.lang.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
<meta http-equiv="pragma" content="no-cache" />
<style type="text/css">
html {
	height: 100%
}

body {
	height: 97%;
	margin: 0;
	padding: 0
}

#map_canvas {
	height: 100%
}
</style>
<script type="text/javascript" src="./prototype/prototype.js"></script>
<script type="text/javascript" src="./js/java.js"></script>
<script type="text/javascript"
	src="https://maps.googleapis.com/maps/api/js?sensor=true">
	
</script>
<LINK rel="stylesheet" type="text/css" href="./css/css.css"?>
<script type="text/javascript">
	var geocoder;
	var map;
	var marker;
	var iconFile;
	var infowindow = new google.maps.InfoWindow();

	function teste() {
		alert('teste');
	}
	
	function change_criticidadeCbx() {
	
		document.getElementById('map_canvas').style.zIndex = -1;
		//document.getElementById('map_canvas').style.maxWidth = "100%"
			//document.getElementById('map_canvas').style.maxHeight = "100%"
		//document.getElementById('x').style.display = "0";
		
		
		 if (document.getElementById("criticidadeCbx").value == "TODAS"){
			 initializeAjax(""); 
		 }else if(document.getElementById("criticidadeCbx").value == "CRI"){
			 initializeAjax("CRITICO"); 
		 }else if(document.getElementById("criticidadeCbx").value == "CRI_M"){
			 initializeAjax("CRITICIDADE MEDIA"); 
		 }else if(document.getElementById("criticidadeCbx").value == "N_CRI"){
			 initializeAjax("NAO CRITICO"); 
		 }		
	}
	
	
	function pesquisarTXT() {
		
		
		 if (document.getElementById("pesquisarTXT").value == ""){
			 alert("O campo pesquisar é obrigatório!");
			 return;
		 }else { 
			 pesquisarAjax(document.getElementById("pesquisarTXT").value, ""); 
			document.getElementById('map_canvas').style.zIndex = -1;
			//document.getElementById('map_canvas').style.maxWidth = "100%"
				//document.getElementById('map_canvas').style.maxHeight = "100%"
			//document.getElementById('x').style.display = "0";
		 }		
	}
	
	function pesquisarAjax(campoPesquisa, nivel) {
		//alert('vou chamar');

		document.getElementById('load').style.display = "block";
		document.getElementById('load').style.maxWidth = "100%"
		document.getElementById('load').style.maxHeight = "100%"
		//alert('Entrando na funcao');
		new Ajax.Request(
				'./MapaServlet',
				{

					method : 'get',
					parameters : {
						login : loginVar,
						senha : senhaVar,
						campoPesquisa : campoPesquisa,
						nivel : nivel
					},
					onSuccess : function(transport) {
						geocoder = new google.maps.Geocoder();
						var mapOptions = {
							center : new google.maps.LatLng(-25.428212853818884,
									-49.268013272857615),
							zoom : 4,
							mapTypeId : google.maps.MapTypeId.ROADMAP
						};
						//document.getElementById('map_canvas').style.display = "none";
						map = new google.maps.Map(document
								.getElementById("map_canvas"), mapOptions);

						var response = (transport.responseText).evalJSON(true);
						// alert(transport.responseText);
						//  alert(response);

						// var response = '{"maquinaList":[{"serie":"034600339","codigoCliente":"0884901","filial":"CURITIBA","horimetro":"","longitude":"-50.723572","latitude":"-23.718124","modelo":"CC34","dtAtualizacao":"11\/06\/2013","nomeCliente":"CONSTRUTORA TRIUNFO S\/A"}]}'.evalJSON();
						// alert(response.maquinaList.length);

						for(var i = 0; i < response.tecnicoList.length; i++){
							 //alert(response[i]);
							 //alert(response[i].localizacao);
							 var descricao = response.tecnicoList[i].numOs+"\nTécnico:"+ response.tecnicoList[i].tecnico +"\nPlaca: "+response.tecnicoList[i].placa +"\nLocalização: "+response.tecnicoList[i].localizacao+"\nVelocidade: "+response.tecnicoList[i].velocidade;
							 //alert(descricao);
							 var latLng = new google.maps.LatLng(parseFloat(replaceAll(response.tecnicoList[i].latitude,',','.')),parseFloat(replaceAll(response.tecnicoList[i].longitude,',','.')));
							 var marker = new google.maps.Marker({'position': latLng, 
							 'map': map,
							 'icon': './img/Pickup.png',
							 'title': replaceAll(descricao,"#","\n")});
							 google.maps.event.addListener( marker, 'click', function(e) {
							 //infowindow.open(map,marker); 
							 //alert(marker);
							 codeLatLng( this);
							 }); 
						 }
						for ( var i = 0; i < response.maquinaList.length; i++) {
							//alert(response.maquinaList[i].serie);
							var descricao = "Dt. Atualização: "
									+ response.maquinaList[i].dtAtualizacao
									+ "\nSérie: "
									+ response.maquinaList[i].serie
									+ "\nModelo: "
									+ response.maquinaList[i].modelo
									+ "\nHorímetro: "
									+ response.maquinaList[i].horimetro
									+ "\nCódigo Cliente: "
									+ response.maquinaList[i].codigoCliente
									+ "\nNome Cliente: "
									+ response.maquinaList[i].nomeCliente
									+ "\nFilial: "
									+ response.maquinaList[i].filial;
							// alert(descricao);    
							var latLng = new google.maps.LatLng(
									parseFloat(replaceAll(
											response.maquinaList[i].latitude,
											',', '.')), parseFloat(replaceAll(
											response.maquinaList[i].longitude,
											',', '.')));

							marker = new google.maps.Marker({
								'position' : latLng,
								'map' : map,
								'title' : descricao
							});

							if (response.maquinaList[i].cor != null) {
								if (response.maquinaList[i].cor == "vermelho") {
									iconFile = './img/red-marker.png';
									marker.setIcon(iconFile);
								} else if (response.maquinaList[i].cor == "amarelo") {
									iconFile = './img/yellow-marker.png';
									marker.setIcon(iconFile);
								} else if (response.maquinaList[i].cor == "verde") {
									iconFile = './img/green-marker.png';
									marker.setIcon(iconFile);
								}
							}

							google.maps.event.addListener(marker, 'click',
									function(e) {
										//infowindow.open(map,marker); 
										//alert(marker);
										codeLatLng(this);
									});

						}
						//document.getElementById('map_canvas').innerHTML = "block";
						document.getElementById('map_canvas').style.display = "block";
						document.getElementById('map_canvas').style.maxWidth = "100%"
						document.getElementById('map_canvas').style.maxHeight = "100%"
							document.getElementById('map_canvas').style.zIndex = 1;

						document.getElementById('load').style.display = "none";
						// document.getElementById('load').innerHTML = "none";
						// alert("Success! \n\n" + response);

					},
					onFailure : function() {
						alert('Erro ao tentar logar');
						//document.getElementById('tabela').innerHTML = "block";
						document.getElementById('map_canvas').style.display = "block";
						document.getElementById('map_canvas').style.maxWidth = "100%"
						document.getElementById('map_canvas').style.maxHeight = "100%"

						document.getElementById('load').style.display = "none";
					}
				});
	}


	function initializeAjax(nivel) {
		//alert('vou chamar');

		document.getElementById('load').style.display = "block";
		document.getElementById('load').style.maxWidth = "100%"
		document.getElementById('load').style.maxHeight = "100%"
		//alert('Entrando na funcao');
		new Ajax.Request(
				'./MapaServlet',
				{

					method : 'get',
					parameters : {
						login : loginVar,
						senha : senhaVar,
						nivel : nivel
					},
					onSuccess : function(transport) {
						geocoder = new google.maps.Geocoder();
						var mapOptions = {
								center : new google.maps.LatLng(-25.428212853818884,
										-49.268013272857615),
							zoom : 4,
							mapTypeId : google.maps.MapTypeId.ROADMAP
						};
						//document.getElementById('map_canvas').style.display = "none";
						map = new google.maps.Map(document
								.getElementById("map_canvas"), mapOptions);

						var response = (transport.responseText).evalJSON(true);
						// alert(transport.responseText);
						//  alert(response);

						// var response = '{"maquinaList":[{"serie":"034600339","codigoCliente":"0884901","filial":"CURITIBA","horimetro":"","longitude":"-50.723572","latitude":"-23.718124","modelo":"CC34","dtAtualizacao":"11\/06\/2013","nomeCliente":"CONSTRUTORA TRIUNFO S\/A"}]}'.evalJSON();
						// alert(response.maquinaList.length);

						for(var i = 0; i < response.tecnicoList.length; i++){
							 //alert(response[i]);
							 //alert(response[i].localizacao);
							 var descricao = response.tecnicoList[i].numOs+"\nTécnico:"+ response.tecnicoList[i].tecnico +"\nPlaca: "+response.tecnicoList[i].placa +"\nLocalização: "+response.tecnicoList[i].localizacao+"\nVelocidade: "+response.tecnicoList[i].velocidade;
							 //alert(descricao);
							 var latLng = new google.maps.LatLng(parseFloat(replaceAll(response.tecnicoList[i].latitude,',','.')),parseFloat(replaceAll(response.tecnicoList[i].longitude,',','.')));
							 var marker = new google.maps.Marker({'position': latLng, 
							 'map': map,
							 'icon': './img/Pickup.png',
							 'title': replaceAll(descricao,"#","\n")});
							 google.maps.event.addListener( marker, 'click', function(e) {
							 //infowindow.open(map,marker); 
							 //alert(marker);
							 codeLatLng( this);
							 }); 
						 }
						for ( var i = 0; i < response.maquinaList.length; i++) {
							//alert(response.maquinaList[i].serie);
							var descricao = "Dt. Atualização: "
									+ response.maquinaList[i].dtAtualizacao
									+ "\nSérie: "
									+ response.maquinaList[i].serie
									+ "\nModelo: "
									+ response.maquinaList[i].modelo
									+ "\nHorímetro: "
									+ response.maquinaList[i].horimetro
									+ "\nCódigo Cliente: "
									+ response.maquinaList[i].codigoCliente
									+ "\nNome Cliente: "
									+ response.maquinaList[i].nomeCliente
									+ "\nFilial: "
									+ response.maquinaList[i].filial;
							// alert(descricao);    
							var latLng = new google.maps.LatLng(
									parseFloat(replaceAll(
											response.maquinaList[i].latitude,
											',', '.')), parseFloat(replaceAll(
											response.maquinaList[i].longitude,
											',', '.')));

							marker = new google.maps.Marker({
								'position' : latLng,
								'map' : map,
								'title' : descricao
							});

							if (response.maquinaList[i].cor != null) {
								if (response.maquinaList[i].cor == "vermelho") {
									iconFile = './img/red-marker.png';
									marker.setIcon(iconFile);
								} else if (response.maquinaList[i].cor == "amarelo") {
									iconFile = './img/yellow-marker.png';
									marker.setIcon(iconFile);
								} else if (response.maquinaList[i].cor == "verde") {
									iconFile = './img/green-marker.png';
									marker.setIcon(iconFile);
								}
							}

							google.maps.event.addListener(marker, 'click',
									function(e) {
										//infowindow.open(map,marker); 
										//alert(marker);
										codeLatLng(this);
									});

						}
						//document.getElementById('map_canvas').innerHTML = "block";
						document.getElementById('map_canvas').style.display = "block";
						document.getElementById('map_canvas').style.maxWidth = "100%"
						document.getElementById('map_canvas').style.maxHeight = "100%"
							document.getElementById('map_canvas').style.zIndex = 1;

						document.getElementById('load').style.display = "none";
						// document.getElementById('load').innerHTML = "none";
						// alert("Success! \n\n" + response);

					},
					onFailure : function() {
						alert('Erro ao tentar logar');
						//document.getElementById('tabela').innerHTML = "block";
						document.getElementById('map_canvas').style.display = "block";
						document.getElementById('map_canvas').style.maxWidth = "100%"
						document.getElementById('map_canvas').style.maxHeight = "100%"

						document.getElementById('load').style.display = "none";
					}
				});
	}

	function replaceAll(string, token, newtoken) {
		while (string.indexOf(token) != -1) {
			string = string.replace(token, newtoken);
		}
		return string;
	}

	function codeAddress() {
		var address = document.getElementById("address").value;
		//alert(address);
		geocoder.geocode({
			'address' : address
		}, function(results, status) {
			if (status == google.maps.GeocoderStatus.OK) {
				map.setCenter(results[0].geometry.location);
				var marker = new google.maps.Marker({
					map : map,
					position : results[0].geometry.location
				});
			} else {
				alert("Não foi possível recuperar o resultado: " + status);
			}
		});
	}

	function codeLatLng(marker) {
		//var input = document.getElementById("latlng").value;
		//var latlngStr = input.split(",",2);
		//alert(marker['position'].lat())
		var lat = parseFloat(marker['position'].lat());
		var lng = parseFloat(marker['position'].lng());
		var latlng = new google.maps.LatLng(lat, lng);
		geocoder.geocode({
			'latLng' : latlng
		}, function(results, status) {
			if (status == google.maps.GeocoderStatus.OK) {
				if (results[1]) {
					map.setZoom(11);
					infowindow.setContent(results[1].formatted_address);
					infowindow.open(map, marker);
				}
			} else {
				alert("Geocoder failed due to: " + status);
			}
		});
	}
	var end;
	function codeAdressLatLng(lat, lng) {
		//var input = document.getElementById("latlng").value;
		//var latlngStr = input.split(",",2);
		//alert(marker['position'].lat())
		var lat = parseFloat(lat);
		var lng = parseFloat(lng);
		var latlng = new google.maps.LatLng(lat, lng);
		geocoder.geocode({
			'latLng' : latlng
		}, function(results, status) {
			if (status == google.maps.GeocoderStatus.OK) {
				if (results[1]) {
					//end = results[1].formatted_address;
					//return end;
					//alert(results[1].formatted_address)
					infowindow.setContent(results[1].formatted_address);
				}
			}
		});
	}
</script>
</head>
<body onload="initializeAjax('')">
	<!-- input id="address" type="textbox" value="Ceará, Fortaleza">
    <input type="button" value="Buscar" onclick="codeAddress()"-->
	<table>
		<tr>
			<td><label for="criticidadeCbx">Criticidade:</label> <select
				id="criticidadeCbx" name="criticidadeCbx"
				onchange="change_criticidadeCbx()">
					<option selected value="TODAS">Todas</option>
					<option value="CRI">Crítico</option>
					<option value="CRI_M">Criticidade Média</option>
					<option value="N_CRI">Não Crítico</option>
			</select></td>
			<td>
				
					<label for="pesquisarText">Pesquisar:</label> 
					<INPUT TYPE="text" NAME="pesquisar"  id="pesquisarTXT" title="Digite o modelo, número de série ou cliente e click em pesquisar."/>
					<button onclick="pesquisarTXT()">Pesquisar</button>
				
			</td>
		</tr>
	</table>
	<div id="message"></div>
	<div class="imagemlod" id='load'>
		<img
			src=<%="http://" + request.getServerName() + ":"
					+ request.getServerPort() + request.getContextPath()
					+ "/img/lc.gif"%>
			width="199" height="64" border="0" alt="">
	</div>
	<div id="map_canvas" style="width: 100%; height: 100%"></div>
</body>
</html>