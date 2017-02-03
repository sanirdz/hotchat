package br.com.paulo.hotchat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;

import br.com.paulo.hotchat.domain.Contato;
import br.com.paulo.hotchat.domain.Usuario;
import br.com.paulo.hotchat.repository.ContatoRepository;
import br.com.paulo.hotchat.repository.UsuarioRepository;
import br.com.paulo.hotchat.websocket.UsuariosConectados;

public class ContatoServiceTest {
	
	@Mock
	private UsuarioRepository usuarioRepositoryMock;
	
	@Mock
	private UsuariosConectados usuariosConectadosMock;
	
	@Mock
	private ContatoRepository contatoRepositoryMock;
	
	@Mock
	private MensagemService mensagemServiceMock;

	private ContatoService contatoService;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		contatoService = new ContatoService(usuarioRepositoryMock, usuariosConectadosMock, contatoRepositoryMock, mensagemServiceMock);
		
		when(usuarioRepositoryMock.findByLogin(anyString())).thenAnswer((InvocationOnMock i) -> {
			return new Usuario().setLogin(i.getArgumentAt(0, String.class));
		});
	}
	
	@Test
	public void listarContatosUsuarioExistenteRetornaListaComUmOnlineUmOffline() {
		Usuario usuario = new Usuario().setLogin("paulo");
		List<Contato> contatos = new ArrayList<>();
		contatos.add(new Contato().setContato(new Usuario().setLogin("contato1")));
		contatos.add(new Contato().setContato(new Usuario().setLogin("contato2")));
		
		when(contatoRepositoryMock.findAllByPrincipal(usuario)).thenReturn(contatos);
		when(usuariosConectadosMock.estaConectado("contato1")).thenReturn(true);
		when(usuariosConectadosMock.estaConectado("contato2")).thenReturn(false);
		
		List<Contato> lista = (List<Contato>) contatoService.listarContatos("paulo");
		
		assertThat(lista).hasSize(2);
		
		assertThat(lista.get(0).getContato().getLogin()).isEqualTo("contato1");
		assertThat(lista.get(0).getContato().getOnline()).isTrue();
		
		assertThat(lista.get(1).getContato().getLogin()).isEqualTo("contato2");
		assertThat(lista.get(1).getContato().getOnline()).isFalse();
	}

	@Test
	public void salvarContatoRetornaContatoOnlineSalvo() {
		when(usuariosConectadosMock.estaConectado(anyString())).thenReturn(true);
		when(contatoRepositoryMock.save(any(Contato.class))).thenAnswer((InvocationOnMock i) -> {
			return i.getArgumentAt(0, Contato.class).setId(1L);
		});
		
		Contato contato = contatoService.salvarContato("login", "contato");
		
		assertThat(contato.getContato().getOnline()).isTrue();
		assertThat(contato.getPrincipal().getLogin()).isEqualTo("login");
		assertThat(contato.getContato().getLogin()).isEqualTo("contato");
	}
}
