package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

import java.time.LocalDateTime;

@Entity
@Table(name = "assessments")
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String review;

    private LocalDateTime reviewDate;

    @ManyToOne
    private Volunteer volunteer;

    @ManyToOne
    private Institution institution;

    public Assessment() {}

    public Assessment(String review, LocalDateTime reviewDate, Institution institution, Volunteer volunteer) {
        setReview(review);
        setReviewDate(reviewDate);
        setInstitution(institution);
        setVolunteer(volunteer);

        verifyInvariants();
        volunteer.addAssessment(this);
        institution.addAssessment(this);
    }

    public Assessment(AssessmentDto assessmentDto, Institution institution, Volunteer volunteer) {
        setId(assessmentDto.getId());
        setReview(assessmentDto.getReview());
        setReviewDate(DateHandler.toLocalDateTime(assessmentDto.getReviewDate()));
        setInstitution(institution);
        setVolunteer(volunteer);

        verifyInvariants();

        volunteer.addAssessment(this);
        institution.addAssessment(this);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public LocalDateTime getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDateTime reviewDate) {
        this.reviewDate = reviewDate;
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    private void verifyInvariants() {
        checkReviewLength();
        if (this.getInstitution() != null) {
            institutionHasOneActivityCompleted();
            if (this.getVolunteer() != null)
                onlyAssessmentFromVolunteerToInstitution();
        }
    }

    private void checkReviewLength() {
        if (this.review == null) throw new HEException(ErrorMessage.ASSESSMENT_INVALID_REVIEW);
        if (this.review.length() < 10) throw new HEException(ErrorMessage.ASSESSMENT_INVALID_REVIEW);
    }

    private void onlyAssessmentFromVolunteerToInstitution() {
        if (institution.getAssessments().stream().anyMatch(assessment -> assessment.getVolunteer().getId().equals(this.volunteer.getId())))
            throw new HEException(ErrorMessage.ASSESSMENT_VOLUNTEER_ALREADY_ASSESSED_INSTITUTION, this.volunteer.getName(), this.institution.getName());
    }

    private void institutionHasOneActivityCompleted() {
        if (this.institution.getActivities().stream().noneMatch(activity -> activity.getEndingDate().isBefore(LocalDateTime.now())))
            throw new HEException(ErrorMessage.ASSESSMENT_INSTITUTION_NO_ACTIVITIES_COMPLETED,this.institution.getName());
    }
}
