package com.projeto.grupo10.vacineja.state;

import com.projeto.grupo10.vacineja.model.usuario.CartaoVacina;
import com.projeto.grupo10.vacineja.model.usuario.Cidadao;
import com.projeto.grupo10.vacineja.model.vacina.Vacina;

import java.time.LocalDate;
import java.util.Date;

public class Tomou1Dose implements Situacao {
    private static final String SITUACAO_ATUAL = "Tomou a 1ª dose";

    public Tomou1Dose(){}

    /**
     * Se a data prevista para segunda dose já tiver passado, o cidadão pode ser habilitado para segunda dose
     * @param cartaoVacina
     */
    @Override
    public void proximaSituacao(CartaoVacina cartaoVacina) {
        LocalDate dataPrevistaSegundaDose = cartaoVacina.getDataPrevistaSegundaDose();
        if (dataPrevistaSegundaDose.isBefore(LocalDate.now())) {
            cartaoVacina.setSituacao(SituacaoEnum.HABILITADO2DOSE);
        }
    }

   
    @Override
    public void proximaSituacao(CartaoVacina cartaoVacina, Vacina vacina, LocalDate data) {}

    @Override
    public String toString() {
        return this.SITUACAO_ATUAL;
    }
}
