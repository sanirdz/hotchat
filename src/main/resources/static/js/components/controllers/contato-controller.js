angular.module('chatapp')
.controller('ContatoController', ContatoController)
	
	function ContatoController($scope, $rootScope, ApiService, $uibModal) {
		
		$scope.contatos = [];
		$scope.contatosNovos = [];
		
		var modalInstance = null;
		
		$scope.adicionarContato = function(usuario) {
			ApiService
				.adicionarContato(usuario)
				.then((contatoNovo) => {
					$scope.contatos.push(contatoNovo)
					//TODO ordenar
				});
				
			modalInstance.close();
		}
		
		$scope.openModalAdicionarContato = function(){
			ApiService
				.buscarContatosNovos()
				.then((contatosNovos) => {
					$scope.contatosNovos = contatosNovos;
			});
			
			modalInstance = $uibModal.open({
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'js/components/partials/modal/modal-adicionar-contato.html'		
			});	
		};

		$scope.confirmExcluirUsuario = function(contato) {
			swal({
				  title: 'Tem certeza?',
				  text: "Você irá excluir o contato " + contato.login + ".",
				  type: 'warning',
				  showCancelButton: true,
				  confirmButtonColor: '#d33',
				  cancelButtonText: 'Cancelar',
				  confirmButtonText: 'Excluir'
				}).then(function () {
					ApiService
						.excluirContato(contato)
						.then((result) => {
							  console.log('removendo o contato ' + contato.login +  ' da lista de contatos')
							  $scope.contatos = $scope.contatos.filter(function(el) {
								 return el.login !== contato.login;
							  });
							  
							  if($rootScope.contatoAtivo && $rootScope.contatoAtivo.login == contato.login) {
								  $rootScope.contatoAtivo = null;
							  }
						});
				})	
		};
		
		$scope.confirmBloqueioUsuario = function(contato) {
			swal({
				  title: 'Tem certeza?',
				  text: "Você irá bloquear o contato " + contato.login + ".",
				  type: 'warning',
				  showCancelButton: true,
				  confirmButtonColor: '#d33',
				  cancelButtonText: 'Cancelar',
				  confirmButtonText: 'Bloquear'
				}).then(function () {
					ApiService
						.bloquearContato(contato)
						.then((result) => {
							  contato.bloqueado = true;
						});
				})	
		};
		
		
		$scope.confirmDesbloqueioUsuario = function(contato){
			swal({
				  title: 'Tem certeza?',
				  text: "Você irá desbloquear o contato " + contato.login + ".",
				  type: 'warning',
				  showCancelButton: true,
				  confirmButtonColor: '#d33',
				  cancelButtonText: 'Cancelar',
				  confirmButtonText: 'Desbloquear'
				}).then(function () {
					ApiService
						.desbloquearContato(contato)
						.then((result) => {
						  contato.bloqueado = false;
						 
						  console.log('enviando evento usuario-desbloqueado')
						  $rootScope.$broadcast('usuario-desbloqueado', {
							  contato: contato
			        	});
					});
				})	
		};
		
		$scope.selecionaContato = function(contato){
			$rootScope.contatoAtivo = contato;
		}
		
		$scope.$on('nova-mensagem', function(event, data) {
			if(!$rootScope.contatoAtivo || data.mensagem.emissor != $rootScope.contatoAtivo.login) {
				for(var i = 0; i < $scope.contatos.length; i++) {
					var contato = $scope.contatos[i];
					if(contato.login == data.mensagem.emissor) {
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
				.then((contatos) => {
					$scope.contatos = contatos;
				});
		} 
		
		function trataErro(result){
			console.error(result);
		}
	}