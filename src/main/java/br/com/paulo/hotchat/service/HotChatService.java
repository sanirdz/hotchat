package br.com.paulo.hotchat.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import br.com.paulo.hotchat.domain.Mensagem;
import br.com.paulo.hotchat.domain.Usuario;
import br.com.paulo.hotchat.repository.MensagemRepository;
import br.com.paulo.hotchat.repository.UsuarioRepository;

@Service
public class HotChatService implements ApplicationListener<AuthenticationSuccessEvent> {

	private static final Logger log = LoggerFactory.getLogger(HotChatService.class);
	
	private final UsuarioRepository usuarioRepository;
	private final MensagemRepository mensagemRepository;
	
	public HotChatService(UsuarioRepository usuarioRepository, MensagemRepository mensagemRepository) {
		this.usuarioRepository = usuarioRepository;
		this.mensagemRepository = mensagemRepository;
	}

	@Override
	public void onApplicationEvent(AuthenticationSuccessEvent event) {
		String username = ((UserDetails) event.getAuthentication().getPrincipal()).getUsername();
		log.debug("Atualizando status do usuario {} para online...", username);
		
		Usuario usuario = usuarioRepository.findByLogin(username);
		if(usuario != null) {
			usuario.setOnline(true);
			usuarioRepository.save(usuario);
		} else {
			//TODO usuario nao existe no banco
			throw new RuntimeException("usuario nao existe no banco");
		}
	}

	public Iterable<Usuario> listarUsuarios(String username) {
		//TODO retirar o usuario logado da lista
		return usuarioRepository.findAllBut(username);
	}
	
	public Iterable<Mensagem> listarMensagensNaoLidasDestinatario(String username) {
		log.info("Listando mensagens não lidas para o destinatario {}...", username);
		
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
}