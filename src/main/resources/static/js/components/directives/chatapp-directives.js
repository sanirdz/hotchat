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
