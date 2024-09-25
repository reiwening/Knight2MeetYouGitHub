package com.g5.cs203proj.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.g5.cs203proj.entity.Player;
import java.util.Optional;


@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    
    
}
