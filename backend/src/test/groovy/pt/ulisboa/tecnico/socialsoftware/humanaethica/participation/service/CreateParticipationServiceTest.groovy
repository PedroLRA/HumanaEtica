package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import spock.lang.Unroll


@DataJpaTest
class CreateParticipationServiceTest extends SpockTest {
    public static final String EXIST = "exist"
    public static final String NO_EXIST = "noExist"

    def member
    def volunteer
    def institution
    def activity
    def theme

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

    /*def 'create participation'() {
        given:
        def participationDto = createParticipationDto(RA, NOW)

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
    }*/


@TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}

}