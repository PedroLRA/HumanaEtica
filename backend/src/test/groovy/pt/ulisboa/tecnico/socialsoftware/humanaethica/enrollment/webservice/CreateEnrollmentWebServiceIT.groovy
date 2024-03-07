package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.webservice

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateEnrollmentWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def activity
    def activityDto
    def enrollmentDto

    def setup() {
        deleteAll()
        given: 'A web client'        
        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        and: 'a institution'
        def institution = institutionService.getDemoInstitution()

        and: 'a theme'
        def theme = createTheme(THEME_NAME_1, Theme.State.APPROVED,null)
        def themes = new ArrayList<>()
        themes.add(theme)
        def themesDto = new ArrayList<ThemeDto>()
        themesDto.add(new ThemeDto(theme,false, false, false))

        and: 'an activity'
        activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,2,ACTIVITY_DESCRIPTION_1,
                IN_ONE_DAY,IN_TWO_DAYS,IN_THREE_DAYS,themesDto)
        activity = createActivity(activityDto, institution, themes)

        and: 'an enrollment dto'
        enrollmentDto = createEnrollmentDto(ENROLLMENT_MOTIVATION_1, NOW);
    }

    def "login as volunteer, and enroll at an activity"() {
        given:
        def volunteer = demoVolunteerLogin()

        when:
        def response = webClient.post()
                .uri('/enrollments/' + activity.id)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(enrollmentDto)
                .retrieve()
                .bodyToMono(EnrollmentDto.class)
                .block()

        then: "check response data"
        response.motivation == ENROLLMENT_MOTIVATION_1
        response.enrollmentDateTime >= DateHandler.toISOString(NOW)
        and: "the enrollment is saved in the database"
        enrollmentRepository.findAll().size() == 1
        and: "the stored data is correct"
        def storedEnrollment = enrollmentRepository.findById(response.id).get()
        storedEnrollment.motivation == ENROLLMENT_MOTIVATION_1
        storedEnrollment.enrollmentDateTime >= NOW
        storedEnrollment.activity.id == activity.id
        storedEnrollment.volunteer.id == volunteer.id

        cleanup:
        deleteAll()
    }

    def "login as volunteer, and create an enrollment with invalid motivation"() {
        given: 'a volunteer'
        demoVolunteerLogin()
        and: 'an enrollmentDto'
        enrollmentDto.motivation = '    '

        when: 'the volunteer tries to create the enrollment'
        def response = webClient.post()
                .uri('/enrollments/' + activity.id)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(enrollmentDto)
                .retrieve()
                .bodyToMono(EnrollmentDto.class)
                .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.BAD_REQUEST
        enrollmentRepository.count() == 0

        cleanup:
        deleteAll()
    }
    
def "login as member, and create an enrollment"() {
        given: 'a member'
        demoMemberLogin()

        when: 'the member tries to create the enrollment'
        def response = webClient.post()
                .uri('/enrollments/' + activity.id)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(enrollmentDto)
                .retrieve()
                .bodyToMono(EnrollmentDto.class)
                .block()

        then: "an error is returned"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        enrollmentRepository.count() == 0

        cleanup:
        deleteAll()
    }

    def "login as admin, and create an enrollment"() {
        given: 'an admin'
        demoAdminLogin()

        when: 'the admin tries to create the enrollment'
        def response = webClient.post()
                .uri('/enrollments/' + activity.id)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(enrollmentDto)
                .retrieve()
                .bodyToMono(EnrollmentDto.class)
                .block()

        then: "an error is returned"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        enrollmentRepository.count() == 0

        cleanup:
        deleteAll()
    }

}
