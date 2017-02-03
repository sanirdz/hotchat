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

import br.com.paulo.hotchat.api.resource.ContatoDTO;
import br.com.paulo.hotchat.api.resource.SalvarUsuarioDTO;
import br.com.paulo.hotchat.domain.Contato;
import br.com.paulo.hotchat.domain.Usuario;
import br.com.paulo.hotchat.service.ContatoService;
import br.com.paulo.hotchat.service.UsuarioService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/api/usuarios")
public class UsuariosRestController {

	private static final Logger log = LoggerFactory.getLogger(UsuariosRestController.class);
	
	private final UsuarioService usuarioService;
	private final ContatoService contatoService;
	
	public UsuariosRestController(UsuarioService usuarioService, ContatoService contatoService) {
		this.usuarioService = usuarioService;
		this.contatoService = contatoService;
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
		Usuario usuarioSalvo = usuarioService.salvar(new Usuario().setLogin(usuario.getLogin()).setSenha(usuario.getSenha()));
		
		return ResponseEntity.ok(usuarioSalvo);
	}

	@ApiOperation(value = "Exclui um contato.", tags = {"UsuariosRestController"})
	@ApiResponses({
			@ApiResponse(code = 200, message = "Usuário excluído com sucesso."),
			@ApiResponse(code = 400, message = "Dados da requisição inválidos."),
			@ApiResponse(code = 500, message = "Erro inesperado no servidor.")})
	@RequestMapping(path="/{contato}", method = RequestMethod.DELETE, produces = "application/json")
	public ResponseEntity<Void> excluir(@ApiParam("Login do contato") @PathVariable("contato") String contato,
			@ApiIgnore @AuthenticationPrincipal User usuarioLogado) {
		log.debug("POST para excluir contato");
		
		contatoService.excluir(usuarioLogado.getUsername(), contato);
		return ResponseEntity.ok().build();
	}
	
	@ApiOperation(value = "Bloqueia um contato.", tags = {"UsuariosRestController"})
	@ApiResponses({
			@ApiResponse(code = 200, message = "Contato bloqueado com sucesso."),
			@ApiResponse(code = 400, message = "Dados da requisição inválidos."),
			@ApiResponse(code = 500, message = "Erro inesperado no servidor.")})
	@RequestMapping(path="/{contato}/bloquear", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Void> bloquear(@ApiParam("Login do contato") @PathVariable("contato") String contato,
			@ApiIgnore @AuthenticationPrincipal User usuarioLogado) {
		log.debug("POST para bloquear contato");
		
		contatoService.bloquear(usuarioLogado.getUsername(), contato);
		
		return ResponseEntity.ok().build();
	}
	
	@ApiOperation(value = "Desbloqueia um contato.", tags = {"UsuariosRestController"})
	@ApiResponses({
			@ApiResponse(code = 200, message = "Contato desbloqueado com sucesso."),
			@ApiResponse(code = 400, message = "Dados da requisição inválidos."),
			@ApiResponse(code = 500, message = "Erro inesperado no servidor.")})
	@RequestMapping(path="/{contato}/desbloquear", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Void> desbloquear(@ApiParam("Login do contato") @PathVariable("contato") String contato,
			@ApiIgnore @AuthenticationPrincipal User usuarioLogado) {
		log.debug("POST para desbloquear contato");

		contatoService.desbloquear(usuarioLogado.getUsername(), contato);
		
		return ResponseEntity.ok().build();
	}
	
	@ApiOperation(value = "Lista contatos do usuário logado.", tags = {"UsuariosRestController"})
	@ApiResponses({
			@ApiResponse(code = 200, message = "Contatos listados com sucesso."),
			@ApiResponse(code = 400, message = "Dados da requisição inválidos."),
			@ApiResponse(code = 500, message = "Erro inesperado no servidor.")})
	@RequestMapping(produces = "application/json", method = RequestMethod.GET)
	public ResponseEntity<Iterable<ContatoDTO>> listar(@ApiIgnore @AuthenticationPrincipal User usuarioLogado) {
		log.debug("GET para listar contatos do usuário logado.. Usuario logado: {}", usuarioLogado.getUsername());
		
		Iterable<Contato> contatos = contatoService.listarContatos(usuarioLogado.getUsername());
		List<ContatoDTO> contatosDTO = new ArrayList<>();
		contatos.forEach(contato -> {
			contatosDTO.add(new ContatoDTO()
					.setLogin(contato.getContato().getLogin())
					.setOnline(contato.getContato().getOnline())
					.setBloqueado(contato.getBloqueado())
					.setTotalMensagensNaoLidas(contato.getContato().getTotalMensagensNaoLidas()));
		});
		
		return ResponseEntity.ok(contatosDTO);
	}
	
	
	@ApiOperation(value = "Lista usuários que não são contatos do usuário logado.", tags = {"UsuariosRestController"})
	@ApiResponses({
			@ApiResponse(code = 200, message = "Usuários listados com sucesso."),
			@ApiResponse(code = 400, message = "Dados da requisição inválidos."),
			@ApiResponse(code = 500, message = "Erro inesperado no servidor.")})
	@RequestMapping(path = "/novos", produces = "application/json", method = RequestMethod.GET)
	public ResponseEntity<Iterable<ContatoDTO>> buscarContatosNovos(@ApiIgnore @AuthenticationPrincipal User usuarioLogado) {
		log.debug("GET para listar usuários que não são contatos. Usuario logado: {}", usuarioLogado.getUsername());
		Iterable<Usuario> usuarios = usuarioService.listarContatosNovos(usuarioLogado.getUsername());
		
		List<ContatoDTO> contatosDTO = new ArrayList<>();
		usuarios.forEach(usuario -> {
			contatosDTO.add(new ContatoDTO()
					.setLogin(usuario.getLogin())
					.setOnline(usuario.getOnline())
					.setTotalMensagensNaoLidas(usuario.getTotalMensagensNaoLidas()));
		});
		
		return ResponseEntity.ok(contatosDTO);
	}
	
	@ApiOperation(value = "Salva um novo contato para o usuário logado.", tags = {"UsuariosRestController"})
	@ApiResponses({
			@ApiResponse(code = 200, message = "Contato salvo com sucesso."),
			@ApiResponse(code = 400, message = "Dados da requisição inválidos."),
			@ApiResponse(code = 500, message = "Erro inesperado no servidor.")})
	@RequestMapping(path = "/{contato}", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<ContatoDTO> salvarContato(@ApiParam("Login do usuário") @PathVariable("contato") String contato,
			@ApiIgnore @AuthenticationPrincipal User usuarioLogado) {
		
		log.debug("POST para salvar contato");
		Contato contatoSalvo = contatoService.salvarContato(usuarioLogado.getUsername(), contato);
		ContatoDTO dto = new ContatoDTO()
				.setLogin(contatoSalvo.getContato().getLogin())
				.setOnline(contatoSalvo.getContato().getOnline());
		
		return ResponseEntity.ok(dto);
	}
}
