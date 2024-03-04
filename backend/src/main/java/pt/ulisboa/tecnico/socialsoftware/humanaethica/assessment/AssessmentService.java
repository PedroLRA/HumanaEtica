package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.repository.InstitutionRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserRepository;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.INSTITUTION_NOT_FOUND;
import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.USER_NOT_FOUND;

@Service
public class AssessmentService {
     @Autowired
    InstitutionRepository institutionRepository;
     @Autowired
    UserRepository userRepository;

     @Transactional(isolation = Isolation.READ_COMMITTED)
    public AssessmentDto registerAssessment(Integer userId, Integer institutionId, AssessmentDto assessmentDto) {
         if (userId == null) throw new HEException(USER_NOT_FOUND);
         if (institutionId == null) throw new HEException(INSTITUTION_NOT_FOUND);

         Volunteer volunteer = (Volunteer) userRepository.findById(userId)
                 .orElseThrow(() -> new HEException(USER_NOT_FOUND, userId));
         Institution institution = institutionRepository.findById(institutionId)
                 .orElseThrow(() -> new HEException(INSTITUTION_NOT_FOUND, institutionId));

         Assessment assessment = new Assessment(assessmentDto, institution, volunteer);

         return new AssessmentDto(assessment, true, true);
     }
}
