package br.com.paulo.hotchat.domain;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

@Entity
public class Mensagem {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private LocalDateTime dataEnvio;
	private Boolean lida;
	private String conteudo;
	
	@ManyToOne
	@JoinColumn(name = "ID_EMISSOR")
	private Usuario emissor;
	
	@ManyToOne
	@JoinColumn(name = "ID_DESTINATARIO")
	private Usuario destinatario;
	
	@Version
	private Long version;
	
	public Long getId() {
		return id;
	}

	public Mensagem setId(Long id) {
		this.id = id;
		return this;
	}

	public LocalDateTime getDataEnvio() {
		return dataEnvio;
	}

	public Mensagem setDataEnvio(LocalDateTime dataEnvio) {
		this.dataEnvio = dataEnvio;
		return this;
	}

	public Usuario getEmissor() {
		return emissor;
	}

	public Mensagem setEmissor(Usuario emissor) {
		this.emissor = emissor;
		return this;
	}

	public Usuario getDestinatario() {
		return destinatario;
	}

	public Mensagem setDestinatario(Usuario destinatario) {
		this.destinatario = destinatario;
		return this;
	}

	public Boolean getLida() {
		return lida;
	}

	public Mensagem setLida(Boolean lida) {
		this.lida = lida;
		return this;
	}

	public String getConteudo() {
		return conteudo;
	}

	public Mensagem setConteudo(String conteudo) {
		this.conteudo = conteudo;
		return this;
	}

	public Long getVersion() {
		return version;
	}

	public Mensagem setVersion(Long version) {
		this.version = version;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Mensagem other = (Mensagem) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
