package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain

import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer

import java.time.LocalDateTime

class CreateAssessmentOneToOneInvariantTest extends SpockTest {

    Institution institution
    Volunteer volunteer
    Activity activityCompleted

    def setup() {
        institution = new Institution()
        volunteer = Mock()
        volunteer.getId() >> VOLUNTEER_ID_1
        activityCompleted = Mock()
        activityCompleted.getEndingDate() >> LocalDateTime.now().minusDays(1)
        activityCompleted.getInstitution() >> institution
        institution.addActivity(activityCompleted)
    }

    def "volunteer already assessed this institution"() {
        given:
        Assessment assessment = Mock()
        assessment.getVolunteer() >> volunteer
        assessment.getInstitution() >> institution
        institution.addAssessment(assessment)
        volunteer.addAssessment(assessment)

        when:
        Assessment duplicateAssessment = new Assessment(REVIEW, NOW, institution, volunteer)

        then:
        HEException heException = thrown(HEException)
        heException.getErrorMessage().equals(ErrorMessage.ASSESSMENT_VOLUNTEER_ALREADY_ASSESSED_INSTITUTION)
    }

    def "volunteer has assessed another institution"() {
        given:
        Institution otherInstitution = Mock()
        otherInstitution.getAssessments() >> []
        otherInstitution.getActivities() >> [activityCompleted]
        Assessment assessment = new Assessment(REVIEW, NOW, otherInstitution, volunteer)

        when:
        Assessment validAssessment = new Assessment(REVIEW, NOW, institution, volunteer)

        then:
        notThrown(Exception)

        validAssessment.getReview() == REVIEW
        validAssessment.getReviewDate() == NOW
        validAssessment.getInstitution() == institution
        validAssessment.getVolunteer() == volunteer

    }

    def "institution has been assessed by another volunteer"() {
        given:
        Volunteer otherVolunteer = Mock()
        otherVolunteer.getId() >> VOLUNTEER_ID_2
        Assessment assessment = new Assessment(REVIEW, NOW, institution, otherVolunteer)

        when:
        Assessment validAssessment = new Assessment(REVIEW, NOW, institution, volunteer)

        then:
        notThrown(Exception)

        validAssessment.getReview() == REVIEW
        validAssessment.getReviewDate() == NOW
        validAssessment.getInstitution() == institution
        validAssessment.getVolunteer() == volunteer
    }

    def "both institution and volunteer have other assessments"() {
        given:
        Volunteer otherVolunteer = Mock()
        otherVolunteer.getId() >> 2
        Institution otherInstitution = Mock()
        otherInstitution.getAssessments() >> []
        otherInstitution.getActivities() >> [activityCompleted]
        Assessment assessment = new Assessment(REVIEW, NOW, institution, otherVolunteer)
        Assessment otherAssessment = new Assessment(REVIEW, NOW, otherInstitution, volunteer)

        when:
        Assessment validAssessment = new Assessment(REVIEW, NOW, institution, volunteer)

        then:
        notThrown(Exception)

        validAssessment.getReview() == REVIEW
        validAssessment.getReviewDate() == NOW
        validAssessment.getInstitution() == institution
        validAssessment.getVolunteer() == volunteer
    }
}