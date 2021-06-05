package com.projeto.grupo10.vacineja.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

public class ErroAgenda {
    static final String DATA_INVALIDA = "Data inválida: data inserida antes do dia de hoje (%s)";
    static final String DATA_MAIOR = "Não é possivel agendar para depois do dia %s";
    static final String SEM_VACINA_MARCADA_NA_DATA = "Sem vacinação marcada para o cidadaão nessa data";
    static final String SEM_VACINA_MARCADA = "Sem vacinação marcada para o cidadaão";

    public static ResponseEntity<CustomErrorType> erroDataInvalida(LocalDate data) {
        return new ResponseEntity<CustomErrorType>(new CustomErrorType(String.format(ErroAgenda.DATA_INVALIDA, data)),
                HttpStatus.NOT_ACCEPTABLE);
    }
    public static ResponseEntity<CustomErrorType> erroDataMaior(LocalDate data) {
        return new ResponseEntity<CustomErrorType>(new CustomErrorType(String.format(ErroAgenda.DATA_MAIOR, data)),
                HttpStatus.NOT_ACCEPTABLE);
    }

    public static ResponseEntity<CustomErrorType> erroVacinaNaoMarcada() {
        return new ResponseEntity<CustomErrorType>(new CustomErrorType(SEM_VACINA_MARCADA_NA_DATA),
                HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<CustomErrorType> erroNenhumaVacinaMarcada() {
        return new ResponseEntity<CustomErrorType>(new CustomErrorType(SEM_VACINA_MARCADA),
                HttpStatus.BAD_REQUEST);
    }
}
