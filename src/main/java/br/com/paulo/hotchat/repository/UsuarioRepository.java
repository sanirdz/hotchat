package br.com.paulo.hotchat.repository;

import org.springframework.data.repository.CrudRepository;

import br.com.paulo.hotchat.domain.Usuario;

public interface UsuarioRepository extends CrudRepository<Usuario, Long> {

	Iterable<Usuario> findAllByOnline(Boolean online);

	Usuario findByLogin(String login);
}
