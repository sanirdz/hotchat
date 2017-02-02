angular.module('chatapp')
	.factory('Mensagem', function($resource, API_URL){
		return $resource(API_URL + '/api/mensagens', {}, {
			create: {
	            method: 'POST'
	        },
	        query: {
	            method: 'GET',
	            isArray: true,
	            url: API_URL + '/api/mensagens/:destinatario',
	            params: {
	            	destinatario: '@destinatario'
	            }
	        },
	        notificaMensagemLida: {
	        	method: 'POST',
	        	url: API_URL + '/api/mensagens/:id/marcarLida',
	        	params: {
	        		id: '@id'
				}
	        },
			lerMensagens: {
				method: 'POST',
				url: API_URL + '/api/mensagens/:emissor/marcarLidas',
				params: {
					emissor: '@emissor'
				}
			}
		});
	});