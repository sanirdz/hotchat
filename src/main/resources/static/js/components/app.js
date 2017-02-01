angular
	.module('chatapp', ['ngResource', 'ngStomp'])
	.constant('API_URL', 'http://localhost:8080/hotchat')
	.run( function($stomp, $rootScope) {
		$rootScope.me = $("#me").val();
		
		console.log('conectando ao websocket...')
	    $stomp.connect('/hotchat/chat-websocket', {})
	      .then(function (frame) {
	        $stomp.subscribe('/user/queue/chat', function (mensagem, headers, res) {
	        	
	        	if(headers.tipo == 'mensagem') {
	        		console.log('fazendo broadcast...')
		        	$rootScope.$broadcast('nova-mensagem', {
		        		  mensagem: mensagem
		        	});
	        	}
	        });
	        
	        $stomp.subscribe('/queue/online-users', function(payload, headers, res) {
	        	if(headers.tipo == 'online') {
		        	$rootScope.$broadcast('usuario-conectado', {
		        		  usuario: payload.usuario
		        	});	
	        	}
	        	if(headers.tipo == 'offline') {
		        	$rootScope.$broadcast('usuario-desconectado', {
		        		  usuario: payload.usuario
		        	});	
	        	}
	        });
      })
	})
