package com.projeto.grupo10.vacineja.service;

import com.projeto.grupo10.vacineja.DTO.RequisitoDTO;
import com.projeto.grupo10.vacineja.model.requisitos_vacina.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RequisitoService {

    public void setIdade(int idade);
    public Requisito setNovaComorbidade(RequisitoDTO requisito) throws IllegalArgumentException;
    public Requisito setNovaProfissao(RequisitoDTO requisito) throws IllegalArgumentException;
    public Optional<Requisito> getRequisitoById(String requisito);
    public List<String> getTodasComorbidades() throws IllegalArgumentException;
    public Requisito getIdade() throws IllegalArgumentException;
    public List<String> getTodasProfissoes()throws IllegalArgumentException;
    public RequisitoDTO setPodeVacinar(RequisitoDTO requisito) throws IllegalArgumentException;
    public List<String> requisitosHabilitados() throws IllegalArgumentException;

}