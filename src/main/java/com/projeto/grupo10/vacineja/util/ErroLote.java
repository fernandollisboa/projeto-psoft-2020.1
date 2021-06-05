package com.projeto.grupo10.vacineja.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ErroLote {
    static final String NAO_HA_LOTES = "Não há nenhum Lote cadastrado!";
    static final String SEM_LOTES_DA_VACINA = "Sem lotes da vacina %s";

    public static ResponseEntity<CustomErrorType> semLotesCadastrados() {
        return new ResponseEntity<CustomErrorType>(new CustomErrorType(String.format(ErroLote.NAO_HA_LOTES)),
                HttpStatus.NOT_FOUND);
    }

    public static ResponseEntity<CustomErrorType> erroCadastroLote(String mensagemErro) {
        return new ResponseEntity<CustomErrorType>(new CustomErrorType(mensagemErro),
                HttpStatus.NOT_FOUND);
    }

    public static ResponseEntity<CustomErrorType> erroSemLoteDaVacina(String vacina){
        return new ResponseEntity<CustomErrorType>(new CustomErrorType(String.format(SEM_LOTES_DA_VACINA, vacina)),
                HttpStatus.NOT_FOUND);
    }
}
