package br.com.paulo.hotchat.config;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.com.paulo.hotchat.domain.Contato;
import br.com.paulo.hotchat.domain.Mensagem;
import br.com.paulo.hotchat.domain.UserRole;
import br.com.paulo.hotchat.domain.Usuario;
import br.com.paulo.hotchat.repository.ContatoRepository;
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
	private final ContatoRepository contatoRepository;

	private final Boolean fakeData;

	
	public BootstrapData(UsuarioRepository usuarioRepository, 
					MensagemRepository mensagemRepository,
					PasswordEncoder passwordEncoder,
					UserRoleRepository userRoleRepository,
					ContatoRepository contatoRepository,
			@Value("${hotchat.fakedata:false}") Boolean fakeData) {
		
		this.usuarioRepository = usuarioRepository;
		this.userRoleRepository = userRoleRepository;
		this.mensagemRepository = mensagemRepository;
		this.passwordEncoder = passwordEncoder;
		this.fakeData = fakeData;
		this.contatoRepository = contatoRepository;
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
			
			contatoRepository.save(new Contato().setContato(paulo2).setPrincipal(paulo));
			contatoRepository.save(new Contato().setContato(paulo).setPrincipal(paulo2));
			
			Usuario paulo3 = new Usuario();
			paulo3.setLogin("paulo3");
			paulo3.setEnabled(true);
			paulo3.setSenha(passwordEncoder.encode("senha"));
			usuarioRepository.save(paulo3);
			userRoleRepository.save(new UserRole().setUsername("paulo3").setAuthority("ROLE_USER"));
			contatoRepository.save(new Contato().setContato(paulo3).setPrincipal(paulo).setBloqueado(true));
			
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
			
			List<Usuario> usuarios = new ArrayList<>();
			List<UserRole> roles = new ArrayList<>();
			for(int i = 0; i < 100; i++) {
				usuarios.add(new Usuario()
						.setLogin("usuario" + i)
						.setEnabled(true)
						.setSenha(passwordEncoder.encode("senha")));
				roles.add(new UserRole().setUsername("usuario" + i).setAuthority("ROLE_USER"));
			}
			usuarioRepository.save(usuarios);
			userRoleRepository.save(roles);
		}		
	}
}
