package com.projeto.grupo10.vacineja.controllers;

import com.projeto.grupo10.vacineja.DTO.MinistraVacinaDTO;
import com.projeto.grupo10.vacineja.DTO.RequisitoDTO;
import com.projeto.grupo10.vacineja.model.lote.Lote;
import com.projeto.grupo10.vacineja.DTO.LoteDTO;
import com.projeto.grupo10.vacineja.model.vacina.Vacina;
import com.projeto.grupo10.vacineja.service.*;
import com.projeto.grupo10.vacineja.util.*;
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
public class FuncionarioControllerAPI {

    @Autowired
    CidadaoService cidadaoService;

    @Autowired
    VacinaService vacinaService;

    @Autowired
    FuncionarioService funcionarioService;

    @Autowired
    LoteService loteService;

    @Autowired
    JWTService jwtService;



    @RequestMapping(value = "/funcionario/ministrar-vacina", method = RequestMethod.POST)
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<?> ministrarVacina(@RequestHeader("Authorization") String headerToken,
                                             @RequestBody MinistraVacinaDTO ministraVacinaDTO){
        try{
            this.funcionarioService.ministraVacina(headerToken, ministraVacinaDTO);
        }
        catch (IllegalArgumentException iae){
            if(iae.getMessage().equals("Sem lotes da vacina requisitada"))
                return ErroLote.erroSemLoteDaVacina(ministraVacinaDTO.getTipoVacina());
            if (iae.getMessage().equals("Cidadão não cadastrado no sistema"))
                return ErroCidadao.erroUsuarioNaoEncontrado();
            else return ErroAgenda.erroVacinaNaoMarcada();
        }
        catch (ServletException e){
            return ErroLogin.erroTokenInvalido();
        }

        return new ResponseEntity<String>("Vacina aplicada com sucesso.", HttpStatus.OK);
    }

    @RequestMapping(value = "/funcionario/habilitar-segunda-dose", method = RequestMethod.POST)
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<?> habilitarSegundaDose(@RequestHeader("Authorization") String headerToken){
        int qtdHabilitados = 0;
        try{
            qtdHabilitados = this.funcionarioService.habilitarSegundaDose(headerToken);
        } catch (ServletException e) {
            return ErroLogin.erroTokenInvalido();
        }

        return new ResponseEntity<String>(String.format("Segunda Dose habilitada para %d pacientes", qtdHabilitados),
                HttpStatus.OK);
    }

    /**
     * Faz uma listagem de todas as Vacinas cadastradas no Sistema. É necessária a apresentção de token de Funcionário
     * valído para realizar essa ação.
     *
     * @param headerToken eh o token do funcionario valido
     * @return response entity adequada, contendo a lista de Vacinas
     */
    @GetMapping("/funcionario/vacinas")
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<?> listaVacinas(@RequestHeader("Authorization") String headerToken){

        try {
            List<Vacina> vacinasList = vacinaService.listarVacinas(headerToken);

            if(vacinasList.isEmpty()){
                return  ErroVacina.semVacinasCadastradas();
            }
            return new ResponseEntity<>(vacinasList,HttpStatus.OK);
        } catch (IllegalArgumentException | ServletException e){
            return ErroVacina.erroListarVacina(e.getMessage());
        }

    }

    /**
     * Cria uma Lote de Vacina a partir de LoteDTO. É necessária a apresentação de token de Funcionário
     * valído para realizar essa ação.
     *
     * @param headerToken eh o token de funcionario valido
     * @param nomeFabricante eh o nome do fabricante do lote
     * @param loteDTO eh o dto do lote, contendo qtd de doses e data de validade
     * @return response entity adequada, contendo o lote criado
     */
    @PostMapping("/funcionario/vacinas/lotes/{nome-fabricante}")
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<?> criaLote(@RequestHeader("Authorization") String headerToken, @PathVariable("nome-fabricante") String nomeFabricante, @RequestBody LoteDTO loteDTO){

        try{
            Lote lote = funcionarioService.criarLote(headerToken, nomeFabricante, loteDTO);
            Vacina vacina = vacinaService.fetchVacina(nomeFabricante);
            //Lote lote = loteService.criaLote(loteDTO,vacina, headerToken);
            return new ResponseEntity<>(lote,HttpStatus.CREATED);

        }
        catch (NullPointerException e){
            System.out.println(e.getMessage());
            return ErroVacina.erroVacinaNaoCadastrada(nomeFabricante);

        }
        catch (IllegalArgumentException | ServletException e){
            return ErroLote.erroCadastroLote(e.getMessage());
        }

    }

