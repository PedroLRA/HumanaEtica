package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/participations")
public class ParticipationController {
    
    @Autowired
    private ParticipationService participationService;

    @GetMapping("/{activityId}")
    @PreAuthorize("hasRole('ROLE_MEMBER') and hasPermission(#activityId, 'ACTIVITY.MEMBER')")
    public List<ParticipationDto>  getActivityParticipations(@PathVariable Integer activityId) {
        return participationService.getParticipationsByActivity(activityId);
    }


    @PostMapping("/{activityId}")
    @PreAuthorize("(hasRole('ROLE_MEMBER'))")
    public ParticipationDto createParticipation(Principal principal,@PathVariable Integer activityId, @Valid @RequestBody ParticipationDto participationDto) {
        int userId = ((AuthUser) ((Authentication) principal).getPrincipal()).getUser().getId();
        return participationService.createParticipation(userId, activityId, participationDto);
    }

}
