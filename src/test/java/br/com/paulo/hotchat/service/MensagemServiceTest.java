package br.com.paulo.hotchat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;

import br.com.paulo.hotchat.domain.Contato;
import br.com.paulo.hotchat.domain.Mensagem;
import br.com.paulo.hotchat.domain.Usuario;
import br.com.paulo.hotchat.repository.MensagemRepository;
import br.com.paulo.hotchat.repository.UsuarioRepository;
import br.com.paulo.hotchat.websocket.UsuariosConectados;
import br.com.paulo.hotchat.websocket.WebSocketService;

public class MensagemServiceTest {

	@Mock
	private UsuarioRepository usuarioRepositoryMock;
	@Mock
	private MensagemRepository mensagemRepositoryMock;
	@Mock
	private WebSocketService webSocketServiceMock;
	@Mock
	private UsuariosConectados usuariosConectadosMock;
	@Mock
	private ContatoService contatoServiceMock;
	
	private MensagemService mensagemService;
	
	private String loginEmissor = "emissor";
	private String loginDestinatario = "destinatario";
	
	@SuppressWarnings("unchecked")
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mensagemService = new MensagemService(usuarioRepositoryMock, mensagemRepositoryMock, webSocketServiceMock, usuariosConectadosMock);
		mensagemService.setContatoService(contatoServiceMock);
	
		
		when(contatoServiceMock.recuperaContato(loginEmissor, loginDestinatario)).then((InvocationOnMock i) -> {
			return new Contato()
					.setContato(new Usuario().setLogin(i.getArgumentAt(1, String.class)))
					.setPrincipal(new Usuario().setLogin(i.getArgumentAt(0, String.class)));
		});
		
		when(usuarioRepositoryMock.findByLogin(anyString())).then((InvocationOnMock i) -> {
			return new Usuario().setLogin(i.getArgumentAt(0, String.class));
		});
		
		List<Mensagem> mensagensEnviadas = new ArrayList<>();
		mensagensEnviadas.add(new Mensagem().setConteudo("mensagem 3").setDataEnvio(LocalDateTime.of(2010, 1, 1, 1, 3)));
		mensagensEnviadas.add(new Mensagem().setConteudo("mensagem 1").setDataEnvio(LocalDateTime.of(2010, 1, 1, 1, 1)));
		
		List<Mensagem> mensagensRecebidas = new ArrayList<>();
		mensagensRecebidas.add(new Mensagem().setConteudo("mensagem 4").setDataEnvio(LocalDateTime.of(2010, 1, 1, 1, 4)));
		mensagensRecebidas.add(new Mensagem().setConteudo("mensagem 2").setDataEnvio(LocalDateTime.of(2010, 1, 1, 1, 2)));
		when(mensagemRepositoryMock.findAllByDestinatarioAndEmissorOrderByDataEnvio(any(Usuario.class), any(Usuario.class))).thenReturn(mensagensEnviadas, mensagensRecebidas);
	}
	
	@Test
	public void listarMensagensDestinatarioEmissor() {
		TreeSet<Mensagem> lista = (TreeSet<Mensagem>) mensagemService.listarMensagensDestinatarioEmissor(loginDestinatario, loginEmissor);
		
		assertThat(lista).hasSize(4);
		assertThat(lista.pollFirst().getConteudo()).isEqualTo("mensagem 1");
		assertThat(lista.pollFirst().getConteudo()).isEqualTo("mensagem 2");
		assertThat(lista.pollFirst().getConteudo()).isEqualTo("mensagem 3");
		assertThat(lista.pollFirst().getConteudo()).isEqualTo("mensagem 4");
	}
	
	@Test
	public void listarMensagensDestinatarioEmissorContatoBloqueadoRetornaApenasMensagensEnviadas() {
		when(contatoServiceMock.recuperaContato(loginEmissor, loginDestinatario)).then((InvocationOnMock i) -> {
			return new Contato()
					.setContato(new Usuario().setLogin(i.getArgumentAt(1, String.class)))
					.setPrincipal(new Usuario().setLogin(i.getArgumentAt(0, String.class)))
					.setBloqueado(true);
		});
		
		TreeSet<Mensagem> lista = (TreeSet<Mensagem>) mensagemService.listarMensagensDestinatarioEmissor(loginDestinatario, loginEmissor);
		
		assertThat(lista).hasSize(2);
		assertThat(lista.pollFirst().getConteudo()).isEqualTo("mensagem 1");
		assertThat(lista.pollFirst().getConteudo()).isEqualTo("mensagem 3");
	}

	public void marcarMensagensLidas() {
	
	}
	
	public void recuperaQuantidadeMensagensNaoLidas() {
	
	}

	public void enviarMensagem() {
	
	}

	public void marcarMensagemLida() {
	
	}
}
