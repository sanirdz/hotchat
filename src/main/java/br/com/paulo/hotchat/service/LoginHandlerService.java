package br.com.paulo.hotchat.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Service;

import br.com.paulo.hotchat.domain.Usuario;
import br.com.paulo.hotchat.repository.UsuarioRepository;

@Service
public class LoginHandlerService extends SimpleUrlLogoutSuccessHandler implements ApplicationListener<AuthenticationSuccessEvent>, LogoutSuccessHandler {

	private static final Logger log = LoggerFactory.getLogger(LoginHandlerService.class);
	
	private final UsuarioRepository usuarioRepository;
	
	public LoginHandlerService(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	@Override
	public void onApplicationEvent(AuthenticationSuccessEvent event) {
		String username = ((UserDetails) event.getAuthentication().getPrincipal()).getUsername();
		log.debug("Atualizando status do usuário {} para online...", username);
		
		Usuario usuario = usuarioRepository.findByLogin(username);
		if(usuario != null) {
			usuario.setOnline(true);
			usuarioRepository.save(usuario);
		} else {
			//TODO usuario nao existe no banco
			throw new RuntimeException("usuario nao existe no banco");
		}
	}
	
	@Override
	public void onLogoutSuccess(HttpServletRequest request, 
			HttpServletResponse response, 
			Authentication authentication) throws IOException, ServletException {
		
		String username = ((UserDetails) authentication.getPrincipal()).getUsername();
		log.debug("Atualizando status do usuário {} para offline...", username);

		Usuario usuario = usuarioRepository.findByLogin(username);
		if(usuario != null) {
			usuario.setOnline(false);
			usuarioRepository.save(usuario);
		} else {
			//TODO usuario nao existe no banco
			throw new RuntimeException("usuario nao existe no banco");
		}
		
		super.onLogoutSuccess(request, response, authentication);
		
	}
}
