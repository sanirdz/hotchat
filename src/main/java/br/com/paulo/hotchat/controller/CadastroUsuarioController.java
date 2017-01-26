package br.com.paulo.hotchat.controller;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.paulo.hotchat.domain.Usuario;
import br.com.paulo.hotchat.service.HotChatService;

@Controller
@RequestMapping("/cadastro")
public class CadastroUsuarioController {

	private final HotChatService hotChatService;
	
	public CadastroUsuarioController(HotChatService hotChatService) {
		this.hotChatService = hotChatService;
	}

	@RequestMapping("/novo")
	public String novo(Model model) {
		model.addAttribute("usuario", new Usuario());
		return "cadastro/novo";
	}
	
	@RequestMapping(path = "/salvar", method = RequestMethod.POST)
	public String salvar(@Valid Usuario usuario, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		if(bindingResult.hasErrors()) {
			//TODO erros de validacao
			throw new RuntimeException("erros de validacao...");
		} 

		usuario = hotChatService.salvar(usuario);
		
		redirectAttributes.addFlashAttribute("mensagem", "Usuário " + usuario.getLogin() + " cadastrado com sucesso.");
		
		return "redirect:/login";
	}
}
