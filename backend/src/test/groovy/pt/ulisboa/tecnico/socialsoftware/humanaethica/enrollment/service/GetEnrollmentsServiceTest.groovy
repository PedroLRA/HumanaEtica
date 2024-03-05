package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User

@DataJpaTest
class GetEnrollmentsServiceTest extends SpockTest {

    def member
    def institution

    def setup() {
        
        given: "a member"
        member = authUserService.loginDemoMemberAuth().getUser()
        and: "an institution"
        institution = institutionService.getDemoInstitution()
        and: "a volunteer"
        def volunteer = createVolunteer(USER_1_NAME, USER_1_USERNAME, USER_1_PASSWORD, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)
        and: "a theme"
        def themes = new ArrayList<>()
        themes.add(createTheme(THEME_NAME_1, Theme.State.APPROVED,null))
        and: "an activity"
        def activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,1,ACTIVITY_DESCRIPTION_1,
            IN_ONE_DAY,IN_TWO_DAYS,IN_THREE_DAYS, null)
        def activity = new Activity(activityDto, institution, themes)
        and: "an enrollment"
        def enrollmentDto = createEnrollmentDto(ENROLLMENT_MOTIVATION_1, NOW)
        def enrollment = new Enrollment(activity, volunteer, enrollmentDto)
        enrollmentRepository.save(enrollment)
        and: "another activity"
        activityDto.name = ACTIVITY_NAME_2
        activity = new Activity(activityDto, institution, themes)
        and: 'another enrollment'
        enrollmentDto.motivation = ENROLLMENT_MOTIVATION_2
        enrollment = new Enrollment(activity, volunteer, enrollmentDto)
        enrollmentRepository.save(enrollment)     
    }

    def 'get two enrollments'() {
        when:
        def result = enrollmentService.getEnrollments()

        then:
        result.size() == 2
        result.get(0).motivation == ENROLLMENT_MOTIVATION_1
        result.get(1).motivation == ENROLLMENT_MOTIVATION_2
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
