package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll

@DataJpaTest
class CreateParticipationMethodTest extends SpockTest {
    Volunteer volunteer = Mock()
    Activity activity = Mock()
    Activity otherActivity = Mock()
    Participation otherParticipation = Mock()
    def participationDto

def setup() {
        given: "participation info"
        participationDto = new ParticipationDto()
        participationDto.rating = PARTICIPATION_RATING_1
        participationDto.acceptanceDate = NOW
    }


def "Create Participation: activity, volunteer, participationDto"() {
        given:
        activity.getApplicationDeadline() >> TWO_DAYS_AGO
        activity.getParticipationsNumber() >> 5
        activity.getParticipantsNumberLimit() >> 5
        volunteer.getParticipations() >> [otherParticipation]
        otherParticipation.getActivity() >> otherActivity

        when:
        def result = new Participation(activity, volunteer, participationDto)

        then: "check result"
        result.getActivity() == activity
        result.getVolunteer() == volunteer
        result.getAcceptanceDate() >= NOW
        result.getRating() == PARTICIPATION_RATING_1
    }


@Unroll
def "create participation and violate invariant participate before activity deadline: applicationDeadline=#date"() {
        given:
        activity.getApplicationDeadline() >> IN_TWO_DAYS
        activity.getParticipationsNumber() >> 5
        activity.getParticipantsNumberLimit() >> 5
        volunteer.getParticipations() >> [otherParticipation]
        otherParticipation.getActivity() >> otherActivity
        and: "a participation dto"
        participationDto = new ParticipationDto()
        participationDto.setRating(PARTICIPATION_RATING_1)
        participationDto.setAcceptanceDate(DateHandler.toISOString(date))

        when:
        new Participation(activity, volunteer, participationDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        date                    || errorMessage
        NOW                     || ErrorMessage.PARTICIPATION_PLACED_ONLY_AFTER_APPLICATION_PERIOD_IS_OVER
        TWO_DAYS_AGO            || ErrorMessage.PARTICIPATION_PLACED_ONLY_AFTER_APPLICATION_PERIOD_IS_OVER
        ONE_DAY_AGO             || ErrorMessage.PARTICIPATION_PLACED_ONLY_AFTER_APPLICATION_PERIOD_IS_OVER
    }

    @Unroll
    def "create participation and violate can participate only once"() {
        given:
        activity.getApplicationDeadline() >> IN_ONE_DAY
        activity.getParticipationsNumber() >> 5
        activity.getParticipantsNumberLimit() >> 5
        volunteer.getParticipations() >> [otherParticipation]
        otherParticipation.getActivity() >> activity;

        when:
        def result = new Participation(activity, volunteer, participationDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.PARTICIPATION_ALREADY_HAD_THIS_PARTICIPANT

    }

    @Unroll
    def "create participation and violate number of participants limit"() {
        given:
        activity.getParticipationsNumber() >> 5
        activity.getParticipantsNumberLimit() >> limit

        when:
        def result = new Participation(activity, volunteer, participationDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        limit          || errorMessage
        0              || ErrorMessage.PARTICIPATION_LIMIT_FOR_ACTIVITY_REACHED
        4              || ErrorMessage.PARTICIPATION_LIMIT_FOR_ACTIVITY_REACHED
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}

}
