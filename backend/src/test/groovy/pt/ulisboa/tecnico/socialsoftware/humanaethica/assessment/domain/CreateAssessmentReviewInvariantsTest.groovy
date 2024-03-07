package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain

import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer

import java.time.LocalDateTime

class CreateAssessmentReviewInvariantsTest extends SpockTest {
    Institution institution
    Volunteer volunteer
    Activity activityCompleted
    Random random = new Random()
    int assessmentId = random.nextInt(100) + 1

    def setup() {
        institution = Mock()
        volunteer = Mock()
        activityCompleted = Mock()
        activityCompleted.getEndingDate() >> ONE_DAY_AGO
        activityCompleted.getInstitution() >> institution
        institution.getActivities() >> [activityCompleted]
        institution.getAssessments() >> []
    }

    def "review can't be null"() {
        when:
        Assessment assessmentNullReview = new Assessment(null, institution, volunteer)

        then:
        HEException heException = thrown(HEException)
        heException.getErrorMessage() == ErrorMessage.ASSESSMENT_INVALID_REVIEW
    }

    def "review can't be shorter than 10 characters"() {
        when:
        Assessment assessmentShortReview = new Assessment("test", institution, volunteer)

        then:
        HEException heException = thrown(HEException)
        heException.getErrorMessage() == ErrorMessage.ASSESSMENT_INVALID_REVIEW
    }

    def "valid review doesn't throw exception"() {
        when:
        Assessment assessmentValidReview = new Assessment("review longer than 10 characters", institution, volunteer)

        then:
        HEException heException = notThrown(HEException)
    }
}

