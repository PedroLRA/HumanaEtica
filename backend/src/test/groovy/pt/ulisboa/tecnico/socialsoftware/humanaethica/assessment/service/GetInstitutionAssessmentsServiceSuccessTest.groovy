package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.service

import org.spockframework.spring.EnableSharedInjection
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer

@EnableSharedInjection
@DataJpaTest
class GetInstitutionAssessmentsServiceSuccessTest extends SpockTest {
    static String INSTITUTION_TARGET_NAME = "target"
    static String INSTITUTION_TARGET_NIF = 111111111
    static String INSTITUTION_TARGET_EMAIL = "target@mail.com"
    static Institution institutionTarget
    static InstitutionDto institutionDtoTarget
    static AssessmentDto assessmentDto1
    static int institutionId
    static int userId
    static Random = new Random()

    static final String review = "test valid review"

    def setup() {
        deleteAll()
        institutionDtoTarget = createInstitutionDto(INSTITUTION_TARGET_NAME, INSTITUTION_TARGET_NIF, INSTITUTION_TARGET_EMAIL)
        institutionTarget = super.institutionService.registerInstitution(institutionDtoTarget)
        institutionId = institutionTarget.getId()
    }

    def "database contains one institution with no assessments"() {
        when:
        List<AssessmentDto> results = assessmentService.getAssessmentsByInstitution(institutionId)

        then:
        notThrown(HEException)
        results.isEmpty()
    }

    def "database contains one institution with one assessment"() {
        given:
        Activity activity = Mock()
        activity.getEndingDate() >> ONE_DAY_AGO
        institutionTarget.addActivity(activity)
        Volunteer volunteer = createVolunteer("abc", "abc123", "password","abc@mail.com", AuthUser.Type.NORMAL, User.State.APPROVED)
        Assessment assessment1 = new Assessment(review, NOW, institutionTarget, volunteer)
        assessmentRepository.save(assessment1)

        when:
        List<AssessmentDto> results = assessmentService.getAssessmentsByInstitution(institutionId)

        then:
        results.size() == 1
        notThrown(HEException)
    }

    def "database contains one institution with multiple assessments"() {
        given:
        Activity activity1 = Mock()
        activity1.getEndingDate() >> ONE_DAY_AGO
        institutionTarget.addActivity(activity1)
        Volunteer volunteer1 = createVolunteer("abc", "assess1", "password","abc1@mail.com", AuthUser.Type.NORMAL, User.State.APPROVED)
        Volunteer volunteer2 = createVolunteer("abc", "assess2", "password","abc2@mail.com", AuthUser.Type.NORMAL, User.State.APPROVED)
        Volunteer volunteer3 = createVolunteer("abc", "assess3", "password","abc3@mail.com", AuthUser.Type.NORMAL, User.State.APPROVED)
        Volunteer volunteer4 = createVolunteer("abc", "assess4", "password","abc4@mail.com", AuthUser.Type.NORMAL, User.State.APPROVED)
        Assessment assessment1 = new Assessment(review, NOW, institutionTarget, volunteer1)
        assessmentRepository.save(assessment1)
        Assessment assessment2 = new Assessment(review, NOW, institutionTarget, volunteer2)
        assessmentRepository.save(assessment2)
        Assessment assessment3 = new Assessment(review, NOW, institutionTarget, volunteer3)
        assessmentRepository.save(assessment3)
        Assessment assessment4 = new Assessment(review, NOW, institutionTarget, volunteer4)
        assessmentRepository.save(assessment4)

        when:
        List<AssessmentDto> results = assessmentService.getAssessmentsByInstitution(institutionId)

        then:
        results.size() == 4
        notThrown(HEException)
    }

    def "database contains multiple institutions + assessments"() {
        given:
        InstitutionDto institutionDtoTarget2 = createInstitutionDto(INSTITUTION_TARGET_NAME, "222222222", INSTITUTION_TARGET_EMAIL)
        Institution institutionTarget2 = super.institutionService.registerInstitution(institutionDtoTarget2)
        InstitutionDto institutionDtoTarget3 = createInstitutionDto(INSTITUTION_TARGET_NAME, "333333333", INSTITUTION_TARGET_EMAIL)
        Institution institutionTarget3 = super.institutionService.registerInstitution(institutionDtoTarget3)

        Activity activity1 = Mock()
        activity1.getEndingDate() >> ONE_DAY_AGO
        institutionTarget.addActivity(activity1)
        institutionTarget2.addActivity(activity1)
        Volunteer volunteer1 = createVolunteer("abc", "assess1", "password","abc1@mail.com", AuthUser.Type.NORMAL, User.State.APPROVED)
        Volunteer volunteer2 = createVolunteer("abc", "assess2", "password","abc2@mail.com", AuthUser.Type.NORMAL, User.State.APPROVED)
        Volunteer volunteer3 = createVolunteer("abc", "assess3", "password","abc3@mail.com", AuthUser.Type.NORMAL, User.State.APPROVED)
        Volunteer volunteer4 = createVolunteer("abc", "assess4", "password","abc4@mail.com", AuthUser.Type.NORMAL, User.State.APPROVED)
        Assessment assessment1 = new Assessment(review, NOW, institutionTarget, volunteer1)
        assessmentRepository.save(assessment1)
        Assessment assessment2 = new Assessment(review, NOW, institutionTarget2, volunteer2)
        assessmentRepository.save(assessment2)
        Assessment assessment3 = new Assessment(review, NOW, institutionTarget2, volunteer3)
        assessmentRepository.save(assessment3)
        Assessment assessment4 = new Assessment(review, NOW, institutionTarget2, volunteer4)
        assessmentRepository.save(assessment4)

        when:
        List<AssessmentDto> results = assessmentService.getAssessmentsByInstitution(institutionTarget2.getId())

        then:
        results.size() == 3
        notThrown(HEException)
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
