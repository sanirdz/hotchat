package br.com.paulo.hotchat.websocket;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class UsuariosConectados {

	private Set<String> activeSessions = new HashSet<String>();
	
	public void usuarioConectado(String usuario) {
		activeSessions.add(usuario);
	}
	
	public void usuarioDesconectado(String usuario) {
		activeSessions.remove(usuario);		
	}

	public Boolean estaConectado(String usuario) {
		return activeSessions.contains(usuario);
	}
}
