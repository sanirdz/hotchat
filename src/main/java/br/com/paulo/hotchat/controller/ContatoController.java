package br.com.paulo.hotchat.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.paulo.hotchat.service.HotChatService;

@Controller
@RequestMapping("/")
public class ContatoController {

	private final HotChatService chatService;
	
	public ContatoController(HotChatService usuarioService) {
		this.chatService = usuarioService;
	}

	@RequestMapping({"", "/", "/index"})
	public String index(Model model, @AuthenticationPrincipal User usuarioLogado) {
		String username = usuarioLogado.getUsername();
		
		model.addAttribute("contatos", chatService.listarUsuarios(usuarioLogado.getUsername()));
		model.addAttribute("mensagens", chatService.listarMensagensNaoLidasDestinatario(username));

		return "index";
	}
}
