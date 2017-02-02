package br.com.paulo.hotchat.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.com.paulo.hotchat.domain.Contato;
import br.com.paulo.hotchat.domain.Usuario;
import br.com.paulo.hotchat.repository.ContatoRepository;
import br.com.paulo.hotchat.repository.UsuarioRepository;
import br.com.paulo.hotchat.websocket.UsuariosConectados;

@Service
public class ContatoService {

	private static final Logger log = LoggerFactory.getLogger(ContatoService.class);
	
	private final UsuarioRepository usuarioRepository;
	private final UsuariosConectados usuariosConectados;
	private final ContatoRepository contatoRepository;
	private final MensagemService mensagemService;

	public ContatoService(UsuarioRepository usuarioRepository, UsuariosConectados usuariosConectados,
			ContatoRepository contatoRepository, MensagemService mensagemService) {
		this.usuarioRepository = usuarioRepository;
		this.usuariosConectados = usuariosConectados;
		this.contatoRepository = contatoRepository;
		this.mensagemService = mensagemService;
	}

	public Iterable<Contato> listarContatos(String username) {
		Iterable<Contato> contatos = contatoRepository.findAllByPrincipal(usuarioRepository.findByLogin(username));

		contatos.forEach(contato -> {
			contato.getContato().setOnline(usuariosConectados.estaConectado(contato.getContato().getLogin()));
			contato.getContato().setTotalMensagensNaoLidas(mensagemService.recuperaQuantidadeMensagensNaoLidas(contato.getContato().getLogin(), username));
		});

		return contatos;
	}
	
	public Contato salvarContato(String loginUsuario, String loginContato) {
		Contato contato = new Contato()
				.setPrincipal(usuarioRepository.findByLogin(loginUsuario))
				.setContato(usuarioRepository.findByLogin(loginContato));
		
		contato.getContato().setOnline(usuariosConectados.estaConectado(contato.getContato().getLogin()));
		
		return contatoRepository.save(contato);
	}

	public void excluir(String loginPrincipal, String loginContato) {
		Contato contatoParaExcluir = recuperaContato(loginPrincipal, loginContato);
		contatoRepository.delete(contatoParaExcluir);
	}

	public void bloquear(String loginPrincipal, String loginContato) {
		Contato contatoParaBloquear = recuperaContato(loginPrincipal, loginContato);		
		contatoRepository.save(contatoParaBloquear.setBloqueado(true));
	}
	

	public void desbloquear(String loginPrincipal, String loginContato) {
		Contato contatoParaDesbloquear = recuperaContato(loginPrincipal, loginContato);		
		contatoRepository.save(contatoParaDesbloquear.setBloqueado(false));
	}

	public Contato recuperaContato(String loginPrincipal, String loginContato) {
		Usuario principal = usuarioRepository.findByLogin(loginPrincipal);
		Usuario contato = usuarioRepository.findByLogin(loginContato);
		
		Contato contatoRecuperado = contatoRepository.findByPrincipalAndContato(principal, contato);
		log.debug("Contato recuperado {}", contatoRecuperado != null ? contatoRecuperado.getId() : null);
		return contatoRecuperado;
	}
}
