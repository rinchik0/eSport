package com.rinchik.esport.controller;

import com.rinchik.esport.dto.user.RateInfoResponse;
import com.rinchik.esport.mapper.UserMapper;
import com.rinchik.esport.model.User;
import com.rinchik.esport.service.RatesService;
import com.rinchik.esport.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rates")
public class RatesController {
    private final RatesService ratesService;
    private final UserService userService;
    private final UserMapper mapper;

    @GetMapping("/{userId}")
    public ResponseEntity<RateInfoResponse> getRatesByUser(@PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(mapper.toRateResponse(ratesService.findRatesByUser(userId)));
    }

    @GetMapping("/team_rate")
    @PreAuthorize("hasRole('ROLE_PLAYER')")
    public ResponseEntity<List<RateInfoResponse>> getRatesByTeam(@AuthenticationPrincipal UserDetails details,
                                                                 @RequestParam(required = false) Integer wKd,
                                                                 @RequestParam(required = false) Integer wAdr,
                                                                 @RequestParam(required = false) Integer wWr,
                                                                 @RequestParam(required = false) Integer wTa,
                                                                 @RequestParam(required = false) Integer wTp,
                                                                 @RequestParam(required = false) Integer wHp) {
        User user = userService.getCurrentUser(details);
        ArrayList<Integer> weights = new ArrayList<>(List.of(wKd, wAdr, wWr, wTa, wTp, wHp));
        ArrayList<Double> normalizedWeights = ratesService.normalizeWeights(weights);
        ArrayList<Map.Entry<Long, Double>> rates = ratesService.getRatesByTeam(user.getTeam().getId(), normalizedWeights);
        List<RateInfoResponse> dtos = new ArrayList<>();
        for (int i = 0; i < rates.size(); i++)
            dtos.add(mapper.toRateResponse(
                    ratesService.findRatesByUser(rates.get(i).getKey()),
                    rates.get(i).getValue(),
                    i + 1
            ));
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    @GetMapping
    public ResponseEntity<List<RateInfoResponse>> getCommonRates(@RequestParam(required = false) Integer wKd,
                                                                 @RequestParam(required = false) Integer wAdr,
                                                                 @RequestParam(required = false) Integer wWr,
                                                                 @RequestParam(required = false) Integer wTa,
                                                                 @RequestParam(required = false) Integer wTp,
                                                                 @RequestParam(required = false) Integer wHp) {
        ArrayList<Integer> weights = new ArrayList<>(List.of(wKd, wAdr, wWr, wTa, wTp, wHp));
        ArrayList<Double> normalizedWeights = ratesService.normalizeWeights(weights);
        ArrayList<Map.Entry<Long, Double>> rates = ratesService.getCommonRates(normalizedWeights);
        List<RateInfoResponse> dtos = new ArrayList<>();
        for (int i = 0; i < rates.size(); i++)
            dtos.add(mapper.toRateResponse(
                    ratesService.findRatesByUser(rates.get(i).getKey()),
                    rates.get(i).getValue(),
                    i + 1
            ));
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }
}
