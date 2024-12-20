package com.g5.cs203proj.security;

import org.springframework.security.config.Customizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.g5.cs203proj.service.PlayerDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    @Autowired
    private PlayerDetailsService playerDetailsService;


    @Autowired
    public SecurityConfig(PlayerDetailsService playerSvc){
        this.playerDetailsService = playerSvc;
    }

    @Bean 
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(playerDetailsService);
        authProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return authProvider;
    }

// JWT
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();

    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((authz) -> authz 
                .requestMatchers(HttpMethod.GET, "/players/admins").hasRole("ADMIN") 
                .requestMatchers(HttpMethod.GET, "/players/users").hasRole("ADMIN") 
                .requestMatchers(HttpMethod.GET, "/players/{username}").authenticated()
                .requestMatchers(HttpMethod.PUT, "/players").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/players/{username}").authenticated()
                .requestMatchers(HttpMethod.POST, "/tournaments").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/tournaments/{id}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/tournaments/{id}/**").hasRole("ADMIN")
                .requestMatchers("/h2-console/**").permitAll()  // Allow H2 console access
                .anyRequest().permitAll()
            )
            .exceptionHandling(customizer -> customizer
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler) 
            )
        .httpBasic(Customizer.withDefaults())
        .csrf(csrf -> csrf.disable())
        .headers(headers -> headers.frameOptions().sameOrigin())  
        .authenticationProvider(authenticationProvider());

        return http.build();
    }

   
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() { 
        return new BCryptPasswordEncoder();
    }
       
}
