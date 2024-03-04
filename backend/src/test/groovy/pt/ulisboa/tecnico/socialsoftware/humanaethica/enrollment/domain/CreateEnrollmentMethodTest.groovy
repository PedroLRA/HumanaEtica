package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll

import java.time.LocalDateTime

@DataJpaTest
class CreateEnrollmentMethodTest extends SpockTest {
    Volunteer volunteer = Mock()
    Activity activity = Mock()
    Activity otherActivity = Mock()
    Enrollment otherEnrollment = Mock()
    def enrollmentDto

    def setup() {
        given: "enrollment info"
        enrollmentDto = new EnrollmentDto()
        enrollmentDto.motivation = ENROLLMENT_MOTIVATION_1
    }

    def "Create Enrollment: activity, volunteer, enrollmentDto"() {
        given:
        activity.getApplicationDeadline() >> IN_ONE_DAY

        when:
        def result = new Enrollment(activity, volunteer, enrollmentDto)

        then: "check result"
        result.getActivity() == activity
        result.getVolunteer() == volunteer
        result.getEnrollmentDateTime() >= NOW
        result.getMotivation() == ENROLLMENT_MOTIVATION_1
    }

    @Unroll
    def "create enrollment and violate motivation has ten or more characters"() {
        given:
        enrollmentDto.setMotivation(motivation)

        when:
        def result = new Enrollment(activity, volunteer, enrollmentDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        motivation     || errorMessage
        ""             || ErrorMessage.MOTIVATION_TOO_SHORT
        "short"        || ErrorMessage.MOTIVATION_TOO_SHORT
        null           || ErrorMessage.MOTIVATION_TOO_SHORT
        "           "  || ErrorMessage.MOTIVATION_TOO_SHORT
        " "            || ErrorMessage.MOTIVATION_TOO_SHORT
    }

    @Unroll
    def "create enrollment and violate can enroll only once"() {
        given:
        activity.getApplicationDeadline() >> IN_ONE_DAY
        volunteer.getEnrollments() >> [otherEnrollment]
        otherEnrollment.getActivity() >> activity;

        when:
        def result = new Enrollment(activity, volunteer, enrollmentDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.VOLUNTEER_HAS_ALREADY_ENROLLED_IN_THIS_ACTIVITY
        
    }

    @Unroll
    def "create enrollment and violate invariants enroll after activity deadline: applicationDeadline=#applicationDeadline"() {
        given:
        activity.getApplicationDeadline() >> applicationDeadline

        when:
        def result = new Enrollment(activity, volunteer, enrollmentDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        applicationDeadline     || errorMessage
        TWO_DAYS_AGO            || ErrorMessage.ENROLLMENT_DATE_AFTER_DEADLINE
        ONE_DAY_AGO             || ErrorMessage.ENROLLMENT_DATE_AFTER_DEADLINE
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}