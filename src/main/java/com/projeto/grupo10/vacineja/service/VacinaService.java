package com.projeto.grupo10.vacineja.service;

import com.projeto.grupo10.vacineja.model.vacina.Vacina;
import com.projeto.grupo10.vacineja.DTO.VacinaDTO;

import javax.servlet.ServletException;
import java.util.List;
import java.util.Optional;

/**
 * Responsável por realizar funções de cadastro, verificação, listagem e busca de Vacinas no sistema.
 */
public interface VacinaService {
    Vacina criaVacina(VacinaDTO vacinaDTO, String authToken) throws ServletException;
    List<Vacina> listarVacinas(String headerToken) throws ServletException;
    Optional<Vacina> getVacinaById(String nomeFabricante) ;
    Vacina fetchVacina(String nomeFabricante);

}
