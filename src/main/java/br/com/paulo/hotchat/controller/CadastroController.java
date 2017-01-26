package br.com.paulo.hotchat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cadastro")
public class CadastroController {

	@RequestMapping("/novo")
	public String novo() {
		return "cadastro/novo";
	}
}
