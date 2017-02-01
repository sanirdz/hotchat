package br.com.paulo.hotchat.api;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import br.com.paulo.hotchat.service.HotChatService;

@RunWith(SpringRunner.class)
@WebMvcTest(value = MensagensRestController.class)
@WithMockUser(username = "paulo", password = "senha")
public class MensagensRestControllerTest {

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
		EnviarMensagemDTO mensagem = new EnviarMensagemDTO().setConteudo("conteudo").setLoginDestinatario("login");
		
		mvc.perform(post("/api/mensagens")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(mensagem)))				
			.andExpect(status().isOk());
	}

	@Test
	public void postComBodyInvalidoRetorna400() throws Exception {
		mvc.perform(post("/api/mensagens")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new EnviarMensagemDTO())))				
			.andExpect(status().isBadRequest());
	}
	
	@Test
	public void postSemBodyRetorna400() throws Exception {
		mvc.perform(post("/api/mensagens")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest());
	}
	
	@Test
	public void getRetorna200MaisListaComDuasMensagens() throws Exception {
		ArrayList<Mensagem> lista = new ArrayList<>();
		lista.add(new Mensagem().setConteudo("conteudo1"));
		lista.add(new Mensagem().setConteudo("conteudo2"));
		given(hotChatService.listarMensagensDestinatarioEmissor("destinatario", "paulo")).willReturn(lista);
		
		mvc.perform(get("/api/mensagens/")
				.param("destinatario", "destinatario")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].conteudo").value("conteudo1"))
			.andExpect(jsonPath("$[1].conteudo").value("conteudo2"));
		
	}
}
