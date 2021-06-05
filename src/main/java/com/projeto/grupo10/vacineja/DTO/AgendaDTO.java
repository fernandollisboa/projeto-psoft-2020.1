package com.projeto.grupo10.vacineja.DTO;

import net.bytebuddy.asm.Advice;

import java.time.LocalDate;

public class AgendaDTO {

    private LocalDate data;
    private String local;
    private String horario;

    public LocalDate getData() {
        return data;
    }
    public String getLocal() {
        return local;
    }
    public String getHorario() {
        return horario;
    }
}
