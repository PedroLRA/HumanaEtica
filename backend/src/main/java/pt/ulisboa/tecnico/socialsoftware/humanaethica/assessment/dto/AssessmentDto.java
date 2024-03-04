package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.VolunteerDto;

import java.time.LocalDateTime;

public class AssessmentDto {

    private Integer id;

    private String review;

    private String reviewDate;

    private VolunteerDto volunteerDto;

    private InstitutionDto institutionDto;

    public AssessmentDto() {}

    public AssessmentDto(Assessment assessment) {
        this(assessment, true, true);

    }

    public AssessmentDto(Assessment assessment, boolean deepCopyVolunteer, boolean deepCopyInstitution) {
        setId(assessment.getId());
        setReview(assessment.getReview());
        setReviewDate(assessment.getReviewDate().toString());

        if(deepCopyVolunteer) {
            setVolunteer(new VolunteerDto(assessment.getVolunteer(), false));
        }

        if(deepCopyInstitution) {
            setInstitution(new InstitutionDto(assessment.getInstitution(), false, false, false));
        }

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

    public String getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(String reviewDate) {
        this.reviewDate = reviewDate;
    }

    public VolunteerDto getVolunteer() {
        return volunteerDto;
    }

    public void setVolunteer(VolunteerDto volunteer) {
        this.volunteerDto = volunteer;
    }

    public InstitutionDto getInstitution() {
        return institutionDto;
    }

    public void setInstitution(InstitutionDto institutionDto) {
        this.institutionDto = institutionDto;
    }

    @Override
    public String toString() {
        return "AssessmentDto{" +
                "id=" + id +
                ", review='" + review + '\'' +
                ", reviewDate=" + reviewDate +
                ", volunteerDto=" + volunteerDto +
                ", institutionDto=" + institutionDto +
                '}';
    }
}
