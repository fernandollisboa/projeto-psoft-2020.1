package com.projeto.grupo10.vacineja.state;

import com.projeto.grupo10.vacineja.model.usuario.CartaoVacina;
import com.projeto.grupo10.vacineja.model.usuario.Cidadao;
import com.projeto.grupo10.vacineja.model.vacina.Vacina;

import java.time.LocalDate;
import java.util.Date;

public class NaoHabilitado implements Situacao{
    private static final String SITUACAO_ATUAL = "Não habilitado para tomar a vacina";
    private static final String MENSSAGEM_ERRO_TENTATIVA_VACINACAO = "Cidadão não habilitado para tomar a vacina";

    public NaoHabilitado() {
    }


    @Override
    public void proximaSituacao(CartaoVacina cartaoVacina) {
        cartaoVacina.setSituacao(SituacaoEnum.HABILITADO1DOSE);
    }

    @Override
    public void proximaSituacao(CartaoVacina cartaoVacina, Vacina vacina, LocalDate data) {
        throw new IllegalArgumentException(this.MENSSAGEM_ERRO_TENTATIVA_VACINACAO);
    }

    @Override
    public String toString() {
        return this.SITUACAO_ATUAL;
    }
}
