package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue(User.UserTypes.VOLUNTEER)
public class Volunteer extends User {


    @OneToMany(mappedBy = "volunteer", orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Assessment> assessments = new ArrayList<>();

    public List<Assessment> getAssessments() {
        return assessments;
    }

    public void setAssessments(List<Assessment> assessments) {
        this.assessments = assessments;
    }

    public void addAssessment(Assessment assessment) { this.assessments.add(assessment); }

    public boolean hasAssessments() { return !this.assessments.isEmpty(); }


    @OneToMany(mappedBy = "volunteer", orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Enrollment> enrollments = new ArrayList<>();

    public Volunteer() {
    }

    public Volunteer(String name, String username, String email, AuthUser.Type type, State state) {
        super(name, username, email, Role.VOLUNTEER, type, state);
    }

    public Volunteer(String name, State state) {
        super(name, Role.VOLUNTEER, state);
    }

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    public void addEnrollments(Enrollment enrollments) {
        this.enrollments.add(enrollments);
    }
}
