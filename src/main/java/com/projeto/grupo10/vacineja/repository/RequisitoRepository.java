package com.projeto.grupo10.vacineja.repository;

import com.projeto.grupo10.vacineja.model.requisitos_vacina.Requisito;
import com.projeto.grupo10.vacineja.model.usuario.Cidadao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequisitoRepository extends JpaRepository<Requisito, String> {

}
