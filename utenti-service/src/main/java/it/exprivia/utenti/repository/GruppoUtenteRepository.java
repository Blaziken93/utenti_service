package it.exprivia.utenti.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.exprivia.utenti.entity.GruppoUtente;


@Repository
public interface GruppoUtenteRepository extends JpaRepository<GruppoUtente, Long>{
    
}
