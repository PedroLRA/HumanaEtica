package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User

@DataJpaTest
class GetParticipationsByActivityServiceTest extends SpockTest{

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
                NOW,IN_ONE_DAY,IN_TWO_DAYS, null)
        activity = new Activity(activityDto, institution, themes)
        and: "a participation"
        def participationDto = createParticipationDto(PARTICIPATION_RATING_1, IN_THREE_DAYS)
        def participation = new Participation(activity, volunteer, participationDto)
        participationRepository.save(participation)
        and: "another activity"
        activityDto.name = ACTIVITY_NAME_2
        otherActivity = new Activity(activityDto, institution, themes)
        and: 'another participation'
        participationDto.rating = PARTICIPATION_RATING_2
        def otherParticipation = new Participation(otherActivity, volunteer, participationDto)
        participationRepository.save(otherParticipation)
    }

    def 'get two participations'() {
        when:
        def result = participationService.getParticipationsByActivity(activity.id)
        def otherResult = participationService.getParticipationsByActivity(otherActivity.id)

        then:
        result.size() == 1
        otherResult.size() == 1
        result.get(0).rating == PARTICIPATION_RATING_1
        otherResult.get(0).rating == PARTICIPATION_RATING_2
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}

}
