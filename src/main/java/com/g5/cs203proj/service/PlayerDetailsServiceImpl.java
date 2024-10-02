package com.g5.cs203proj.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.g5.cs203proj.repository.PlayerRepository;

@Service
public class PlayerDetailsServiceImpl implements UserDetailsService {

    private final PlayerRepository playerRepository;

    public PlayerDetailsServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return playerRepository.findByUsername(username)
                               .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    
}
