package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.InstitutionService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto

@DataJpaTest
class GetInstitutionAssessmentsServiceTest extends SpockTest{

    def "institutionId can't be negative integer"(){
        given:
        int institutionId = -1

        when:
        assessmentService.getAssignmentsByInstitution(institutionId)

        then:
        HEException heException = thrown(HEException)
        heException.errorMessage.equals(ErrorMessage.INSTITUTION_INVALID_ID)
    }

    def "institutionId doesn't refer to existing institution"(){
        given:
        int institutionId = 1

        when:
        println assessmentService.getAssignmentsByInstitution(institutionId)

        then:
        HEException heException = thrown(HEException)
        heException.errorMessage.equals(ErrorMessage.INSTITUTION_NOT_FOUND)
    }

    def "institutionId refers to existing institution"(){
        given:
        InstitutionDto institutionDto = new InstitutionDto()
        institutionDto.setEmail("test@mail.com")
        institutionDto.setNif("123123123")
        institutionDto.setName("Test")
        Institution institution = institutionService.registerInstitution(institutionDto)
        institution.addAssessment(new Assessment())

        when:
        assessmentService.getAssignmentsByInstitution(institution.getId())

        then:
        notThrown(HEException)
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration{}
}
