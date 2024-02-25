package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    public Participation(ParticipationDto participationDto, Activity activity, Volunteer volunteer) {
       

        //verifyInvariants();
    }

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

    public Activity getActivity(Activity activity){
        return activity;
    }

    public void setActivity(Activity activity){
        this.activity = activity;
    }

    /*public Volunteer getVolunteer(Volunteer volunteer){
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer){
        this.volunteer = volunteer;
    }*/


    /*private void verifyInvariants() {
        -motivação >= 10 caracteres
        -voluntario nunca participou nesta atividade
        -o voluntario esta dentro do prazo de candidatura
        -Apenas um voluntário pode concorrer a uma atividade
        -Apenas o membro da instituição da atividade pode obter a lista das suas candidaturas
    }
*/

}
