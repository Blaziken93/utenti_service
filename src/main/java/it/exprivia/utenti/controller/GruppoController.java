package it.exprivia.utenti.controller;

import it.exprivia.utenti.dto.UtenteDTO;
import it.exprivia.utenti.entity.Gruppo;
import it.exprivia.utenti.service.GruppoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gruppi")
@RequiredArgsConstructor
public class GruppoController {

    private final GruppoService gruppoService;

    @PostMapping
    public ResponseEntity<Gruppo> crea(@RequestParam String nome) {
        return ResponseEntity.ok(gruppoService.crea(nome));
    }

    @GetMapping
    public ResponseEntity<List<Gruppo>> findAll() {
        return ResponseEntity.ok(gruppoService.findAll());
    }

    @PostMapping("/{idGruppo}/utenti/{idUtente}")
    public ResponseEntity<Void> aggiungiUtente(@PathVariable Long idGruppo, @PathVariable Long idUtente) {
        gruppoService.aggiungiUtente(idGruppo, idUtente);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{idGruppo}/utenti/{idUtente}")
    public ResponseEntity<Void> rimuoviUtente(@PathVariable Long idGruppo, @PathVariable Long idUtente) {
        gruppoService.rimuoviUtente(idGruppo, idUtente);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{idGruppo}/utenti")
    public ResponseEntity<List<UtenteDTO>> getUtenti(@PathVariable Long idGruppo) {
        return ResponseEntity.ok(gruppoService.getUtentiDelGruppo(idGruppo));
    }
}
