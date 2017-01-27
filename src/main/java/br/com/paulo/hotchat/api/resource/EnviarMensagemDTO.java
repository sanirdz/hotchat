package br.com.paulo.hotchat.api.resource;

import org.hibernate.validator.constraints.NotBlank;

public class EnviarMensagemDTO {

	@NotBlank
	private String conteudo;
	
	@NotBlank
	private String loginDestinatario;

	public String getConteudo() {
		return conteudo;
	}

	public EnviarMensagemDTO setConteudo(String conteudo) {
		this.conteudo = conteudo;
		return this;
	}

	public String getLoginDestinatario() {
		return loginDestinatario;
	}

	public EnviarMensagemDTO setLoginDestinatario(String loginDestinatario) {
		this.loginDestinatario = loginDestinatario;
		return this;
	}
}
