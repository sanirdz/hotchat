package br.com.paulo.hotchat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.paulo.hotchat.domain.Contato;
import br.com.paulo.hotchat.domain.Usuario;
import br.com.paulo.hotchat.repository.ContatoRepository;
import br.com.paulo.hotchat.repository.UserRoleRepository;
import br.com.paulo.hotchat.repository.UsuarioRepository;

public class UsuarioServiceTest {

	@Mock
	private UsuarioRepository usuarioRepositoryMock;
	@Mock
	private UserRoleRepository userRoleRepositoryMock;
	@Mock
	private PasswordEncoder passwordEncoderMock;
	@Mock
	private ContatoRepository contatoRepositoryMock;
	
	private UsuarioService usuarioService;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		usuarioService = new UsuarioService(usuarioRepositoryMock, userRoleRepositoryMock, passwordEncoderMock, contatoRepositoryMock);
	}
	
	@Test
	public void listarContatosNovosRetornaTres() {
		Usuario usuario = new Usuario().setLogin("login");
		ArrayList<Usuario> usuarios = new ArrayList<>();
		usuarios.add(new Usuario().setLogin("login1").setId(1L));
		usuarios.add(new Usuario().setLogin("login2").setId(2L));
		usuarios.add(new Usuario().setLogin("login3").setId(3L));
		usuarios.add(new Usuario().setLogin("login4").setId(4L));
		
		ArrayList<Contato> contatos = new ArrayList<>();
		contatos.add(new Contato().setContato(new Usuario().setLogin("login2").setId(2L)));
		
		when(usuarioRepositoryMock.findByLogin("login")).thenReturn(usuario);
		when(usuarioRepositoryMock.findAllBut("login")).thenReturn(usuarios);
		when(contatoRepositoryMock.findAllByPrincipal(usuario)).thenReturn(contatos);
		
		Iterable<Usuario> lista = usuarioService.listarContatosNovos("login");
		
		assertThat(lista).hasSize(3);
	}
}
