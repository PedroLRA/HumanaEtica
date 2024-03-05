package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.ACTIVITY_NOT_FOUND;
import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.USER_NOT_FOUND;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.repository.ActivityRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.repository.EnrollmentRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserRepository;

@Service
public class EnrollmentService {
    @Autowired
    EnrollmentRepository enrollmentRepository;
    @Autowired
    ActivityRepository activityRepository;
    @Autowired
    UserRepository userRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<EnrollmentDto> getEnrollments() {
        return enrollmentRepository.findAll().stream()
                .map(enrollment -> new EnrollmentDto(enrollment))
                .sorted(Comparator.comparingInt(EnrollmentDto::getId))
                .toList();

    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public EnrollmentDto createEnrollment(Integer userId, Integer activityId, EnrollmentDto enrollmentDto) {
        if (userId == null)
            throw new HEException(USER_NOT_FOUND);
        if (activityId == null)
            throw new HEException(ACTIVITY_NOT_FOUND);

        Volunteer volunteer = (Volunteer) userRepository.findById(userId)
                .orElseThrow(() -> new HEException(USER_NOT_FOUND, userId));

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new HEException(ACTIVITY_NOT_FOUND, activityId));

        Enrollment enrollment = new Enrollment(activity, volunteer, enrollmentDto);

        enrollmentRepository.save(enrollment);

        return new EnrollmentDto(enrollment);
    }
}
