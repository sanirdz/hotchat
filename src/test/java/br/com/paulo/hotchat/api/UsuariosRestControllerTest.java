package br.com.paulo.hotchat.api;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.paulo.hotchat.api.resource.SalvarUsuarioDTO;
import br.com.paulo.hotchat.domain.Usuario;
import br.com.paulo.hotchat.service.HotChatService;

@RunWith(SpringRunner.class)
@WebMvcTest(value = UsuariosRestController.class)
@WithMockUser(username = "paulo", password = "senha")
public class UsuariosRestControllerTest {

	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private HotChatService hotChatService;
	
	private ObjectMapper mapper;
	
	@Before
	public void setup() {
		mapper = new ObjectMapper();
	}

	@Test
	public void postComBodyCorretoRetorna200() throws Exception {
		SalvarUsuarioDTO usuario = new SalvarUsuarioDTO().setLogin("login").setSenha("senha");
		given(hotChatService.salvar(any(Usuario.class))).willAnswer(new Answer<Usuario>() {
			@Override
			public Usuario answer(InvocationOnMock invocation) throws Throwable {
				return invocation.getArgumentAt(0, Usuario.class).setId(1L).setVersion(0L).setEnabled(true);
			}
		});
		
		mvc.perform(post("/api/usuarios")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(usuario)))				
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.login").value("login"))
			.andExpect(jsonPath("$.senha").value("senha"))
			.andExpect(jsonPath("$.id").value("1"))
			.andExpect(jsonPath("$.version").value("0"))
			.andExpect(jsonPath("$.enabled").value("true"));
	}

	@Test
	public void postComBodyInvalidoRetorna400() throws Exception {
		mvc.perform(post("/api/usuarios")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new Usuario())))				
			.andExpect(status().isBadRequest());
	}
	
	@Test
	public void postSemBodyRetorna400() throws Exception {
		mvc.perform(post("/api/usuarios")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest());
	}
	
	@Test
	public void getRetorna200MaisListaComDoisUsuarios() throws Exception {
		ArrayList<Usuario> lista = new ArrayList<>();
		lista.add(new Usuario().setLogin("usuario1"));
		lista.add(new Usuario().setLogin("usuario2"));
		given(hotChatService.listarUsuarios("paulo")).willReturn(lista);
		
		mvc.perform(get("/api/usuarios/")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].login").value("usuario1"))
			.andExpect(jsonPath("$[1].login").value("usuario2"));
	}
}
