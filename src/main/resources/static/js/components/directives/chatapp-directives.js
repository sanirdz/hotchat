angular.module('chatapp')

    .directive('contatoList', function() {
        return {
            restrict: "E",
            templateUrl: 'js/components/partials/contato-list.html',
            controller: 'ContatoController'
        }
    })

    .directive('chatPanel', function() {
        return {
            restrict: "E",
            templateUrl: 'js/components/partials/chat-panel.html',
            controller: 'ChatController'
        }
    })
    
    .directive('hotEnter', function () {
    	return function (scope, element, attrs) {
	        element.bind("keydown keypress", function (event) {
	            if(event.which === 13) {
	                scope.$apply(function (){
	                    scope.$eval(attrs.hotEnter);
	                });
	
	                event.preventDefault();
	            }
	        })
	    }
    })
