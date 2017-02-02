package br.com.paulo.hotchat.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.com.paulo.hotchat.domain.Mensagem;
import br.com.paulo.hotchat.domain.Usuario;
import br.com.paulo.hotchat.repository.MensagemRepository;
import br.com.paulo.hotchat.repository.UsuarioRepository;
import br.com.paulo.hotchat.websocket.UsuariosConectados;
import br.com.paulo.hotchat.websocket.WebSocketService;

@Service
public class MensagemService {

	private static final Logger log = LoggerFactory.getLogger(MensagemService.class);
	
	private final UsuarioRepository usuarioRepository;
	private final MensagemRepository mensagemRepository;
	private final WebSocketService webSocketService;
	private final UsuariosConectados usuariosConectados;
	
	public MensagemService(UsuarioRepository usuarioRepository, MensagemRepository mensagemRepository,
			WebSocketService webSocketService, UsuariosConectados usuariosConectados) {
		this.usuarioRepository = usuarioRepository;
		this.mensagemRepository = mensagemRepository;
		this.webSocketService = webSocketService;
		this.usuariosConectados = usuariosConectados;
	}

	public Iterable<Mensagem> listarMensagensDestinatarioEmissor(String loginDestinatario, String loginEmissor) {
		log.info("Listando mensagens trocadas entre o destinatário {} e o emissor {}...", loginDestinatario, loginEmissor);

		Usuario destinatario = usuarioRepository.findByLogin(loginDestinatario);
		Usuario emissor = usuarioRepository.findByLogin(loginEmissor);

		Iterable<Mensagem> mensagensEnviadas = mensagemRepository.findAllByDestinatarioAndEmissorOrderByDataEnvio(destinatario, emissor);
		Iterable<Mensagem> mensagensRecebidas = mensagemRepository.findAllByDestinatarioAndEmissorOrderByDataEnvio(emissor, destinatario);

		Set<Mensagem> mensagens = new TreeSet<>(new Comparator<Mensagem>() {
			@Override
			public int compare(Mensagem o1, Mensagem o2) {
				return o1.getDataEnvio().compareTo(o2.getDataEnvio());
			}
		});
		
		mensagensEnviadas.forEach(m -> mensagens.add(m));
		mensagensRecebidas.forEach(m -> mensagens.add(m));
		
		log.debug("{} mensagens trocadas entre o destinatário {} e o emissor {}.", mensagens.size(), loginDestinatario, loginEmissor);
		
		return mensagens;
	}

	public Integer marcarMensagensLidas(String loginEmissor, String loginDestinatario) {
		log.info("Marcando mensagens como lidas. {} -> {}...", loginEmissor, loginDestinatario);
		
		Usuario destinatario = usuarioRepository.findByLogin(loginDestinatario);
		Usuario emissor = usuarioRepository.findByLogin(loginEmissor);
		
		Iterable<Mensagem> mensagens = mensagemRepository.findAllByDestinatarioAndEmissorOrderByDataEnvio(destinatario, emissor);	
		
		Integer qtd = 0;
		for (Mensagem mensagem : mensagens) {
			if(mensagem.getLida() != null && !mensagem.getLida()) {
				mensagem.setLida(true);
				qtd++;
			}
		}

		mensagemRepository.save(mensagens);
		
		return qtd;
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

		if(usuariosConectados.estaConectado(mensagem.getDestinatario().getLogin())) {
			log.info("Enviando mensagem - destinatario: {}, emissor: {}, conteudo: {}...", mensagem.getDestinatario().getLogin(), emissor, mensagem.getConteudo());

			webSocketService.enviarMensagem(mensagem);
		}
		
		mensagemRepository.save(mensagem);
	}
}
