package com.projeto.grupo10.vacineja.service;

import com.projeto.grupo10.vacineja.DTO.RequisitoDTO;
import com.projeto.grupo10.vacineja.model.requisitos_vacina.Requisito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import java.util.ArrayList;

@Service
public class AdministradorServiceImpl implements AdministradorService{
    private static final String CPF_ADM = "00000000000";

    @Autowired
    CidadaoService cidadaoService;

    @Autowired
    RequisitoService requisitoService;

    @Autowired
    private JWTService jwtService;

    public ArrayList<String> getUsuariosNaoAutorizados(String headerToken) throws ServletException{
        verificaLoginAdmin(headerToken);
        return this.cidadaoService.getUsuariosNaoAutorizados();
    }

    /**
     * Metodo responsavel por autorizar o cadastro de um funcionario do governo
     * @param headerToken - token do adimin que esta tentando autorizar o cadastro
     * @param cpfFuncionario - cpf do funcionario que tera o seu cadastro aprovado
     * @throws ServletException
     * @author Caetano Albuquerque
     */
    public void autorizarCadastroFuncionario(String headerToken, String cpfFuncionario) throws ServletException{
        verificaLoginAdmin(headerToken);
        this.cidadaoService.autorizarCadastroFuncionario(cpfFuncionario);
    }

    /**
     * Verifica o cpf do administrador ao logar
     * @param id
     * @return retorna um boolean se o cpf for igual ao cpf do administrador
     */
    private boolean isAdmin(String id){
        return id.equals(CPF_ADM);
    }

    /**
     * verifica se é o administrador que esta fazendo o login
     * @param tipoLogin
     * @return retorna um boolean se o tipo de login for administrador
     */
    private boolean loginAsAdmin(String tipoLogin){ return tipoLogin.equals("administrador");}

    /**
     * Verifica se o token passado é do administrador
     * @param headerToken eh o token do suposto adm
     * @throws ServletException excecao lançada se houver erro de autenticacao
     *
     */
    public void verificaLoginAdmin (String headerToken) throws ServletException{
        String id = jwtService.getCidadaoDoToken(headerToken);
        String tipoLogin = jwtService.getTipoLogin(headerToken);

        if(!isAdmin(id) || !loginAsAdmin(tipoLogin)) {
            throw new IllegalArgumentException("O usuário não tem permissão de Administrador!");
        }
    }

    /**
     * Adiciona uma nova comorbidade no sistema
     * @param requisito comorbidade a ser adicionada
     * @param headerToken token com o acesso atual
     * @return o requisito cadastrado
     * @throws ServletException caso o token esteja expirado ou nao pertença a um admin
     * @throws IllegalArgumentException caso a comorbidade ja existe no sistema
     */
    @Override
    public Requisito adicionaNovaComorbidade(RequisitoDTO requisito, String headerToken) throws ServletException, IllegalArgumentException{
        this.verificaLoginAdmin(headerToken);
        return requisitoService.setNovaComorbidade(requisito);
    }

    /**
     * Adiciona uma nova profissao no sistema
     * @param requisito profissao a ser adicionada
     * @param headerToken token com o acesso atual
     * @throws ServletException caso o token esteja expirado ou nao pertença a um admin
     * @throws IllegalArgumentException caso a profissao ja existe no sistema
     */
    @Override
    public Requisito adicionaNovaProfissao(RequisitoDTO requisito, String headerToken) throws ServletException, IllegalArgumentException {
        this.verificaLoginAdmin(headerToken);
        return requisitoService.setNovaProfissao(requisito);
    }
}
