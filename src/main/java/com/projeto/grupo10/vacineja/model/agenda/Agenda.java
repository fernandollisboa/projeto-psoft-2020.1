package com.projeto.grupo10.vacineja.model.agenda;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;
import java.util.Date;
@Entity
public class Agenda {
    @Id
    private String cpf;
    private LocalDate data;
    private String horario;
    private String local;

    public Agenda(){}
    public Agenda(String cpf, LocalDate data, String horario, String local){
        this.cpf = cpf;
        this.data = data;
        this.horario = horario;
        this.local = local;
    }

    public String getCpf() {
        return cpf;
    }

    public String getHorario() {
        return horario;
    }

    public LocalDate getData() {
        return data;
    }

    public String getLocal() {
        return local;
    }
}
