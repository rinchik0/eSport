package com.rinchik.esport.service;

import com.rinchik.esport.exception.UserNotFoundException;
import com.rinchik.esport.model.User;
import com.rinchik.esport.model.enums.Game;
import com.rinchik.esport.model.enums.TeamRole;
import com.rinchik.esport.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final UserRepository userRepo;

    public List<GrantedAuthority> getAuthorities(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
    }

    public List<TeamRole> getRolesByGame(Game game) {
        switch (game) {
            case CS -> {
                return List.of(TeamRole.CS_RIFLER,
                        TeamRole.CS_AWPER,
                        TeamRole.CS_SUPPORT,
                        TeamRole.CS_LURKER,
                        TeamRole.CS_IGL,
                        TeamRole.CS_FRAGGER);
            }
            case DOTA -> {
                return List.of(TeamRole.DOTA_CARRY,
                        TeamRole.DOTA_MID,
                        TeamRole.DOTA_OFFLANE,
                        TeamRole.DOTA_ROAMER,
                        TeamRole.DOTA_HARD_SUPPORT,
                        TeamRole.DOTA_SUPPORT);
            }
            case VALORANT -> {
                return List.of(TeamRole.VAL_CONTROLLER,
                        TeamRole.VAL_FLEX,
                        TeamRole.VAL_DUELIST,
                        TeamRole.VAL_INITIATOR,
                        TeamRole.VAL_SENTINEL);
            }
            case MOBILE_LEGEND -> {
                return List.of(TeamRole.MOLE_MAGE,
                        TeamRole.MOLE_ASSASSIN,
                        TeamRole.MOLE_MARKSMAN,
                        TeamRole.MOLE_FIGHTER,
                        TeamRole.MOLE_SUPPORT,
                        TeamRole.MOLE_TANK);
            }
            default -> { return null; }
        }
    }

    public boolean doesRoleMatchGame(TeamRole role, Game game) {
        return getRolesByGame(game).contains(role);
    }
}
