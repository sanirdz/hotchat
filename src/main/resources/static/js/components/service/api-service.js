angular.module('chatapp')

.service('ApiService', function(Contato, Mensagem) {
    var service = {};

    service.recuperaContatos = function() {
    	return Contato.query().$promise;
    };
        
    service.enviaMensagem = function(login, mensagem) {
    	return Mensagem.create({"loginDestinatario": login, "conteudo": mensagem}).$promise;
    };

    service.recuperaHistorico = function(destinatario) {
    	return Mensagem.query({"destinatario": destinatario}).$promise;	
    };

    service.recuperaQuantidadeMensagensNaoLidas = function(emissor, destinatario) {
    	return 12;	
    };
    
    return service;
})
