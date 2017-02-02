package br.com.paulo.hotchat.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class ChatController {

	@RequestMapping({"", "/", "/index"})
	public String index(Model model, @AuthenticationPrincipal User usuarioLogado) {
		model.addAttribute("me" ,usuarioLogado.getUsername());

		return "index";
	}
}
