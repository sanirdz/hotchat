angular
	.module('chatapp', ['ngResource', 'ngStomp'])
	.constant('API_URL', 'http://localhost:8080/hotchat')
	.run( function($rootScope) {
		$rootScope.me = $("#me").val();
	})
