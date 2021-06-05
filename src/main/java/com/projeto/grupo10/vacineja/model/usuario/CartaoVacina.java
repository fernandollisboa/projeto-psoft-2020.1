package com.projeto.grupo10.vacineja.model.usuario;

import com.projeto.grupo10.vacineja.model.vacina.Vacina;
import com.projeto.grupo10.vacineja.state.Situacao;
import com.projeto.grupo10.vacineja.state.SituacaoEnum;
import com.sun.istack.NotNull;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

@Entity
public class CartaoVacina {

    @Id
    private String cartaoSus;

    @ManyToOne
    private Vacina vacina;

    private LocalDate dataPrimeiraDose;
    private LocalDate dataSegundaDose;

    private LocalDate dataPrevistaSegundaDose;

    @Enumerated(EnumType.STRING)
    @NotNull
    private SituacaoEnum situacao;

    public CartaoVacina() {
    }

    public CartaoVacina(String cartaoSus) {
        this.cartaoSus = cartaoSus;
        this.situacao = SituacaoEnum.NAO_HABILITADO;
    }

    public void proximaSituacao(){
        this.getSituacaoAtual().proximaSituacao(this);
    }

    public void proximaSituacao(Vacina vacina, LocalDate dataVacina){
        if (this.vacina != null && !this.vacina.equals(vacina)){
            throw new IllegalArgumentException("As duas doses tomadas por um cidadão devem ser do mesmo tipo.");
        }
        this.getSituacaoAtual().proximaSituacao(this, vacina, dataVacina);
    }


    public void setDataSegundaDose(LocalDate dataSegundaDose) {
        this.dataSegundaDose = dataSegundaDose;
    }

    public void setSituacao(SituacaoEnum situacao) {
        this.situacao = situacao;
    }

    public void setDataPrimeiraDose(LocalDate dataPrimeiraDose) {
        this.dataPrimeiraDose = dataPrimeiraDose;
    }

    public void setDataPrevistaSegundaDose(LocalDate dataPrevistaSegundaDoseSegundaDose) {
        this.dataPrevistaSegundaDose = dataPrevistaSegundaDoseSegundaDose;
    }

    public LocalDate getDataPrevistaSegundaDose() {
        return dataPrevistaSegundaDose;
    }

    public void setVacina(Vacina vacina) {
        this.vacina = vacina;
    }

    public int getQtdDoseVacina() {
        return vacina.getNumDosesNecessarias();
    }

    public int getIntervaloVacina() {
        return this.vacina.getDiasEntreDoses();
    }

    public LocalDate getDataPrimeiraDose() {
        return dataPrimeiraDose;
    }

    public Situacao getSituacaoAtual(){
        return this.situacao.getSituacao();
    }

    public String getVacinaString() {
        return vacina.getNomeFabricante();
    }
}
