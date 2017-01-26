package br.com.paulo.hotchat.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import br.com.paulo.hotchat.domain.Mensagem;

public interface MensagemRepository extends PagingAndSortingRepository<Mensagem, Long> {


}
