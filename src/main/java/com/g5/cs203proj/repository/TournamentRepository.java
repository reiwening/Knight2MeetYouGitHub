package com.g5.cs203proj.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.g5.cs203proj.entity.Tournament;
import java.util.List;



@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    List<Tournament> findByTournamentStatus(String tournamentStatus);
    
}