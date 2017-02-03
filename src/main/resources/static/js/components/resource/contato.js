angular.module('chatapp')
	.factory('Contato', function($resource, API_URL){
		return $resource(API_URL + '/api/usuarios', {}, {
			query: {
	            method: 'GET',
	            isArray: true
	        },
	        adicionarContato: {
	            method: 'POST',
	            url: API_URL + '/api/usuarios/:usuario',
	            params: {
	            	usuario: '@usuario'
				}
	        },
	        buscarContatosNovos: {
	            method: 'GET',
	            url: API_URL + '/api/usuarios/novos',
	            isArray: true
	        },
	        create: {
	            method: 'POST'
	        },
	        bloquear: {
	        	url: API_URL + '/api/usuarios/:contato/bloquear',
	            method: 'POST',
				params: {
					contato: '@contato'
				}
	        },
	        desbloquear: {
	        	url: API_URL + '/api/usuarios/:contato/desbloquear',
	            method: 'POST',
				params: {
					contato: '@contato'
				}
	        },
	        excluir: {
	        	url: API_URL + '/api/usuarios/:contato',
	            method: 'DELETE',
				params: {
					contato: '@contato'
				}
	        },
		});
	});