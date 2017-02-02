package br.com.paulo.hotchat.service;

import org.springframework.stereotype.Service;

import br.com.paulo.hotchat.domain.Contato;
import br.com.paulo.hotchat.domain.Usuario;
import br.com.paulo.hotchat.repository.ContatoRepository;
import br.com.paulo.hotchat.repository.UsuarioRepository;
import br.com.paulo.hotchat.websocket.UsuariosConectados;

@Service
public class ContatoService {

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
		Usuario principal = usuarioRepository.findByLogin(loginPrincipal);
		Usuario contato = usuarioRepository.findByLogin(loginContato);
		
		Contato contatoParaExcluir = contatoRepository.findByPrincipalAndContato(principal, contato);
		contatoRepository.delete(contatoParaExcluir);
	}

	public void bloquear(String loginPrincipal, String loginContato) {
		Usuario principal = usuarioRepository.findByLogin(loginPrincipal);
		Usuario contato = usuarioRepository.findByLogin(loginContato);
		
		Contato contatoParaBloquear = contatoRepository.findByPrincipalAndContato(principal, contato);		
		contatoRepository.save(contatoParaBloquear.setBloqueado(true));
	}
	

	public void desbloquear(String loginPrincipal, String loginContato) {
		Usuario principal = usuarioRepository.findByLogin(loginPrincipal);
		Usuario contato = usuarioRepository.findByLogin(loginContato);
		
		Contato contatoParaDesbloquear = contatoRepository.findByPrincipalAndContato(principal, contato);		
		contatoRepository.save(contatoParaDesbloquear.setBloqueado(false));
	}
}
