package com.projeto.grupo10.vacineja.controllers;

import com.projeto.grupo10.vacineja.DTO.RequisitoDTO;
import com.projeto.grupo10.vacineja.DTO.VacinaDTO;
import com.projeto.grupo10.vacineja.model.requisitos_vacina.Requisito;
import com.projeto.grupo10.vacineja.model.vacina.Vacina;
import com.projeto.grupo10.vacineja.service.AdministradorService;
import com.projeto.grupo10.vacineja.service.CidadaoService;
import com.projeto.grupo10.vacineja.service.VacinaService;
import com.projeto.grupo10.vacineja.util.ErroCidadao;
import com.projeto.grupo10.vacineja.util.ErroLogin;
import com.projeto.grupo10.vacineja.util.ErroRequisito;
import com.projeto.grupo10.vacineja.util.ErroVacina;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class AdministradorControllerAPI {
    @Autowired
    CidadaoService cidadaoService;

    @Autowired
    AdministradorService administradorService;

    @Autowired
    VacinaService vacinaService;

    @RequestMapping(value = "/admin/funcionarios-nao-cadastrados", method = RequestMethod.GET)
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<?> getFuncionariosNaoAutorizados(@RequestHeader("Authorization") String headerToken){

        List<String> usuariosNaoAutorizados = new ArrayList<>();

        try{
            usuariosNaoAutorizados = this.administradorService.getUsuariosNaoAutorizados(headerToken);
        }

        catch (IllegalArgumentException iae){
                return ErroCidadao.erroSemPermissaoAdministrador();
        }
        catch (ServletException e){
            return ErroLogin.erroTokenInvalido();
        }

        return new ResponseEntity<List<String>>(usuariosNaoAutorizados, HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/funcionarios", method = RequestMethod.POST)
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<?> autorizarCadastroFuncionario(@RequestHeader("Authorization") String headerToken,
                                                                        @RequestHeader String cpfFuncionario){

        try{
            this.administradorService.autorizarCadastroFuncionario(headerToken, cpfFuncionario);
        }

        catch (IllegalArgumentException iae){
            if (iae.getMessage().equals("Usuario não cadastrado"))
                return ErroCidadao.erroUsuarioNaoEncontrado();
            if (iae.getMessage().equals("Usuario não é um funcionario não autorizado"))
                return ErroCidadao.erroUsuarioSemCadastroPendente(cpfFuncionario);
        }
        catch (ServletException e){
            return ErroLogin.erroTokenInvalido();
        }

        return new ResponseEntity<String>("Cadastro aprovado.", HttpStatus.OK);
    }


    /**
     * Cria uma Vacina a partir de uma VacinaDTO. É necessária a apresentação do token de Administrador para relizar essa
     * ação. Nesse sistema, por padrão, o ADM tem CPF = "00000000000".
     *
     * @param headerToken eh o token do administrador
     * @param vacinaDTO eh o dto da vacina a ser criada
     * @return response entity adequada, contendo a vacina criada
     */
    @PostMapping("/admin/vacinas")
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<?> criaVacina(@RequestHeader("Authorization") String headerToken, @RequestBody VacinaDTO vacinaDTO){

        try {
            Vacina vacina = vacinaService.criaVacina(vacinaDTO, headerToken);
            return new ResponseEntity<>(vacina, HttpStatus.CREATED);

        } catch (IllegalArgumentException | ServletException e){
            return ErroVacina.erroCadastroVacina(e.getMessage());
        }
    }

    @RequestMapping(value = "/admin/novo-requisito-comorbidade", method = RequestMethod.POST)
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<?> cadastraComorbidade(@RequestHeader("Authorization") String headerToken, @RequestBody RequisitoDTO requisito){
        Requisito requisitoCadastrado = new Requisito();
        try{
            requisitoCadastrado = administradorService.adicionaNovaComorbidade(requisito,headerToken);
        } catch (ServletException e){
            ErroCidadao.erroSemPermissaoAdministrador();
        } catch (IllegalArgumentException iae) {
            ErroRequisito.requisitoCadastrado(requisito);
        }

        return new ResponseEntity<String>(String.format("Requisito comorbidade %s adicionado no sistema",requisitoCadastrado.getRequisito()),HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/novo-requisito-profissao", method = RequestMethod.POST)
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<?> cadastraProfissao(@RequestHeader("Authorization") String headerToken, @RequestBody RequisitoDTO requisito){
        Requisito requisitoCadastrado = new Requisito();
        try{
            requisitoCadastrado = administradorService.adicionaNovaProfissao(requisito,headerToken);
        } catch (ServletException e){
            ErroCidadao.erroSemPermissaoAdministrador();
        } catch (IllegalArgumentException iae) {
            ErroRequisito.requisitoCadastrado(requisito);
        }

        return new ResponseEntity<String>(String.format("Requisito profissao %s adicionado no sistema",requisitoCadastrado.getRequisito()),HttpStatus.OK);
    }

}
