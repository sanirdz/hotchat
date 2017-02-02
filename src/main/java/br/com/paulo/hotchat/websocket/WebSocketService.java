package br.com.paulo.hotchat.websocket;

import java.util.HashMap;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import br.com.paulo.hotchat.api.resource.MensagemDTO;
import br.com.paulo.hotchat.domain.Mensagem;

@Service
public class WebSocketService {

	private final SimpMessagingTemplate simpMessagingTemplate;
	
	public WebSocketService(SimpMessagingTemplate simpMessagingTemplate) {
		this.simpMessagingTemplate = simpMessagingTemplate;
	}

	public void enviarMensagem(Mensagem mensagem) {
		SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.create();
		headers.addNativeHeader("tipo", "mensagem");
		
		simpMessagingTemplate.convertAndSendToUser(
				mensagem.getDestinatario().getLogin(), 
				"/queue/chat", //TODO parametrizar
				new MensagemDTO()
					.setConteudo(mensagem.getConteudo())
					.setDataEnvio(mensagem.getDataEnvio())
					.setEmissor(mensagem.getEmissor().getLogin()),
				headers.getMessageHeaders());
	}
	
	public void usuarioOnline(String usuario) {
		SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.create();
		headers.addNativeHeader("tipo", "online");
		
		Map<String, String> payload = new HashMap<>();
		payload.put("usuario", usuario);
		
		simpMessagingTemplate.convertAndSend(
				"/queue/online-users", //TODO parametrizar
				payload,
				headers.getMessageHeaders());
	}
	
	public void usuarioOffline(String usuario) {
		SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.create();
		headers.addNativeHeader("tipo", "offline");
		
		Map<String, String> payload = new HashMap<>();
		payload.put("usuario", usuario);
		
		simpMessagingTemplate.convertAndSend(
				"/queue/online-users", 
				payload,
				headers.getMessageHeaders());
	}
}
