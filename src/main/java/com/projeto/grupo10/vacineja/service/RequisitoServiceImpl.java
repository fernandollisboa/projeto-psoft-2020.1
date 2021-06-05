package com.projeto.grupo10.vacineja.service;

import com.projeto.grupo10.vacineja.DTO.RequisitoDTO;
import com.projeto.grupo10.vacineja.model.requisitos_vacina.*;
import com.projeto.grupo10.vacineja.repository.RequisitoRepository;
import com.projeto.grupo10.vacineja.util.PadronizaString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.projeto.grupo10.vacineja.util.PadronizaString.padronizaString;

@Service
public class RequisitoServiceImpl implements RequisitoService{

    @Autowired
    RequisitoRepository requisitoRepository;

    /**
     * Método responsável por setar true o podeVacinar da idade passada por parametro
     * @param idade idade que poderá habilitar cidadãos
     * @author Caio Silva
     */
    @Override
    public void setIdade(int idade) {
        Optional<Requisito> idadeRequisito = getRequisitoById("idade");
        Requisito idadeRequisitoNovo;
        if(idadeRequisito.isEmpty()){
            idadeRequisitoNovo = new Requisito("idade",idade,TipoRequisito.IDADE);
            idadeRequisitoNovo.setPodeVacinar();
        } else {
            idadeRequisitoNovo = idadeRequisito.get();
            idadeRequisitoNovo.setIdade(idade);
            idadeRequisitoNovo.setPodeVacinar();
        }
        requisitoRepository.save(idadeRequisitoNovo);
    }

    /**
     * Método responsável por criar um novo requisito que seja uma comorbidade. Um requisito só pode ser criado por um administrador, e sempre será iniciado com a variável podeVacinar = false
     * @param requisito Um DTO do Requisito, com o nome do requisito e a idade mínima
     * @return O requisito que foi criado e armazenado no repository
     * @throws IllegalArgumentException caso a comorbidade já exista como requisito
     * @author Caio Silva
     */
    @Override
    public Requisito setNovaComorbidade(RequisitoDTO requisito) throws IllegalArgumentException{

        if(!getRequisitoById(requisito.getRequisito()).isEmpty()){
            throw new IllegalArgumentException(String.format("Requisito %s já cadastrado",requisito.getRequisito()));
        }
        Requisito novaComorbidade = new Requisito(padronizaString(requisito.getRequisito()),requisito.getIdade(),TipoRequisito.COMORBIDADE);

        requisitoRepository.save(novaComorbidade);

        return getRequisitoById(PadronizaString.padronizaString(requisito.getRequisito())).get();
    }

    /**
     * Método responsável por criar um novo requisito que seja uma profissão. Um requisito só pode ser criado por um administrador, e sempre será iniciado com a variável podeVacinar = false
     * @param requisito Um DTO do Requisito, com o nome do requisito e a idade mínima
     * @return O requisito que foi criado e armazenado no repository
     * @throws IllegalArgumentException caso a profissão já exista como comorbidade
     * @author Caio Silva
     */
    @Override
    public Requisito setNovaProfissao(RequisitoDTO requisito) throws IllegalArgumentException{

        if(!getRequisitoById(requisito.getRequisito()).isEmpty()){
            throw new IllegalArgumentException(String.format("Requisito %s já cadastrado",requisito.getRequisito()));
        }
        Requisito novaComorbidade = new Requisito(padronizaString(requisito.getRequisito()),requisito.getIdade(),TipoRequisito.PROFISSAO);

        requisitoRepository.save(novaComorbidade);

        return getRequisitoById(PadronizaString.padronizaString(requisito.getRequisito())).get();
    }

    /**
     * Método responsável por retornar o requisito utilizando o id, que é o nome do requisito
     * @param requisito nome do requisito a ser retornado
     * @return um Optional do requisito, caso esse requisito não exista no repository será empty
     * @author Caio Silva
     */
    @Override
    public Optional<Requisito> getRequisitoById(String requisito){
        Optional<Requisito> requisitoExistente = requisitoRepository.findById(PadronizaString.padronizaString(requisito));
        return requisitoExistente;
    }

