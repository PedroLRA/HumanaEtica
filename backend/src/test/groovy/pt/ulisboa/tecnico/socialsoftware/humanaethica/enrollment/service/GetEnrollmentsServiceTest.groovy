package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User

@DataJpaTest
class GetEnrollmentsServiceTest extends SpockTest {

    def activity
    def otherActivity

    def setup() {
        
        given: "a member"
        def member = authUserService.loginDemoMemberAuth().getUser()
        and: "an institution"
        def institution = institutionService.getDemoInstitution()
        and: "a volunteer"
        def volunteer = new Volunteer()
        and: "a theme"
        def themes = new ArrayList<>()
        themes.add(createTheme(THEME_NAME_1, Theme.State.APPROVED,null))
        and: "an activity"
        def activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,1,ACTIVITY_DESCRIPTION_1,
            IN_ONE_DAY,IN_TWO_DAYS,IN_THREE_DAYS, null)
        activity = new Activity(activityDto, institution, themes)
        and: "an enrollment"
        def enrollmentDto = createEnrollmentDto(ENROLLMENT_MOTIVATION_1, NOW)
        def enrollment = new Enrollment(activity, volunteer, enrollmentDto)
        enrollmentRepository.save(enrollment)
        and: "another activity"
        activityDto.name = ACTIVITY_NAME_2
        otherActivity = new Activity(activityDto, institution, themes)
        and: 'another enrollment'
        enrollmentDto.motivation = ENROLLMENT_MOTIVATION_2
        def otherEnrollment = new Enrollment(otherActivity, volunteer, enrollmentDto)
        enrollmentRepository.save(otherEnrollment)     
    }

    def 'get two enrollments'() {
        when:
        def result = enrollmentService.getEnrollmentsByActivity(activity.id)
        def otherResult = enrollmentService.getEnrollmentsByActivity(otherActivity.id)

        then:
        result.size() == 1
        otherResult.size() == 1
        result.get(0).motivation == ENROLLMENT_MOTIVATION_1
        otherResult.get(0).motivation == ENROLLMENT_MOTIVATION_2
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
