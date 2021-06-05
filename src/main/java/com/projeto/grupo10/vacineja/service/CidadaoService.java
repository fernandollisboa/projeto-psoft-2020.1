package com.projeto.grupo10.vacineja.service;

import com.projeto.grupo10.vacineja.DTO.*;
import com.projeto.grupo10.vacineja.model.agenda.Agenda;
import com.projeto.grupo10.vacineja.model.requisitos_vacina.Requisito;
import com.projeto.grupo10.vacineja.model.usuario.*;
import com.projeto.grupo10.vacineja.model.vacina.Vacina;
import com.projeto.grupo10.vacineja.state.Situacao;

import javax.servlet.ServletException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface CidadaoService {
    Cidadao getCidadaoById(String cpf);
    boolean validaCidadaoSenha (CidadaoLoginDTO cidadaoLogin);
    boolean validaLoginComoFuncionario (CidadaoLoginDTO cidadaoLogin);
    boolean validaLoginComoAdministrador (CidadaoLoginDTO cidadaoLogin);
    void cadastroFuncionario(String headerToken, FuncionarioCadastroDTO cadastroFuncionario) throws ServletException;
    ArrayList<String> getUsuariosNaoAutorizados();
    void autorizarCadastroFuncionario(String cpfFuncionario) throws ServletException;
    Cidadao cadastraCidadao(CidadaoDTO cidadaoDTO) throws IllegalArgumentException;
    Cidadao updateCidadao(String headerToken, CidadaoUpdateDTO cidadaoUpdateDTO) throws ServletException, IllegalArgumentException;
    void verificaTokenFuncionario(String authHeader) throws ServletException;
    int habilitarSegundaDose();
    void recebeVacina(String cpfCidadao, Vacina vacina, LocalDate dataVacina);
    boolean podeAlterarIdade(int idade);
    boolean podeHabilitarRequisito(RequisitoDTO requisito);
    Situacao getSituacao(String cpf);
    void habilitaPelaIdade(Requisito requisito);
    void habilitaPorRequisito(Requisito requisito);
    String getEstadoVacinacao(String headerToken) throws ServletException;
    int contaCidadaosAcimaIdade(int idade);
    int contaCidadaosAtendeRequisito(RequisitoDTO requisito);
    List<String> listarCidadaosHabilitados();
    void verificaDataSegundaDose();
    void verificaDataMarcadaVacinacao();
    Agenda getAgendamentobyCpf(String headerToken) throws ServletException;
}
