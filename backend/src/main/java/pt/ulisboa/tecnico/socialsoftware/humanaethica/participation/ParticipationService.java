package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.ACTIVITY_NOT_FOUND;
import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.USER_NOT_FOUND;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.repository.ActivityRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.repository.ParticipationRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Member;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserRepository;

@Service
public class ParticipationService {

    @Autowired
    ParticipationRepository participationRepository;

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    UserRepository userRepository;


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ParticipationDto> getParticipationsByActivity(Integer activityId) {
        return participationRepository.findAll().stream()
                .filter(participation -> participation.getActivity().getId().equals(activityId))
                .map(participation -> new ParticipationDto(participation))
                .sorted(Comparator.comparingInt(ParticipationDto::getId))
                .toList();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ParticipationDto createParticipation(Integer userId, Integer activityId, ParticipationDto participationDto) {
        if (userId == null)
            throw new HEException(USER_NOT_FOUND);

        if (activityId == null)
            throw new HEException(ACTIVITY_NOT_FOUND);


        /*Volunteer volunteer = (Volunteer) userRepository.findById(participationDto.getVolunteer().getId())
            .orElseThrow(() -> new HEException(USER_NOT_FOUND, participationDto.getVolunteer().getId()));
        */

        Volunteer volunteer = (Volunteer) userRepository.findById(userId)
            .orElseThrow(() -> new HEException(USER_NOT_FOUND, userId));

        
        Activity activity = activityRepository.findById(activityId)
            .orElseThrow(() -> new HEException(ACTIVITY_NOT_FOUND, activityId));
        
            Participation participation = new Participation(activity, volunteer, participationDto);
        
            participationRepository.save(participation);

        return new ParticipationDto(participation);
    }


}
