package br.com.paulo.hotchat.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import br.com.paulo.hotchat.domain.Mensagem;
import br.com.paulo.hotchat.service.HotChatService;

@Controller
@RequestMapping("/")
public class ChatController {

	private final HotChatService chatService;
	
	public ChatController(HotChatService usuarioService) {
		this.chatService = usuarioService;
	}

	@RequestMapping({"", "/", "/index"})
	public String index(Model model, @AuthenticationPrincipal User usuarioLogado) {
		model.addAttribute("me" ,usuarioLogado.getUsername());

		return "index";
	}
	
	@RequestMapping(value = "enviarMensagem", method = RequestMethod.POST)
	public void enviarMensagem(Mensagem mensagem, @AuthenticationPrincipal User usuarioLogado) {
		chatService.enviarMensagem(mensagem, usuarioLogado.getUsername());
	}
}
