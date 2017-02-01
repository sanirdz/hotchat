angular.module('chatapp')
	.factory('Mensagem', function($resource, API_URL){
		return $resource(API_URL + '/api/mensagens?destinatario=:destinatario', {}, {
			create: {
	            method: 'POST'
	        },
	        query: {
	            method: 'GET',
	            isArray: true,
	            params: {
	            	destinatario: '@destinatario'
	            }
	        },
		});
	});