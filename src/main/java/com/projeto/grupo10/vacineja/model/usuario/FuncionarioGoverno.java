
package com.projeto.grupo10.vacineja.model.usuario;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class FuncionarioGoverno {

    @Id
    private String cpf;

    private String cargo;

    private String localTrabalho;

    private boolean aprovado;

    public FuncionarioGoverno() {
    }

    public FuncionarioGoverno(String cpf, String cargo, String localTrabalho) {
        this.cpf = cpf;
        this.cargo = cargo;
        this.localTrabalho = localTrabalho;
        this.aprovado = false;
    }

    public boolean isAprovado(){
        return aprovado;
    }

    public void aprovaCadastro(){
        this.aprovado = true;
    }

}