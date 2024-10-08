package com.g5.cs203proj.service;

import com.g5.cs203proj.entity.*;
import com.g5.cs203proj.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TournamentServiceImpl implements TournamentService {

    @Autowired
    private TournamentRepository tournamentRepository;

    //Contructors
    public TournamentServiceImpl(){};
    public TournamentServiceImpl(TournamentRepository tournamentRepository){
        this.tournamentRepository = tournamentRepository;
    }


    // Tournament cycle methods
    @Override
    public Tournament createTournament(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }

    @Override
    public Tournament updateTournament(Tournament existingTournament, Tournament updatedTournament) {
        existingTournament.setName(updatedTournament.getName());
        existingTournament.setTournamentStatus(updatedTournament.getTournamentStatus());
        existingTournament.setTournamentStyle(updatedTournament.getTournamentStyle());
        existingTournament.setMaxPlayers(updatedTournament.getMaxPlayers());
        existingTournament.setMinPlayers(updatedTournament.getMinPlayers());
        existingTournament.setMinElo(updatedTournament.getMinElo());
        existingTournament.setMaxElo(updatedTournament.getMaxElo());
        existingTournament.setRegistrationCutOff(updatedTournament.getRegistrationCutOff());
        return tournamentRepository.save(existingTournament);
    }

    @Override
    public Tournament deleteTournament(Tournament tournament) {
        tournamentRepository.delete(tournament);
        return tournament;
    }

    @Override
    public Tournament getTournamentById(Long tournamentId) {
        return tournamentRepository.findById(tournamentId).orElse(null);
    }

    @Override
    public List<Tournament> getAllTournaments() {
        return tournamentRepository.findAll();
    }

    @Override
    public List<Tournament> getAllRegisterableTournaments() {
        return tournamentRepository.findByTournamentStatus("Registration");
    }

    @Override
    public Tournament startOrCancelTournament(Tournament tournament) {
        if (tournament.getRegisteredPlayers().size() >= tournament.getMinPlayers()) {
            tournament.setTournamentStatus("In Progress");
        } else {
            tournament.setTournamentStatus("Cancelled");
        }
        return tournamentRepository.save(tournament);
    }

    @Override
    public Map<Long, Integer> getTournamentRankings(Tournament tournament) {
        return tournament.getRankings();
    }

    // Player management
    @Override
    public Tournament registerPlayer(Player player, Tournament tournament) {
        tournament.getRegisteredPlayers().add(player);
        return tournamentRepository.save(tournament);
    }

    @Override
    public Tournament removePlayer(Player player, Tournament tournament) {
        tournament.getRegisteredPlayers().remove(player);
        return tournamentRepository.save(tournament);
    }

    @Override
    public List<Player> getRegisteredPlayers(Tournament tournament) {
        return tournament.getRegisteredPlayers();
    }

    // Match management

    @Override
    public void scheduleMatches(Tournament tournament) {
        List<Player> players = tournament.getRegisteredPlayers();
        //not sure how to implement
    }

    @Override
    public List<Match> getTournamentMatchHistory(Tournament tournament) {
        return tournament.getTournamentMatchHistory();
    }

    @Override
    public void sendMatchNotification(Tournament tournament, List<Match> matches) {
        /*
        for (Match match : matches){
            match.matchService.sendNotification();
        }
        */
    }

    // Tournament settings methods

    @Override
    public Tournament setTournamentEloRange(Tournament tournament, int minElo, int maxElo) {
        tournament.setMinElo(minElo);
        tournament.setMaxElo(maxElo);
        return tournamentRepository.save(tournament);
    }

    @Override
    public Tournament setTournamentStatus(Tournament tournament, String status) {
        tournament.setTournamentStatus(status);
        return tournamentRepository.save(tournament);
    }

    @Override
    public Tournament setTournamentStyle(Tournament tournament, String style) {
        tournament.setTournamentStyle(style);
        return tournamentRepository.save(tournament);
    }

    @Override
    public Tournament setTournamentPlayerRange(Tournament tournament, int minPlayers, int maxPlayers) {
        tournament.setMinPlayers(minPlayers);
        tournament.setMaxPlayers(maxPlayers);
        return tournamentRepository.save(tournament);
    }

    @Override
    public Tournament setTournamentRegistrationCutOff(Tournament tournament, LocalDateTime registrationCutOff) {
        tournament.setRegistrationCutOff(registrationCutOff);
        return tournamentRepository.save(tournament);
    }

    @Override
    public Tournament setAdmin(Tournament tournament, Admin newAdmin) {
        tournament.setAdmin(newAdmin);
        return tournamentRepository.save(tournament);
    }

    @Override
    public Tournament setName(Tournament tournament, String newTournamentName) {
        tournament.setName(newTournamentName);
        return tournamentRepository.save(tournament);
    }
}
