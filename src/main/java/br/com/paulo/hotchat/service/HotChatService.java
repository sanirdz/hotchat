package br.com.paulo.hotchat.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
		});
		
		return usuarios;
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
			.setDestinatario(usuarioRepository.findByLogin(mensagem.getDestinatario().getLogin()))
			.setDataEnvio(LocalDateTime.now())
			.setLida(false);
		

		if(usuariosConectados.estaConectado(mensagem.getDestinatario())) {
			//TODO mudar como registra que a mensagem é lida
			mensagem.setLida(true);
			
			log.info("Enviando mensagem - destinatario: {}, emissor: {}, conteudo: {}...", mensagem.getDestinatario().getLogin(), emissor, mensagem.getConteudo());
			
			simpMessagingTemplate.convertAndSendToUser(
					mensagem.getDestinatario().getLogin(), 
					"/queue/chat", 
					mensagem);
		}
		mensagemRepository.save(mensagem);
	}
	
	@EventListener
	private void handleSessionConnected(SessionConnectEvent event) {
		log.info("{} conectado ao websocket.", event.getUser().getName());
		
		usuariosConectados.usuarioConectado(usuarioRepository.findByLogin(event.getUser().getName()));
	}

	@EventListener
	private void handleSessionDisconnect(SessionDisconnectEvent event) {
		log.info("{} desconectado do websocket.", event.getUser().getName());
		
		usuariosConectados.usuarioDesconectado(usuarioRepository.findByLogin(event.getUser().getName()));
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