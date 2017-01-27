package br.com.paulo.hotchat.api.resource;

import org.hibernate.validator.constraints.NotBlank;

public class SalvarUsuarioDTO {

	@NotBlank
	private String login;
	
	@NotBlank
	private String senha;
	
	public String getLogin() {
		return login;
	}
	public SalvarUsuarioDTO setLogin(String login) {
		this.login = login;
		return this;
	}
	public String getSenha() {
		return senha;
	}
	public SalvarUsuarioDTO setSenha(String senha) {
		this.senha = senha;
		return this;
	}
	
}
