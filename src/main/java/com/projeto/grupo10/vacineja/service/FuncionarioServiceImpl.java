package com.projeto.grupo10.vacineja.service;

import com.projeto.grupo10.vacineja.DTO.LoteDTO;
import com.projeto.grupo10.vacineja.DTO.MinistraVacinaDTO;
import com.projeto.grupo10.vacineja.DTO.RequisitoDTO;
import com.projeto.grupo10.vacineja.model.agenda.Agenda;
import com.projeto.grupo10.vacineja.model.lote.Lote;
import com.projeto.grupo10.vacineja.model.requisitos_vacina.Requisito;
import com.projeto.grupo10.vacineja.model.vacina.Vacina;
import com.projeto.grupo10.vacineja.util.PadronizaString;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class FuncionarioServiceImpl implements FuncionarioService {

    @Autowired
    CidadaoService cidadaoService;

    @Autowired
    RequisitoService requisitoService;

    @Autowired
    LoteService loteService;

    @Autowired
    VacinaService vacinaService;

    @Autowired
    AgendaService agendaService;

    /**
     * Método responsável por chamar o método de requisitoService que altera a idade geral das pessoas que podem vacinar
     * @param headerToken token do funcionário que está executando a operação
     * @throws ServletException lança caso o token seja inválido
     * @throws IllegalArgumentException lança caso o requisito esteja errado
     * @throws IllegalCallerException  lança caso tenha mais pessoas a serem habilitadas do que doses disponiveis
     */
    @Override
    public void alteraIdadeGeral(int idade, String headerToken) throws ServletException, IllegalArgumentException, IllegalCallerException {
        cidadaoService.verificaTokenFuncionario(headerToken);
        if(!cidadaoService.podeAlterarIdade(idade))
            throw new IllegalCallerException("Impossivel habilitar a idade");

        requisitoService.setIdade(idade);
        Optional<Requisito> requisitoOptional = requisitoService.getRequisitoById("idade");
        if(requisitoOptional.isEmpty())
            throw new IllegalCallerException("Ocorreu algum erro com o requisito");

        cidadaoService.habilitaPelaIdade(requisitoOptional.get());
    }

    /**
     * Método responsável por chamar o método de requisitoService que habilita um novo requisito
     * @param requisito requisito a ser habilitado para vacina
     * @param headerToken token do funcionário que está executando a operação
     * @throws ServletException lança caso o token seja inválido
     * @throws IllegalArgumentException lança caso o requisito esteja errado
     * @throws IllegalCallerException  lança caso tenha mais pessoas a serem habilitadas do que doses disponiveis
     */
    @Override
    public void setRequisitoHabilitado(RequisitoDTO requisito, String headerToken) throws ServletException, IllegalArgumentException, IllegalCallerException {
        requisito.setRequisito(PadronizaString.padronizaString(requisito.getRequisito()));
        cidadaoService.verificaTokenFuncionario(headerToken);
        if(!cidadaoService.podeHabilitarRequisito(requisito))
            throw new IllegalCallerException("Impossível habilitar tal requisito");

        requisitoService.setPodeVacinar(requisito);
        Optional<Requisito> requisitoOptional = requisitoService.getRequisitoById(requisito.getRequisito());
        if(requisitoOptional.isEmpty())
            throw new IllegalCallerException("Ocorreu algum erro com o requisito");

        cidadaoService.habilitaPorRequisito(requisitoOptional.get());
    }

    /**
     * Metodo responsavel por ministrar uma dose da vacina ao cidadão que esteja apto a receber uma vacina,
     * a partir disso caso essa seja a segunda dose ou a vacina ministrada seja de dose unica o estado de
     * vacinação passa para finalizado e se for a primeira dose de uma vacina que tem duas doses o cidadão deve
     * ir para o estado de "tomou primeira dose"
     * @param headerToken - token do funcionario que deve ministrar a dose
     * @param  ministraVacinaDTO - Objeto que contem todas as informações necesLocalDatesarias para testar a vacina
     * @throws ServletException
     * @author Caetano Albuquerque
     */
    @Override
    public void ministraVacina(String headerToken, MinistraVacinaDTO ministraVacinaDTO) throws ServletException {
        ministraVacinaDTO.setTipoVacina(PadronizaString.padronizaString(ministraVacinaDTO.getTipoVacina()));
        this.cidadaoService.verificaTokenFuncionario(headerToken);

        String cpfCidadao = ministraVacinaDTO.getCpf();
        LocalDate dataVacina = ministraVacinaDTO.getDataVacinacao();
        String Tipovacina = ministraVacinaDTO.getTipoVacina();

        Agenda agenda = this.agendaService.getAgendamentobyCpf(cpfCidadao);

        if (!agenda.getData().equals(dataVacina)){
            throw new IllegalArgumentException("Sem agendamento marcado");
        }
        Vacina vacina = this.loteService.retirarVacinaValidadeProxima(Tipovacina);
        this.cidadaoService.recebeVacina(cpfCidadao, vacina, dataVacina);

    }

    /**

     * Método que retorna todas as comorbidades cadastradas no sistema
     * @return uma lista com todas as comorbidades já cadastradas
     * @throws IllegalArgumentException caso não exista nenhuma comorbidade
     * @throws  ServletException lança caso o token seja inválido
     * @author Caio Silva
     */
    @Override
    public List<String> listaComorbidadesCadastradas(String headerToken) throws ServletException, IllegalArgumentException {
        cidadaoService.verificaTokenFuncionario(headerToken);
        List<String> requisitos = requisitoService.getTodasComorbidades();
        return requisitos;
    }

    /**
     * Método que retorna todas as profissoes cadastradas no sistema
     * @return uma lista com todas as comorbidades já cadastradas
     * @throws IllegalArgumentException caso não exista nenhuma comorbidade
     * @throws  ServletException lança caso o token seja inválido
     * @author Caio Silva
     */
    @Override
    public List<String> listaProfissoesCadastradas(String headerToken) throws ServletException, IllegalArgumentException {
        cidadaoService.verificaTokenFuncionario(headerToken);
        List<String> requisitos = requisitoService.getTodasProfissoes();
        return requisitos;
    }

    /**
     * Retorna a quantidade de cidadaos não habilitados com idade igual ou superior a idade passada como parametro.
     * Método pode ser utilizado para o funcionário saber se pode ou não habilitar tal idade, visto que uma idade so
     * pode ter podeVacinar = true caso tenha doses suficientes para vacinar todos que têm aquela idade ou mais.
     * @param headerToken token do funcionario logado
     * @param idade idade a ser usada para o calculo
     * @return quantidade de cidadaos nao habilitados com idade igual ou superior a idade passada
     * @throws ServletException lança caso o token seja inválido
     * @author Caio Silva
     */
    @Override
    public int getCidadaosAcimaIdade(String headerToken, int idade) throws ServletException {
       cidadaoService.verificaTokenFuncionario(headerToken);
       return cidadaoService.contaCidadaosAcimaIdade(idade);
    }

    /**
     * Retorna a quantidade de cidadaos não habilitados que atendem ao requisito passado como parametro. Método pode ser
     * utilizado para o funcionário saber se pode ou não habilitar tal requisito, visto que um requisito so pode ter
     * podeVacinar = true caso tenha doses suficientes para vacinar todos que têm aquele requisito.
     * @param headerToken token do funcionario logado
     * @param requisito requisito a ser utilizado para o calculo
     * @return quantidade de cidadaos nao habilitados que se encaixam naquele requisito
     * @throws ServletException lança caso o token seja inválido
     * @throws IllegalArgumentException caso o requisito não esteja cadastrado no sistema
     */
    public int getQtdCidadaosAtendeRequisito(String headerToken, RequisitoDTO requisito) throws ServletException, IllegalArgumentException{
        cidadaoService.verificaTokenFuncionario(headerToken);
        return cidadaoService.contaCidadaosAtendeRequisito(requisito);
    }


     /** Retorna todos os lotes armazenados no sistema. Realiza verifição jwt para ver se o dono do Token passado é um funcionário
     * @param headerToken - token do funcionario que ta criando a vacina
     * @return a lista de lotes
     * @throws ServletException se houver aglum problema na verificacao jwt
     */
    @Override
    public List<Lote> listaLotes(String headerToken) throws ServletException {
        this.cidadaoService.verificaTokenFuncionario(headerToken);
        return this.loteService.listaLotes();
    }

    /**
     *
     * Cria um lote com base em LoteDTO. Realiza verifição jwt para ver se o dono do Token passado é um administrador
     * @param headerToken - token do funcionario que ta criando a vacina
     * @param loteDTO eh o modelo do lote
     * @param nomeFabricante o tipo da vacina
     * @return o lote criado
     * @throws ServletException se houver algum problema na validacao jwt
     */
    @Override
    public Lote criarLote(String headerToken, String nomeFabricante, LoteDTO loteDTO) throws ServletException {
        this.cidadaoService.verificaTokenFuncionario(headerToken);

        Vacina vacina = this.vacinaService.fetchVacina(nomeFabricante);
        return this.loteService.criaLote(loteDTO, vacina);
    }

    /**
     * Retorna todos os lotes de vacina de um determinado fabricante. Realiza verifição jwt para ver se o dono do Token passado é um funcionário
     * @param headerToken - token do funcionario
     * @param nomeFabricante eh o nome do fabricante da vacina procurada
     * @return a lista de lotes da fabricante
     * @throws ServletException se houver algum problema na verificacao jwt
     */
    @Override
    public List<Lote> listaLotesPorFabricante(String nomeFabricante, String headerToken) throws ServletException {
        this.cidadaoService.verificaTokenFuncionario(headerToken);
        return this.loteService.listaLotesPorFabricante(nomeFabricante);
    }

    /**
     * Pega uma lista com todos os cpfs dos cidadãos que estão autorizados a tomar primeira ou segunda dose
     * @param headerToken - token do funcionario
     * @return uma lista com os cpfs autorizados para tomar alguma dose
     */
    @Override
    public List<String> listarCidadaosHabilitados(String headerToken) throws ServletException {
        this.cidadaoService.verificaTokenFuncionario(headerToken);
        return this.cidadaoService.listarCidadaosHabilitados();
    }

    @Override
    public int habilitarSegundaDose(String headerToken) throws ServletException {
        this.cidadaoService.verificaTokenFuncionario(headerToken);
        return this.cidadaoService.habilitarSegundaDose();
    }

}