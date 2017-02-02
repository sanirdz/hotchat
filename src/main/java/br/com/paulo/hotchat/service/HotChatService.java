package br.com.paulo.hotchat.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import br.com.paulo.hotchat.domain.Mensagem;
import br.com.paulo.hotchat.domain.UserRole;
import br.com.paulo.hotchat.domain.Usuario;
import br.com.paulo.hotchat.repository.MensagemRepository;
import br.com.paulo.hotchat.repository.UserRoleRepository;
import br.com.paulo.hotchat.repository.UsuarioRepository;
import br.com.paulo.hotchat.websocket.UsuariosConectados;

@Service
public class HotChatService {

	private static final Logger log = LoggerFactory.getLogger(HotChatService.class);

	private final UsuarioRepository usuarioRepository;
	private final UserRoleRepository userRoleRepository;
	private final MensagemRepository mensagemRepository;
	private final SimpMessagingTemplate simpMessagingTemplate;
	private final UsuariosConectados usuariosConectados;
	private final PasswordEncoder passwordEncoder;

	public HotChatService(UsuarioRepository usuarioRepository, 
			MensagemRepository mensagemRepository, 
			SimpMessagingTemplate simpMessagingTemplate,
			UsuariosConectados usuariosConectados,
			PasswordEncoder passwordEncoder,
			UserRoleRepository userRoleRepository) {

		this.usuarioRepository = usuarioRepository;
		this.mensagemRepository = mensagemRepository;
		this.simpMessagingTemplate = simpMessagingTemplate;
		this.usuariosConectados = usuariosConectados;
		this.passwordEncoder = passwordEncoder;
		this.userRoleRepository = userRoleRepository;
	}

	public Iterable<Usuario> listarUsuarios(String username) {
		Iterable<Usuario> usuarios = usuarioRepository.findAllBut(username);

		usuarios.forEach(usuario -> {
			usuario.setOnline(usuariosConectados.estaConectado(usuario));
			usuario.setTotalMensagensNaoLidas(recuperaQuantidadeMensagensNaoLidas(usuario.getLogin(), username));
		});

		return usuarios;
	}

	public Iterable<Mensagem> listarMensagensDestinatarioEmissor(String loginDestinatario, String loginEmissor) {
		log.info("Listando mensagens trocadas entre o destinatário {} e o emissor {}...", loginDestinatario, loginEmissor);

		Usuario destinatario = usuarioRepository.findByLogin(loginDestinatario);
		Usuario emissor = usuarioRepository.findByLogin(loginEmissor);

		Iterable<Mensagem> mensagens1 = mensagemRepository.findAllByDestinatarioAndEmissorOrderByDataEnvio(destinatario, emissor);
		Iterable<Mensagem> mensagens2 = mensagemRepository.findAllByDestinatarioAndEmissorOrderByDataEnvio(emissor, destinatario);


		Set<Mensagem> mensagens = new TreeSet<>(new Comparator<Mensagem>() {
			@Override
			public int compare(Mensagem o1, Mensagem o2) {
				return o1.getDataEnvio().compareTo(o2.getDataEnvio());
			}
		});
		
		mensagens1.forEach(m -> mensagens.add(m));
		mensagens2.forEach(m -> mensagens.add(m));
		
		log.debug("{} mensagens trocadas entre o destinatário {} e o emissor {}.", mensagens.size(), loginDestinatario, loginEmissor);
		
		return mensagens;
	}

	public void marcarMensagensLidas(String loginEmissor, String loginDestinatario) {
		log.info("Marcando mensagens como lidas. {} -> {}...", loginEmissor, loginDestinatario);
		
		Usuario destinatario = usuarioRepository.findByLogin(loginDestinatario);
		Usuario emissor = usuarioRepository.findByLogin(loginEmissor);
		
		Iterable<Mensagem> mensagens = mensagemRepository.findAllByDestinatarioAndEmissorOrderByDataEnvio(destinatario, emissor);	
		
		mensagens.forEach(m -> m.setLida(true));
		mensagemRepository.save(mensagens);
	}
	
	public Integer recuperaQuantidadeMensagensNaoLidas(String loginEmissor, String loginDestinatario) {
		Integer qtd = mensagemRepository.recuperaQuantidadeMensagensNaoLidas(loginEmissor, loginDestinatario);
		log.debug("quantidade {} -> {}: {}", loginEmissor, loginDestinatario, qtd);
		return qtd;
	}

	public void enviarMensagem(Mensagem mensagem, String emissor) {
		mensagem
		.setEmissor(usuarioRepository.findByLogin(emissor))
		.setDestinatario(usuarioRepository.findByLogin(mensagem.getDestinatario().getLogin()))
		.setDataEnvio(LocalDateTime.now())
		.setLida(false);


		if(usuariosConectados.estaConectado(mensagem.getDestinatario())) {
			log.info("Enviando mensagem - destinatario: {}, emissor: {}, conteudo: {}...", mensagem.getDestinatario().getLogin(), emissor, mensagem.getConteudo());

			SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.create();
			headers.addNativeHeader("tipo", "mensagem");
			
			simpMessagingTemplate.convertAndSendToUser(
					mensagem.getDestinatario().getLogin(), 
					"/queue/chat", 
					mensagem,
					headers.getMessageHeaders());
		}
		mensagemRepository.save(mensagem);
	}

	@EventListener
	private void handleSessionConnected(SessionConnectEvent event) {
		log.info("{} conectado ao websocket.", event.getUser().getName());

		usuariosConectados.usuarioConectado(usuarioRepository.findByLogin(event.getUser().getName()));
		
		SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.create();
		headers.addNativeHeader("tipo", "online");
		
		Map<String, String> payload = new HashMap<>();
		payload.put("usuario", event.getUser().getName());
		
		simpMessagingTemplate.convertAndSend(
				"/queue/online-users", 
				payload,
				headers.getMessageHeaders());
	}

	@EventListener
	private void handleSessionDisconnect(SessionDisconnectEvent event) {
		log.info("{} desconectado do websocket.", event.getUser().getName());

		usuariosConectados.usuarioDesconectado(usuarioRepository.findByLogin(event.getUser().getName()));
		
		SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.create();
		headers.addNativeHeader("tipo", "offline");
		
		Map<String, String> payload = new HashMap<>();
		payload.put("usuario", event.getUser().getName());
		
		simpMessagingTemplate.convertAndSend(
				"/queue/online-users", 
				payload,
				headers.getMessageHeaders());
	}

	public Usuario salvar(Usuario usuario) {
		//TODO conferir o login unique?
		usuario
		.setSenha(passwordEncoder.encode(usuario.getSenha()))
		.setEnabled(true);

		userRoleRepository.save(new UserRole().setUsername(usuario.getLogin()).setAuthority("ROLE_USER"));

		return usuarioRepository.save(usuario);
	}
}