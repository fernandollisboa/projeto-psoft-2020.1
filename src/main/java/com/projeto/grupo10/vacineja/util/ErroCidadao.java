package com.projeto.grupo10.vacineja.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

public class ErroCidadao {
    static final String USUARIO_SEM_PERMISSAO_FUNCIONARIO = "O usuário não tem permissão para realizar operações" +
            " como funcionario";
    static final String USUARIO_SEM_PERMISSAO_ADMINISTRADOR = "O usuário não tem permissão para realizar operações " +
            "como administrador";
    static final String USUARIO_SEM_CADASTRO_FUNC_PENDENTE = "O usuário %s não esta com um cadastro de funcionario pendente";
    static final String USUARIO_NAO_ENCONTRADO = "O usuário não foi encontrado";
    static final String CIDADAO_CADASTRADO = "Cidadão com cpf %s já está cadastrado";
    static final String CARTAO_SUS_CADASTRADO = "Cartão do SUS com numero %s já está cadastrado";
    static final String EMAIL_INVALIDO = "Email inválido";
    static final String CIDADAO_NAO_CADASTRADO = "Cidadão com cpf %s não está cadastrado";
    static final String CIDADAO_NAO_HABILITADO = "Cidadão não habilitado";
    static final String CPF_INVALIDO = "Não é possivel cadastrar um Cidadão com esse cpf";
    static final String SENHA_INVALIDA = "Não é possivel cadastrar um Cidadão com essa senha";
    static final String DATA_DE_NASCIMENTO_INVALIDA = "Não é possivel cadastrar um Cidadão com essa data de nascimento";
    static final String CARTAO_DO_SUS_INVALIDO = "Não é possivel cadastrar um Cidadão com esse cartao do SUS";

    public static ResponseEntity<CustomErrorType> erroSemPermissaoFuncionario(String usuario) {
        return new ResponseEntity<CustomErrorType>(new CustomErrorType(String.format(ErroCidadao.USUARIO_SEM_PERMISSAO_FUNCIONARIO, usuario)),
                HttpStatus.UNAUTHORIZED);
    }

    public static ResponseEntity<CustomErrorType> erroSemPermissaoAdministrador() {
        return new ResponseEntity<CustomErrorType>(new CustomErrorType(ErroCidadao.USUARIO_SEM_PERMISSAO_ADMINISTRADOR),
                HttpStatus.UNAUTHORIZED);
    }

    public static ResponseEntity<CustomErrorType> erroUsuarioNaoEncontrado() {
        return new ResponseEntity<CustomErrorType>(new CustomErrorType(ErroCidadao.USUARIO_NAO_ENCONTRADO), HttpStatus.NOT_FOUND);
    }
    public static ResponseEntity<CustomErrorType> erroCidadaoCadastrado(String cpf) {
		return new ResponseEntity<CustomErrorType>(
                new CustomErrorType(String.format(ErroCidadao.CIDADAO_CADASTRADO, cpf)),
				HttpStatus.NOT_ACCEPTABLE);
	}

    public static ResponseEntity<CustomErrorType> erroCartaoSUSCadastrado(String cartao_SUS) {
        return new ResponseEntity<CustomErrorType>(
                new CustomErrorType(String.format(ErroCidadao.CARTAO_SUS_CADASTRADO, cartao_SUS)),
                HttpStatus.NOT_ACCEPTABLE);
    }
    public static ResponseEntity<CustomErrorType> erroEmailInvalido() {
        return new ResponseEntity<CustomErrorType>(
                new CustomErrorType(String.format(ErroCidadao.EMAIL_INVALIDO)),
                HttpStatus.NOT_ACCEPTABLE);
    }

    public static ResponseEntity<CustomErrorType> erroCidadaoNaoCadastrado(String cpf) {
        return new ResponseEntity<CustomErrorType>(
                new CustomErrorType(String.format(ErroCidadao.CIDADAO_NAO_CADASTRADO, cpf)),
                HttpStatus.NOT_ACCEPTABLE);
    }

    public static ResponseEntity<CustomErrorType> erroCidadaoNaoHabilitado() {
        return new ResponseEntity<CustomErrorType>(
                new CustomErrorType(ErroCidadao.CIDADAO_NAO_HABILITADO),
                HttpStatus.NOT_ACCEPTABLE);
    }

    public static ResponseEntity<CustomErrorType> erroCPFInvalido() {
        return new ResponseEntity<CustomErrorType>(
                new CustomErrorType(ErroCidadao.CPF_INVALIDO),
                HttpStatus.NOT_ACCEPTABLE);
    }

    public static ResponseEntity<CustomErrorType> erroCartaoSUSInvalido() {
        return new ResponseEntity<CustomErrorType>(
                new CustomErrorType(ErroCidadao.CARTAO_DO_SUS_INVALIDO),
                HttpStatus.NOT_ACCEPTABLE);
    }

    public static ResponseEntity<CustomErrorType> erroSenhaInvalida() {
        return new ResponseEntity<CustomErrorType>(
                new CustomErrorType(ErroCidadao.SENHA_INVALIDA),
                HttpStatus.NOT_ACCEPTABLE);
    }

    public static ResponseEntity<CustomErrorType> erroDataInvalida() {
        return new ResponseEntity<CustomErrorType>(
                new CustomErrorType(ErroCidadao.DATA_DE_NASCIMENTO_INVALIDA),
                HttpStatus.NOT_ACCEPTABLE);
    }

    public static ResponseEntity<CustomErrorType> erroUsuarioSemCadastroPendente(String usuario) {
        return new ResponseEntity<CustomErrorType>(new CustomErrorType(String.format(ErroCidadao.USUARIO_SEM_CADASTRO_FUNC_PENDENTE, usuario)),
                HttpStatus.UNAUTHORIZED);
    }


    /**
     * Metodo responsavel por verificar se o cpf do cidadao contem 11 numerais.
     * @param cpf - cpf que sera analisado
     */
    public static boolean CPFInvalido(String cpf) {
        Boolean retorno = false;
        if (!PadronizaString.validaApenasNumeroString(cpf)) {
            retorno = true;
        }
        if (!PadronizaString.validaTamanhoString(cpf, 11)) {
            retorno = true;
        }
        return retorno;
    }

    /**
     * Metodo responsavel por verificar se o cartao do SUS do cidadao contem 15 numerais.
     * @param cartaoSUS - cartao do SUS que sera analisado
     */
    public static boolean cartaoSUSInvalido(String cartaoSUS) {
        Boolean retorno = false;
        if (!PadronizaString.validaApenasNumeroString(cartaoSUS)) {
            retorno = true;
        }
        if (!PadronizaString.validaTamanhoString(cartaoSUS, 15)) {
            retorno = true;
        }
        return retorno;

    }

    /**
     * Metodo responsavel por verificar se a senha do cidadao nao eh vazia.
     * @param senha - senha que sera analisado
     */
    public static boolean senhaInvalida(String senha) {
        return (PadronizaString.validaStringVazia(senha));
    }

    /**
     * Metodo responsavel por verificar se adata de nascimento do cidadao nao igual ou superior a data atual do sistema
     * @param data - senha que sera analisado
     */
    public static boolean dataInvalida(LocalDate data) {
        LocalDate dataAtual = LocalDate.now();
        if(data.compareTo(dataAtual) >= 0) {
            return true;
        }
        return false;
    }

}
