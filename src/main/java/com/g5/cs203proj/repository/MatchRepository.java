package com.g5.cs203proj.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.fasterxml.jackson.databind.deser.DataFormatReaders.Match;
import com.g5.cs203proj.entity.Match;
import java.util.Optional;


@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    
    
}

