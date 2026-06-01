package com.rinchik.esport.service;

import com.rinchik.esport.exception.SteamAccountNotFoundException;
import com.rinchik.esport.exception.SteamErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import com.rinchik.esport.model.User;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SteamService {
    private final UserService userService;
    @Value("${steam.api.key}")
    private String apiKey;
    private String baseSteam = "https://api.steampowered.com";
    private String urlForCheckingExisting = "/ISteamUser/GetPlayerSummaries/v2/";
    private String urlForGetMinutesInGame = "/IPlayerService/GetOwnedGames/v1/";

    public void connectUserToSteam(User user, String steamId) {
        RestTemplate restTemplate = new RestTemplate();
        String url = UriComponentsBuilder.fromHttpUrl(baseSteam + urlForCheckingExisting)
                .queryParam("key", apiKey)
                .queryParam("steamids", steamId)
                .queryParam("format", "json")
                .toUriString();
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null || !response.containsKey("response"))
            throw new SteamErrorException();
        Map<String, Object> responseData = (Map<String, Object>) response.get("response");
        List<Map<String, Object>> players = (List<Map<String, Object>>) responseData.get("players");
        if (players == null || players.isEmpty())
            throw new SteamAccountNotFoundException(steamId);

        userService.updateSteam(user, steamId);
    }

    public Double getHours(User user) {
        RestTemplate restTemplate = new RestTemplate();
        String url = UriComponentsBuilder.fromHttpUrl(baseSteam + urlForGetMinutesInGame)
                .queryParam("key", apiKey)
                .queryParam("steamid", user.getSteamId())
                .queryParam("include_appinfo", true)
                .queryParam("format", "json")
                .toUriString();
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null || !response.containsKey("response"))
            return 0.0;
        Map<String, Object> responseData = (Map<String, Object>) response.get("response");
        List<Map<String, Object>> games = (List<Map<String, Object>>) responseData.get("games");
        if (games == null || games.isEmpty())
            return 0.0;
        for (Map<String, Object> game : games) {
            String gameName = (String) game.get("name");
            if (gameName != null && gameName.equals("Counter-Strike 2")) {
                long minutes = ((Number) game.getOrDefault("playtime_forever", 0)).longValue();
                double hours = minutes / 60.0;
                return hours;
            }
        }
        return 0.0;
    }
}
