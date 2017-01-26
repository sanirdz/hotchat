package br.com.paulo.hotchat.config;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.com.paulo.hotchat.domain.Mensagem;
import br.com.paulo.hotchat.domain.UserRole;
import br.com.paulo.hotchat.domain.Usuario;
import br.com.paulo.hotchat.repository.MensagemRepository;
import br.com.paulo.hotchat.repository.UserRoleRepository;
import br.com.paulo.hotchat.repository.UsuarioRepository;

@Component
public class BootstrapData {

	private static final Logger log = LoggerFactory.getLogger(BootstrapData.class);
	
	private final UsuarioRepository usuarioRepository;
	private final UserRoleRepository userRoleRepository;
	private final MensagemRepository mensagemRepository;
	private final PasswordEncoder passwordEncoder;
	private final Boolean fakeData;
	
	public BootstrapData(UsuarioRepository usuarioRepository, 
					MensagemRepository mensagemRepository,
					PasswordEncoder passwordEncoder,
					UserRoleRepository userRoleRepository,
			@Value("${hotchat.fakedata:false}") Boolean fakeData) {
		
		this.usuarioRepository = usuarioRepository;
		this.userRoleRepository = userRoleRepository;
		this.mensagemRepository = mensagemRepository;
		this.passwordEncoder = passwordEncoder;
		this.fakeData = fakeData;
	}
	
	@EventListener
	public void onApplicationRefresh(ContextRefreshedEvent event) {
		if(fakeData) {
			log.info("Inserindo dados fake de dev...");
			Usuario usuario1 = new Usuario();
			usuario1.setLogin("paulo");
			usuario1.setOnline(false);
			usuario1.setSenha(passwordEncoder.encode("senha"));
			usuario1.setEnabled(true);
			usuarioRepository.save(usuario1);
			userRoleRepository.save(new UserRole().setUsername("paulo").setAuthority("ROLE_USER"));
			
			Usuario usuario2 = new Usuario();
			usuario2.setLogin("paulo2");
			usuario2.setOnline(false);
			usuario2.setEnabled(true);
			usuario2.setSenha(passwordEncoder.encode("senha"));
			usuarioRepository.save(usuario2);
			userRoleRepository.save(new UserRole().setUsername("paulo2").setAuthority("ROLE_USER"));
			
			
			Mensagem mensagem = new Mensagem();
			mensagem.setDataEnvio(LocalDateTime.now());
			mensagem.setEmissor(usuario2);
			mensagem.setDestinatario(usuario1);
			mensagem.setLida(false);
			mensagem.setConteudo("Essa é uma mensagem não lida");
			mensagemRepository.save(mensagem);
		}		
	}
}
