package com.projeto.grupo10.vacineja.service;

import com.projeto.grupo10.vacineja.DTO.LoteDTO;
import com.projeto.grupo10.vacineja.DTO.MinistraVacinaDTO;
import com.projeto.grupo10.vacineja.DTO.RequisitoDTO;
import com.projeto.grupo10.vacineja.model.lote.Lote;

import javax.servlet.ServletException;
import java.util.List;

public interface FuncionarioService {

    void alteraIdadeGeral(int idade, String headerToken) throws ServletException,IllegalArgumentException, IllegalCallerException;
    void setRequisitoHabilitado(RequisitoDTO requisito, String headerToken) throws ServletException,IllegalArgumentException, IllegalCallerException;
    void ministraVacina(String headerToken, MinistraVacinaDTO ministraVacinaDTO) throws ServletException;
    List<String> listaComorbidadesCadastradas(String headerToken) throws ServletException, IllegalArgumentException;
    List<String> listaProfissoesCadastradas(String headerToken) throws ServletException, IllegalArgumentException;
    int getCidadaosAcimaIdade(String headerToken, int idade) throws ServletException;
    int getQtdCidadaosAtendeRequisito(String headerToken, RequisitoDTO requisito) throws ServletException, IllegalArgumentException;
    List<Lote> listaLotes(String headerToken) throws ServletException;
    Lote criarLote(String headerToken, String nomeFabricante, LoteDTO loteDTO) throws ServletException;
    List<Lote> listaLotesPorFabricante(String nomeFabricante, String headerToken) throws ServletException;
    List<String> listarCidadaosHabilitados(String headerToken) throws ServletException;
    int habilitarSegundaDose(String headerToken) throws ServletException;
}