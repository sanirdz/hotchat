var stompClient = null;

function conectar() {
    var socket = new SockJS('/hotchat/chat-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        stompClient.subscribe('/user/queue/chat', function (mensagem) {
        	exibirMensagem(JSON.parse(mensagem.body));
        });
    });
}

function exibirMensagem(mensagem) {
	$('#tabela-mensagens > tbody:last-child')
		.append("<tr class='animated flash'>" + 
				"<td>" + mensagem.dataEnvio + "</td>" + 
				"<td>" + mensagem.emissor.login + "</td>" +
				"<td>" + mensagem.conteudo + "</td>" +
				"</tr>");
	
}