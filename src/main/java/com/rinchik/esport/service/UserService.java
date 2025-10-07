package com.rinchik.esport.service;

import com.rinchik.esport.dto.user.UserChangesRequest;
import com.rinchik.esport.dto.user.UserLoginRequest;
import com.rinchik.esport.dto.user.UserRegistrationRequest;
import com.rinchik.esport.exception.*;
import com.rinchik.esport.model.User;
import com.rinchik.esport.model.enums.SystemRole;
import com.rinchik.esport.model.enums.TeamRole;
import com.rinchik.esport.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final RoleService roleService;

    @Transactional
    public User registerNewUser(UserRegistrationRequest dto) {
        if (userRepo.existsByLogin(dto.getLogin())) {
            throw new LoginAlreadyTakenException(dto.getLogin());
        }

        User newUser = new User();
        newUser.setLogin(dto.getLogin());
        newUser.setEmail(dto.getEmail());

        newUser.setPassword(encoder.encode(dto.getPassword()));

        newUser.setRoles(new HashSet<>(Arrays.asList(SystemRole.ROLE_GUEST)));

        return userRepo.save(newUser);
    }

    public User findUserByLogin(String login) {
        return userRepo.findByLogin(login)
                .orElseThrow(() -> new UserNotFoundException(login));
    }

    public User loginUser(UserLoginRequest dto) {
        User user = findUserByLogin(dto.getLogin());

        if (!encoder.matches(dto.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException();
        }

        return user;
    }

    public User findUserById(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public List<User> findAllUsers() {
        return userRepo.findAll();
    }

    @Transactional
    public User updateUser(Long id, UserChangesRequest dto) {
        User user = findUserById(id);

        if (!dto.getDescription().equals(""))
            user.setDescription(dto.getDescription());

        if (!dto.getEmail().equals(""))
            user.setEmail(dto.getEmail());

        if (!dto.getLogin().equals("")) {
            if (!userRepo.existsByLogin(dto.getLogin()) ||
                    userRepo.existsByLogin(dto.getLogin()) &&
                            (userRepo.findByLogin(dto.getLogin())
                                    .orElseThrow(() -> new UserNotFoundException(id))
                                    .getId().equals(id)))
                user.setLogin(dto.getLogin());
            else
                throw new LoginAlreadyTakenException(dto.getLogin());
        }
        return user;
    }

    @Transactional
    public void changePassword(Long id, String oldPass, String newPass) {
        User user = findUserById(id);
        if (encoder.matches(oldPass, user.getPassword()))
            user.setPassword(encoder.encode(newPass));
        else
            throw new InvalidPasswordException();
    }

    @Transactional
    public User addSystemRole(Long id, SystemRole newRole) {
        User user = findUserById(id);
        user.getRoles().add(newRole);
        return user;
    }

    @Transactional
    public User deleteSystemRole(Long userId, SystemRole role) {
        User user = findUserById(userId);
        if (user.getRoles().contains(role))
            user.getRoles().remove(role);
        return user;
    }

    public boolean isFromOneTeam(Long userId1, Long userId2) {
        return findUserById(userId1).getTeam().equals(findUserById(userId2).getTeam());
    }

    @Transactional
    public void changeTeamRole(Long id, TeamRole newRole) {
        User user = findUserById(id);
        if (user.getTeam() != null)
            user.setRoleInTeam(newRole);
    }

    @Transactional
    public void changeTeamRoleByCaptain(Long userId, TeamRole newRole, Long captainId) {
        if (isFromOneTeam(userId, captainId))
            if (roleService.doesRoleMatchGame(newRole, findUserById(captainId).getTeam().getGame()))
                changeTeamRole(userId, newRole);
            else
                throw new RoleNotMatchesGameException(newRole, findUserById(captainId).getTeam().getGame());
        else
            throw new NotCaptainOfTeamException(captainId, findUserById(userId).getTeam().getId());
    }

    @Transactional
    public void deleteUser(Long id) {
        if (userRepo.existsById(id))
            userRepo.deleteById(id);
        else
            throw new UserNotFoundException(id);
    }

    public User getCurrentUser(UserDetails details) {
        return findUserByLogin(details.getUsername());
    }
}
