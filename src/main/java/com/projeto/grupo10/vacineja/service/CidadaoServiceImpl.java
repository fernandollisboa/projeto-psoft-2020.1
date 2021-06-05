package com.projeto.grupo10.vacineja.service;

import com.projeto.grupo10.vacineja.DTO.*;
import com.projeto.grupo10.vacineja.job.Verificador;
import com.projeto.grupo10.vacineja.model.agenda.Agenda;
import com.projeto.grupo10.vacineja.model.requisitos_vacina.Requisito;
import com.projeto.grupo10.vacineja.model.usuario.CartaoVacina;
import com.projeto.grupo10.vacineja.model.usuario.Cidadao;
import com.projeto.grupo10.vacineja.model.usuario.FuncionarioGoverno;
import com.projeto.grupo10.vacineja.model.vacina.Vacina;
import com.projeto.grupo10.vacineja.repository.CartaoVacinaRepository;
import com.projeto.grupo10.vacineja.repository.CidadaoRepository;
import com.projeto.grupo10.vacineja.repository.FuncionarioGovernoRepository;
import com.projeto.grupo10.vacineja.state.*;
import com.projeto.grupo10.vacineja.util.CalculaIdade;
import com.projeto.grupo10.vacineja.util.email.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import java.time.LocalDate;
import java.util.*;

import static com.projeto.grupo10.vacineja.util.PadronizaString.padronizaSetsDeString;


@Service
public class CidadaoServiceImpl implements CidadaoService {

    private static final String MENSSAGEM_EMAIL_ALERTA_HABILITADO_PARA_VACINA = "Ola! \nVocê esta apto para receber a %s dose da vacina! " +
            "\nPor favor acesse o sistema Vacine Já para agendar sua vacinação";
    private static final String ASSUNTO_EMAIL_ALERTA_HABILITADO_PARA_VACINA = "Vacinação %s dose";
    private static final String MENSSAGEM_EMAIL_ALERTA_DIA_MARCADO_VACINA = "Ola! \nA data que você marcou para ser vacinado é hpje!" +
            "\nPor favor compareça ao local %s no horario %s";
    private static final String ASSUNTO_EMAIL_ALERTA_DIA_MARCADO_VACINA = "Vacinação marcada para %s";

    @Autowired
    private CidadaoRepository cidadaoRepository;

    @Autowired
    private FuncionarioGovernoRepository funcionarioGovernoRepository;

    @Autowired
    private CartaoVacinaRepository cartaoVacinaRepository;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private LoteService loteService;

    @Autowired
    private Verificador verificador;

    @Autowired
    private AgendaService agendaService;


    @Override
    public Cidadao getCidadaoById(String cpf) {
        return this.cidadaoRepository.findById(cpf).orElseThrow(() -> new IllegalArgumentException("Cidadao nao cadastrado"));
    }

    /**
     * Salva um cidadao no repository
     * @param cidadao
     * @author Holliver Costa
     */
    private void salvarCidadao(Cidadao cidadao){
        this.cidadaoRepository.save(cidadao);
    }

    private FuncionarioGoverno adicionarFuncionarioGoverno(FuncionarioCadastroDTO funcionarioCadastroDTO, String cpfCidadao) {
        FuncionarioGoverno funcionarioGoverno = new FuncionarioGoverno(cpfCidadao, funcionarioCadastroDTO.getCargo(),
                funcionarioCadastroDTO.getLocalTrabalho());

        this.funcionarioGovernoRepository.save(funcionarioGoverno);

        return funcionarioGoverno;
    }


