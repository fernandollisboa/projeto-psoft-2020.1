package com.projeto.grupo10.vacineja.state;

import com.projeto.grupo10.vacineja.model.usuario.CartaoVacina;
import com.projeto.grupo10.vacineja.model.usuario.Cidadao;
import com.projeto.grupo10.vacineja.model.vacina.Vacina;

import java.time.LocalDate;
import java.util.Date;

public interface Situacao {
    void proximaSituacao(CartaoVacina cartaoVacina);
    void proximaSituacao(CartaoVacina cartaoVacina, Vacina vacina, LocalDate data);
    String toString();
}
