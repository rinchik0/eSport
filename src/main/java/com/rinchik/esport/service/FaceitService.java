package com.rinchik.esport.service;

import com.rinchik.esport.dto.faceit.FaceitConnectRequest;
import com.rinchik.esport.exception.FaceitAccountNotFoundException;
import com.rinchik.esport.exception.FaceitErrorException;
import com.rinchik.esport.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FaceitService {
    private final UserService userService;
    private final SteamService steamService;
    @Value("${faceit.api.key}")
    private String apiKey;
    private String baseFaceit = "https://open.faceit.com/data/v4/players";
    private String csStats = "/stats/cs2";


    public void connectUserToFaceit(User user, FaceitConnectRequest dto) {
        RestTemplate restTemplate = new RestTemplate();
        String url = UriComponentsBuilder.fromHttpUrl(baseFaceit)
                .queryParam("nickname", dto.getFaceitNickname())
                .toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
        if (response.getStatusCode() == HttpStatus.NOT_FOUND)
            throw new FaceitAccountNotFoundException(dto.getFaceitNickname());
        if (response.getStatusCode() != HttpStatus.OK)
            throw new FaceitErrorException();

        steamService.connectUserToSteam(user, (String) response.getBody().get("steam_id_64"));

        userService.updateFaceit(user, dto.getFaceitNickname(), (String) response.getBody().get("player_id"));
    }

    public ArrayList<Double> getParameters(User user) {
        RestTemplate restTemplate = new RestTemplate();
        String url = UriComponentsBuilder.fromHttpUrl(baseFaceit + "/" + user.getFaceitPlayerId() + csStats).toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
        if (response.getStatusCode() == HttpStatus.NOT_FOUND)
            throw new FaceitAccountNotFoundException(user.getId());
        if (response.getStatusCode() != HttpStatus.OK)
            throw new FaceitErrorException();

        Map<String, Object> lifetime = (Map<String, Object>) response.getBody().get("lifetime");
        ArrayList<Double> kdWinrateHeadshots = new ArrayList<>();
        kdWinrateHeadshots.add(Double.parseDouble((String) lifetime.get("K/D Ratio")));
        //ADR?
        kdWinrateHeadshots.add(Double.parseDouble((String) lifetime.get("Win Rate %")));
        kdWinrateHeadshots.add(Double.parseDouble((String) lifetime.get("Average Headshots %")));
        return kdWinrateHeadshots;
    }
}
