package br.com.riachuelo.challenge.to_do_list_api.repository;

import br.com.riachuelo.challenge.to_do_list_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
