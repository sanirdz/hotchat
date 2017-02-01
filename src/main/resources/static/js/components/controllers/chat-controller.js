angular.module('chatapp')
	.controller('ChatController', ChatController)
	
	function ChatController($stomp, $scope, $rootScope, ApiService) {
		init();
		
		$scope.enviaMensagem = function(mensagem, destinatario) {
			console.log('enviando mensagem ' + mensagem + ' para ' + destinatario + '...');
			
			ApiService.enviaMensagem(destinatario, mensagem);
			
			$scope.historico.push({'conteudo' : mensagem, 'emissor': {'login': $rootScope.me}, 'dataEnvio': new Date()});
		}
		
		function scrollToBottom(length) {
			$(".cht-content").animate({ scrollTop: length*100}, 100);
		}
		
		$scope.$watchCollection('historico', (historico) => {
			if (historico) {
				scrollToBottom(historico.length);
			}
		});

		
		function init() {
			conectarWebSocket();
			
			$rootScope.$watch('contatoAtivo', (contato) => {
				if (contato) {
					recuperaHistorico(contato.login);
				}
			});
		} 

		function recuperaHistorico(destinatario){
			 ApiService
			 	.recuperaHistorico(destinatario)
			 	.then((result) =>  $scope.historico = result);	
		}
		
		function conectarWebSocket() {
			console.log('conectando ao websocket...')
		    $stomp.connect('/hotchat/chat-websocket', {})
		      .then(function (frame) {
		        var subscription = $stomp.subscribe('/user/queue/chat', function (mensagem, headers, res) {

		        	if($scope.historico && mensagem.emissor.login == $rootScope.contatoAtivo.login) {
		        		console.log('adcionando msg do ' + mensagem.emissor.login + '...');
			        	$scope.historico.push({'conteudo' : mensagem.conteudo, 'emissor': {'login': mensagem.emissor.login}, 'dataEnvio': mensagem.dataEnvio});
			        	
			        	$scope.$apply();
		        	}
		        })
	      })
		}
	}