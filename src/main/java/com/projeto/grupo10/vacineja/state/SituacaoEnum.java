package com.projeto.grupo10.vacineja.state;

public enum SituacaoEnum {

    NAO_HABILITADO{public Situacao getSituacao(){return new NaoHabilitado();}},
    HABILITADO1DOSE{public Situacao getSituacao(){return new Habilitado1Dose();}},
    TOMOU1DOSE{public Situacao getSituacao(){return new Tomou1Dose();}},
    HABILITADO2DOSE{public Situacao getSituacao(){return new Habilitado2Dose();}},
    VACINACAOFINALIZADA{public Situacao getSituacao(){return new VacinacaoFinalizada();}};


    public abstract Situacao getSituacao();
}
