package br.com.paulo.hotchat.repository;

import org.springframework.data.repository.CrudRepository;

import br.com.paulo.hotchat.domain.Contato;
import br.com.paulo.hotchat.domain.Usuario;

public interface ContatoRepository extends CrudRepository<Contato, Long> {

	Iterable<Contato> findAllByPrincipal(Usuario principal);

}
