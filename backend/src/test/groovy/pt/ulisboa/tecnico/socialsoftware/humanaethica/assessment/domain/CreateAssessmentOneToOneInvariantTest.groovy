package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain

import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler

import java.time.LocalDateTime

class CreateAssessmentOneToOneInvariantTest extends SpockTest {

    Institution institution
    Volunteer volunteer
    Activity activity
    AssessmentDto assessmentDto

    def setup() {
        institution = new Institution()

        volunteer = Mock()
        volunteer.getId() >> VOLUNTEER_ID_1

        activity = Mock()
        activity.getEndingDate() >> LocalDateTime.now().minusDays(1)

        institution.addActivity(activity)

        assessmentDto = Mock()
        assessmentDto.getReview() >> REVIEW
        assessmentDto.getReviewDate() >> DateHandler.toISOString(NOW)
    }

    def "volunteer already assessed this institution"() {
        given:
        Assessment assessment = Mock()
        assessment.getVolunteer() >> volunteer
        assessment.getInstitution() >> institution

        institution.addAssessment(assessment)
        volunteer.addAssessment(assessment)

        when:
        Assessment duplicateAssessment = new Assessment(institution, volunteer, assessmentDto)

        then:
        HEException heException = thrown(HEException)
        heException.getErrorMessage().equals(ErrorMessage.ASSESSMENT_VOLUNTEER_ALREADY_ASSESSED_INSTITUTION)
    }

    def "volunteer has assessed another institution"() {
        given:
        Institution otherInstitution = Mock()
        otherInstitution.getAssessments() >> []
        otherInstitution.getActivities() >> [activity]
        Assessment assessment = new Assessment(otherInstitution, volunteer, assessmentDto)

        when:
        Assessment validAssessment = new Assessment(institution, volunteer, assessmentDto)

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
        Assessment assessment = new Assessment(institution, otherVolunteer, assessmentDto)

        when:
        Assessment validAssessment = new Assessment(institution, volunteer, assessmentDto)

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
        otherVolunteer.getId() >> VOLUNTEER_ID_2

        Institution otherInstitution = Mock()
        otherInstitution.getAssessments() >> []
        otherInstitution.getActivities() >> [activity]

        Assessment assessment = new Assessment(institution, otherVolunteer, assessmentDto)
        Assessment otherAssessment = new Assessment(otherInstitution, volunteer, assessmentDto)

        when:
        Assessment validAssessment = new Assessment(institution, volunteer, assessmentDto)

        then:
        notThrown(Exception)

        validAssessment.getReview() == REVIEW
        validAssessment.getReviewDate() == NOW
        validAssessment.getInstitution() == institution
        validAssessment.getVolunteer() == volunteer
    }
}