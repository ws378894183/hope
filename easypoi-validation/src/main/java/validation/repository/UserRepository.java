package validation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import validation.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByAccount(String account);
}
