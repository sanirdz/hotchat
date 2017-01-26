package br.com.paulo.hotchat.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import br.com.paulo.hotchat.domain.Usuario;
import br.com.paulo.hotchat.repository.UsuarioRepository;

@Service
public class UsuarioService implements ApplicationListener<AuthenticationSuccessEvent> {

	private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);
	
	private final UsuarioRepository usuarioRepository;
	
	public UsuarioService(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	@Override
	public void onApplicationEvent(AuthenticationSuccessEvent event) {
		String username = ((UserDetails) event.getAuthentication().getPrincipal()).getUsername();
		log.debug("Atualizando status do usuario {} para online...", username);
		
		Usuario usuario = usuarioRepository.findByLogin(username);
		if(usuario != null) {
			usuario.setOnline(true);
			usuarioRepository.save(usuario);
		} else {
			//TODO usuario nao existe no banco
		}
	}
}