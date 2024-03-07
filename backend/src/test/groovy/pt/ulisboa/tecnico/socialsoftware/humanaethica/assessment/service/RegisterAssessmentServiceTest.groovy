package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll

import java.time.LocalDateTime
import java.util.logging.Handler

@DataJpaTest
class RegisterAssessmentServiceTest extends SpockTest {
    public static final String EXIST = "exist"
    public static final String NO_EXIST = "noExist"

    def institution
    def activity
    def volunteer
    def assessmentDto

    def setup() {
        activity = new Activity()
        activity.setEndingDate(ONE_DAY_AGO)
        activityRepository.save(activity)

        institution = new Institution()
        institution.addActivity(activity)
        institutionRepository.save(institution)

        volunteer = authUserService.loginDemoVolunteerAuth().getUser()

        assessmentDto = createAssessmentDto(REVIEW, NOW)
    }

    def "register assessment"() {
        when:
        def result = assessmentService.createAssessment(volunteer.getId(), institution.getId(), assessmentDto)

        then:
        assessmentRepository.findAll().size() == 1
        and:
        result.review == REVIEW
        result.reviewDate != null
        result.getVolunteer().getId() == volunteer.getId()
        result.getInstitution().getId() == institution.getId()
        and:
        def storedAssessment = assessmentRepository.findById(result.id).get()
        storedAssessment.review == REVIEW
        storedAssessment.reviewDate != null
        storedAssessment.volunteer.getId() == volunteer.getId()
        storedAssessment.institution.getId() == institution.getId()

    }

    @Unroll
    def 'invalid arguments: userId=#userId | institutionId=#institutionId'() {
        when:
        assessmentService.createAssessment(getUserId(userId), getInstitutionId(institutionId), assessmentDto)

        then:
        def error = thrown(HEException)
        error.errorMessage == errorMessage
        and:
        assessmentRepository.findAll().size() == 0

        where:
        userId      | institutionId || errorMessage
        null        | EXIST         || ErrorMessage.USER_NOT_FOUND
        null        | null          || ErrorMessage.USER_NOT_FOUND
        null        | NO_EXIST      || ErrorMessage.USER_NOT_FOUND
        NO_EXIST    | EXIST         || ErrorMessage.USER_NOT_FOUND
        NO_EXIST    | NO_EXIST      || ErrorMessage.USER_NOT_FOUND
        NO_EXIST    | null          || ErrorMessage.INSTITUTION_NOT_FOUND
        EXIST       | NO_EXIST      || ErrorMessage.INSTITUTION_NOT_FOUND
        EXIST       | null          || ErrorMessage.INSTITUTION_NOT_FOUND

    }

    def getUserId(userId) {
        if (userId == EXIST)
            return volunteer.getId()
        else if (userId == NO_EXIST)
            return 333
        return null
    }

    def getInstitutionId(institutionId) {
        if (institutionId == EXIST)
            return institution.getId()
        else if (institutionId == NO_EXIST)
            return 333
        return null
    }



    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration{}
}
