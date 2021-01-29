package validation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import validation.model.Clazz;

public interface ClazzRepository extends JpaRepository<Clazz, Long> {

}