    /**
     * Método responsável por listar uma representação de todas as comorbidades existentes no sistema
     * @return Uma lista com representações em String de todas as comorbidades
     * @throws IllegalArgumentException caso não exista nenhuma comorbidade cadastrada
     */
    @Override
    public List<String> getTodasComorbidades() throws IllegalArgumentException{
        List<Requisito> requisitos = requisitoRepository.findAll();
        List<String> requisitoComorbidades = new ArrayList<>();

        if(requisitos.isEmpty()){
            throw new IllegalArgumentException("Nenhuma comorbidade cadastrada");
        }
        for(Requisito requisito:requisitos){
            if(requisito.getTipoRequisito().equals(TipoRequisito.COMORBIDADE))
                requisitoComorbidades.add(requisito.getRequisito());
        }

        return requisitoComorbidades;
    }

    /**
     * Método reponsável por retornar a idade que pode se vacinar
     * @return idade mínima cadastrada para poder se vacinar
     * @throws IllegalArgumentException caso não tenhuma nenhuma idade cadastrada
     */
    @Override
    public Requisito getIdade()throws IllegalArgumentException {
        Optional<Requisito> idadeRequisito = getRequisitoById("idade");

        if(idadeRequisito.isEmpty())
            throw new IllegalArgumentException("Nenhuma idade registrada como requisito");

        return idadeRequisito.get();
    }

    /**
     * Método responsável por listar uma representação de todas as profissões existentes no sistema
     * @return Uma lista com representações em String de todas as profissões
     * @throws IllegalArgumentException caso não exista nenhuma profissão cadastrada
     */
    @Override
    public List<String> getTodasProfissoes() throws IllegalArgumentException{
        List<Requisito> requisitos = requisitoRepository.findAll();
        List<String> requisitoProfissoes = new ArrayList<>();

        if(requisitos.isEmpty()){
            throw new IllegalArgumentException("Nenhuma profissao cadastrada");
        }
        for(Requisito requisito:requisitos){
            if(requisito.getTipoRequisito().equals(TipoRequisito.PROFISSAO))
                requisitoProfissoes.add(requisito.getRequisito());
        }

        return requisitoProfissoes;
    }

    /**
     * Torna a variável podeVacinar de um determinado requisito true, assim podendo hablitar pessoas a vacina que têm
     * aquele determinado requisito
     * @param requisito um DTO do requisito a ser alterado
     * @return Um DTO do requisitlo alterado
     * @throws IllegalArgumentException caso o requisito não esteja cadastrado no sistema
     */
    @Override
    public RequisitoDTO setPodeVacinar(RequisitoDTO requisito) throws IllegalArgumentException{
        Optional<Requisito> requisitoPodeVacinar = requisitoRepository.findById(requisito.getRequisito());

        if(requisitoPodeVacinar.isEmpty()){
            throw new IllegalArgumentException("Requisito inexistete");
        }

        Requisito requisitoAlterado = requisitoPodeVacinar.get();
        requisitoAlterado.setIdade(requisito.getIdade());
        requisitoAlterado.setPodeVacinar();
        requisitoRepository.save(requisitoAlterado);

        return new RequisitoDTO(requisitoAlterado.getIdade(),requisitoAlterado.getRequisito());
    }

    /**
     * Método responsável por montar uma lista com representações em String de todos os requisitos com podeVacinar = true
     * @return uma lista de representações em String de requisitos com podeVacinar = true
     * @throws IllegalArgumentException caso não existe nenhum requisito com podeVacinar = true
     */
    @Override
    public List<String> requisitosHabilitados() throws IllegalArgumentException {
        List<String> requisitosString = new ArrayList<>();
        List<Requisito> requisitos = requisitoRepository.findAll();

        for(Requisito requisito:requisitos){
            if(requisito.isPodeVacinar()){
                String aux = "Requisito: " + requisito.getRequisito() + "\n" + "Idade: " + requisito.getIdade();
                requisitosString.add(aux);
            }
        }
        if(requisitosString.isEmpty()){
            throw new IllegalArgumentException("Nenhum requisito está habilitado ainda");
        }

        return requisitosString;
    }

}