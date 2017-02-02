package br.com.paulo.hotchat.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import br.com.paulo.hotchat.domain.Mensagem;
import br.com.paulo.hotchat.domain.Usuario;

public interface MensagemRepository extends PagingAndSortingRepository<Mensagem, Long> {

	Iterable<Mensagem> findAllByDestinatarioAndEmissorOrderByDataEnvio(Usuario destinatario, Usuario emissor);

	@Query("select count(m) from Mensagem m where m.lida = false and m.emissor.login = :loginEmissor and m.destinatario.login = :loginDestinatario")
	Integer recuperaQuantidadeMensagensNaoLidas(@Param("loginEmissor") String loginEmissor, @Param("loginDestinatario") String loginDestinatario);

}
