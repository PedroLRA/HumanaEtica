package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User;

import java.util.List;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Integer> {
}