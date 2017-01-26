package br.com.paulo.hotchat.websocket;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import br.com.paulo.hotchat.domain.Usuario;

@Component
public class UsuariosConectados {

	private Set<Usuario> activeSessions = new HashSet<Usuario>();
	
	public void usuarioConectado(Usuario usuario) {
		activeSessions.add(usuario);
	}
	
	public void usuarioDesconectado(Usuario usuario) {
		activeSessions.remove(usuario);		
	}

	public Boolean estaConectado(Usuario usuario) {
		return activeSessions.contains(usuario);
	}
}
