package it.exprivia.utenti.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.exprivia.utenti.entity.Gruppo;


@Repository
public interface GruppoRepository extends JpaRepository<Gruppo, Long> {
    
}
