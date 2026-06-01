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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamRequestService {
    private final TeamRequestRepository requestRepo;
    private final TeamService teamService;
    private final RatesService ratesService;

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

    public List<TeamRequest> findTeamRequestsByTeam(Team team) {
        return requestRepo.findByTeam(team);
    }

    public List<TeamRequest> findTeamRequestsRanking(Team team, ArrayList<Integer> weights) {
        ArrayList<Double> normalizedWeights = ratesService.normalizeWeights(weights);
        List<TeamRequest> requests = findTeamRequestsByTeam(team);
        if (requests.isEmpty()) {
            return new ArrayList<>();
        }
        Map<Long, TeamRequest> latestRequestByUser = new HashMap<>();
        for (TeamRequest request : requests) {
            Long userId = request.getUser().getId();
            TeamRequest existing = latestRequestByUser.get(userId);
            if (existing == null || request.getCreatedDate().isAfter(existing.getCreatedDate())) {
                latestRequestByUser.put(userId, request);
            }
        }
        List<User> uniqueUsers = new ArrayList<>();
        for (TeamRequest request : latestRequestByUser.values()) {
            uniqueUsers.add(request.getUser());
        }
        ArrayList<Map.Entry<Long, Double>> ranking = ratesService.getRatingFor(uniqueUsers, normalizedWeights);
        Map<Long, Double> zScoreMap = new HashMap<>();
        for (Map.Entry<Long, Double> entry : ranking) {
            zScoreMap.put(entry.getKey(), entry.getValue());
        }
        List<TeamRequest> sortedRequests = new ArrayList<>(latestRequestByUser.values());
        sortedRequests.sort((r1, r2) -> {
            Double z1 = zScoreMap.get(r1.getUser().getId());
            Double z2 = zScoreMap.get(r2.getUser().getId());
            if (z1 == null && z2 == null) return 0;
            if (z1 == null) return 1;
            if (z2 == null) return -1;
            return z2.compareTo(z1);
        });
        return sortedRequests;
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
        request.setRespondedDate(LocalDateTime.now());
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
        request.setRespondedDate(LocalDateTime.now());
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

    public long getResponseTimeByTeam(Long teamId) {
        List<TeamRequest> requests = findTeamRequestsByTeam(teamService.findTeamById(teamId));
        if (requests == null || requests.isEmpty())
            return 0;
        long sum = 0;
        for (TeamRequest r : requests)
            sum += Duration.between(r.getCreatedDate(), r.getRespondedDate()).toMinutes();
        return sum / requests.size();
    }
}
