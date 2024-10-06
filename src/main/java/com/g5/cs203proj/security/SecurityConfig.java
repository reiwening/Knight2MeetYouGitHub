package com.g5.cs203proj.security;

import org.springframework.security.config.Customizer;
import org.springframework.beans.factory.annotation.Autowired;
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
// this.accessDeniedHandler = accessDeniedHandler;
// this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
    }

    @Bean 
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(playerDetailsService);
        authProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((authz) -> authz 
                .requestMatchers(HttpMethod.GET, "/players/admins").hasRole("ADMIN") // spring convention is "ADMIN" , not "ROLE_ADMIN"
                .requestMatchers(HttpMethod.GET, "/players/users").hasRole("ADMIN") 
                .requestMatchers(HttpMethod.GET, "/players/{username}").authenticated()
                .requestMatchers(HttpMethod.PUT, "/players").authenticated()
                // .requestMatchers(HttpMethod.POST, "/players").permitAll()
                // .requestMatchers(HttpMethod.GET, "/players").permitAll()
                .requestMatchers("/h2-console/**").permitAll()  // Allow H2 console access
                .anyRequest().permitAll()
            )
            .exceptionHandling(customizer -> customizer
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler) 
            )
        .httpBasic(Customizer.withDefaults())
        .csrf(csrf -> csrf.disable())
        .headers(headers -> headers.frameOptions().sameOrigin())  // Allow frames from the same origin for H2 console
        .authenticationProvider(authenticationProvider());

        return http.build();
    }

   
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() { // use the default no-argument constructor that helps to generate a random salt and hash password
        return new BCryptPasswordEncoder();
    }
       
}
