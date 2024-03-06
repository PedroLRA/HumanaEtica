package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

public class EnrollmentDto {
    private Integer id;
    private String motivation;
    private String enrollmentDateTime;

    // Add ActivityDto and UserDto if needed when working on frontend

    public EnrollmentDto() {
    }

    public EnrollmentDto(Enrollment enrollment) {
        setId(enrollment.getId());
        setMotivation(enrollment.getMotivation());
        setEnrollmentDateTime(DateHandler.toISOString(enrollment.getEnrollmentDateTime()));
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

    public String getEnrollmentDateTime() {
        return enrollmentDateTime;
    }

    public void setEnrollmentDateTime(String enrollmenDateTime) {
        this.enrollmentDateTime = enrollmenDateTime;
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
