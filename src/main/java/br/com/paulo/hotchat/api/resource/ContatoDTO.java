package br.com.paulo.hotchat.api.resource;

public class ContatoDTO {

	private String login;
	private Integer totalMensagensNaoLidas;
	private Boolean online;
	
	public String getLogin() {
		return login;
	}

	public ContatoDTO setLogin(String login) {
		this.login = login;
		return this;
	}

	public Integer getTotalMensagensNaoLidas() {
		return totalMensagensNaoLidas;
	}

	public ContatoDTO setTotalMensagensNaoLidas(Integer totalMensagensNaoLidas) {
		this.totalMensagensNaoLidas = totalMensagensNaoLidas;
		return this;
	}

	public Boolean getOnline() {
		return online;
	}

	public ContatoDTO setOnline(Boolean online) {
		this.online = online;
		return this;
	}
	
	
}