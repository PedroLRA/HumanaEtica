package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto;

import java.time.LocalDateTime;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto;

import java.util.List;

public class EnrollmentDto {
    private Integer id;
    private String motivation;
    private LocalDateTime enrollmentDateTime;
    private ActivityDto activity;

    // Needs add volunteer ???

    public EnrollmentDto() {
    }

    public EnrollmentDto(Enrollment enrollment) {
        setId(enrollment.getId());
        setMotivation(enrollment.getMotivation());
        setEnrollmentDateTime(enrollment.getEnrollmentDateTime());

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public void setEnrollmentDateTime(LocalDateTime enrollmenDateTime) {
        this.enrollmentDateTime = enrollmenDateTime;
    }

    public ActivityDto getActivity() {
        return activity;
    }

    public void setActivity(ActivityDto activity) {
        this.activity = activity;
    }

    // @Override
    // public String toString() {
    // return "EnrollmentDto{" +
    // "id=" + id +
    // ", motivation='" + motivation + '\'' +
    // ", enrollmentDateTime='" + enrollmentDateTime + '\'' +
    // ", activity='" + activity + '\'' +
    // ", volunteer='" + volunteer +
    // '}';
    // }
}
