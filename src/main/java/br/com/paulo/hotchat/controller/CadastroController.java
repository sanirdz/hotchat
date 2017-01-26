package br.com.paulo.hotchat.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cadastro")
public class CadastroController {

	@RequestMapping("/novo")
	public String novo() {
		return "cadastro/novo";
	}
}
