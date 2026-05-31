package com.rinchik.esport.service;

import com.rinchik.esport.dto.teamrequest.TeamRequestMessageRequest;
import com.rinchik.esport.exception.TeamRequestNotFoundException;
import com.rinchik.esport.exception.UserNotCaptainOfTeamException;
import com.rinchik.esport.exception.UserNotTeamRequestCreatorException;
import com.rinchik.esport.model.Team;
import com.rinchik.esport.model.TeamRequest;
import com.rinchik.esport.model.User;
import com.rinchik.esport.model.enums.TeamRequestStatus;
import com.rinchik.esport.repository.TeamRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamRequestService {
    private final TeamRequestRepository requestRepo;
    private final TeamService teamService;

    @Transactional
    public TeamRequest createNewTeamRequest(TeamRequestMessageRequest dto, User user, Long teamId) {
        Team team = teamService.findTeamById(teamId);
        TeamRequest request = new TeamRequest();
        request.setUser(user);
        request.setTeam(team);
        request.setMessage(dto.getMessage());
        request.setStatus(TeamRequestStatus.AWAITING);
        return requestRepo.save(request);
    }

    public List<TeamRequest> findTeamRequests(Team team) {
        return requestRepo.findByTeam(team);
    }

    public TeamRequest findTeamRequestById(Long requestId) {
        return requestRepo.findById(requestId)
                .orElseThrow(() -> new TeamRequestNotFoundException(requestId));
    }

    @Transactional
    public TeamRequest acceptTeamRequestByCaptain(Long teamRequestId, User captain) {
        TeamRequest request = findTeamRequestById(teamRequestId);
        Team destTeam = request.getTeam();
        Team captainedTeam = captain.getCaptainedTeam();
        if (!destTeam.getId().equals(captainedTeam.getId()))
            throw new UserNotCaptainOfTeamException(captain.getId(), destTeam.getId());
        request.setStatus(TeamRequestStatus.ACCEPTED);
        return request;
    }

    @Transactional
    public TeamRequest declineTeamRequestByCaptain(Long teamRequestId, User captain) {
        TeamRequest request = findTeamRequestById(teamRequestId);
        Team destTeam = request.getTeam();
        Team captainedTeam = captain.getCaptainedTeam();
        if (!destTeam.getId().equals(captainedTeam.getId()))
            throw new UserNotCaptainOfTeamException(captain.getId(), destTeam.getId());
        request.setStatus(TeamRequestStatus.DECLINED);
        return request;
    }

    public List<TeamRequest> findTeamRequestsByUser(User user) {
        return requestRepo.findByUser(user);
    }

    @Transactional
    public void deleteTeamRequestByUser(Long teamRequestId, User user) {
        TeamRequest request = findTeamRequestById(teamRequestId);
        if (!user.getId().equals(request.getUser().getId()))
            throw new UserNotTeamRequestCreatorException(user.getId(), teamRequestId);
        requestRepo.delete(request);
    }
}