    /**
     * Retorna todos os Lotes de um certo fabricante. É necessária a apresentação de token de Funcionário
     * valído para realizar essa ação.
     *
     * @param nomeFabricante eh o nome do fabricante do lote
     * @param headerToken eh o token de funcionario valido
     * @return response entity adequada, contendo a lista de Lotes do fabricante
     */
    @GetMapping("/funcionario/vacinas/lotes/{nome-fabricante}")
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<?> listaLotesPorFabricante(@PathVariable ("nome-fabricante") String nomeFabricante, @RequestHeader("Authorization") String headerToken){
        try {
            List<Lote> loteList = funcionarioService.listaLotesPorFabricante(nomeFabricante, headerToken);

            if(loteList.isEmpty()){
                return ErroLote.semLotesCadastrados();
            }

            return new ResponseEntity<>(loteList, HttpStatus.OK);

        }
        catch (NullPointerException e){
            return ErroVacina.erroVacinaNaoCadastrada(nomeFabricante);
        } catch (IllegalArgumentException | ServletException e){
            return ErroVacina.erroListarVacina(e.getMessage());
        }
    }

    /**
     * Retorna todos os Lotes de Vacina criadas. É necessária a apresentação de token de Funcionário
     * valído para realizar essa ação.
     *
     * @param headerToken eh o token de funcionario valido
     * @return response entity adequada, contendo a lista de Lotes
     */
    @GetMapping("/funcionario/vacinas/lotes")
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<?> listaLotes(@RequestHeader("Authorization") String headerToken){


        try {
            List<Lote> loteList = funcionarioService.listaLotes(headerToken);
            if(loteList.isEmpty()){
                return ErroLote.semLotesCadastrados();
            }
            return new ResponseEntity<>(loteList,HttpStatus.OK);
        } catch (IllegalArgumentException | ServletException e){
            return ErroVacina.erroListarVacina(e.getMessage());
        }
    }

    @RequestMapping(value = "/funcionario/habilita-idade", method = RequestMethod.PUT)
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<?> habilitaIdade(@RequestHeader("Authorization") String headerToken,
                                           @RequestParam Integer idade){

        try{
            this.funcionarioService.alteraIdadeGeral(idade,headerToken);
        } catch (ServletException e){
            ErroLogin.erroTokenInvalido();
        } catch (IllegalCallerException ice){
            ErroRequisito.requisitoNaoPodeHabilitar(new RequisitoDTO(idade,"idade"));
        } catch (IllegalArgumentException iae){
            ErroRequisito.requisitoNaoCadastrado("idade");
        }

        return new ResponseEntity<String>(String.format("A partir de agora pessoas com %d ou mais poderão se vacinar",idade),HttpStatus.OK);
    }

    @RequestMapping(value = "/funcionario/habilita-requisito", method = RequestMethod.PUT)
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<?> habilitaRequisito(@RequestHeader("Authorization") String headerToken,
                                               @RequestBody RequisitoDTO requisito){

        try{
            this.funcionarioService.setRequisitoHabilitado(requisito,headerToken);
        } catch (ServletException e){
            ErroLogin.erroTokenInvalido();
        } catch (IllegalCallerException ice){
            ErroRequisito.requisitoNaoPodeHabilitar(requisito);
        } catch (IllegalArgumentException iae){
            ErroRequisito.requisitoNaoCadastrado(requisito.getRequisito());
        }

        return new ResponseEntity<String>(String.format("A partir de agora pessoas com o requisito %s com a idade %d ou mais poderão se vacinar",requisito.getRequisito(),requisito.getIdade()),HttpStatus.OK);
    }

