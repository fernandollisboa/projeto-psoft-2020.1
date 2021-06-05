package com.projeto.grupo10.vacineja.DTO;

import javax.validation.constraints.*;
import java.util.Set;

public class CidadaoUpdateDTO {

    @NotBlank(message = "email não pode ser vazio")
    @Email(message = "Esse endereço de email não é valido")
    private String email;

    @NotBlank(message = "nome não pode ser vazio")
    private String nome;

    @NotBlank(message = "endereco não pode ser vazio")
    private String endereco;

    @NotBlank(message = "telefone não pode ser vazio")
    private String telefone;


    private Set<@NotBlank(message = "profissoes não pode ser vazio") String> profissoes;

    @NotEmpty
    private Set<@NotBlank(message = "comorbidades não pode ser vazio") String> comorbidades;

    @NotBlank(message = "Senha não pode ser vazia")
    private String senha;


    public String getEmail() {
        return email;
    }

    public String getNome() {
        return nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public Set<String> getProfissoes() {
        return profissoes;
    }

    public Set<String> getComorbidades() {
        return comorbidades;
    }

    public String getSenha() {
        return senha;
    }
}
