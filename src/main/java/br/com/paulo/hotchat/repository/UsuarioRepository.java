package br.com.paulo.hotchat.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import br.com.paulo.hotchat.domain.Usuario;

public interface UsuarioRepository extends CrudRepository<Usuario, Long> {

	Usuario findByLogin(String login);

	@Query("select u from Usuario u where u.login <> :username")
	Iterable<Usuario> findAllBut(@Param("username") String username);
}
