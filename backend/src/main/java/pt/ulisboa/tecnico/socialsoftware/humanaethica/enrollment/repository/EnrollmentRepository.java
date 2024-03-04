package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment;

import java.util.List;
import java.util.Set;

@Repository
@Transactional
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    @Query("SELECT a FROM Enrollment a WHERE a.activity.id = :activityId")
    List<Enrollment> getEnrollmentsByActivityId(Integer activityId);

}