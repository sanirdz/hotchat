package br.com.paulo.hotchat.websocket;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import br.com.paulo.hotchat.api.resource.MensagemDTO;
import br.com.paulo.hotchat.domain.Mensagem;

@Service
public class WebSocketService {

	private static final Logger log = LoggerFactory.getLogger(WebSocketService.class);
	
	private final SimpMessagingTemplate simpMessagingTemplate;
	private final UsuariosConectados usuariosConectados;
	private final String websocketChat;
	private final String websocketOnlineUsers;
	
	public WebSocketService(SimpMessagingTemplate simpMessagingTemplate, 
			UsuariosConectados usuariosConectados,
			@Value("${hotchat.websocket.chat}") String websocketChat,
			@Value("${hotchat.websocket.online-users}") String websocketOnlineUsers) {
		this.simpMessagingTemplate = simpMessagingTemplate;
		this.usuariosConectados = usuariosConectados;
		this.websocketChat = websocketChat;
		this.websocketOnlineUsers = websocketOnlineUsers;
	}

	public void enviarMensagem(Mensagem mensagem) {
		log.info("Enviando mensagem " + mensagem.getId());
		SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.create();
		headers.addNativeHeader("tipo", "mensagem");
		
		simpMessagingTemplate.convertAndSendToUser(
				mensagem.getDestinatario().getLogin(), 
				websocketChat, 
				new MensagemDTO()
					.setConteudo(mensagem.getConteudo())
					.setDataEnvio(mensagem.getDataEnvio())
					.setEmissor(mensagem.getEmissor().getLogin())
					.setId(mensagem.getId()),
				headers.getMessageHeaders());
	}
	
	public void usuarioOnline(String usuario) {
		SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.create();
		headers.addNativeHeader("tipo", "online");
		
		Map<String, String> payload = new HashMap<>();
		payload.put("usuario", usuario);
		
		simpMessagingTemplate.convertAndSend(
				websocketOnlineUsers,
				payload,
				headers.getMessageHeaders());
	}
	
	public void usuarioOffline(String usuario) {
		SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.create();
		headers.addNativeHeader("tipo", "offline");
		
		Map<String, String> payload = new HashMap<>();
		payload.put("usuario", usuario);
		
		simpMessagingTemplate.convertAndSend(
				websocketOnlineUsers, 
				payload,
				headers.getMessageHeaders());
	}
	
	@EventListener
	private void handleSessionConnected(SessionConnectEvent event) {
		log.info("{} conectado ao websocket.", event.getUser().getName());

		usuariosConectados.usuarioConectado(event.getUser().getName());
		usuarioOnline(event.getUser().getName());
	}

	@EventListener
	private void handleSessionDisconnect(SessionDisconnectEvent event) {
		log.info("{} desconectado do websocket.", event.getUser().getName());

		usuariosConectados.usuarioDesconectado(event.getUser().getName());
		usuarioOffline(event.getUser().getName());		
	}
}
