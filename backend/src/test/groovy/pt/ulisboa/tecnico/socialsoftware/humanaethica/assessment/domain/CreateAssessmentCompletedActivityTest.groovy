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

class CreateAssessmentCompletedActivityTest extends SpockTest {

    Institution institution
    Volunteer volunteer
    Activity activity
    AssessmentDto assessmentDto

    def setup() {
        institution = new Institution()

        volunteer = Mock()
        volunteer.getId() >> VOLUNTEER_ID_1

        activity = Mock()
        activity.getEndingDate() >> LocalDateTime.now().plusDays(1)

        institution.addActivity(activity)

        assessmentDto = Mock()
        assessmentDto.getId() >> ASSESSMENT_ID_1
        assessmentDto.getReview() >> REVIEW
        assessmentDto.getReviewDate() >> DateHandler.toISOString(NOW)
    }

    def "institution doesn't have completed activity"() {
        when:
        Assessment invalidAssessment = new Assessment(volunteer, institution, assessmentDto)

        then:
        HEException heException = thrown(HEException)
        heException.getErrorMessage() == ErrorMessage.ASSESSMENT_INSTITUTION_NO_ACTIVITIES_COMPLETED
    }

    def "institution has one completed and one incomplete activity"() {
        given:
        Activity otherActivity = Mock()
        otherActivity.getEndingDate() >> LocalDateTime.now().minusDays(1)

        institution.addActivity(otherActivity)

        when:
        Assessment validAssessment = new Assessment(volunteer, institution, assessmentDto)

        then:
        notThrown(Exception)

        validAssessment.getId() == ASSESSMENT_ID_1
        validAssessment.getReview() == REVIEW
        validAssessment.getReviewDate() == NOW
        validAssessment.getInstitution() == institution
        validAssessment.getVolunteer() == volunteer
    }

}
