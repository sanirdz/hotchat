angular.module('chatapp')
.controller('ContatoController', ContatoController)
	
	function ContatoController($scope, $rootScope, ApiService) {
		
		$scope.contatos = [];
		
		$scope.selecionaContato = function(contato){
			$rootScope.contatoAtivo = contato;
		}

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