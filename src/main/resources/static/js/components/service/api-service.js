angular.module('chatapp')

.service('ApiService', function(Contato, Mensagem) {
    var service = {};

    service.notificaMensagemLida = function(mensagem) {
    	console.log('notificando mensagem lida ' + mensagem.id);
    	Mensagem.notificaMensagemLida({"id": mensagem.id});
    }
    
    service.adicionarContato = function(usuario) {
    	return Contato.adicionarContato({"usuario": usuario.login}).$promise;
    };
    
    service.buscarContatosNovos = function() {
    	return Contato.buscarContatosNovos().$promise;
    };
    
    service.recuperaContatos = function() {
    	return Contato.query().$promise;
    };
    
    service.bloquearContato = function(contato) {
    	return Contato.bloquear({"contato": contato.login}).$promise;
    };

    service.excluirContato = function(contato) {
    	return Contato.excluir({"contato": contato.login}).$promise;
    };
    
    service.desbloquearContato = function(contato) {
    	return Contato.desbloquear({"contato": contato.login}).$promise;
    };

    service.enviaMensagem = function(login, mensagem) {
    	return Mensagem.create({"loginDestinatario": login, "conteudo": mensagem}).$promise;
    };

    service.recuperaHistorico = function(destinatario) {
    	return Mensagem.query({"destinatario": destinatario}).$promise;	
    };

    service.marcarMensagensComoLidas = function(emissor) {
    	Mensagem.lerMensagens({"emissor": emissor});
    }
    
    return service;
})
