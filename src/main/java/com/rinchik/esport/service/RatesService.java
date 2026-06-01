package com.rinchik.esport.service;

import com.rinchik.esport.model.*;
import com.rinchik.esport.repository.RatesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RatesService {
    private final RatesRepository ratesRepo;
    private final UserService userService;
    private final EventService eventService;
    private final TeamService teamService;
    private final AttendanceService attService;
    private final FaceitService faceitService;
    private final SteamService steamService;
    private final Integer updateIntervalInMinutes = 60;

    @Transactional
    private Rates updateRates(Rates rates) {
        if (rates.getUser().getFaceitPlayerId() != null && !rates.getUser().getFaceitPlayerId().equals("")) {
            ArrayList<Double> faceit = faceitService.getParameters(rates.getUser());
            rates.setKD(faceit.get(0));
            rates.setWinRate(faceit.get(1));
            rates.setAverageHeadshots(faceit.get(2));
            // Заглушка
            //rates.setADR(0.0);
            rates.setHoursPlayed(steamService.getHours(rates.getUser()));
        }
        else {
            rates.setKD(0.0);
            rates.setWinRate(0.0);
            rates.setAverageHeadshots(0.0);
            rates.setHoursPlayed(0.0);
        }
        List<Event> tournamentPlayed = eventService.findTournamentsByParticipant(rates.getUser().getId());
        rates.setTournamentPlayed((double) tournamentPlayed.size());
        if (rates.getUser().getTeam() != null) {
            List<Event> allTrainings = eventService.findAllTrainingsByTeam(rates.getUser().getTeam().getId());
            List<TrainingAttendance> attendance = attService.findTrainingAttendanceByUser(rates.getUser().getId());
            int attendedTrainings = 0;
            for (TrainingAttendance a : attendance)
                if (a.isAttended())
                    attendedTrainings++;
            rates.setTrainingAttendance(allTrainings.isEmpty() ? 0.0 : (double) attendedTrainings / allTrainings.size());
        }
        else
            rates.setTrainingAttendance(0.0);
        return rates;
    }

    @Transactional
    public Rates findRatesByUser(Long userId) {
        User user = userService.findUserById(userId);
        Rates rates = ratesRepo.findByUser(user).orElseGet(() -> {
            Rates newRates = new Rates();
            newRates.setUser(user);
            newRates = updateRates(newRates);
            return ratesRepo.save(newRates);
        });
        LocalDateTime now = LocalDateTime.now();
        if (Duration.between(rates.getLastUpdateDate(), now).toMinutes() >= updateIntervalInMinutes)
            rates = updateRates(rates);
        return rates;
    }

    public ArrayList<Double> normalizeWeights(ArrayList<Integer> weights) {
        for (int i = 0; i < weights.size(); i++)
            if (weights.get(i) == null)
                weights.set(i, 5);
        int sum = 0;
        for (Integer w : weights)
            sum += w;
        ArrayList<Double> normal = new ArrayList<>();
        for (Integer w : weights)
            normal.add((double) w / sum);
        return normal;
    }

    private Double getArithmeticMean(ArrayList<Double> values) {
        Double sum = 0.0;
        for (Double v : values)
            sum += v;
        return (double) sum / values.size();
    }

    private Double getStandardDeviation(ArrayList<Double> values, Double mean) {
        Double sum = 0.0;
        for (Double v : values)
            sum += Math.pow(v - mean, 2);
        return Math.sqrt(sum);
    }

    private void addFieldToZScore(Map<Long, Double> userId_score, Map<Long, Double> fields, Double weight) {
        ArrayList<Double> values = new ArrayList<>();
        for (Double value : fields.values())
            if (value != null)
                values.add(value);
        Double mean = getArithmeticMean(values);
        Double deviation = getStandardDeviation(values, mean);

        if (deviation == null || deviation == 0)
            return;

        for (Map.Entry<Long, Double> entry : userId_score.entrySet())
            entry.setValue(entry.getValue() + ((fields.get(entry.getKey()) - mean) / deviation) * weight);
    }

    @Transactional
    private Map<Long, Double> getRates(List<User> users, ArrayList<Double> weights) {
        ArrayList<Rates> rates = new ArrayList<>();
        for (User u : users)
            rates.add(findRatesByUser(u.getId()));

        Map<Long, Double> userId_score = new HashMap<>();
        for (Rates r : rates)
            userId_score.put(r.getUser().getId(), 0.0);

        Map<Long, Double> KDs = new HashMap<>();
        for (Rates r : rates)
            KDs.put(r.getUser().getId(), r.getKD());
        addFieldToZScore(userId_score, KDs, weights.get(0));

        //Map<Long, Double> ADRs = new HashMap<>();
        //for (Rates r : rates)
        //ADRs.put(r.getUser().getId(), r.getADR());
        //addFieldToZScore(userId_score, ADRs, weights.get(1));

        Map<Long, Double> averageHeadshots = new HashMap<>();
        for (Rates r : rates)
            averageHeadshots.put(r.getUser().getId(), r.getAverageHeadshots());
        addFieldToZScore(userId_score, averageHeadshots, weights.get(1));

        Map<Long, Double> winRates = new HashMap<>();
        for (Rates r : rates)
            winRates.put(r.getUser().getId(), r.getWinRate());
        addFieldToZScore(userId_score, winRates, weights.get(2));

        Map<Long, Double> trainingAttendances = new HashMap<>();
        for (Rates r : rates)
            trainingAttendances.put(r.getUser().getId(), r.getTrainingAttendance());
        addFieldToZScore(userId_score, trainingAttendances, weights.get(3));

        Map<Long, Double> tournamentPlayeds = new HashMap<>();
        for (Rates r : rates)
            tournamentPlayeds.put(r.getUser().getId(), r.getTournamentPlayed());
        addFieldToZScore(userId_score, tournamentPlayeds, weights.get(4));

        Map<Long, Double> hoursPlayeds = new HashMap<>();
        for (Rates r : rates)
            hoursPlayeds.put(r.getUser().getId(), r.getHoursPlayed());
        addFieldToZScore(userId_score, hoursPlayeds, weights.get(5));

        return userId_score;
    }

    @Transactional
    public ArrayList<Map.Entry<Long, Double>> getRatingFor(List<User> users, ArrayList<Double> weights) {
        ArrayList<Map.Entry<Long, Double>> result = new ArrayList<>(
                getRates(users, weights).entrySet());
        result.sort(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder()));
        return result;
    }

    @Transactional
    public ArrayList<Map.Entry<Long, Double>> getRatesByTeam(Long teamId, ArrayList<Double> weights) {
        Team team = teamService.findTeamById(teamId);
        return getRatingFor(team.getMembers(), weights);
    }

    @Transactional
    public ArrayList<Map.Entry<Long, Double>> getCommonRates(ArrayList<Double> weights) {
        return getRatingFor(userService.findAllUsers(), weights);
    }

    private Double getTeamRate(Team team, Map<Long, Double> rates) {
        Double rate = 0.0;
        for (User player : team.getMembers())
            rate += rates.get(player.getId());
        return rate;
    }

    @Transactional
    public ArrayList<Map.Entry<Long, Double>> getRatesOfTeams(ArrayList<Double> weights) {
        Map<Long, Double> userRating = getRates(userService.findAllUsers(), weights);
        List<Team> teams = teamService.findAllTeams();
        Map<Long, Double> ratesOfTeams = new HashMap<>();
        for (Team t : teams)
            ratesOfTeams.put(t.getId(), getTeamRate(t, userRating));

        ArrayList<Map.Entry<Long, Double>> result = new ArrayList<>(ratesOfTeams.entrySet());
        result.sort(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder()));
        return result;
    }
}
