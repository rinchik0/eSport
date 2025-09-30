package com.rinchik.esport.service;

import com.rinchik.esport.dto.user.UserChangesRequest;
import com.rinchik.esport.dto.user.UserLoginRequest;
import com.rinchik.esport.dto.user.UserRegistrationRequest;
import com.rinchik.esport.exception.InvalidPasswordException;
import com.rinchik.esport.exception.LoginAlreadyTakenException;
import com.rinchik.esport.exception.UserNotFoundException;
import com.rinchik.esport.model.User;
import com.rinchik.esport.model.enums.SystemRole;
import com.rinchik.esport.model.enums.TeamRole;
import com.rinchik.esport.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    @Transactional
    public User registerNewUser(UserRegistrationRequest dto) {
        if (userRepo.existsByLogin(dto.getLogin())) {
            throw new LoginAlreadyTakenException(dto.getLogin());
        }

        User newUser = new User();
        newUser.setLogin(dto.getLogin());
        newUser.setEmail(dto.getEmail());

        newUser.setPassword(encoder.encode(dto.getPassword()));

        newUser.getRoles().add(SystemRole.ROLE_GUEST);

        return userRepo.save(newUser);
    }

    public User loginUser(UserLoginRequest dto) {
        User user = userRepo.findByLogin(dto.getLogin())
                .orElseThrow(() -> new UserNotFoundException(dto.getLogin()));

        if (!encoder.matches(dto.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException();
        }

        return user;
    }

    public User findUserById(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public User findUserByLogin(String login) {
        return userRepo.findByLogin(login)
                .orElseThrow(() -> new UserNotFoundException(login));
    }

    public List<User> findAllUsers() {
        return userRepo.findAll();
    }

    @Transactional
    public User updateUser(Long id, UserChangesRequest dto) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

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
        User user = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        if (encoder.matches(oldPass, user.getPassword()))
            user.setPassword(encoder.encode(newPass));
        else
            throw new InvalidPasswordException();
    }

    @Transactional
    public User addSystemRole(Long id, SystemRole newRole) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.getRoles().add(newRole);
        return user;
    }

    @Transactional
    public User deleteSystemRole(Long id, SystemRole newRole) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.getRoles().remove(newRole);
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

//    public UserDetails toUserDetails(User user) {
//        return org.springframework.security.core.userdetails.User
//                .withUsername(user.getLogin())
//                .password(user.getPassword())
//                .authorities(user.getRole().name())
//                .build();
//    }
}
