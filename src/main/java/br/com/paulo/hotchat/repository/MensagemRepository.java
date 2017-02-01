package br.com.paulo.hotchat.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import br.com.paulo.hotchat.domain.Mensagem;
import br.com.paulo.hotchat.domain.Usuario;

public interface MensagemRepository extends PagingAndSortingRepository<Mensagem, Long> {

	Iterable<Mensagem> findAllByDestinatarioAndEmissorOrderByDataEnvio(Usuario destinatario, Usuario emissor);

}
