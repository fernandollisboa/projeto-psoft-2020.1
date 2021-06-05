package com.projeto.grupo10.vacineja.DTO;

import com.projeto.grupo10.vacineja.model.vacina.Vacina;

import java.time.LocalDate;

public class LoteDTO {

    private int qtdDoses;

    private LocalDate dataDeValidade;

    public int getQtdDoses() {
        return qtdDoses;
    }

    public LocalDate getDataDeValidade() {
        return dataDeValidade;
    }


}
