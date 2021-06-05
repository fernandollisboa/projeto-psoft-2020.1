package com.projeto.grupo10.vacineja.state;
import com.projeto.grupo10.vacineja.model.usuario.CartaoVacina;
import com.projeto.grupo10.vacineja.model.usuario.Cidadao;
import com.projeto.grupo10.vacineja.model.vacina.Vacina;

import java.time.LocalDate;
import java.util.Date;

public class VacinacaoFinalizada implements Situacao{
    private static final String SITUACAO_ATUAL = "Vacinação já finalizada.";

    public VacinacaoFinalizada() {
    }

    @Override
    public void proximaSituacao(CartaoVacina cartaoVacina) {
        throw new IllegalArgumentException(SITUACAO_ATUAL);
    }

    @Override
    public void proximaSituacao(CartaoVacina cartaoVacina, Vacina vacina, LocalDate data) {
        throw new IllegalArgumentException(SITUACAO_ATUAL);
    }

    @Override
    public String toString() {
        return this.SITUACAO_ATUAL;
    }
}
