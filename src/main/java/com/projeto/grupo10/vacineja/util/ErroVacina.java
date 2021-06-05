package com.projeto.grupo10.vacineja.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ErroVacina {
    static final String VACINA_NAO_CADASTRADA = "Vacina da Fabricante %s não cadastrada!";
    static final String VACINA_JA_CADASTRADA = "Vacina da Fabricante %s já está cadastrada, tente adicionar novas doses ao invés de criar!";
    static final String SEM_DOSES_SUFICIENTES = "Não há doses suficientes da Vacina  %s!";
    static final String NAO_HA_VACINAS = "Não há nenhuma Vacina cadastrada!";
    static final String NUM_MAX_DOSES = "O número máximo de doses deve ser 2!";

    public static ResponseEntity<CustomErrorType> erroVacinaNaoCadastrada(String nomeFabricante) {
        return new ResponseEntity<CustomErrorType>(new CustomErrorType(String.format(ErroVacina.VACINA_NAO_CADASTRADA, nomeFabricante)),
                HttpStatus.NOT_FOUND);
    }

    public static ResponseEntity<CustomErrorType> erroVacinaJaCadastrada(String nomeFabricante) {
        return new ResponseEntity<CustomErrorType>(new CustomErrorType(String.format(ErroVacina.VACINA_JA_CADASTRADA, nomeFabricante)),
                HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<CustomErrorType> erroNaoHaVacinaSuficiente(String nomeFabricante) {
        return new ResponseEntity<CustomErrorType>(new CustomErrorType(String.format(ErroVacina.SEM_DOSES_SUFICIENTES, nomeFabricante)),
                HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<CustomErrorType> semVacinasCadastradas() {
        return new ResponseEntity<CustomErrorType>(new CustomErrorType(ErroVacina.NAO_HA_VACINAS),
                HttpStatus.NOT_FOUND);
    }

    public static ResponseEntity<CustomErrorType> numMaxDoses() {
        return new ResponseEntity<CustomErrorType>(new CustomErrorType(String.format(ErroVacina.NUM_MAX_DOSES)),
                HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<CustomErrorType> erroCadastroVacina(String mensagemDeErro){
        return new ResponseEntity<CustomErrorType>(new CustomErrorType(("Erro ao cadastrar Vacina! " + mensagemDeErro)),
                HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<CustomErrorType> erroGenericoVacina(String mensagemDeErro){
        return new ResponseEntity<CustomErrorType>(new CustomErrorType((mensagemDeErro)),
                HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<CustomErrorType> erroListarVacina(String mensagemDeErro){
        return new ResponseEntity<CustomErrorType>(new CustomErrorType((mensagemDeErro)),
                HttpStatus.BAD_REQUEST);
    }
}