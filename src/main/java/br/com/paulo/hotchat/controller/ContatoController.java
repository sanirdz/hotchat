package br.com.paulo.hotchat.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.paulo.hotchat.repository.UsuarioRepository;

@Controller
@RequestMapping("/")
public class ContatoController {

	private static final Logger log = LoggerFactory.getLogger(ContatoController.class);
	
	private final UsuarioRepository usuarioRepository;
	
	public ContatoController(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	@RequestMapping({"", "/", "/index"})
	public String index(Model model) {
		log.debug("Listando contatos...");

		model.addAttribute("lista", usuarioRepository.findAllByOnline(true));

		return "index";
	}
}
