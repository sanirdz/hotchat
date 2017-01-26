package br.com.paulo.hotchat.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import br.com.paulo.hotchat.domain.Mensagem;
import br.com.paulo.hotchat.domain.Usuario;
import br.com.paulo.hotchat.repository.MensagemRepository;
import br.com.paulo.hotchat.repository.UsuarioRepository;

@Service
public class HotChatService {

	private static final Logger log = LoggerFactory.getLogger(HotChatService.class);
	
	private final UsuarioRepository usuarioRepository;
	private final MensagemRepository mensagemRepository;
	private final SimpMessagingTemplate simpMessagingTemplate;
	
	public HotChatService(UsuarioRepository usuarioRepository, MensagemRepository mensagemRepository, SimpMessagingTemplate simpMessagingTemplate) {
		this.usuarioRepository = usuarioRepository;
		this.mensagemRepository = mensagemRepository;
		this.simpMessagingTemplate = simpMessagingTemplate;
	}

	public Iterable<Usuario> listarUsuarios(String username) {
		//TODO retirar o usuario logado da lista
		return usuarioRepository.findAllBut(username);
	}
	
	public Iterable<Mensagem> listarMensagensNaoLidasDestinatario(String username) {
		log.info("Listando mensagens não lidas para o destinatário {}...", username);
		
		Usuario destinatario = usuarioRepository.findByLogin(username);
		
		//TODO parametrizar isso
		PageRequest pageable = new PageRequest(0, 1000);
		Page<Mensagem> mensagens = mensagemRepository.findAllByLidaAndDestinatario(false, destinatario, pageable);
		
		for (Mensagem mensagem : mensagens) {
			mensagem.setLida(true);
		}
		mensagemRepository.save(mensagens);
		
		
		log.debug("{} mensagens não lidas para o destinatario {}...", mensagens.getTotalElements(), username);
		
		return mensagens;
	}

	public void enviarMensagem(Mensagem mensagem, String emissor) {
		mensagem
			.setEmissor(usuarioRepository.findByLogin(emissor))
			.setDestinatario(usuarioRepository.findOne(mensagem.getDestinatario().getId()))
			.setDataEnvio(LocalDateTime.now())
			.setLida(false);
		

//TODO	if(usuarioIsOnline()) {
		mensagem.setLida(true);
		log.info("Enviando mensagem - destinatario: {}, emissor: {}, conteudo: {}...", mensagem.getDestinatario().getLogin(), emissor, mensagem.getConteudo());
		
		simpMessagingTemplate.convertAndSendToUser(
				mensagem.getDestinatario().getLogin(), 
				"/queue/chat", 
				mensagem);
//		}
		mensagemRepository.save(mensagem);
	}

}