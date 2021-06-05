package com.projeto.grupo10.vacineja.service;

import com.projeto.grupo10.vacineja.DTO.RequisitoDTO;
import com.projeto.grupo10.vacineja.model.requisitos_vacina.Requisito;

import javax.servlet.ServletException;
import java.util.ArrayList;

public interface AdministradorService {
    ArrayList<String> getUsuariosNaoAutorizados(String headerToken) throws ServletException;
    void autorizarCadastroFuncionario(String headerToken, String cpfFuncionario)  throws ServletException;
    void verificaLoginAdmin (String headerToken) throws ServletException;
    public Requisito adicionaNovaComorbidade(RequisitoDTO requisito, String headerToken) throws ServletException, IllegalArgumentException;
    public Requisito adicionaNovaProfissao(RequisitoDTO requisito, String headerToken) throws ServletException, IllegalArgumentException;
}
