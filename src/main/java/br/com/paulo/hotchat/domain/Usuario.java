package br.com.paulo.hotchat.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Usuario {

	@Id
	private Long id;
	private String login;
	private String senha;
	private Boolean online;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getLogin() {
		return login;
	}
	
	public void setLogin(String login) {
		this.login = login;
	}
	
	public String getSenha() {
		return senha;
	}
	
	public void setSenha(String senha) {
		this.senha = senha;
	}
	
	public Boolean getOnline() {
		return online;
	}
	
	public void setOnline(Boolean online) {
		this.online = online;
	}
}
