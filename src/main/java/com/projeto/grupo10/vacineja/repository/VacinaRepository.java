package com.projeto.grupo10.vacineja.repository;

import com.projeto.grupo10.vacineja.model.vacina.Vacina;
import org.springframework.data.jpa.repository.JpaRepository;


public interface VacinaRepository extends JpaRepository<Vacina, String> {
}
