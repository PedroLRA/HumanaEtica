package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain

import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.VolunteerDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler

import java.time.LocalDateTime

class CreateAssessmentTest extends SpockTest {
    Institution institution
    Volunteer volunteer
    Activity activity
    Random random = new Random()
    int institutionId = random.nextInt(100) + 1
    int volunteerId = random.nextInt(100) + 1
    int assessmentId = random.nextInt(100) + 1
    final String review = "test valid review"
    LocalDateTime localDateTime = LocalDateTime.now()

    def setup(){
        activity = Mock()
        activity.getEndingDate() >> localDateTime.minusDays(1)
        institution = Mock()
        institution.getId() >> institutionId
        institution.getAssessments() >> []
        institution.getActivities() >> [activity]
        volunteer = Mock()
        volunteer.getId() >> volunteerId
    }

    def "using empty constructor and getters + setters"(){
        when:
        Assessment assessment = new Assessment()
        assessment.setId(assessmentId)
        assessment.setReview(review)
        assessment.setReviewDate(localDateTime)
        assessment.setInstitution(institution)
        assessment.setVolunteer(volunteer)

        then:
        assessmentIsInstantiatedCorrectly(assessment)
    }

    def "using constructor with dto object"() {
        given:
        VolunteerDto volunteerDto = Mock()
        InstitutionDto institutionDto = Mock()
        AssessmentDto assessmentDto = new AssessmentDto(assessmentId, review, DateHandler.toISOString(localDateTime), volunteerDto, institutionDto)

        when:
        Assessment assessment = new Assessment(assessmentDto, institution, volunteer)

        then:
        assessmentIsInstantiatedCorrectly(assessment)
    }

    def "using explicit constructor"() {
        when:
        Assessment assessment = new Assessment(volunteerId, review, localDateTime, institution, volunteer)

        then:
        assessmentIsInstantiatedCorrectly(assessment)
    }

    boolean assessmentIsInstantiatedCorrectly(Assessment assessment) {
        assessment.getId() == assessmentId
        assessment.getInstitution().getId() == institutionId
        assessment.getReview().equals(review)
        assessment.getReviewDate().isEqual(localDateTime)
        assessment.getVolunteer().getId() == volunteerId
    }
}
