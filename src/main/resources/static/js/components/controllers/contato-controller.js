angular.module('chatapp')
.controller('ContatoController', ContatoController)
	
	function ContatoController($scope, $rootScope, ApiService) {
		
		$scope.contatos = [];
		
		$scope.selecionaContato = function(contato){
			$rootScope.contatoAtivo = contato;
		}
		
		$scope.$on('nova-mensagem', function(event, data) {
			if(!$rootScope.contatoAtivo || data.mensagem.emissor.login != $rootScope.contatoAtivo.login) {
				for(var i = 0; i < $scope.contatos.length; i++) {
					var contato = $scope.contatos[i];
					if(contato.login == data.mensagem.emissor.login) {
						contato.totalMensagensNaoLidas++;
					}
				}
				$scope.$apply();
			}
   		});
		
		$scope.$on('usuario-conectado', function(event, data) {
			for(var i = 0; i < $scope.contatos.length; i++) {
				var contato = $scope.contatos[i];
				if(contato.login == data.usuario) {
					contato.online = true;
				}
			}
			$scope.$apply();
   		});

		$scope.$on('usuario-desconectado', function(event, data) {
			for(var i = 0; i < $scope.contatos.length; i++) {
				var contato = $scope.contatos[i];
				if(contato.login == data.usuario) {
					contato.online = false;
				}
			}
			$scope.$apply();
		});
		
		init();
		
		function init() {
			ApiService
				.recuperaContatos()
				.then((result) =>  {
					for(i = 0; i < result.length; i++) {
						var contato = result[i];
						var qtd = ApiService.recuperaQuantidadeMensagensNaoLidas(contato, $rootScope.me)
						
						contato.totalMensagensNaoLidas = qtd;
					}
					
					$scope.contatos = result;
				});
		} 
		
		function trataErro(result){
			console.error(result);
		}
	}