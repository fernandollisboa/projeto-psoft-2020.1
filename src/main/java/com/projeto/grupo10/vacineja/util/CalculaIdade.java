package com.projeto.grupo10.vacineja.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class CalculaIdade {
    public static Integer idade(LocalDate dataNascimento){

        LocalDate dataHoje = LocalDate.now();
        SimpleDateFormat sdfy = new SimpleDateFormat("yyyy");
        SimpleDateFormat sdfm = new SimpleDateFormat("MM");
        SimpleDateFormat sdfd = new SimpleDateFormat("dd");

        int idade = dataHoje.getYear() - dataNascimento.getYear();

        if(dataHoje.getMonthValue() < dataNascimento.getMonthValue())
            idade--;

        if(dataHoje.getDayOfMonth() < dataNascimento.getDayOfMonth())
            idade--;

        return idade;
    }
}
