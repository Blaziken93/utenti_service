package it.exprivia.utenti.service;

import it.exprivia.utenti.dto.UtenteDTO;
import it.exprivia.utenti.entity.Gruppo;
import it.exprivia.utenti.entity.GruppoUtente;
import it.exprivia.utenti.repository.GruppoRepository;
import it.exprivia.utenti.repository.GruppoUtenteRepository;
import it.exprivia.utenti.repository.UtenteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GruppoService {

    private final GruppoRepository gruppoRepository;
    private final GruppoUtenteRepository gruppoUtenteRepository;
    private final UtenteRepository utenteRepository;

    public Gruppo crea(String nome) {
        Gruppo gruppo = new Gruppo();
        gruppo.setNome(nome);
        return gruppoRepository.save(gruppo);
    }

    public List<Gruppo> findAll() {
        return gruppoRepository.findAll();
    }

    public void aggiungiUtente(Long idGruppo, Long idUtente) {
        if (!gruppoRepository.existsById(idGruppo)) {
            throw new EntityNotFoundException("Gruppo non trovato con id: " + idGruppo);
        }
        if (!utenteRepository.existsById(idUtente)) {
            throw new EntityNotFoundException("Utente non trovato con id: " + idUtente);
        }
        if (gruppoUtenteRepository.existsByIdGruppoAndIdUtente(idGruppo, idUtente)) {
            throw new IllegalArgumentException("L'utente è già nel gruppo");
        }

        GruppoUtente gu = new GruppoUtente();
        gu.setIdGruppo(idGruppo);
        gu.setIdUtente(idUtente);
        gruppoUtenteRepository.save(gu);
    }

    public void rimuoviUtente(Long idGruppo, Long idUtente) {
        GruppoUtente gu = gruppoUtenteRepository.findByIdGruppoAndIdUtente(idGruppo, idUtente)
                .orElseThrow(() -> new EntityNotFoundException("L'utente non appartiene a questo gruppo"));
        gruppoUtenteRepository.delete(gu);
    }

    public List<UtenteDTO> getUtentiDelGruppo(Long idGruppo) {
        if (!gruppoRepository.existsById(idGruppo)) {
            throw new EntityNotFoundException("Gruppo non trovato con id: " + idGruppo);
        }

        return gruppoUtenteRepository.findByIdGruppo(idGruppo)
                .stream()
                .map(gu -> utenteRepository.findById(gu.getIdUtente()).orElse(null))
                .filter(u -> u != null)
                .map(u -> new UtenteDTO(u.getId(), u.getFullName(), u.getEmail(), u.getRuolo()))
                .collect(Collectors.toList());
    }
}
