package com.projeto.grupo10.vacineja.controllers;



import com.projeto.grupo10.vacineja.DTO.AgendaDTO;
import com.projeto.grupo10.vacineja.model.agenda.Agenda;
import com.projeto.grupo10.vacineja.DTO.CidadaoUpdateDTO;
import com.projeto.grupo10.vacineja.model.usuario.Cidadao;
import com.projeto.grupo10.vacineja.DTO.CidadaoDTO;
import com.projeto.grupo10.vacineja.DTO.FuncionarioCadastroDTO;
import com.projeto.grupo10.vacineja.service.*;
import com.projeto.grupo10.vacineja.util.ErroAgenda;
import com.projeto.grupo10.vacineja.util.ErroCidadao;
import com.projeto.grupo10.vacineja.util.ErroLogin;
import com.projeto.grupo10.vacineja.util.ErroRequisito;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.ServletException;
import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class CidadaoControllerAPI {

    @Autowired
    CidadaoService cidadaoService;

    @Autowired
    VacinaService vacinaService;

    @Autowired
    LoteService loteService;

    @Autowired
    AgendaService agendaService;

    @Autowired
    RequisitoService requisitoService;

    @Autowired
    JWTService jwtService;

    /**
     * Cria um cidadao a partir de um CidadaoDTO
     * @param cidadaoDTO
     * @return retorna o cidadaoDTO
     */
    @RequestMapping(value = "/cidadao/cadastra-cidadao", method = RequestMethod.POST)
    public ResponseEntity<?> cadastraCidadao(@Valid @RequestBody CidadaoDTO cidadaoDTO) {

        Cidadao cidadao = new Cidadao();

        try {
            cidadao = cidadaoService.cadastraCidadao(cidadaoDTO);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("Cart??o do SUS j?? cadastrado")) {
                return ErroCidadao.erroCartaoSUSCadastrado(cidadaoDTO.getCartaoSus());
            } else {
                return ErroCidadao.erroCidadaoCadastrado(cidadaoDTO.getCpf());
            }
        }
        return new ResponseEntity<String>(String.format("O cidadao com o cpf %s foi cadastrado", cidadao.getCpf()), HttpStatus.CREATED);
    }
        /**
         * Metodo para o cadastro do funcionario
         * @param headerToken - token do cidadao que esta tentando viarr funcionario
         * @param cadastroFuncionario -Um objeto com as seguintes informa????es -cpf do cidad??o, local de trabalho e a fun????o-
         * @return response entity adequada, dizendo se o cidadao foi definido com funcionario
         *
         * @author Caetano Albuquerque
         */
        @RequestMapping(value = "/cidadao/cadastrar-funcionario", method = RequestMethod.POST)
        @ApiOperation(value = "", authorizations = {@Authorization(value = "jwtToken")})
        public ResponseEntity<?> cadastrarFuncionario( @RequestHeader("Authorization") String headerToken,
                @RequestBody FuncionarioCadastroDTO cadastroFuncionario){

            try {
                cidadaoService.cadastroFuncionario(headerToken, cadastroFuncionario);
            } catch (IllegalArgumentException iae) {
                return ErroCidadao.erroUsuarioNaoEncontrado();
            } catch (ServletException e) {
                return ErroLogin.erroTokenInvalido();
            }

            return new ResponseEntity<String>("Cidad??o definido como funcionario, aguardando aprova????o do administrador.",
                    HttpStatus.OK);
        }

        /**
         * Altera os valores de um Cidadao a partir de uma cidadaoUpdateDTO. ?? necess??ria a apresenta????o do token de Cidad??o para relizar essa
         * a????o. O unico valor que n??o pode ser alterado ?? o cpf do Cidadao, visto que ?? sua primaryKey.
         *
         * @param headerToken eh o token do cidad??o
         * @param cidadaoUpdateDTO eh o dto das novas informa????es desejadas para o usu??rio
         * @return response entity adequada, contendo o Cidadao atualizado
         */

        @RequestMapping(value = "/cidadao/{id}", method = RequestMethod.PUT)
        @ApiOperation(value = "", authorizations = {@Authorization(value = "jwtToken")})
        public ResponseEntity<?> updateCidadao ( @RequestHeader("Authorization") String headerToken,
                                                 @Valid @RequestBody CidadaoUpdateDTO cidadaoUpdateDTO){

            Cidadao cidadao = new Cidadao();

            try {
                cidadao = cidadaoService.updateCidadao(headerToken, cidadaoUpdateDTO);
                return new ResponseEntity<String>(String.format("O cidadao com o cpf %s foi atualizado",
                        cidadao.getCpf()), HttpStatus.ACCEPTED);
            } catch (IllegalArgumentException iae) {
                return ErroCidadao.erroUsuarioNaoEncontrado();
            } catch (ServletException e) {
                return ErroLogin.erroTokenInvalido();
            }

        }

        /**
         * O cidadao agenda sua vacina????o com o seu cpf e informando o horario, data e local da vacina????o
         * @param headerToken
         * @param agendaDTO
         * @return retorna o agendamento feito
         */
        @RequestMapping(value = "/cidadao/agenda-vacina", method = RequestMethod.POST)
        @ApiOperation(value = "", authorizations = {@Authorization(value = "jwtToken")})
        public ResponseEntity<?> AgendamentoVacina (@RequestHeader("Authorization") String headerToken,
                @RequestBody AgendaDTO agendaDTO) throws ServletException {
            LocalDate data_limite = loteService.getMaiorValidadeLotes();
            String cpf_cidadao = jwtService.getCidadaoDoToken(headerToken);
            Agenda agenda = new Agenda();
            String response = "";
            try {
                agenda = agendaService.agendaVacina????o(headerToken, agendaDTO);
                response = "Voc?? agendou sua vacina????o para o dia " + agenda.getData() + " no local: " + agenda.getLocal();
            } catch (IllegalArgumentException e) {
                if (e.getMessage().toString() == "Cidadao nao cadastrado")
                    return ErroCidadao.erroCidadaoNaoCadastrado(cpf_cidadao);
                else if (e.getMessage().toString() == "Cidadao nao habilitado")
                    return ErroCidadao.erroCidadaoNaoHabilitado();
                else if (e.getMessage().toString() == "Data invalida")
                    return ErroAgenda.erroDataInvalida(LocalDate.now());
                else if (e.getMessage().toString() == "Data maior que a validade dos lotes")
                    return ErroAgenda.erroDataMaior(data_limite);
            } catch (ServletException e) {
                return ErroLogin.erroTokenInvalido();
            }
            return new ResponseEntity<String>(response, HttpStatus.CREATED);
        }

        /**
         * Pega os agendamentos feitos por um cidadao usando o cpf
         * @param headerToken
         * @return Retorna todos os agendamentos feitos pelo cidadao
         */
        @RequestMapping(value = "/cidadao/agenda-vacina/{cpf}", method = RequestMethod.GET)
        @ApiOperation(value = "", authorizations = {@Authorization(value = "jwtToken")})
        public ResponseEntity<?> listaAgendamentoCidadao (@RequestHeader("Authorization") String headerToken) throws
        ServletException {
            Agenda agenda;
            String response = "";
            try {
                agenda = cidadaoService.getAgendamentobyCpf(headerToken);
                response = "Sua vacina????o est?? marcada para o dia " + agenda.getData() + " no local: " + agenda.getLocal();

            } catch (ServletException e) {
                return ErroLogin.erroTokenInvalido();
            }catch (IllegalArgumentException iae){
                return ErroAgenda.erroNenhumaVacinaMarcada();
            }
            return new ResponseEntity<String>(response, HttpStatus.OK);
        }

        /**
         * Metodo responsavel por pegar o estado de vacina????o
         * @param headerToken - token cidadao que quer ver o seu estado
         * @return - um ResponseEntity contendo o estado de vacina????o do cidad??o
         *
         * @author Caetano Albuquerque
         */
        @RequestMapping(value = "/cidadao/estado-vacinacao", method = RequestMethod.GET)
        @ApiOperation(value = "", authorizations = {@Authorization(value = "jwtToken")})
        public ResponseEntity<?> getEstadoVacinacao (@RequestHeader("Authorization") String headerToken){

            String estadoVacinacao = "";

            try {
                estadoVacinacao = cidadaoService.getEstadoVacinacao(headerToken);
            } catch (IllegalArgumentException iae) {
                return ErroCidadao.erroUsuarioNaoEncontrado();
            } catch (ServletException e) {
                return ErroLogin.erroTokenInvalido();
            }

            return new ResponseEntity<String>(estadoVacinacao, HttpStatus.OK);
        }


        /**
         * M??todo aberto para qualquer usu??rio para informar a idade m??nima que poder?? vacinar
         * @return a idade m??nima que ser?? habilitada para tomar a vacina
         */
        @RequestMapping(value = "/cidadao/idade-habilitada", method = RequestMethod.GET)
        public ResponseEntity<?> getIdadeHabilitada () {
            int idade = 0;
            try {
                idade = requisitoService.getIdade().getIdade();
            } catch (IllegalArgumentException iae) {
                ErroRequisito.idadeAindaNaoCadastrada();
            }

            return new ResponseEntity<String>(String.format("Idade m??nima para ser vacinado(a) atualmente ??: %d", idade), HttpStatus.OK);
        }

        /**
         * M??todo aberto para qualquer usu??rio para informar os requisitos que podem vacinar
         * @return os requisitos habilitados para vacinar
         */
        @RequestMapping(value = "/cidadao/requisitos-habilitados", method = RequestMethod.GET)
        public ResponseEntity<?> getRequisitosHabilitados () {
            List<String> requisitos = new ArrayList<>();

            try {
                requisitos = requisitoService.requisitosHabilitados();
            } catch (IllegalArgumentException iae) {
                ErroRequisito.nenhumRequisitoHabilitado();
            }

            return new ResponseEntity<List<String>>(requisitos, HttpStatus.OK);
        }

        /**
         * M??todo aberto para qualquer usu??rio para informar os requisitos que podem vacinar
         * @return os requisitos habilitados para vacinar
         */
        @RequestMapping(value = "/cidadao/consulta-requisitos", method = RequestMethod.GET)
        public ResponseEntity<?> getRequisito (@RequestParam String requisito){
            String requisitoString = "";

            try {
                requisitoString = requisitoService.getRequisitoById(requisito).get().toString();
            } catch (IllegalArgumentException iae) {
                ErroRequisito.requisitoNaoCadastrado(requisito);
            }

            return new ResponseEntity<String>(requisitoString, HttpStatus.OK);
        }


}
