package it.exprivia.utenti.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import it.exprivia.utenti.dto.UtenteDTO;
import it.exprivia.utenti.entity.Utente;
import it.exprivia.utenti.repository.UtenteRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UtenteService {

    private final UtenteRepository utenteRepository;

    public List<UtenteDTO> findAll() {
        return utenteRepository.findAll()       //prende gli utente del database
                .stream()                       //li trasforma in un flusso di elementi
                .map(this::toDTO)               // per ogni utente sul nastro, lo trasforma in un utenteDTO usando il metodo toDTO
                .collect(Collectors.toList()); //raccoglie tutti i DTO dal nastro e li rimette in una lista
    }

    private UtenteDTO toDTO(Utente utente) {
        return new UtenteDTO(
                utente.getId(),
                utente.getFullName(),
                utente.getEmail(),
                utente.getRuolo()
        );
    }
}