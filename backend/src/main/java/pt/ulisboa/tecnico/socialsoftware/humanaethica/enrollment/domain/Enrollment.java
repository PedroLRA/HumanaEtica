package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.ENROLLMENT_DATE_AFTER_DEADLINE;
import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.MOTIVATION_TOO_SHORT;
import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.VOLUNTEER_HAS_ALREADY_ENROLLED_IN_THIS_ACTIVITY;

import java.time.LocalDateTime;

@Entity
@Table(name = "enrollment")
public class Enrollment {
    // public enum State {REPORTED, APPROVED, SUSPENDED}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String motivation;
    private LocalDateTime enrollmentDateTime;

    // Relations between other entities
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Activity activity;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Volunteer volunteer;

    // Constructors
    public Enrollment() {
    }

    public Enrollment(Activity activity, Volunteer volunteer, EnrollmentDto enrollmentDto) {
        setActivity(activity);
        setVolunteer(volunteer);
        setMotivation(enrollmentDto.getMotivation());
        setEnrollmentDateTime(DateHandler.now());

        verifyInvariants();
    }

    // Getters & Setters
    public Integer getId() {
        return id;
    }

    public String getMotivation() {
        return motivation;
    }

    public void setMotivation(String motivation) {
        this.motivation = motivation;
    }

    public LocalDateTime getEnrollmentDateTime() {
        return enrollmentDateTime;
    }

    public void setEnrollmentDateTime(LocalDateTime enrollmentDateTime) {
        this.enrollmentDateTime = enrollmentDateTime;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
        activity.addEnrollments(this);
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
        volunteer.addEnrollments(this);
    }

    //Invariants
    private void verifyInvariants() {
        motivationHasTenCharacters();
        canEnrollOnlyOnce();
        cannotEnrollAfterDeadline();        
    }

    private void motivationHasTenCharacters() {
        if (this.motivation == null || this.motivation.trim().length() < 10) {
            throw new HEException(MOTIVATION_TOO_SHORT);
        }
    }

    private void canEnrollOnlyOnce() {
        if (this.volunteer.getEnrollments() != null && this.volunteer.getEnrollments().stream()
                .anyMatch(enrollment -> enrollment != this && enrollment.getActivity().equals(this.activity))
            ) {
            throw new HEException(VOLUNTEER_HAS_ALREADY_ENROLLED_IN_THIS_ACTIVITY);
        }
    }

    private void cannotEnrollAfterDeadline() {
        if (this.enrollmentDateTime.isAfter(activity.getApplicationDeadline())) {
            throw new HEException(ENROLLMENT_DATE_AFTER_DEADLINE);
        }
    }
    
}
