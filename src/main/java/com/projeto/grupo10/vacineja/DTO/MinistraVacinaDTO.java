package com.projeto.grupo10.vacineja.DTO;

import org.apache.tomcat.jni.Local;

import java.time.LocalDate;
import java.util.Date;

public class MinistraVacinaDTO {

    private String cpf;
    private LocalDate dataVacinacao;
    private long loteVacina;
    private String tipoVacina;

    public String getCpf() {
        return this.cpf;
    }

    public LocalDate getDataVacinacao() {
        return dataVacinacao;
    }

    public long getLoteVacina() {
        return loteVacina;
    }

    public String getTipoVacina() {
        return tipoVacina;
    }

    public void setTipoVacina(String tipoVacina) {
        this.tipoVacina = tipoVacina;
    }
}
