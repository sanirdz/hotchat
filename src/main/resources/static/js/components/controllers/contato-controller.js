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
				.then((result) =>  $scope.contatos = result);
		} 
		
		function trataErro(result){
			console.error(result);
		}
	}