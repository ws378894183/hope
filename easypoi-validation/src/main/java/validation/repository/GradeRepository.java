package validation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import validation.model.Grade;

public interface GradeRepository extends JpaRepository<Grade, Long> {

}