    @RequestMapping(value = "/funcionario/comorbidades-cadastradas", method = RequestMethod.GET)
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<?> getComorbidadesCadastradas(@RequestHeader("Authorization") String headerToken){
        List<String> listaComorbidades = new ArrayList<String>();
        try{
           listaComorbidades = this.funcionarioService.listaComorbidadesCadastradas(headerToken);
        } catch (IllegalArgumentException iae){
            ErroRequisito.nenhumRequisitoCadastrado();
        } catch (ServletException e){
            ErroLogin.erroTokenInvalido();
        }

        return new ResponseEntity<List<String>>(listaComorbidades,HttpStatus.OK);
    }

    @RequestMapping(value = "/funcionario/profissoes-cadastradas", method = RequestMethod.GET)
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<?> getProfissoesCadastradas(@RequestHeader("Authorization") String headerToken){
        List<String> listaProfissoes = new ArrayList<String>();

        try{
            listaProfissoes = this.funcionarioService.listaProfissoesCadastradas(headerToken);
        } catch (IllegalArgumentException iae){
            ErroRequisito.nenhumRequisitoCadastrado();
        } catch (ServletException e){
            ErroLogin.erroTokenInvalido();
        }

        return new ResponseEntity<List<String>>(listaProfissoes,HttpStatus.OK);
    }

    /**
     * Retorna a quantidade de cidadaos não habilitados com idade igual ou superior a idade passada como parametro
     * @param headerToken token do usuario logado
     * @param idade idade a ser usada para o calculo
     * @return quantidade de cidadaos nao habilitados com idade igual ou superior a idade passada
     * @author Caio Silva
     */
    @RequestMapping(value = "/funcionario/cidadaos-por-idade", method = RequestMethod.GET)
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<?> getNumeroCidadaosNaoHabilitadosPorIdade(@RequestHeader("Authorization") String headerToken, @RequestParam int idade){
        System.out.println(idade);
        int qtdCidadaosAcimadeIdade = 0;

        try{
            qtdCidadaosAcimadeIdade = funcionarioService.getCidadaosAcimaIdade(headerToken,idade);
        } catch (ServletException e){
            ErroLogin.erroTokenInvalido();
        }

        return new ResponseEntity<Integer>(qtdCidadaosAcimadeIdade,HttpStatus.OK);
    }

    /**
     * Retorna a quantidade de cidadaos não habilitados que atendem ao requisito passado por parametro
     * @param headerToken token do usuario logado
     * @param requisito requisito a ser utilizado
     * @return quantidade de cidadaos nao habilitados que se encaixam no requisito enviado
     * @author Caio Silva
     */
    @RequestMapping(value = "/funcionario/cidadaos-por-requisito", method = RequestMethod.GET)
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<?> getNumeroCidadaosNaoHabilitadosPorRequisito(@RequestHeader("Authorization") String headerToken, @RequestParam String requisito, @RequestParam int idade){

        int qtdCidadaosAcimadeIdade = 0;

        try{
            qtdCidadaosAcimadeIdade = funcionarioService.getQtdCidadaosAtendeRequisito(headerToken,new RequisitoDTO(idade,requisito));
        } catch (ServletException e){
            ErroLogin.erroTokenInvalido();
        }

        return new ResponseEntity<Integer>(qtdCidadaosAcimadeIdade,HttpStatus.OK);
    }

    @RequestMapping(value = "/funcionario/cidadao-habilitados", method = RequestMethod.GET)
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<?> listaCidadaosHabilitados(@RequestHeader("Authorization") String headerToken){
        List<String> cpfsAutorizados = new ArrayList<String>();

        try{
            cpfsAutorizados = this.funcionarioService.listarCidadaosHabilitados(headerToken);
        } catch (ServletException e){
            ErroLogin.erroTokenInvalido();
        }

        return new ResponseEntity<List<String>>(cpfsAutorizados,HttpStatus.OK);
    }
}
