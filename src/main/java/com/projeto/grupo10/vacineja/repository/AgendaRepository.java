package com.projeto.grupo10.vacineja.repository;

import com.projeto.grupo10.vacineja.model.agenda.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AgendaRepository extends JpaRepository<Agenda, String> {

}
