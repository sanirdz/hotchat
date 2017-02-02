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
			Usuario paulo = new Usuario();
			paulo.setLogin("paulo");
			paulo.setSenha(passwordEncoder.encode("senha"));
			paulo.setEnabled(true);
			usuarioRepository.save(paulo);
			userRoleRepository.save(new UserRole().setUsername("paulo").setAuthority("ROLE_USER"));
			
			Usuario paulo2 = new Usuario();
			paulo2.setLogin("paulo2");
			paulo2.setEnabled(true);
			paulo2.setSenha(passwordEncoder.encode("senha"));
			usuarioRepository.save(paulo2);
			userRoleRepository.save(new UserRole().setUsername("paulo2").setAuthority("ROLE_USER"));
			
			Mensagem mensagem = new Mensagem();
			mensagem.setDataEnvio(LocalDateTime.now());
			mensagem.setEmissor(paulo2);
			mensagem.setDestinatario(paulo);
			mensagem.setLida(false);
			mensagem.setConteudo("Essa é uma mensagem do paulo2 pro paulo");
			mensagemRepository.save(mensagem);
			
			mensagem = new Mensagem();
			mensagem.setDataEnvio(LocalDateTime.now());
			mensagem.setEmissor(paulo);
			mensagem.setDestinatario(paulo2);
			mensagem.setLida(false);
			mensagem.setConteudo("Essa é uma mensagem do paulo pro paulo2");
			mensagemRepository.save(mensagem);
			
			for(int i = 0; i < 100; i++) {
				Usuario usuario = new Usuario();
				usuario.setLogin("usuario" + i);
				usuario.setEnabled(true);
				usuario.setSenha(passwordEncoder.encode("senha"));
				usuarioRepository.save(usuario);
				userRoleRepository.save(new UserRole().setUsername("usuario" + i).setAuthority("ROLE_USER"));
			}
		}		
	}
}
