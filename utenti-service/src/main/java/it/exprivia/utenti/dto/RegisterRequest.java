package it.exprivia.utenti.dto;

import it.exprivia.utenti.entity.RuoloUtente;
import lombok.Data;

@Data
public class RegisterRequest {
    private String fullName;
    private String email;
    private String password;
    private RuoloUtente ruolo; // opzionale: se non specificato usa USER
}
