angular.module('chatapp')
	.factory('Contato', function($resource, API_URL){
		return $resource(API_URL + '/api/usuarios', {}, {
			query: {
	            method: 'GET',
	            isArray: true
	        },
	        create: {
	            method: 'POST'
	        },
		});
	});