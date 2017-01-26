package br.com.paulo.hotchat.repository;

import org.springframework.data.repository.CrudRepository;

import br.com.paulo.hotchat.domain.UserRole;

public interface UserRoleRepository extends CrudRepository<UserRole, Long> {

}
