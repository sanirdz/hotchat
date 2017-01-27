package br.com.paulo.hotchat.api;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.paulo.hotchat.api.resource.EnviarMensagemDTO;
import br.com.paulo.hotchat.domain.Mensagem;
import br.com.paulo.hotchat.domain.Usuario;
import br.com.paulo.hotchat.service.HotChatService;
import io.swagger.annotations.ApiOperation;
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

	@ApiOperation(value = "Lista mensagens não lidas do usuário logado.", tags = {"MensagensRestController"})
	@ApiResponses({
			@ApiResponse(code = 200, message = "Mensagens listados com sucesso"),
			@ApiResponse(code = 400, message = "Dados da requisição inválidos."),
			@ApiResponse(code = 500, message = "Erro inesperado no servidor.")})
	@RequestMapping(produces = "application/json", method = RequestMethod.GET)
	public ResponseEntity<Iterable<Mensagem>> listarMensagensNaoLidas(@ApiIgnore @AuthenticationPrincipal User usuarioLogado) {
		log.debug("GET para listar mensagens. Usuário logado: {}", usuarioLogado.getUsername());
		//TODO incluir parametros de paginacao
		//TODO fazer um DTO mais clean pro retorno
		
		Iterable<Mensagem> mensagens = hotChatService.listarMensagensNaoLidasDestinatario(usuarioLogado.getUsername());
		
		return ResponseEntity.ok(mensagens);
	}
	
	@ApiOperation(value = "Envia uma mensagem para um destinatário.", tags = {"MensagensRestController"})
	@ApiResponses({
			@ApiResponse(code = 200, message = "Mensagens enviadas com sucesso."),
			@ApiResponse(code = 400, message = "Dados da requisição inválidos."),
			@ApiResponse(code = 500, message = "Erro inesperado no servidor.")})
	@RequestMapping(method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<Void> enviarMensagem(@RequestBody @Valid EnviarMensagemDTO mensagem, @ApiIgnore @AuthenticationPrincipal User usuarioLogado) {
		hotChatService.enviarMensagem(
				new Mensagem().setConteudo(mensagem.getConteudo()).setDestinatario(new Usuario().setLogin(mensagem.getLoginDestinatario())), 
				usuarioLogado.getUsername());
		
		return ResponseEntity.ok().build();
	}
}
