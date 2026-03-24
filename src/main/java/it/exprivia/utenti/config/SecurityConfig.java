package it.exprivia.utenti.config;

import it.exprivia.utenti.security.JwtAuthFilter;
import it.exprivia.utenti.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtils jwtUtils;

    static {
        System.out.println("🔒 SecurityConfig caricato!");
    }

    // Il filtro viene creato qui (non è @Component), così Spring Boot non lo registra due volte
    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter(jwtUtils);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            // STATELESS: il server non ricorda le sessioni, ogni richiesta porta il proprio token
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()               // login e register: liberi
                .requestMatchers("/api/utenti").hasRole("ADMIN")           // lista utenti: solo ADMIN
                .requestMatchers("/api/utenti/**").hasRole("ADMIN")        // get/put/delete per id: solo ADMIN
                .requestMatchers("/api/gruppi/**").hasRole("ADMIN")        // gestione gruppi: solo ADMIN
                .anyRequest().authenticated()                              // tutto il resto: serve il token
            )
            // Inserisce il filtro JWT prima del filtro standard di Spring
            .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
