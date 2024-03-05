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
        activityCompleted.getEndingDate() >> LocalDateTime.now().minusDays(1)
        activityCompleted.getInstitution() >> institution
        institution.getActivities() >> [activityCompleted]
        institution.getAssessments() >> []
    }

    def "review can't be null"() {
        when:
        Assessment assessmentNullReview = new Assessment(assessmentId, null, LocalDateTime.now(), institution, volunteer)

        then:
        HEException heException = thrown(HEException)
        heException.getErrorMessage().equals(ErrorMessage.ASSESSMENT_INVALID_REVIEW)
    }

    def "review can't be shorter than 10 characters"() {
        when:
        Assessment assessmentShortReview = new Assessment(assessmentId,"test", LocalDateTime.now(), institution, volunteer)

        then:
        HEException heException = thrown(HEException)
        heException.getErrorMessage().equals(ErrorMessage.ASSESSMENT_INVALID_REVIEW)
    }

    def "valid review doesn't throw exception"() {
        when:
        Assessment assessmentValidReview = new Assessment(assessmentId,"review longer than 10 characters", LocalDateTime.now(), institution, volunteer)

        then:
        HEException heException = notThrown(HEException)
    }

    def "volunteer already assessed this institution"() {
        given:
        Assessment assessment = Mock()
        assessment.getVolunteer() >> volunteer
        assessment.getInstitution() >> institution
        institution.addAssessment(assessment)
        volunteer.addAssessment(assessment)

        when:
        Assessment duplicateAssessment = new Assessment("review longer than 10 characters", LocalDateTime.now(), institution, volunteer)

        then:
        HEException heException = thrown(HEException)
        heException.getErrorMessage().equals(ErrorMessage.ASSESSMENT_VOLUNTEER_ALREADY_ASSESSED_INSTITUTION)
    }

    def "volunteer has assessed another institution"() {
        given:
        Institution otherInstitution = Mock()
        otherInstitution.getAssessments() >> []
        otherInstitution.getActivities() >> [activityCompleted]
        Assessment assessment = new Assessment("review longer than 10 characters", LocalDateTime.now(), otherInstitution, volunteer)

        when:
        Assessment validAssessment = new Assessment("review longer than 10 characters", LocalDateTime.now(), institution, volunteer)

        then:
        notThrown(Exception)

    }

    def "institution has been assessed by another volunteer"() {
        given:
        Volunteer otherVolunteer = Mock()
        otherVolunteer.getId() >> 2
        Assessment assessment = new Assessment("review longer than 10 characters", LocalDateTime.now(), institution, otherVolunteer)

        when:
        Assessment validAssessment = new Assessment("review longer than 10 characters", LocalDateTime.now(), institution, volunteer)

        then:
        notThrown(Exception)
    }

    def "both institution and volunteer have other assessments"() {
        given:
        Volunteer otherVolunteer = Mock()
        otherVolunteer.getId() >> 2
        Institution otherInstitution = Mock()
        otherInstitution.getAssessments() >> []
        otherInstitution.getActivities() >> [activityCompleted]
        Assessment assessment = new Assessment("review longer than 10 characters", LocalDateTime.now(), institution, otherVolunteer)
        Assessment otherAssessment = new Assessment("review longer than 10 characters", LocalDateTime.now(), otherInstitution, volunteer)

        when:
        Assessment validAssessment = new Assessment("review longer than 10 characters", LocalDateTime.now(), institution, volunteer)

        then:
        notThrown(Exception)
    }
}

