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

	public ContatoService(UsuarioRepository usuarioRepository, UsuariosConectados usuariosConectados,
			ContatoRepository contatoRepository) {
		this.usuarioRepository = usuarioRepository;
		this.usuariosConectados = usuariosConectados;
		this.contatoRepository = contatoRepository;
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
}
