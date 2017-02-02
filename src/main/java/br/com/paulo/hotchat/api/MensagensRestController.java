package br.com.paulo.hotchat.api;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.paulo.hotchat.api.resource.EnviarMensagemDTO;
import br.com.paulo.hotchat.api.resource.MensagemDTO;
import br.com.paulo.hotchat.domain.Mensagem;
import br.com.paulo.hotchat.domain.Usuario;
import br.com.paulo.hotchat.service.HotChatService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/api/mensagens")
public class MensagensRestController {

	private static final Logger log = LoggerFactory.getLogger(MensagensRestController.class);
	
	private final HotChatService hotChatService;

	public MensagensRestController(HotChatService hotChatService) {
		this.hotChatService = hotChatService;
	}

	@ApiOperation(value = "Lista todas as mensagens trocadas entre o usuário logado e um destinatário.", tags = {"MensagensRestController"})
	@ApiResponses({
			@ApiResponse(code = 200, message = "Mensagens listadas com sucesso"),
			@ApiResponse(code = 400, message = "Dados da requisição inválidos."),
			@ApiResponse(code = 500, message = "Erro inesperado no servidor.")})
	@RequestMapping(path="/{destinatario}", produces = "application/json", method = RequestMethod.GET)
	public ResponseEntity<Iterable<MensagemDTO>> listarMensagens(
			@ApiIgnore @AuthenticationPrincipal User usuarioLogado, 
			@ApiParam("Login do destinatário") @PathVariable("destinatario") String destinatario) {
		
		log.debug("GET para listar mensagens. Usuário logado: {}", usuarioLogado.getUsername());
		//TODO incluir parametros de paginacao
		
		Iterable<Mensagem> mensagens = hotChatService.listarMensagensDestinatarioEmissor(destinatario, usuarioLogado.getUsername());
		List<MensagemDTO> mensagensDTO = new ArrayList<>();
		
		mensagens.forEach(m -> {
			mensagensDTO.add(new MensagemDTO()
					.setConteudo(m.getConteudo())
					.setDataEnvio(m.getDataEnvio())
					.setEmissor(m.getEmissor().getLogin()));
		});
		
		return ResponseEntity.ok(mensagensDTO);
	}
	
	@ApiOperation(value = "Marca as mensagens enviadas por um emissor ao usuario logado como lidas", tags = {"MensagensRestController"})
	@ApiResponses({
			@ApiResponse(code = 200, message = "Operação executada com sucesso."),
			@ApiResponse(code = 400, message = "Dados da requisição inválidos."),
			@ApiResponse(code = 500, message = "Erro inesperado no servidor.")})
	@RequestMapping(path = "/{emissor}/marcarLidas", method = RequestMethod.POST, consumes = "application/json")
	public ResponseEntity<Void> marcarMensagensLidas(@ApiParam("Login do emissor") @PathVariable("emissor") String emissor, 
			@ApiIgnore @AuthenticationPrincipal User usuarioLogado) {
		
		log.debug("POST para marcar mensagens lidas. Usuário logado: {}", usuarioLogado.getUsername());
		hotChatService.marcarMensagensLidas(emissor, usuarioLogado.getUsername());
		
		return ResponseEntity.ok().build();
	}
	
	@ApiOperation(value = "Envia uma mensagem para um destinatário.", tags = {"MensagensRestController"})
	@ApiResponses({
			@ApiResponse(code = 200, message = "Mensagens enviadas com sucesso."),
			@ApiResponse(code = 400, message = "Dados da requisição inválidos."),
			@ApiResponse(code = 500, message = "Erro inesperado no servidor.")})
	@RequestMapping(method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<Void> enviarMensagem(@RequestBody @Valid EnviarMensagemDTO mensagem, 
			@ApiIgnore @AuthenticationPrincipal User usuarioLogado) {
		log.debug("POST para enviar mensagem. Usuário logado: {}", usuarioLogado.getUsername());
		hotChatService.enviarMensagem(
				new Mensagem().setConteudo(mensagem.getConteudo()).setDestinatario(new Usuario().setLogin(mensagem.getLoginDestinatario())), 
				usuarioLogado.getUsername());
		
		return ResponseEntity.ok().build();
	}
}
