package it.exprivia.utenti.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.exprivia.utenti.dto.UtenteDTO;
import it.exprivia.utenti.service.UtenteService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/utenti")
@RequiredArgsConstructor
public class UtenteController {

    private final UtenteService utenteService;

    @GetMapping
    public ResponseEntity<List<UtenteDTO>> findAll() {
        return ResponseEntity.ok(utenteService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UtenteDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(utenteService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UtenteDTO> update(@PathVariable Long id, @Valid @RequestBody UtenteDTO dto) {
        return ResponseEntity.ok(utenteService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        utenteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}