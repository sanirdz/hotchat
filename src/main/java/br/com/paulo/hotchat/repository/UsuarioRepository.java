package br.com.paulo.hotchat.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import br.com.paulo.hotchat.domain.Usuario;

public interface UsuarioRepository extends CrudRepository<Usuario, Long> {

	Usuario findByLogin(String login);

	@Query("select u from Usuario u where u.login <> :username")
	Iterable<Usuario> findAllBut(@Param("username") String username);

	@Query("select c.contato from Contato c where c.principal = :principal")
	Iterable<Usuario> findAllByContatoPrincipal(@Param("principal") Usuario principal);

	//selecionar todos os usuarios que nao sao contatos do principal... ta meio estranho eu acho...
//	@Query("select distinct u from Usuario u "
//			+ "left join u.contatos c "
//			+ "left join u.contatosDe c2 "
//			+ "where c.principal <> :principal "
//			+ "and c2.principal <> :principal "
//			+ "or c.principal is null")
//	Iterable<Usuario> findAllContatosBut(@Param("principal") Usuario principal);
}
