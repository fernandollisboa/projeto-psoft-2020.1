package com.projeto.grupo10.vacineja.model.usuario;

import com.projeto.grupo10.vacineja.model.vacina.Vacina;
import com.projeto.grupo10.vacineja.state.Situacao;
import com.sun.istack.NotNull;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;



@Entity
public class Cidadao {

    @Id
    private String cpf;

    @ElementCollection
    private Set<String> profissoes;

    @ElementCollection
    private Set<String> comorbidades;

    @OneToOne
    private FuncionarioGoverno funcionarioGoverno;

    @OneToOne
    private CartaoVacina cartaoVacina;


    private String email;

    private String nome;

    private String endereco;

    @Column(unique=true)
    private String cartaoSus;

    private LocalDate data_nascimento;


    private String telefone;
    


    /**
     * É a senha do Cidadão. Utilizada para fazer o login no sistema.
     */

    @NotNull
    private String senha;

    public Cidadao() {
    }

    public Cidadao(String nome, String cpf,String endereco, String cartaoSus, String email,  LocalDate data_nascimento,
                   String telefone, Set<String> profissoes, Set<String> comorbidades, String senha, CartaoVacina cartaoVacina) {

        this.cpf = cpf;
        this.nome = nome;
        this.endereco = endereco;
        this.cartaoSus = cartaoSus;
        this.email = email;
        this.data_nascimento = data_nascimento;
        this.telefone = telefone;
        this.profissoes = profissoes;
        this.comorbidades = comorbidades;
        this.funcionarioGoverno = null;
        this.senha = senha;
        this.cartaoVacina = cartaoVacina;

    }
    public String getSenha(){
        return this.senha;
    }

    public void setData_nascimento(LocalDate data_nascimento) {
        this.data_nascimento = data_nascimento;
    }

    public void setCartaoSus(String cartaoSus) {
        this.cartaoSus = cartaoSus;
    }

    public String getCpf(){
        return this.cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public boolean isFuncionario (){
        return this.funcionarioGoverno != null && this.funcionarioGoverno.isAprovado();
    }

    public void setCartaoVacina(CartaoVacina cartaoVacina) {
        this.cartaoVacina = cartaoVacina;
    }

    public boolean isCidadao (){
        return this.funcionarioGoverno == null;
    }

    public boolean aguardandoAutorizacaoFuncionario() {
        return this.funcionarioGoverno != null && !this.funcionarioGoverno.isAprovado();
    }

    public void autorizaCadastroFuncionario(){
        this.funcionarioGoverno.aprovaCadastro();
    }

    public void avancarSituacaoVacina(){
        this.cartaoVacina.proximaSituacao();
    }

    public void receberVacina(Vacina vacina, LocalDate dataVacina) {
        this.cartaoVacina.proximaSituacao(vacina, dataVacina);
    }

    public void setFuncionarioGoverno (FuncionarioGoverno funcionarioGoverno){
        this.funcionarioGoverno = funcionarioGoverno;
    }

    public Situacao getSituacao(){
        return this.cartaoVacina.getSituacaoAtual();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getCartaoSus() {
        return cartaoSus;
    }

    public LocalDate getData_nascimento() {
        return data_nascimento;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Set<String> getProfissoes() {
        return profissoes;
    }

    public void setProfissoes(Set<String> profissoes) {
        this.profissoes = profissoes;
    }

    public Set<String> getComorbidades() {
        return comorbidades;
    }

    public void setComorbidades(Set<String> comorbidades) {
        this.comorbidades = comorbidades;
    }

    public FuncionarioGoverno getFuncionarioGoverno() {
        return funcionarioGoverno;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public LocalDate getDataPrevistaSegundaDose (){
        return this.cartaoVacina.getDataPrevistaSegundaDose();
    }

    public String getTipoVacina() {
        return this.cartaoVacina.getVacinaString();
    }

    public CartaoVacina getCartaoVacina() {
        return cartaoVacina;
    }
}