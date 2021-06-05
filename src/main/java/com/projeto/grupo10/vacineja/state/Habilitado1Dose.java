package com.projeto.grupo10.vacineja.state;

import com.projeto.grupo10.vacineja.model.usuario.CartaoVacina;
import com.projeto.grupo10.vacineja.model.vacina.Vacina;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class Habilitado1Dose implements Situacao{
    private static final String SITUACAO_ATUAL = "Habilitado para tomar a 1ª dose";

    public Habilitado1Dose() { }

    /**
     * Ao passar para o proximo estado, caso o vacina escolhida para o cidadão seja de dose unica, a vacinação dele
     * sera considerada finalizada, caso não ele deve passar para o estado "tomou primeira dose"
     * @param cartaoVacina
     */
    @Override
    public void proximaSituacao(CartaoVacina cartaoVacina) {}

    @Override
    public void proximaSituacao(CartaoVacina cartaoVacina, Vacina vacina, LocalDate data) {
        cartaoVacina.setDataPrimeiraDose(data);
        cartaoVacina.setVacina(vacina);

        if (cartaoVacina.getQtdDoseVacina() == 1) {
            cartaoVacina.setSituacao(SituacaoEnum.VACINACAOFINALIZADA);
        } else {
            cartaoVacina.setSituacao(SituacaoEnum.TOMOU1DOSE);

            LocalDate dataSegundaDose = this.diaPropostoSegundaDose(cartaoVacina.getDataPrimeiraDose(),
                    cartaoVacina.getIntervaloVacina());

            cartaoVacina.setDataPrevistaSegundaDose(dataSegundaDose);
        }

    }

    private LocalDate diaPropostoSegundaDose(LocalDate primeiraDose, int diasEntreDoses){
        return primeiraDose.plusDays(diasEntreDoses);
    }

    @Override
    public String toString() {
        return this.SITUACAO_ATUAL;
    }


}
