package com.rinchik.esport.mapper;

import com.rinchik.esport.dto.user.LoginResponse;
import com.rinchik.esport.dto.user.RateInfoResponse;
import com.rinchik.esport.dto.user.UserInfoResponse;
import com.rinchik.esport.model.Rates;
import com.rinchik.esport.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserInfoResponse toUserInfoResponse(User user) {
        UserInfoResponse dto = new UserInfoResponse();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setBio(user.getBio());
        dto.setRoles(user.getRoles());
        dto.setEmail(user.getEmail());
        if (user.getTeam() != null) {
            dto.setTeamRole(user.getRoleInTeam());
            dto.setTeamId(user.getTeam().getId());
            dto.setTeamName(user.getTeam().getName());
        }
        else {
            dto.setTeamRole(null);
            dto.setTeamId(null);
            dto.setTeamName(null);
        }
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setLastOnline(user.getLastOnline());
        dto.setJoinDate(user.getJoinDate());
        return dto;
    }

    public LoginResponse toLoginResponse(User user, String token) {
        UserInfoResponse info = toUserInfoResponse(user);
        LoginResponse dto = new LoginResponse();
        dto.setUser(info);
        dto.setToken(token);
        return dto;
    }

    public RateInfoResponse toRateResponse(Rates rates, Double zScore, int rankPosition) {
        RateInfoResponse dto = new RateInfoResponse();
        dto.setUserId(rates.getUser().getId());
        dto.setUserName(rates.getUser().getUsername());
        dto.setKd(rates.getKD());
        dto.setAdr(rates.getADR());
        dto.setWinRate(rates.getWinRate());
        dto.setTournamentPlayed(rates.getTournamentPlayed());
        dto.setTrainingAttendance(rates.getTrainingAttendance());
        dto.setHoursPlayed(rates.getHoursPlayed());
        dto.setZScore(zScore);
        dto.setRankPosition(rankPosition);
        return dto;
    }

    public RateInfoResponse toRateResponse(Rates rates) {
        RateInfoResponse dto = new RateInfoResponse();
        dto.setUserId(rates.getUser().getId());
        dto.setUserName(rates.getUser().getUsername());
        dto.setKd(rates.getKD());
        dto.setAdr(rates.getADR());
        dto.setWinRate(rates.getWinRate());
        dto.setTournamentPlayed(rates.getTournamentPlayed());
        dto.setTrainingAttendance(rates.getTrainingAttendance());
        dto.setHoursPlayed(rates.getHoursPlayed());
        dto.setZScore(0.0);
        dto.setRankPosition(0);
        return dto;
    }
}
