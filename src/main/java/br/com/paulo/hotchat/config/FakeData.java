package br.com.paulo.hotchat.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import br.com.paulo.hotchat.domain.Usuario;
import br.com.paulo.hotchat.repository.UsuarioRepository;

@Component
public class FakeData {

	private static final Logger log = LoggerFactory.getLogger(FakeData.class);
	
	private final UsuarioRepository usuarioRepository;
	private final Boolean fakeData;
	
	public FakeData(UsuarioRepository usuarioRepository, 
			@Value("${hotchat.fakedata:false}") Boolean fakeData) {
		
		this.usuarioRepository = usuarioRepository;
		this.fakeData = fakeData;
	}
	
	@EventListener()
	public void onApplicationRefresh(ContextRefreshedEvent event) {
		if(fakeData) {
			log.info("Inserindo dados fake de dev...");
			Usuario usuario = new Usuario();
			usuario.setId(1L);
			usuario.setLogin("paulo");
			usuario.setOnline(false);
			usuario.setSenha("senha");
			usuarioRepository.save(usuario);
			
			usuario = new Usuario();
			usuario.setId(2L);
			usuario.setLogin("paulo2");
			usuario.setOnline(false);
			usuario.setSenha("senha");
			usuarioRepository.save(usuario);
		}		
	}
}
