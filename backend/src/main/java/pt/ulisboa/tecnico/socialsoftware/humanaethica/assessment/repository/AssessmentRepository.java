package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.repository;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment;

import java.util.List;

@Repository
@Transactional
public interface AssessmentRepository extends JpaRepository<Assessment, Integer> {
    @Query("SELECT a FROM Assessment a WHERE a.institution.id = :institutionId")
    List<Assessment> getAssessmentsByInstitutionId(Integer institutionId);
}
