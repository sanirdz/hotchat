package br.com.paulo.hotchat.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.paulo.hotchat.domain.UserRole;
import br.com.paulo.hotchat.domain.Usuario;
import br.com.paulo.hotchat.repository.UserRoleRepository;
import br.com.paulo.hotchat.repository.UsuarioRepository;
import br.com.paulo.hotchat.websocket.UsuariosConectados;

@Service
public class UsuarioService {

	private final UsuarioRepository usuarioRepository;
	private final UserRoleRepository userRoleRepository;
	private final UsuariosConectados usuariosConectados;
	private final PasswordEncoder passwordEncoder;
	private final MensagemService mensagemService;
	
	public UsuarioService(UsuarioRepository usuarioRepository, UserRoleRepository userRoleRepository,
			UsuariosConectados usuariosConectados, PasswordEncoder passwordEncoder, MensagemService mensagemService) {
		this.usuarioRepository = usuarioRepository;
		this.userRoleRepository = userRoleRepository;
		this.usuariosConectados = usuariosConectados;
		this.passwordEncoder = passwordEncoder;
		this.mensagemService = mensagemService;
	}

	public Iterable<Usuario> listarContatos(String username) {
		Iterable<Usuario> contatos = usuarioRepository.findAllByContatoPrincipal(usuarioRepository.findByLogin(username));

		contatos.forEach(contato -> {
			contato.setOnline(usuariosConectados.estaConectado(contato.getLogin()));
			contato.setTotalMensagensNaoLidas(mensagemService.recuperaQuantidadeMensagensNaoLidas(contato.getLogin(), username));
		});

		return contatos;
	}
	
	public Iterable<Usuario> listarContatosNovos(String username) {
//		TODO listar quem nao é contato
		return usuarioRepository.findAllBut(username);
	}

	public Usuario salvar(Usuario usuario) {
		//TODO conferir o login unique
		usuario
		.setSenha(passwordEncoder.encode(usuario.getSenha()))
		.setEnabled(true);

		userRoleRepository.save(new UserRole().setUsername(usuario.getLogin()).setAuthority("ROLE_USER"));

		return usuarioRepository.save(usuario);
	}
}