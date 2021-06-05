package com.projeto.grupo10.vacineja.util;

import com.projeto.grupo10.vacineja.DTO.RequisitoDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ErroRequisito {
    static final String REQUISITO_EXISTENTE = "Requisito %s já cadastrado";
    static final String REQUISITOS_INEXISTENTE = "Nenhum requisito cadastrado";
    static final String REQUISITO_INEXISTENTE = "Requisito %s não cadastrado";
    static final String REQUISITO_NAO_PODE_HABILITAR = "Não pode habilitar o requisito %s, temos mais cidadãos do que dose";
    static final String IDADE_NAO_CADASTRADA = "Idade minima ainda não cadastrada";
    static final String REQUISITOS_NAO_HABILITADOS = "Nenhum requisito habilitado ainda";



    public static ResponseEntity<CustomErrorType> requisitoCadastrado(RequisitoDTO requisito){
        return new ResponseEntity<CustomErrorType>(new CustomErrorType(String.format(REQUISITO_EXISTENTE,requisito.getRequisito())), HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<CustomErrorType> nenhumRequisitoCadastrado(){
        return new ResponseEntity<CustomErrorType>(new CustomErrorType(REQUISITOS_INEXISTENTE), HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<CustomErrorType> requisitoNaoCadastrado(String requisito){
        return new ResponseEntity<CustomErrorType>(new CustomErrorType(String.format(REQUISITO_INEXISTENTE,requisito)), HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<CustomErrorType> requisitoNaoPodeHabilitar(RequisitoDTO requisito){
        return new ResponseEntity<CustomErrorType>(new CustomErrorType(String.format(REQUISITO_NAO_PODE_HABILITAR,requisito.getRequisito())), HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<CustomErrorType> idadeAindaNaoCadastrada(){
        return new ResponseEntity<CustomErrorType>(new CustomErrorType(IDADE_NAO_CADASTRADA), HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<CustomErrorType> nenhumRequisitoHabilitado(){
        return new ResponseEntity<CustomErrorType>(new CustomErrorType(REQUISITOS_NAO_HABILITADOS), HttpStatus.BAD_REQUEST);
    }


}
