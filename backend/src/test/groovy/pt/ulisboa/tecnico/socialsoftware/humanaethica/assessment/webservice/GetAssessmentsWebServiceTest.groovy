package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.webservice

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetAssessmentsWebServiceTest extends SpockTest {
    @LocalServerPort
    private int port

    def institution
    def volunteerIDs = []
    def assessmentIDs = []

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        populateDatabaseWithAssessments()

        def activity = new Activity()
        activity.setEndingDate(ONE_DAY_AGO)
        activityRepository.save(activity)

        institution = new Institution()
        institution.addActivity(activity)
        institutionRepository.save(institution)

        def volunteer
        def assessment
        for (int i = 0; i < 4; i++) {
            volunteer = new Volunteer(USER_1_NAME, "a"* (i+1), USER_1_EMAIL, AuthUser.Type.DEMO, User.State.ACTIVE)
            userRepository.save(volunteer)
            volunteerIDs.add(volunteer.getId())

            assessment = new Assessment(REVIEW, NOW, institution, volunteer)
            assessmentRepository.save(assessment)
            assessmentIDs.add(assessment.getId())
        }
    }

    def "successful test"() {
        given:
        demoMemberLogin()
        def id = institution.getId()

        when:
        def response = webClient.get()
                .uri("""/assessments/search/$id""")
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(AssessmentDto.class)
                .collectList()
                .block()

        then:
        response.size() == volunteerIDs.size()
        for (dto in response) {
            dto.getReview() == REVIEW
            dto.reviewDate == DateHandler.toISOString(NOW)
            dto.getInstitution().getId() == id
            dto.getVolunteer().getId() in volunteerIDs
            dto.getId() in assessmentIDs
        }
    }

    def "bad request"() {
        given:
        demoMemberLogin()
        def id = 2000

        when:
        webClient.get()
                .uri("""/assessments/search/$id""")
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(AssessmentDto.class)
                .collectList()
                .block()

        then:
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.BAD_REQUEST

        cleanup:
        deleteAll()
    }
}
