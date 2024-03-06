package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
//import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

@Entity
@Table(name = "participation")
public class Participation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer rating;
    private LocalDateTime acceptanceDate;

    @ManyToOne
    private Activity activity;

    @ManyToOne
    private Volunteer volunteer;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    //private static int instanceCount = 0;

    public Participation(){

    }

    public Participation(Activity activity, Volunteer volunteer, ParticipationDto participationDto) {
        //instanceCount++;
        setActivity(activity);
        setRating(participationDto.getRating());
        setAcceptanceDate(DateHandler.now());
        setVolunteer(volunteer);

        verifyInvariants();
    }

    /*public static int getInstanceCount() {
        return instanceCount;
    }*/

    public Integer getId() {
        return id;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
       this.rating = rating;
    }

    public LocalDateTime getAcceptanceDate() {
        return acceptanceDate;
    }

    public void setAcceptanceDate(LocalDateTime acceptanceDate) {
        this.acceptanceDate = acceptanceDate;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Activity getActivity(){
        return activity;
    }

    public void setActivity(Activity activity){
        this.activity = activity;
        activity.addParticipations(this);

    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
        volunteer.addParticipations(this);
    }



    private void verifyInvariants() {
        
        //-Apenas um membro da instituição pode associar um participante à atividade
        //-Apenas o membro da instituição da atividade pode obter a lista das suas candidaturas
        hasLessParticipantsThanTheLimit();
        volunteerHasNeverParticipated();
        isCreatedAfterApplicationDeadline();
    }


private void hasLessParticipantsThanTheLimit() {
    if (this.activity.getParticipationsNumber() > this.activity.getParticipantsNumberLimit()) {
        throw new HEException(PARTICIPATION_LIMIT_FOR_ACTIVITY_REACHED);
    }
}

private void volunteerHasNeverParticipated() {
    if (this.volunteer.getParticipations().stream()
            .anyMatch(participation -> participation != this && participation.getActivity().equals(this.activity))) {
        throw new HEException(PARTICIPATION_ALREADY_HAD_THIS_PARTICIPANT);
    }
}

private void isCreatedAfterApplicationDeadline() {
    if (this.acceptanceDate.isBefore(this.activity.getApplicationDeadline())) {
        throw new HEException(PARTICIPATION_PLACED_ONLY_AFTER_APPLICATION_PERIOD_IS_OVER);
    }
}

}
