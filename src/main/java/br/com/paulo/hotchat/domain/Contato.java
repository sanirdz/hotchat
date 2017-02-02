package br.com.paulo.hotchat.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Contato {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "ID_PRINCIPAL")
	private Usuario principal;
	
	@ManyToOne
	@JoinColumn(name = "ID_CONTATO")
	private Usuario contato;

	public Long getId() {
		return id;
	}

	public Contato setId(Long id) {
		this.id = id;
		return this;
	}

	public Usuario getContato() {
		return contato;
	}

	public Contato setContato(Usuario contato) {
		this.contato = contato;
		return this;
	}

	public Usuario getPrincipal() {
		return principal;
	}

	public Contato setPrincipal(Usuario principal) {
		this.principal = principal;
		return this;
	}
}
