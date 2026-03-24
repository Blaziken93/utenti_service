package it.exprivia.utenti.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.exprivia.utenti.entity.GruppoUtente;

import java.util.List;
import java.util.Optional;

@Repository
public interface GruppoUtenteRepository extends JpaRepository<GruppoUtente, Long>{

    List<GruppoUtente> findByIdGruppo(Long idGruppo);

    Optional<GruppoUtente> findByIdGruppoAndIdUtente(Long idGruppo, Long idUtente);

    boolean existsByIdGruppoAndIdUtente(Long idGruppo, Long idUtente);
}