    @Override
    public boolean validaCidadaoSenha(CidadaoLoginDTO cidadaoLogin) {
        try {
            return getCidadaoById(cidadaoLogin.getCpfLogin()).getSenha().equals(cidadaoLogin.getSenhaLogin());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validaLoginComoFuncionario(CidadaoLoginDTO cidadaoLogin) {
        return cidadaoLogin.getTipoLogin().equals("funcionario") && !this.isFuncionario(cidadaoLogin.getCpfLogin());
    }

    public boolean validaLoginComoAdministrador(CidadaoLoginDTO cidadaoLogin) {
        return cidadaoLogin.getTipoLogin().equals("administrador") && !this.isAdmin(cidadaoLogin.getCpfLogin());
    }

    private boolean isFuncionario(String id) {
        try {
            return getCidadaoById(id).isFuncionario();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Metodo responsavel pelo auto cadastro de um funcionario, ao final desse metodo o funcionario se encontrar
     * num estado de aguardando aprovação do administrador
     *
     * @param headerToken         - Token do cidadão que esta realizando seu cadastro de funcionario
     * @param cadastroFuncionario -Um objeto com as seguintes informações -cpf do cidadão, local de trabalho e a função-
     * @throws ServletException
     * @author Caetano Albuquerque
     */
    public void cadastroFuncionario(String headerToken, FuncionarioCadastroDTO cadastroFuncionario) throws ServletException {
        String id = jwtService.getCidadaoDoToken(headerToken);
        Cidadao cidadao = this.getCidadaoById(id);
        cidadao.setFuncionarioGoverno(this.adicionarFuncionarioGoverno(cadastroFuncionario, id));
        this.salvarCidadao(cidadao);
    }

    /**
     * Metodo responsavel por listar todos os cidadãos que estão aguardando a autorização do administrador para
     * virarem funcionarios
     *
     * @return - Um ArrayList com todos os funcionarios com o cadastro pendente
     * @author Caetano Albuquerque
     */
    public ArrayList<String> getUsuariosNaoAutorizados() {
        ArrayList<String> funcionariosNaoAutorizados = new ArrayList<String>();
        for (Cidadao cidadao : this.cidadaoRepository.findAll()) {
            if (cidadao.aguardandoAutorizacaoFuncionario()) {
                funcionariosNaoAutorizados.add(cidadao.getCpf());
            }
        }
        return funcionariosNaoAutorizados;
    }

    /**
     * Metodo responsavel por autorixar o cadastro de um funcionario
     *
     * @param cpfFuncionario - Cpf do funcionario que tera seu cadastro aprovado
     * @author Caetano Albuquerque
     */
    public void autorizarCadastroFuncionario(String cpfFuncionario) {
        Cidadao cidadao = getCidadaoById(cpfFuncionario);
        if (!cidadao.aguardandoAutorizacaoFuncionario()) {
            throw new IllegalArgumentException();
        }
        cidadao.autorizaCadastroFuncionario();
        this.funcionarioGovernoRepository.save(cidadao.getFuncionarioGoverno());
    }

    private boolean isAdmin(String id) {
        return id.equals("00000000000");
    }

    private boolean loginAsAdmin(String tipoLogin) {
        return tipoLogin.equals("administrador");

    }


    public void verificaTokenFuncionario(String authHeader) throws ServletException {
        String id = jwtService.getCidadaoDoToken(authHeader);
        String tipoLogin = jwtService.getTipoLogin(authHeader);

        if (!isFuncionario(id) && tipoLogin.equals("funcionario"))
            throw new ServletException("Usuario não é um Funcionário cadastrado!");

    }

    /**
     * Metodo que cadastra um cidadao a partir de um cidadao DTO, verifica se o cidadao ja esta cadastrado e se o email colocoado é valido
     * @param cidadaoDTO
     * @author Holliver Costa
     * @throws IllegalArgumentException
     * @return
     */

    public Cidadao cadastraCidadao(CidadaoDTO cidadaoDTO) throws IllegalArgumentException {
        Cidadao cidadao = new Cidadao();
        Optional<Cidadao> cidadaoOptional = this.cidadaoRepository.findById(cidadaoDTO.getCpf());
        verificacaoCadastraCidadao(cidadaoDTO, cidadaoOptional);
        copyDTOCidadaoToEntity(cidadaoDTO, cidadao);
        this.salvarCidadao(cidadao);
        return this.cidadaoRepository.findById(cidadaoDTO.getCpf()).get();
    }

    /**
     * Metodo privado responsavel por fazer verificações mais especificas nos dados do DTO para ser possivel fazer o cadastro.
     * @param cidadaoDTO - DTO contendo as novas informacoes desejadas para o usuario.
     * @param cidadaoOptional - possivel cidadao para ver se o novo cidadao que deseja-se cadastrar realmente é único.
     * @throws IllegalArgumentException
     */
    private void verificacaoCadastraCidadao (CidadaoDTO cidadaoDTO, Optional<Cidadao> cidadaoOptional) {
        if (cidadaoOptional.isPresent()) {
            throw new IllegalArgumentException("Cidadao cadastrado");
        }
        for (Cidadao cid : cidadaoRepository.findAll()) {
            if (cid.getCartaoSus() != null) {
                if (cid.getCartaoSus().equals(cidadaoDTO.getCartaoSus())) {
                    throw new IllegalArgumentException("Cartão do SUS já cadastrado");
                }
            }
        }
    }

    /**
     * Metodo privado responsavel por passar os valores do cidadaoDTO para um cidadao.
     * @param cidadaoDTO - DTO contendo as novas informacoes desejadas para o usuario.
     * @param cidadao - cidadao que tera seus valores setados para os valores do cidadaoDTO.
     * @throws IllegalArgumentException
     */
    private void copyDTOCidadaoToEntity(CidadaoDTO cidadaoDTO, Cidadao cidadao) {

        CartaoVacina cartaoVacina = new CartaoVacina(cidadaoDTO.getCartaoSus());
        this.cartaoVacinaRepository.save(cartaoVacina);

        cidadao.setNome(cidadaoDTO.getNome());
        cidadao.setCpf(cidadaoDTO.getCpf());
        cidadao.setEndereco(cidadaoDTO.getEndereco());
        cidadao.setCartaoSus(cidadaoDTO.getCartaoSus());
        cidadao.setEmail(cidadaoDTO.getEmail());
        cidadao.setData_nascimento(cidadaoDTO.getData_nascimento());
        cidadao.setTelefone(cidadaoDTO.getTelefone());
        cidadao.setProfissoes(padronizaSetsDeString(cidadaoDTO.getProfissoes()));
        cidadao.setComorbidades(padronizaSetsDeString(cidadaoDTO.getComorbidades()));
        cidadao.setSenha(cidadaoDTO.getSenha());
        cidadao.setCartaoVacina(cartaoVacina);
    }

    /**
     * Metodo responsavel por alterar os atributos de Cidadao. Verificas-se quais informacoes deseja-se mudar, de acordo
     * com as informacoes que vem do DTO.
     *
     * @param headerToken      - token do Cidadao que tera seus dados alterardos
     * @param cidadaoUpdateDTO - DTO contendo as novas informacoes desejadas para o usuario
     * @throws ServletException
     * @throws IllegalArgumentException
     */
    @Override
    public Cidadao updateCidadao(String headerToken, CidadaoUpdateDTO cidadaoUpdateDTO)  throws ServletException, IllegalArgumentException{

        var cidadao = getCidadaoById(jwtService.getCidadaoDoToken(headerToken));
        copyDTOCidadaoUpdateToEntity(cidadaoUpdateDTO, cidadao);
        this.salvarCidadao(cidadao);
        return cidadao;
    }


    /**
     * Metodo privado responsavel por verificar os atributos do DTO e trocar com os de Cidadao, se nescessário.
     * @param cidadao - cidadao verificado que desejasse alterar informações
     * @param cidadaoUpdateDTO - DTO contendo as novas informacoes desejadas para o usuario
     * @throws ServletException
     */
    private void copyDTOCidadaoUpdateToEntity(CidadaoUpdateDTO cidadaoUpdateDTO, Cidadao cidadao) throws IllegalArgumentException {

        cidadao.setComorbidades(cidadaoUpdateDTO.getComorbidades());
        cidadao.setEmail(cidadaoUpdateDTO.getEmail());
        cidadao.setEndereco(cidadaoUpdateDTO.getEndereco());
        cidadao.setSenha(cidadaoUpdateDTO.getSenha());
        cidadao.setNome(cidadaoUpdateDTO.getNome());
        cidadao.setTelefone(cidadaoUpdateDTO.getTelefone());
        cidadao.setProfissoes(cidadaoUpdateDTO.getProfissoes());
    }

    /**
     * Metodo responsavel por adicionar uma vacina no cartão de vacinação do cidadão e avaçar o estado de vacinação
     *
     * @param cpfCidadao - Cpf do Cidadão que deve receber a dose da vacina
     * @param vacina     - O tipo da vacina
     * @param dataVacina - A data em que a vacina foi aplicada
     * @author Caetano Albuquerque
     */
    public void recebeVacina(String cpfCidadao, Vacina vacina, LocalDate dataVacina) {
        Cidadao cidadao = this.getCidadaoById(cpfCidadao);
        cidadao.receberVacina(vacina, dataVacina);
        this.cartaoVacinaRepository.save(cidadao.getCartaoVacina());
    }

    /**
     * Metodo que deve ser chamado por um funcionario para habilitar os cidadãos que podem ser habilitados
     * para segunda dose
     *
     * @author Caetano Albuquerque
     */
    @Override
    public int habilitarSegundaDose()  {
        int result = 0;

        List<Cidadao> cidadaos = this.cidadaoRepository.findAll();
        int qtdCidadaosQuePossoLiberar = this.getQtdDosesSemDependencia();
        result = qtdCidadaosQuePossoLiberar;

        StringBuilder emails = new StringBuilder();
        StringBuilder telefones = new StringBuilder();

        for (Cidadao cidadao : cidadaos) {
            if (cidadao.getSituacao() instanceof Tomou1Dose && cidadao.getDataPrevistaSegundaDose().isBefore(LocalDate.now())) {
                if (qtdCidadaosQuePossoLiberar > 0 && this.loteService.existeLoteDaVacina(cidadao.getTipoVacina())) {
                    cidadao.avancarSituacaoVacina();
                    this.cartaoVacinaRepository.save(cidadao.getCartaoVacina());
                    emails.append(cidadao.getEmail()).append(", ");
                    telefones.append(cidadao.getTelefone()).append(", ");
                    qtdCidadaosQuePossoLiberar--;
                } else if (qtdCidadaosQuePossoLiberar <= 0) break;
            }
        }
        this.enviaAlertaVacinacao(emails.toString(), telefones.toString(), "segunda");

        return result - qtdCidadaosQuePossoLiberar;
    }

    /**
     * Metodo responsavel por calcular a quantidade de cidadãos estão aptos a receber a vacina, seja
     * primeira ou segunda dose
     *
     * @return a quantidade total de pessoas habilitadas
     * @author Caetano Albuquerque
     */
    private int getQtdHabilitados() {
        int result = 0;

        List<Cidadao> cidadaos = this.cidadaoRepository.findAll();

        for (Cidadao cidadao : cidadaos) {
            if (cidadao.getSituacao() instanceof Habilitado1Dose || cidadao.getSituacao() instanceof Habilitado2Dose) {
                result++;
            }
        }
        return result;
    }

    /**
     * Metodo responsavél por calcular a diferença entre a quantidade de doses cadastradas no sistema, e a quantidade
     * de pessoas já habilitadas para receber alguma dose
     *
     * @return a diferença entre a quantidade de doses e a quantidade de pessoas já habilitadas
     * @author Caetano Albuquerque
     */
    private int getQtdDosesSemDependencia() {
        return this.loteService.getQtdVacinaDisponivel() - this.getQtdHabilitados();
    }


    /**
     * Método que verifica se temos doses suficientes para todas as pessoas mais velhas do que a idade a ser habilitada
     *
     * @return true caso tenhamos mais doses do que pessoas a serem habilitadas, false caso contrario
     * @author Caio Silva
     */
    public boolean podeAlterarIdade(int idade) {
        List<Cidadao> cidadaos = this.cidadaoRepository.findAll();
        int contProvaveisHabilitados = 0;

        for (Cidadao cidadao : cidadaos) {
            Integer idadeCidadao = CalculaIdade.idade(cidadao.getData_nascimento());
            if (idadeCidadao >= idade && cidadao.getSituacao() instanceof NaoHabilitado)
                contProvaveisHabilitados++;
        }

        return this.getQtdDosesSemDependencia() >= contProvaveisHabilitados + this.getQtdHabilitados();
    }

    /**
     * Método que verifica se temos doses suficientes para todas as pessoas que tenham o requisito que o funcionário quer habilitar
     *
     * @param requisito requisito a ser habilitado
     * @return true caso tenhamos mais doses do que pessoas a serem habilitadas, false caso contrario
     * @author Caio Silva
     */
    public boolean podeHabilitarRequisito(RequisitoDTO requisito) {
        int contProvaveisHabilitados = this.contaCidadaosAtendeRequisito(requisito);
        return this.getQtdDosesSemDependencia() >= contProvaveisHabilitados + this.getQtdHabilitados();
    }

    /**
     * Metodo que pega o estagio de vacinação do cidadao
     * @param cpf
     * @return retorna a situacao do cidadao
     * @author Holliver Costa
     */
    @Override
    public Situacao getSituacao(String cpf){
        return this.getCidadaoById(cpf).getSituacao();
    }

     /** Método que habilita cidadaos utilizando a idade como requisito
     *
     * @param requisito idade a ser utilizada como requisito
     * @author Caio Silva
     */
    public void habilitaPelaIdade(Requisito requisito) {
        Integer idadeRequisito = requisito.getIdade();
        List<Cidadao> cidadaos = this.cidadaoRepository.findAll();

        StringBuilder emails = new StringBuilder();
        StringBuilder telefones = new StringBuilder();

        for(Cidadao cidadao: cidadaos){
            Integer idadeCidadao = CalculaIdade.idade(cidadao.getData_nascimento());
            if(idadeCidadao >= idadeRequisito && cidadao.getSituacao() instanceof NaoHabilitado) {
                cidadao.avancarSituacaoVacina();
                cartaoVacinaRepository.save(cidadao.getCartaoVacina());
                emails.append(cidadao.getEmail()).append(", ");
                telefones.append(cidadao.getTelefone()).append(", ");
            }
        }
        this.enviaAlertaVacinacao(emails.toString(), telefones.toString(), "primeira");
    }

    /**
     * Método que habilita cidadaos utilizando o requisito no parametro como requisito
     *
     * @param requisito requisito que irá habilitar cidadaos
     * @author Caio Silva
     */
    public void habilitaPorRequisito(Requisito requisito) {
        String requisitoPodeHabilitar = requisito.getRequisito();
        Integer idadeRequisito = requisito.getIdade();

        StringBuilder emails = new StringBuilder();
        StringBuilder telefones = new StringBuilder();

        List<Cidadao> cidadaos = this.cidadaoRepository.findAll();

        for (Cidadao cidadao : cidadaos) {
            Integer idadeCidadao = CalculaIdade.idade(cidadao.getData_nascimento());
            Set<String> profissoesCidadao = cidadao.getProfissoes();
            Set<String> comorbidadesCidadao = cidadao.getComorbidades();

            if (profissoesCidadao.contains(requisitoPodeHabilitar) || comorbidadesCidadao.contains(requisitoPodeHabilitar)) {
                if (idadeCidadao >= idadeRequisito && cidadao.getSituacao() instanceof NaoHabilitado) {
                    cidadao.avancarSituacaoVacina();
                    cartaoVacinaRepository.save(cidadao.getCartaoVacina());
                    emails.append(cidadao.getEmail()).append(", ");
                    telefones.append(cidadao.getTelefone()).append(", ");
                }
            }
        }
        this.enviaAlertaVacinacao(emails.toString(), telefones.toString(), "primeira");
    }

    /**
     * Metodo para enviar os alertas para os cidadãos que foram habilitados
     * @param emails - uma string contendo todos os endereços de email
     * @param telefones - uma string contendo todos os numeros de telefone
     * @param dose - uma string indicando a dose
     * @author Caetano Albuquerque
     */
    private void enviaAlertaVacinacao(String emails, String telefones, String dose){
        this.enviarEmailsAptoParaVacina(emails, dose);
        this.enviarSms(telefones);
    }

    /**
     * Metodo que envia o email para os cidadaos quando eles se tornam aptos a tomarem vacina
     * @param emails - uma string contendo todos os endereços de email
     * @param dose - uma string indicando a dose
     * @author Caetano Albuquerque
     */
    private void enviarEmailsAptoParaVacina(String emails, String dose){
        if (!emails.equals("")) {
            emails = emails.substring(0, emails.length() - 2);
            Email.enviarAlertaVacinacao(String.format(ASSUNTO_EMAIL_ALERTA_HABILITADO_PARA_VACINA, dose),
                    String.format(MENSSAGEM_EMAIL_ALERTA_HABILITADO_PARA_VACINA, dose), emails);
        }
    }

    /**
     * Metodo que deve enviar os sms para os cidadãos, porem não foi implementado por falta de soluções gratuitas
     * @param telefones - uma string contendo todos os numeros de telefone
     * @author Caetano Albuquerque
     */
    private void enviarSms(String telefones){
        //Enviar sms
    }

    /**
     * Metodo que deve retornar a um determinado cidadao o seu estado na vacinação
     *
     * @param headerToken - token de login do cidadao
     * @return o estado de vacinação do cidadao
     * @throws ServletException
     * @author Caetano Albuquerque
     */
    @Override
    public String getEstadoVacinacao(String headerToken) throws ServletException {
        String id = jwtService.getCidadaoDoToken(headerToken);
        Cidadao cidadao = this.getCidadaoById(id);
        return cidadao.getSituacao().toString();
    }

    /**
     * conta quantos cidadaos temos de idade igual ou maior do que a passada por parametro que não estao habilitados
     *
     * @param idade idade usada como parametro para o calculo
     * @return quantidade de cidadaos com idade maior ou igual a idade passada por parametro que não estão habilitados
     * @author Caio Silva
     */
    public int contaCidadaosAcimaIdade(int idade) {
        int qtdCidadaosMaisVelhos = 0;
        List<Cidadao> cidadaos = this.cidadaoRepository.findAll();

        for (Cidadao cidadao : cidadaos) {
            Integer idadeCidadao = CalculaIdade.idade(cidadao.getData_nascimento());
            if (idadeCidadao >= idade && cidadao.getSituacao() instanceof NaoHabilitado)
                qtdCidadaosMaisVelhos++;
        }
        return qtdCidadaosMaisVelhos;
    }

    /**
     * conta quantos cidadãos tem no sistema ainda não habilitados que atendem a esse requisito
     *
     * @param requisito requisito usado como parametro
     * @return quantidade de cidadaos que atendem ao requisito inserido e ainda não estão habilitados
     * @author
     */
    @Override
    public int contaCidadaosAtendeRequisito(RequisitoDTO requisito) {
        int contProvaveisHabilitados = 0;
        String requisitoPodeHabilitar = requisito.getRequisito();
        Integer idadeRequisito = requisito.getIdade();

        List<Cidadao> cidadaos = this.cidadaoRepository.findAll();

        for (Cidadao cidadao : cidadaos) {
            Integer idadeCidadao = CalculaIdade.idade(cidadao.getData_nascimento());
            Set<String> profissoesCidadao = cidadao.getProfissoes();
            Set<String> comorbidadesCidadao = cidadao.getComorbidades();

            if (profissoesCidadao.contains(requisitoPodeHabilitar) || comorbidadesCidadao.contains(requisitoPodeHabilitar)) {
                if (idadeCidadao >= idadeRequisito && cidadao.getSituacao() instanceof NaoHabilitado)
                    contProvaveisHabilitados++;
            }
        }
        return contProvaveisHabilitados;
    }

    /**
     * Método que coloca em uma lista todos os cidadões que estão habilitados.
     * @return uma lista dos cidadões que estão habilitados
     */
    @Override
    public List<String> listarCidadaosHabilitados() {
        List<String> cidadaosHabilitados = new ArrayList<>();
        List<Cidadao> cidadaos = this.cidadaoRepository.findAll();

        for (Cidadao cidadao : cidadaos){
            if (cidadao.getSituacao() instanceof Habilitado1Dose || cidadao.getSituacao() instanceof Habilitado2Dose){
                cidadaosHabilitados.add(cidadao.getCpf());
            }
        }

        return cidadaosHabilitados;
    }

    /**
     * Método que verifica se a Data da segunda dose chegou
     */
    @Override
    public void verificaDataSegundaDose() {

        List<Cidadao> listaCidadaos = cidadaoRepository.findAll();
        if(listaCidadaos.isEmpty()) return;

        for (Cidadao cid : listaCidadaos) {
            if (cid.getSituacao() instanceof Tomou1Dose) {
                if ((cid.getDataPrevistaSegundaDose().compareTo(LocalDate.now())) < 1) {
                    cid.avancarSituacaoVacina();
                }
            }
        }
    }

    /**
     * Método que verifica se a Data marcada para vacinação chegou, e chama o metodo que avisa para aqueles que chegaram
     */
    public void verificaDataMarcadaVacinacao() {

        List<Cidadao> listaCidadaos = cidadaoRepository.findAll();
        System.out.println("chegou 1");
        if(listaCidadaos.isEmpty()) return ;

        for (Cidadao cid : listaCidadaos) {

            if (agendaService != null) {
                System.out.println("chegou 2");
                if (agendaService.getAgendamentobyCpf(cid.getCpf()).getData().compareTo(LocalDate.now()) == 0) {
                    enviaAlertaVacinacaoMarcadaParaHoje(cid.getEmail(), cid.getTelefone(), cid.getCpf());

                }
            }

        }
    }

    /**
     * Metodo para enviar o alerta para o cidadão para alerta-lo que hoje é o dia marcado para a vacinação
     * @param email - uma string contendo o endereço de email de um cidadão
     * @param telefone - uma string contendo o telefone de um cidadão
     * @param cpf - uma string contendo o cpf de um cidadão
     */
    private void enviaAlertaVacinacaoMarcadaParaHoje(String email, String telefone, String cpf){
        this.enviarEmailsVacinaMarcadaParaHoje(email, cpf);
        this.enviarSmsVacinacaoMarcadaParaHoje(telefone, cpf);
    }

    /**
     * Metodo que envia o email para um cidadão para alerta-lo que hoje é o dia marcado para a vacinação
     * @param email - uma string contendo o endereço de email de um cidadão
     * @param cpf - uma string contendo o cpf de um cidadão
     */
    private void enviarEmailsVacinaMarcadaParaHoje(String email, String cpf){
        if (!email.equals("")) {
            Agenda agendamento = agendaService.getAgendamentobyCpf(cpf);
            email = email.substring(0, email.length() - 2);
            Email.enviarAlertaVacinacao(String.format(ASSUNTO_EMAIL_ALERTA_DIA_MARCADO_VACINA, LocalDate.now().getDayOfYear()),
                    String.format(MENSSAGEM_EMAIL_ALERTA_DIA_MARCADO_VACINA, agendamento.getLocal(), agendamento.getHorario()), email);
        }
    }

    /**
     * Metodo que deve enviar os sms para os cidadãos para alerta-los que hoje é o dia marcado para a vacinação,
     * porem não foi implementado por falta de soluções gratuitas
     * @param telefone - uma string contendo o telefone de um cidadão
     * @param cpf - uma string contendo o cpf de um cidadão
     */
    private void enviarSmsVacinacaoMarcadaParaHoje(String telefone, String cpf){
        //Enviar sms
    }

    /**
     * pega um agendamento pelo cpf
     * @param headerToken
     * @return retorna um objeto de agenda
     * @throws ServletException
     */
    @Override
    public Agenda getAgendamentobyCpf(String headerToken) throws ServletException {
        String cpf = jwtService.getCidadaoDoToken(headerToken);

        return this.agendaService.getAgendamentobyCpf(cpf);
    }
}
