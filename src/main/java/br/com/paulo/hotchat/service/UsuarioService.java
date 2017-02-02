package br.com.paulo.hotchat.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.paulo.hotchat.domain.Contato;
import br.com.paulo.hotchat.domain.UserRole;
import br.com.paulo.hotchat.domain.Usuario;
import br.com.paulo.hotchat.repository.ContatoRepository;
import br.com.paulo.hotchat.repository.UserRoleRepository;
import br.com.paulo.hotchat.repository.UsuarioRepository;
import br.com.paulo.hotchat.websocket.UsuariosConectados;

@Service
public class UsuarioService {

	private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);
	
	private final UsuarioRepository usuarioRepository;
	private final UserRoleRepository userRoleRepository;
	private final UsuariosConectados usuariosConectados;
	private final PasswordEncoder passwordEncoder;
	private final MensagemService mensagemService;
	private final ContatoRepository contatoRepository;
	
	public UsuarioService(UsuarioRepository usuarioRepository, UserRoleRepository userRoleRepository,
			UsuariosConectados usuariosConectados, PasswordEncoder passwordEncoder, MensagemService mensagemService,
			ContatoRepository contatoRepository) {
		this.usuarioRepository = usuarioRepository;
		this.userRoleRepository = userRoleRepository;
		this.usuariosConectados = usuariosConectados;
		this.passwordEncoder = passwordEncoder;
		this.mensagemService = mensagemService;
		this.contatoRepository = contatoRepository;
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
		//TODO Da pra melhorar :(
		Iterable<Usuario> usuarios = usuarioRepository.findAllBut(username);
		Iterable<Contato> contatosUsuario = contatoRepository.findAllByPrincipal(usuarioRepository.findByLogin(username));
		
		List<Usuario> contatosNovos = new ArrayList<>();

		for (Usuario usuario : usuarios) {
			boolean achei = false;
			
			log.debug("usuario -> {}", usuario.getLogin());
			//se o usuario da lista de usuario nao ta na lista de contatos 
			for (Contato contatoUsuario : contatosUsuario) {
				log.debug("contato -> {}", contatoUsuario.getContato().getLogin());
				if(usuario.equals(contatoUsuario.getContato())) {
					achei = true;
					log.debug("achei!");
					break;
				}
			}
			if(!achei) {
				contatosNovos.add(usuario);
			}
		}
		
		return contatosNovos;
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