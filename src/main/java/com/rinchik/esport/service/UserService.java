package com.rinchik.esport.service;

import com.rinchik.esport.dto.user.UserChangesDto;
import com.rinchik.esport.dto.user.UserDetailsDto;
import com.rinchik.esport.dto.user.UserRegistrationDto;
import com.rinchik.esport.exception.InvalidPasswordException;
import com.rinchik.esport.exception.LoginAlreadyTakenException;
import com.rinchik.esport.exception.TeamNotFoundException;
import com.rinchik.esport.exception.UserNotFoundException;
import com.rinchik.esport.model.User;
import com.rinchik.esport.model.enums.SystemRole;
import com.rinchik.esport.model.enums.TeamRole;
import com.rinchik.esport.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    @Transactional
    public User registerNewUser(UserRegistrationDto dto) {
        if (userRepo.existsByLogin(dto.getLogin())) {
            throw new LoginAlreadyTakenException(dto.getLogin());
        }

        User newUser = new User();
        newUser.setLogin(dto.getLogin());
        newUser.setEmail(dto.getEmail());

        newUser.setPassword(encoder.encode(dto.getPassword()));

        newUser.setRole(SystemRole.ROLE_GUEST);

        return userRepo.save(newUser);
    }

    public User findUserById(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Transactional
    public User updateUser(UserChangesDto dto) {
        User user = userRepo.findById(dto.getId())
                .orElseThrow(() -> new UserNotFoundException(dto.getId()));
        user.setName(dto.getName());
        user.setDescription(dto.getDescription());
        user.setEmail(dto.getEmail());

        if (!userRepo.existsByLogin(dto.getLogin()) ||
                userRepo.existsByLogin(dto.getLogin()) &&
                        (userRepo.findByLogin(dto.getLogin())
                                .orElseThrow(() -> new UserNotFoundException(dto.getId())).getId() == dto.getId()))
            user.setLogin(dto.getLogin());
        else
            throw new LoginAlreadyTakenException(dto.getLogin());
        return user;
    }

    @Transactional
    public void changePassword(Long id, String oldPass, String newPass) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        if (encoder.matches(oldPass, user.getPassword()))
            user.setPassword(encoder.encode(newPass));
        else
            throw new InvalidPasswordException();
    }

    @Transactional
    public User changeSystemRole(Long id, SystemRole newRole) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setRole(newRole);
        return user;
    }

    @Transactional
    public User changeTeamRole(Long id, TeamRole newRole) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        if (user.getTeam() != null)
            user.setRoleInTeam(newRole);
        return user;
    }
}
