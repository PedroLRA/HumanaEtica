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
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateAssessmentWebServiceTest extends SpockTest {
	@LocalServerPort
	private int port
	static String ASSESSMENT_REVIEW = "sample valid review"
	static LocalDateTime localDateTime = NOW.truncatedTo(ChronoUnit.MILLIS)

	static AssessmentDto assessmentDto
	static Institution institution
	static InstitutionDto institutionDto

	def setup() {
		deleteAll()

		webClient = WebClient.create("http://localhost:" + port)
		headers = new HttpHeaders()
		headers.setContentType(MediaType.APPLICATION_JSON)

		Theme theme = createTheme(THEME_NAME_1, Theme.State.APPROVED, null)

		institution = new Institution(INSTITUTION_1_NAME, INSTITUTION_1_NIF, INSTITUTION_1_EMAIL)
		institutionRepository.save(institution)
		institutionDto = new InstitutionDto(institution)

		ActivityDto activityDto = createActivityDto(ACTIVITY_NAME_1, ACTIVITY_REGION_1, 1, ACTIVITY_DESCRIPTION_1, NOW.minusDays(3), NOW.minusDays(2), NOW.minusDays(1), [new ThemeDto(theme, false, false, false)])
		Activity activity = new Activity(activityDto, institution, [theme])
		activityRepository.save(activity)

		assessmentDto = createAssessmentDto(ASSESSMENT_REVIEW, localDateTime)
		assessmentDto.setInstitution(institutionDto)
	}

	def "login as volunteer, and create an assessment"() {
		given:
		demoVolunteerLogin()

		when:
		def response = webClient.post()
				.uri('/assessments')
				.headers(httpHeaders -> httpHeaders.putAll(headers))
				.bodyValue(assessmentDto)
				.retrieve()
				.bodyToMono(AssessmentDto.class)
				.block()

		then: "check response data"
		response.getReview().equals(ASSESSMENT_REVIEW)
		DateHandler.toLocalDateTime(response.getReviewDate()).isEqual(localDateTime)
		response.getInstitution().getNif().equals(institution.getNIF())
		and: 'check database data'
		assessmentRepository.count() == 1
		Assessment assessmentCheck = assessmentRepository.findAll().get(0)
		assessmentCheck.getReview().equals(assessmentDto.getReview())
		assessmentCheck.getReviewDate().isEqual(localDateTime)
		assessmentCheck.getInstitution().getNIF().equals(institution.getNIF())
	}

	def "login as volunteer, and create an activity with error"() {
		given: 'a volunteer'
		demoVolunteerLogin()
		and: 'an invalid review'
		assessmentDto.setReview("test")

		when: 'the volunteer registers the assessment'
		webClient.post()
				.uri('/assessments')
				.headers(httpHeaders -> httpHeaders.putAll(headers))
				.bodyValue(assessmentDto)
				.retrieve()
				.bodyToMono(AssessmentDto.class)
				.block()

		then: "check response status"
		def error = thrown(WebClientResponseException)
		error.statusCode == HttpStatus.BAD_REQUEST
		assessmentRepository.count() == 0
	}

	def "login as member, and create an assessment"() {
		given: 'a member'
		demoMemberLogin()

		when: 'the member creates the assessment'
		webClient.post()
				.uri('/assessments')
				.headers(httpHeaders -> httpHeaders.putAll(headers))
				.bodyValue(assessmentDto)
				.retrieve()
				.bodyToMono(AssessmentDto.class)
				.block()

		then: "an error is returned"
		def error = thrown(WebClientResponseException)
		error.statusCode == HttpStatus.FORBIDDEN
		assessmentRepository.count() == 0
	}

	def "login as admin, and create an assessment"() {
		given: 'a demo'
		demoAdminLogin()

		when: 'the admin registers the assessment'
		webClient.post()
				.uri('/assessments')
				.headers(httpHeaders -> httpHeaders.putAll(headers))
				.bodyValue(assessmentDto)
				.retrieve()
				.bodyToMono(ActivityDto.class)
				.block()

		then: "an error is returned"
		def error = thrown(WebClientResponseException)
		error.statusCode == HttpStatus.FORBIDDEN
		assessmentRepository.count() == 0
	}
}

