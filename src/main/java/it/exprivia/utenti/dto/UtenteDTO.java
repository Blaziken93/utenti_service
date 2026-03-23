package it.exprivia.utenti.dto;

import it.exprivia.utenti.entity.RuoloUtente;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UtenteDTO {

    private Long id;
    private String fullName;
    private String email;
    private RuoloUtente ruolo;
}