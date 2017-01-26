package br.com.paulo.hotchat.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import br.com.paulo.hotchat.domain.Mensagem;
import br.com.paulo.hotchat.domain.Usuario;

public interface MensagemRepository extends PagingAndSortingRepository<Mensagem, Long> {

	Page<Mensagem> findAllByLidaAndDestinatario(Boolean lida, Usuario destinatario, Pageable pageable);

}
