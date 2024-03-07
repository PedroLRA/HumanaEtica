package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import spock.lang.Unroll


@DataJpaTest
class CreateEnrollmentServiceTest extends SpockTest {
    public static final String EXIST = "exist"
    public static final String NO_EXIST = "noExist"

    def volunteer
    def activity
    def otherActivity

    def setup() {
        given: "an institution"
        def institution = institutionService.getDemoInstitution()

        and: "a volunteer"
        volunteer = new Volunteer()
        userRepository.save(volunteer)

        and: "an theme"
        def themes = new ArrayList<>()
        themes.add(createTheme(THEME_NAME_1, Theme.State.APPROVED,null))

        and: "a activity"
        def activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,1,ACTIVITY_DESCRIPTION_1,
            IN_ONE_DAY,IN_TWO_DAYS,IN_THREE_DAYS, null)
        activity = createActivity(activityDto, institution, themes)

        and: "another activity"
        def otherActivityDto = createActivityDto(ACTIVITY_NAME_2,ACTIVITY_REGION_2,2,ACTIVITY_DESCRIPTION_2,
            IN_ONE_DAY,IN_TWO_DAYS,IN_THREE_DAYS, null)
        otherActivity = createActivity(otherActivityDto, institution, themes)
    }

    def 'create enrollment'() {
        given:
        def enrollmentDto = createEnrollmentDto(ENROLLMENT_MOTIVATION_1, NOW)

        when:
        def result = enrollmentService.createEnrollment(volunteer.id, activity.id, enrollmentDto)
        
        then:
        result.motivation == ENROLLMENT_MOTIVATION_1
        result.enrollmentDateTime >= DateHandler.toISOString(NOW)
        and: "the enrollment is saved in the database"
        enrollmentRepository.findAll().size() == 1
        and: "the stored data is correct"
        def storedEnrollment = enrollmentRepository.findById(result.id).get()
        storedEnrollment.motivation == ENROLLMENT_MOTIVATION_1
        storedEnrollment.enrollmentDateTime >= NOW
        storedEnrollment.activity.id == activity.id
        storedEnrollment.volunteer.id == volunteer.id
    }

    @Unroll
    def 'invalid arguments: motivation=#motivation | volunteerId=#volunteerId | activityId=#activityId'() {
        given:
        def enrollmentDto = createEnrollmentDto(motivation, NOW)

        when:
        def result = enrollmentService.createEnrollment(getVolunteerId(volunteerId), getActivityId(activityId), enrollmentDto)
        
        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and: "no enrollment is stored in the database"
        enrollmentRepository.findAll().size() == 0

        where:
        motivation              | volunteerId   | activityId    || errorMessage
        // null                    | EXIST         | EXIST         || ErrorMessage.MOTIVATION_TOO_SHORT
        ENROLLMENT_MOTIVATION_1 | null          | EXIST         || ErrorMessage.USER_NOT_FOUND
        ENROLLMENT_MOTIVATION_1 | NO_EXIST      | EXIST         || ErrorMessage.USER_NOT_FOUND
        ENROLLMENT_MOTIVATION_1 | EXIST         | null          || ErrorMessage.ACTIVITY_NOT_FOUND
        ENROLLMENT_MOTIVATION_1 | EXIST         | NO_EXIST      || ErrorMessage.ACTIVITY_NOT_FOUND
    }

    def 'creating two enrollments from the same user in the same activity'() {
        given:
        def enrollmentDto = createEnrollmentDto(ENROLLMENT_MOTIVATION_1, NOW)
        def otherEnrollmentDto = createEnrollmentDto(ENROLLMENT_MOTIVATION_2, NOW)

        when:
        def result = enrollmentService.createEnrollment(volunteer.id, activity.id, enrollmentDto)
        def otherResult = enrollmentService.createEnrollment(volunteer.id, activity.id, otherEnrollmentDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.VOLUNTEER_HAS_ALREADY_ENROLLED_IN_THIS_ACTIVITY
        and: "no enrollment is stored in the database"
        enrollmentRepository.findAll().size() == 1
    }

    def getVolunteerId(volunteerId){
        if (volunteerId == EXIST)
            return volunteer.id
        else if (volunteerId == NO_EXIST)
            return 222
        return null
    }

    def getActivityId(activityId){
        if (activityId == EXIST)
            return activity.id
        else if (activityId == NO_EXIST)
            return 222
        return null
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
