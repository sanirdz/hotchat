package br.com.paulo.hotchat.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.validator.constraints.NotBlank;

@Entity
@Table(name = "USERS")
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotBlank
	@Column(name = "username", unique = true)
	private String login;

	@NotBlank
	@Column(name = "password")
	private String senha;

	private Boolean enabled;

	@Version
	private Long version;
	
	@Transient
	private Boolean online;

	public Long getId() {
		return id;
	}
	
	public Usuario setId(Long id) {
		this.id = id;
		return this;
	}
	
	public String getLogin() {
		return login;
	}
	
	public Usuario setLogin(String login) {
		this.login = login;
		return this;
	}
	
	public String getSenha() {
		return senha;
	}
	
	public Usuario setSenha(String senha) {
		this.senha = senha;
		return this;
	}
	
	public Boolean getEnabled() {
		return enabled;
	}

	public Usuario setEnabled(Boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Boolean getOnline() {
		return online;
	}

	public Usuario setOnline(Boolean online) {
		this.online = online;
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
		Usuario other = (Usuario) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
