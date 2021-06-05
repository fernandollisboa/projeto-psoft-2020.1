package com.projeto.grupo10.vacineja.state;

import com.projeto.grupo10.vacineja.model.usuario.CartaoVacina;
import com.projeto.grupo10.vacineja.model.vacina.Vacina;
import com.projeto.grupo10.vacineja.util.email.Email;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Date;

public class Habilitado2Dose implements Situacao {
    private static final String SITUACAO_ATUAL = "Habilitado para tomar a 2ª dose";

    public Habilitado2Dose() {}

    @Override
    public void proximaSituacao(CartaoVacina cartaoVacina) {
        cartaoVacina.setSituacao(SituacaoEnum.VACINACAOFINALIZADA);
    }

    @Override
    public void proximaSituacao(CartaoVacina cartaoVacina, Vacina vacina, LocalDate data) {
        cartaoVacina.setDataSegundaDose(data);
        this.proximaSituacao(cartaoVacina);
    }

    @Override
    public String toString() {
        return this.SITUACAO_ATUAL;
    }

}
