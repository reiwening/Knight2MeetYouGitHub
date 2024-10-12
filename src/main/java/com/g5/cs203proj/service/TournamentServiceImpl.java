package com.g5.cs203proj.service;

import com.g5.cs203proj.exception.*;
import com.g5.cs203proj.entity.*;
import com.g5.cs203proj.repository.*;

import jakarta.validation.OverridesAttribute;

import org.hibernate.mapping.Array;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TournamentServiceImpl implements TournamentService {

    @Autowired
    private TournamentRepository tournamentRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private MatchRepository matchRepository;

    //Contructors
    public TournamentServiceImpl(){};
    public TournamentServiceImpl(TournamentRepository tournamentRepository, PlayerRepository playerRepository){
        this.tournamentRepository = tournamentRepository;
        this.playerRepository = playerRepository;
    }


    // Tournament cycle methods
    @Override
    public Tournament createTournament(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }

    @Override
    public Tournament updateTournament(Long tournamentId, Tournament updatedTournament) {
        Tournament existingTournament = getTournamentById(tournamentId);
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
    public Tournament deleteTournament(Long tournamentId) {
        Tournament tournament = getTournamentById(tournamentId);
        tournamentRepository.delete(tournament);
        return tournament;
    }

    @Override
    public Tournament getTournamentById(Long tournamentId) {
        return tournamentRepository.findById(tournamentId)
            .orElseThrow(() -> new TournmentNotFoundException(tournamentId));
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
    public Tournament startOrCancelTournament(Long tournamentId) {
        Tournament tournament = getTournamentById(tournamentId);
        if (tournament.getRegisteredPlayers().size() >= tournament.getMinPlayers()) {
            tournament.setTournamentStatus("In Progress");
        } else {
            tournament.setTournamentStatus("Cancelled");
        }
        return tournamentRepository.save(tournament);
    }

    @Override
    public Map<Long, Integer> getTournamentRankings(Long tournamentId) {
        Tournament tournament = getTournamentById(tournamentId);
        return tournament.getRankings();
    }

    // Player management

    @Override
    public Tournament registerPlayer(Long playerId, Long tournamentId) {
        Tournament tournament = getTournamentById(tournamentId);
        Player player = playerRepository.findById(playerId).orElse(null);
        if (player == null){
            throw new PlayerNotFoundException(playerId);
        }
        if (tournament.getRegisteredPlayers().size() < tournament.getMaxPlayers()) {
            tournament.getRegisteredPlayers().add(player);
            return tournamentRepository.save(tournament);
        } else {
            throw new TournamentFullException(tournamentId);
        }
    }

    @Override
    public Tournament removePlayer(Long playerId, Long tournamentId) {
        Tournament tournament = getTournamentById(tournamentId);
        Player player = playerRepository.findById(playerId).orElse(null);
        if (player == null){
            throw new PlayerNotFoundException(playerId);
        }
        if (tournament.getRegisteredPlayers().remove(player)){
            return tournamentRepository.save(tournament);
        }
        throw new PlayerNotInTournamentException(playerId, tournamentId);
    }

    @Override
    public List<Player> getRegisteredPlayers(Long tournamentId) {
        Tournament tournament = getTournamentById(tournamentId);
        return tournament.getRegisteredPlayers();
    }

    // Match management

    @Override
    public void scheduleMatches(Long tournamentId) {
        Tournament tournament = getTournamentById(tournamentId);
        List<Player> players = tournament.getRegisteredPlayers();
        //not sure how to implement
    }

    @Override
    public List<ArrayList<String>> getTournamentMatchHistory(Long tournamentId) {
        // Extract matches for a tournament
        Tournament tournament = getTournamentById(tournamentId);
        List<Match> matches = tournament.getTournamentMatchHistory();
        
        // Add each match's info to a list of all matches' info
        List<ArrayList<String>> detailedMatchInfo = new ArrayList<ArrayList<String>>();
        ArrayList<String> matchInfo = new ArrayList<String>();
        for (Match m : matches) {
            // Combine each match's info into 1 ArrayList
            String matchId = "" + m.getMatchId();

            Player p1 = m.getPlayer1();
            String p1Name = null;
            if (p1 != null) {
                p1Name = p1.getUsername();
            }

            Player p2 = m.getPlayer2();
            String p2Name = null;
            if (p2 != null) {
                p2Name = p2.getUsername();
            }

            Player winner = m.getWinner();
            String winnerName = null;
            if (winner != null) {
                winnerName = winner.getUsername();
            }

            String eloChange = "" + m.getEloChange();
            String isDraw = "" + m.getDraw();

            matchInfo.add(matchId);
            matchInfo.add(p1Name);
            matchInfo.add(p2Name);
            matchInfo.add(winnerName);
            matchInfo.add(eloChange);
            matchInfo.add(isDraw);
            detailedMatchInfo.add(matchInfo);
        }

        return detailedMatchInfo;
    }

    @Override
    public boolean addTestMatchToTournament(Long tournamentId, Match match) {
        Tournament t = getTournamentById(tournamentId);
        
        // attach this tournament to the match added
        match.setTournament(t);
        matchRepository.save(match);

        // add match to tournament matches
        t.addTestMatch(match);
        tournamentRepository.save(t);
        return true;
    }

    @Override
    public void sendMatchNotification(Long tournamentId, List<Match> matches) {
        /*
        for (Match match : matches){
            match.matchService.sendNotification();
        }
        */
    }

    // Tournament settings methods

    @Override
    public Tournament setTournamentEloRange(Long tournamentId, int minElo, int maxElo) {
        Tournament tournament = getTournamentById(tournamentId);
        tournament.setMinElo(minElo);
        tournament.setMaxElo(maxElo);
        return tournamentRepository.save(tournament);
    }

    @Override
    public Tournament setTournamentStatus(Long tournamentId, String status) {
        //need check status validity?
        Tournament tournament = getTournamentById(tournamentId);
        tournament.setTournamentStatus(status);
        return tournamentRepository.save(tournament);
    }

    @Override
    public Tournament setTournamentStyle(Long tournamentId, String style) {
        //need check style validity?
        Tournament tournament = getTournamentById(tournamentId);
        tournament.setTournamentStyle(style);
        return tournamentRepository.save(tournament);
    }

    @Override
    public Tournament setTournamentPlayerRange(Long tournamentId, int minPlayers, int maxPlayers) {
        Tournament tournament = getTournamentById(tournamentId);
        tournament.setMinPlayers(minPlayers);
        tournament.setMaxPlayers(maxPlayers);
        return tournamentRepository.save(tournament);
    }

    @Override
    public Tournament setTournamentRegistrationCutOff(Long tournamentId, LocalDateTime registrationCutOff) {
        Tournament tournament = getTournamentById(tournamentId);
        tournament.setRegistrationCutOff(registrationCutOff);
        return tournamentRepository.save(tournament);
    }

    @Override
    public Tournament setAdmin(Long tournamentId, Admin newAdmin) {
        Tournament tournament = getTournamentById(tournamentId);
        tournament.setAdmin(newAdmin);
        return tournamentRepository.save(tournament);
    }

    @Override
    public Tournament setName(Long tournamentId, String newTournamentName) {
        Tournament tournament = getTournamentById(tournamentId);
        tournament.setName(newTournamentName);
        return tournamentRepository.save(tournament);
    }
}
