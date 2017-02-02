package br.com.paulo.hotchat.api;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.paulo.hotchat.api.resource.EnviarMensagemDTO;
import br.com.paulo.hotchat.domain.Mensagem;
import br.com.paulo.hotchat.domain.Usuario;
import br.com.paulo.hotchat.service.MensagemService;

@RunWith(SpringRunner.class)
@WebMvcTest(value = MensagensRestController.class)
@WithMockUser(username = "paulo", password = "senha")
public class MensagensRestControllerTest {

	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private MensagemService mensagemService;
	
	private ObjectMapper mapper;
	
	@Before
	public void setup() {
		mapper = new ObjectMapper();
	}

	@Test
	public void postEnviarMensagemComBodyCorretoRetorna200() throws Exception {
		EnviarMensagemDTO mensagem = new EnviarMensagemDTO().setConteudo("conteudo").setLoginDestinatario("login");
		
		mvc.perform(post("/api/mensagens")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(mensagem)))				
			.andExpect(status().isOk());
	}

	@Test
	public void postEnviarMensagemComBodyInvalidoRetorna400() throws Exception {
		mvc.perform(post("/api/mensagens")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new EnviarMensagemDTO())))				
			.andExpect(status().isBadRequest());
	}
	
	@Test
	public void postEnviarMensagemSemBodyRetorna400() throws Exception {
		mvc.perform(post("/api/mensagens")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest());
	}
	
	@Test
	public void postMarcarLidasRetorna200() throws Exception {
		given(mensagemService.marcarMensagensLidas("emissor", "paulo")).willReturn(5);
		
		mvc.perform(post("/api/mensagens/emissor/marcarLidas")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().string("5"));
	}
	
	@Test
	public void postMarcarLidasRetorna200EmissorInexistente() throws Exception {
		given(mensagemService.marcarMensagensLidas("emissor2", "paulo")).willReturn(5);
		
		mvc.perform(post("/api/mensagens/emissor/marcarLidas")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().string("0"));
	}
	
	@Test
	public void getRetorna200MaisListaComDuasMensagens() throws Exception {
		ArrayList<Mensagem> lista = new ArrayList<>();
		lista.add(new Mensagem().setConteudo("conteudo1").setEmissor(new Usuario().setLogin("login1")));
		lista.add(new Mensagem().setConteudo("conteudo2").setEmissor(new Usuario().setLogin("login2")));
		given(mensagemService.listarMensagensDestinatarioEmissor("destinatario", "paulo")).willReturn(lista);
		
		mvc.perform(get("/api/mensagens/destinatario")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].conteudo").value("conteudo1"))
			.andExpect(jsonPath("$[1].conteudo").value("conteudo2"));
	}
}
