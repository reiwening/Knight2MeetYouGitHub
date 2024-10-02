package com.g5.cs203proj.security;

import org.springframework.security.config.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.g5.cs203proj.service.PlayerDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private PlayerDetailsServiceImpl playerDetailsServiceImpl;

    public SecurityConfig(PlayerDetailsServiceImpl playerSvc){
        this.playerDetailsServiceImpl = playerSvc;
    }

    @Bean 
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(playerDetailsServiceImpl);
        authProvider.setPasswordEncoder(encoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((authz) -> authz 
                .requestMatchers(HttpMethod.POST, "/players").permitAll()
                .requestMatchers("/h2-console/**").permitAll()  // Allow H2 console access
                .anyRequest().authenticated()
            )
        .httpBasic(Customizer.withDefaults())
        .csrf(csrf -> csrf.disable())
        .headers(headers -> headers.frameOptions().sameOrigin())  // Allow frames from the same origin for H2 console
        .authenticationProvider(authenticationProvider());

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder encoder() {
        // auto-generate a random salt internally
        return new BCryptPasswordEncoder();
    }
       
}