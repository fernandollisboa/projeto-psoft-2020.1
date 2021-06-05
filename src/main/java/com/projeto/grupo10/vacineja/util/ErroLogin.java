package com.projeto.grupo10.vacineja.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ErroLogin {
    static final String ERRO_AUTENTICAR_LOGIN_USUARIO_SENHA_ERRADO = "Cpf ou senha incorreto";
    static final String ERRO_AUTENTICAR_LOGIN_ADMINISTRADOR = "Esse usuário não pode realizar login como administrador";
    static final String ERRO_AUTENTICAR_LOGIN_FUNCIONARIO = "Esse usuário não pode realizar login como funcionario";
    static final String ERRO_TOKEN_INVALIDO = "Token invalido ou expirado!";


    public static ResponseEntity<CustomErrorType> erroLoginSenhaUsuarioErrado() {
        return new ResponseEntity<CustomErrorType>( new CustomErrorType(ErroLogin.ERRO_AUTENTICAR_LOGIN_USUARIO_SENHA_ERRADO), HttpStatus.FORBIDDEN);
    }

    public static ResponseEntity<CustomErrorType> erroLoginNaoAutorizadoFuncionario() {
        return new ResponseEntity<CustomErrorType>(new CustomErrorType(ErroLogin.ERRO_AUTENTICAR_LOGIN_FUNCIONARIO), HttpStatus.FORBIDDEN);
    }

    public static ResponseEntity<CustomErrorType> erroLoginNaoAutorizadoAdministrador() {
        return new ResponseEntity<CustomErrorType>(new CustomErrorType(ErroLogin.ERRO_AUTENTICAR_LOGIN_ADMINISTRADOR), HttpStatus.FORBIDDEN);
    }

    public static ResponseEntity<CustomErrorType> erroTokenInvalido(){
        return new ResponseEntity<CustomErrorType>(new CustomErrorType(ErroLogin.ERRO_TOKEN_INVALIDO), HttpStatus.FORBIDDEN);
    }
}
