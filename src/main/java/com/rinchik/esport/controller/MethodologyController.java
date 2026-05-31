package com.rinchik.esport.controller;

import com.rinchik.esport.dto.methodology.MethodologyContentResponse;
import com.rinchik.esport.dto.methodology.MethodologyEditRequest;
import com.rinchik.esport.dto.methodology.MethodologyInfoResponse;
import com.rinchik.esport.mapper.MethodologyMapper;
import com.rinchik.esport.model.Methodology;
import com.rinchik.esport.model.User;
import com.rinchik.esport.model.enums.SystemRole;
import com.rinchik.esport.service.MethodologyService;
import com.rinchik.esport.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/methodologies")
public class MethodologyController {
    private final MethodologyService methodologyService;
    private final UserService userService;
    private final MethodologyMapper mapper;

    @GetMapping("/all_available")
    public ResponseEntity<List<MethodologyInfoResponse>> getTeamMethodologies(@AuthenticationPrincipal UserDetails details) {
        List<MethodologyInfoResponse> dtos = new ArrayList<>();
        if (userService.getCurrentUser(details).getTeam() != null)
            for (var e : methodologyService.findMethodologiesByTeam(userService.getCurrentUser(details).getTeam().getId()))
                dtos.add(mapper.toMethodologyInfoResponse(e));
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    @GetMapping("/{methodologyId}")
    public ResponseEntity<MethodologyContentResponse> getMethodologyById(@AuthenticationPrincipal UserDetails details,
                                                                         @PathVariable Long methodologyId) {
        User user = userService.getCurrentUser(details);
        Methodology methodology;
        if (user.getRoles().contains(SystemRole.ROLE_ADMIN))
            methodology = methodologyService.findMethodologyById(methodologyId);
        else
            methodology = methodologyService.findMethodologyByIdIfAvailable(methodologyId, user.getId());
        return ResponseEntity.status(HttpStatus.OK).body(mapper.toMethodologyContentResponse(methodology));
    }

    @PutMapping("/{methodologyId}")
    public ResponseEntity<MethodologyContentResponse> updateMethodologyByAuthor(@AuthenticationPrincipal UserDetails details,
                                                                                @PathVariable Long methodologyId,
                                                                                @Valid @RequestBody MethodologyEditRequest dto) {
        User user = userService.getCurrentUser(details);
        Methodology methodology = methodologyService.updateMethodologyByAuthor(dto, methodologyId, user.getId());
        return ResponseEntity.status(HttpStatus.OK).body(mapper.toMethodologyContentResponse(methodology));
    }

    @PostMapping("/new")
    @PreAuthorize("hasRole('ROLE_CAPTAIN')")
    public ResponseEntity<MethodologyContentResponse> createNewTeamMethodology(@AuthenticationPrincipal UserDetails details,
                                                                               @Valid @RequestBody MethodologyEditRequest dto) {
        User user = userService.getCurrentUser(details);
        Methodology methodology = methodologyService.createNewMethodology(user.getId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toMethodologyContentResponse(methodology));
    }

    @DeleteMapping("/{methodologyId}")
    @PreAuthorize("hasAnyRole('ROLE_CAPTAIN', 'ROLE_ADMIN')")
    public ResponseEntity<Void> deleteTeamMethodology(@AuthenticationPrincipal UserDetails details,
                                                      @PathVariable Long methodologyId) {
        User user = userService.getCurrentUser(details);
        if (user.getRoles().contains(SystemRole.ROLE_ADMIN))
            methodologyService.deleteMethodology(methodologyId);
        else
            methodologyService.deleteMethodologyByCaptain(methodologyId, user.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}