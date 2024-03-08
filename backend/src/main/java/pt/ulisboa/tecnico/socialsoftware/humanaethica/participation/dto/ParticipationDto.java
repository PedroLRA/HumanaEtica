package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation;
//import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
//import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto;
//import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

//import java.time.LocalDateTime;
//import java.util.List;


public class ParticipationDto {
    private Integer id;
    private Integer rating;
    private String acceptanceDate;
    private String creationDate;
    private ActivityDto activityDto;
    //private Volunteer volunteer;

    public ParticipationDto() {

    }

    public ParticipationDto(Participation participation){
        setId(participation.getId());
        setActivity(activityDto);
        setRating(participation.getRating());
        setAcceptanceDate(DateHandler.toISOString(participation.getAcceptanceDate()));
        setCreationDate(DateHandler.toISOString(participation.getCreationDate()));
        //setVolunteer(participation.getVolunteer());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
       this.rating = rating;
    }   

    public String getAcceptanceDate() {
        return acceptanceDate;
    }

    public void setAcceptanceDate(String acceptanceDate) {
        this.acceptanceDate = acceptanceDate;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public ActivityDto getActivity() {
        return activityDto;
    }

    public void setActivity(ActivityDto activityDto) {
        this.activityDto = activityDto;
    }

    public void setParticipantsNumberLimit(Integer participantsNumberLimit) {
        this.activityDto.setParticipantsNumberLimit(participantsNumberLimit);
    }

    /*public void setVolunteer(Volunteer volunteer){
        this.volunteer = volunteer;
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }*/

    @Override
    public String toString() {
        return "ActivityDto{" +
                "id=" + id +
                ", rating='" + rating + '\'' +
                ", acceptanceDate='" + acceptanceDate +
                '}';
    }

}
