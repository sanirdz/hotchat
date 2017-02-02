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

import br.com.paulo.hotchat.api.resource.SalvarUsuarioDTO;
import br.com.paulo.hotchat.domain.Usuario;
import br.com.paulo.hotchat.service.HotChatService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/api/usuarios")
public class UsuariosRestController {

	private static final Logger log = LoggerFactory.getLogger(UsuariosRestController.class);
	
	private final HotChatService hotChatService;
	
	public UsuariosRestController(HotChatService hotChatService) {
		this.hotChatService = hotChatService;
	}

	@ApiOperation(value = "Salva novos usuários.", tags = {"UsuariosRestController"})
	@ApiResponses({
			@ApiResponse(code = 200, message = "Usuário salvo com sucesso."),
			@ApiResponse(code = 400, message = "Dados do usuário inválidos."),
			@ApiResponse(code = 500, message = "Erro inesperado no servidor.")})
	@RequestMapping(method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<Usuario> salvar(@ApiParam(value = "Dados do usuário para salvar.") @RequestBody @Valid SalvarUsuarioDTO usuario) {
		log.debug("POST para salvar usuario");
		//TODO mudar pra usar usar isso
		Usuario usuarioSalvo = hotChatService.salvar(new Usuario().setLogin(usuario.getLogin()).setSenha(usuario.getSenha()));
		
		return ResponseEntity.ok(usuarioSalvo);
	}
	
	@ApiOperation(value = "Lista usuários, excluindo o usuário logado.", tags = {"UsuariosRestController"})
	@ApiResponses({
			@ApiResponse(code = 200, message = "Usuários listados com sucesso."),
			@ApiResponse(code = 400, message = "Dados da requisição inválidos."),
			@ApiResponse(code = 500, message = "Erro inesperado no servidor.")})
	@RequestMapping(produces = "application/json", method = RequestMethod.GET)
	public ResponseEntity<Iterable<Usuario>> listar(@ApiIgnore @AuthenticationPrincipal User usuarioLogado) {
		log.debug("GET para listar usuarios. Usuario logado: {}", usuarioLogado.getUsername());
		
		Iterable<Usuario> usuarios = hotChatService.listarUsuarios(usuarioLogado.getUsername());
		
		return ResponseEntity.ok(usuarios);
	}
}
