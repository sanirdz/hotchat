angular.module('chatapp')
	.controller('ChatController', ChatController)
	
	function ChatController($scope, $rootScope, ApiService) {
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
			$rootScope.$watch('contatoAtivo', (contato) => {
				if (contato) {
					recuperaHistorico(contato.login);
					contato.totalMensagensNaoLidas = 0;
				}
			});
		} 
		
		$scope.$on('nova-mensagem', function(event, data) {
			var mensagem = data.mensagem;
			
			if($rootScope.contatoAtivo && mensagem.emissor.login == $rootScope.contatoAtivo.login) {
	        	if($scope.historico) {
	        		console.log('adcionando msg do ' + mensagem.emissor.login + '...');
		        	$scope.historico.push({'conteudo' : mensagem.conteudo, 'emissor': {'login': mensagem.emissor.login}, 'dataEnvio': mensagem.dataEnvio});
		        	
		        	$scope.$apply();
	        	}
    		}
		});

		function recuperaHistorico(destinatario){
			 ApiService
			 	.recuperaHistorico(destinatario)
			 	.then((result) =>  $scope.historico = result);	
		}
	}