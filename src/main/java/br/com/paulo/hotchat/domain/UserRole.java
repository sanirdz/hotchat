package br.com.paulo.hotchat.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "AUTHORITIES")
public class UserRole {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String authority;
	private String username;
	
	public Long getId() {
		return id;
	}
	
	public UserRole setId(Long id) {
		this.id = id;
		return this;
	}
	
	public String getAuthority() {
		return authority;
	}
	
	public UserRole setAuthority(String authority) {
		this.authority = authority;
		return this;
	}
	
	public String getUsername() {
		return username;
	}
	
	public UserRole setUsername(String username) {
		this.username = username;
		return this;
	}
}
