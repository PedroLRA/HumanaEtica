package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.webservice

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.http.HttpStatus
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetEnrollmentsWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def activity
    def activityDto
    def enrollment
    def enrollmentDto

    def setup() {
        deleteAll()
        given: 'A web client'        
        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        and: "an institution"
        def institution = institutionService.getDemoInstitution()

        and: "a volunteer"
        def volunteer = new Volunteer()
        userRepository.save(volunteer)

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
        
        and: "an enrollment"
        enrollmentDto = createEnrollmentDto(ENROLLMENT_MOTIVATION_1, NOW)
        enrollment = createEnrollment(activity, volunteer, enrollmentDto)
        enrollmentRepository.save(enrollment)   
    }
    
    def "get enrollment as a member of institution"() {

        given: 'a member'
        demoMemberLogin()

        when:
        def response = webClient.get()
                .uri('/enrollments/' + activity.id)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(enrollmentDto)
                .retrieve()
                .bodyToFlux(EnrollmentDto.class)
                .collectList()
                .block()

        then: "check response"
        response.size() == 1
        response.get(0).motivation == ENROLLMENT_MOTIVATION_1

        cleanup:
        deleteAll()
    }

    def "get enrollment as a member of another institution"() {

        given:
        def otherInstitution = new Institution(INSTITUTION_1_NAME, INSTITUTION_1_EMAIL, INSTITUTION_1_NIF)
        institutionRepository.save(otherInstitution)
        def otherMember = createMember(USER_1_NAME,USER_1_USERNAME,USER_1_PASSWORD,USER_1_EMAIL, AuthUser.Type.NORMAL, otherInstitution, User.State.APPROVED)
        normalUserLogin(USER_1_USERNAME, USER_1_PASSWORD)

        when:
        def response = webClient.get()
                .uri('/enrollments/' + activity.id)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(enrollmentDto)
                .retrieve()
                .bodyToFlux(EnrollmentDto.class)
                .collectList()
                .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        response == null

        cleanup:
        deleteAll()
    }

    def "get enrollment as a volunteer"() {

        given:
        def volunteer = demoVolunteerLogin()

        when:
        def response = webClient.get()
                .uri('/enrollments/' + activity.id)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(enrollmentDto)
                .retrieve()
                .bodyToFlux(EnrollmentDto.class)
                .collectList()
                .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        response == null

        cleanup:
        deleteAll()
    }
}