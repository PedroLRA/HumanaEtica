package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;

import java.util.ArrayList;
import java.util.List;

public class VolunteerDto extends UserDto {

    public List<AssessmentDto> assessmentDto;

    public VolunteerDto() {}

    public VolunteerDto(Volunteer volunteer) {
        this(volunteer, true);
    }

    public VolunteerDto(Volunteer volunteer, boolean deepCopyAssessments) {
        super(volunteer);
        if(deepCopyAssessments && volunteer.hasAssessments())
            this.assessmentDto = volunteer.getAssessments().stream()
                    .map(assessment -> new AssessmentDto(assessment, false, true))
                    .toList();
    }

    public List<AssessmentDto> getAssessments() {
        return assessmentDto;
    }

    public void setAssessments(List<AssessmentDto> assessmentDto) {
        this.assessmentDto = assessmentDto;
    }

    public boolean hasAssessments() { return this.assessmentDto != null && !this.assessmentDto.isEmpty(); }
}
