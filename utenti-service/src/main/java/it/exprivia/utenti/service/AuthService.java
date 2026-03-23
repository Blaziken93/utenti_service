package it.exprivia.utenti.service;

import it.exprivia.utenti.dto.LoginRequest;
import it.exprivia.utenti.dto.LoginResponse;
import it.exprivia.utenti.dto.RegisterRequest;
import it.exprivia.utenti.dto.UtenteDTO;
import it.exprivia.utenti.entity.RuoloUtente;
import it.exprivia.utenti.entity.Utente;
import it.exprivia.utenti.repository.UtenteRepository;
import it.exprivia.utenti.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UtenteRepository utenteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    /**
     * Registra un nuovo utente.
     * La password viene hashata (cifrata) prima di salvarla nel DB.
     * Non salviamo mai la password in chiaro!
     */
    public UtenteDTO register(RegisterRequest request) {
        if (utenteRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email già registrata");
        }

        Utente utente = new Utente();
        utente.setFullName(request.getFullName());
        utente.setEmail(request.getEmail());
        utente.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        utente.setRuolo(request.getRuolo() != null ? request.getRuolo() : RuoloUtente.USER);

        Utente saved = utenteRepository.save(utente);
        return new UtenteDTO(saved.getId(), saved.getFullName(), saved.getEmail(), saved.getRuolo());
    }

    /**
     * Effettua il login.
     * 1. Cerca l'utente per email
     * 2. Confronta la password fornita con l'hash salvato nel DB
     * 3. Se tutto ok, genera e restituisce un token JWT
     */
    public LoginResponse login(LoginRequest request) {
        Utente utente = utenteRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Credenziali non valide"));

        if (!passwordEncoder.matches(request.getPassword(), utente.getPasswordHash())) {
            throw new IllegalArgumentException("Credenziali non valide");
        }

        String token = jwtUtils.generateToken(utente.getEmail(), utente.getRuolo().name());
        return new LoginResponse(token);
    }
}
