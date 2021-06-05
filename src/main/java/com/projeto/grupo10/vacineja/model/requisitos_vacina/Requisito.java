package com.projeto.grupo10.vacineja.model.requisitos_vacina;

import com.sun.istack.NotNull;

import javax.persistence.*;

/**
 * Entidade que representa um requisito. Com o decorrer do processo de vacinação, requisitos vão ser utilizados para
 * habilitar pessoas a se vacinarem. Os requisitos podem ser idade, uma profissão ou uma comorbidade. Caso um cidadão
 * tenha algum desses requisitos e/ou esteja na faixa da idade habilitada para poder se vacinar, o state dele muda
 * automaticamente para podeVacinar.
 * @author Caio Silva
 */
@Entity
public class Requisito{

    @Id
    protected String requisito;
    protected int idade;
    boolean podeVacinar;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TipoRequisito tipoRequisito;

    public Requisito(){}
    public Requisito(String requisito, int idade, TipoRequisito tipoRequisito){
        this.requisito = requisito;
        this.idade = idade;
        this.tipoRequisito = tipoRequisito;
    }
    public int getIdade(){return idade;}
    public String getRequisito(){return requisito;}
    public void setIdade(int idade){this.idade = idade;}
    public void setPodeVacinar(){this.podeVacinar = true;}
    public boolean isPodeVacinar() {
        return podeVacinar;
    }

    public TipoRequisito getTipoRequisito() {
        return tipoRequisito;
    }

    @Override
    public String toString() {
        return "Requisito: " + requisito + '\n' +
                "Idade: " + idade + '\n' +
                "Habilitado para vacina: " + podeVacinar;
    }
}
