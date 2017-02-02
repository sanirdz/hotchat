package br.com.paulo.hotchat.api.resource;

import java.time.LocalDateTime;

public class MensagemDTO {

	private Long id;
	private String emissor;
	private LocalDateTime dataEnvio;
	private String conteudo;
	
	public String getEmissor() {
		return emissor;
	}
	public MensagemDTO setEmissor(String emissor) {
		this.emissor = emissor;
		return this;
	}
	public LocalDateTime getDataEnvio() {
		return dataEnvio;
	}
	public MensagemDTO setDataEnvio(LocalDateTime dataEnvio) {
		this.dataEnvio = dataEnvio;
		return this;
	}
	public String getConteudo() {
		return conteudo;
	}
	public MensagemDTO setConteudo(String conteudo) {
		this.conteudo = conteudo;
		return this;
	}
	public Long getId() {
		return id;
	}
	public MensagemDTO setId(Long id) {
		this.id = id;
		return this;
	}
}
