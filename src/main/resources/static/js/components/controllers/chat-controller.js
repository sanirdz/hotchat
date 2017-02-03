angular.module('chatapp')
	.controller('ChatController', ChatController)
	
	function ChatController($scope, $rootScope, ApiService) {
		init();
		
		$scope.enviaMensagem = function(mensagem, destinatario) {
			console.log('enviando mensagem ' + mensagem + ' para ' + destinatario + '...');
			
			ApiService.enviaMensagem(destinatario, mensagem);
			
			$scope.historico.push({'conteudo' : mensagem, 'emissor': $rootScope.me, 'dataEnvio': new Date()});
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
					marcarMensagensComoLidas(contato.login);
					contato.totalMensagensNaoLidas = 0;
				}
			});
		} 
		
		$scope.$on('usuario-desbloqueado', function(event, data) {
			var contato = data.contato;
			if($rootScope.contatoAtivo && contato.login == $rootScope.contatoAtivo.login) {
				recuperaHistorico(contato.login);
				
			}
		});
		
		$scope.$on('nova-mensagem', function(event, data) {
			var mensagem = data.mensagem;
			
			if($rootScope.contatoAtivo && mensagem.emissor == $rootScope.contatoAtivo.login) {
	        	if($scope.historico) {
	        		console.log('adcionando msg do ' + mensagem.emissor + '...');
		        	$scope.historico.push({'conteudo' : mensagem.conteudo, 'emissor': {'login': mensagem.emissor}, 'dataEnvio': mensagem.dataEnvio});
		        	
		        	$scope.$apply();
		        	
		        	ApiService.notificaMensagemLida(mensagem);
	        	}
    		}
		});

		function recuperaHistorico(destinatario){
			 ApiService
			 	.recuperaHistorico(destinatario)
			 	.then((result) =>  $scope.historico = result);	
		}
		
		function marcarMensagensComoLidas(emissor) {
			ApiService.marcarMensagensComoLidas(emissor);
		}
	}