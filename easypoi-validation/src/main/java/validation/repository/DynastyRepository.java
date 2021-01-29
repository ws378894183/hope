package validation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import validation.model.Dynasty;

public interface DynastyRepository extends JpaRepository<Dynasty, Long> {

    Dynasty findByName(String name);
}
