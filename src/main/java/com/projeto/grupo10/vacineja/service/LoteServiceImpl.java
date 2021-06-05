package com.projeto.grupo10.vacineja.service;

import com.projeto.grupo10.vacineja.comparators.ComparatorLotePorValidade;
import com.projeto.grupo10.vacineja.model.lote.Lote;
import com.projeto.grupo10.vacineja.DTO.LoteDTO;
import com.projeto.grupo10.vacineja.model.vacina.Vacina;
import com.projeto.grupo10.vacineja.repository.LoteRepository;
import com.projeto.grupo10.vacineja.util.OrdenarPorData;
import com.projeto.grupo10.vacineja.util.PadronizaString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;


/**
 * Implementação re LoteService responsável por ministrar métodos de criação, listagem, remoção, reserva e validação de
 * Vacinas em lotes.
 */
@Service
public class LoteServiceImpl implements LoteService {
    @Autowired
    private LoteRepository loteRepository;

    /**
     *
     * Cria um lote com base em LoteDTO. Realiza verifição jwt para ver se o dono do Token passado é um administrador
     *
     * @param loteDTO eh o modelo do lote
     * @param vacina eh a vacina do lote
     * @return o lote criado
     */
    @Override
    public Lote criaLote(LoteDTO loteDTO, Vacina vacina){
        validaDoseLotes(loteDTO.getQtdDoses());
        validaDataDeValidade(loteDTO.getDataDeValidade());
        Lote lote = new Lote(vacina, loteDTO.getQtdDoses(), loteDTO.getDataDeValidade());

        loteRepository.save(lote);
        return lote;
    }

    /**
     * Retorna todos os lotes armazenados no sistema. Realiza verifição jwt para ver se o dono do Token passado é um funcionário
     *
     * @return a lista de lotes
     */
    @Override
    public List<Lote> listaLotes(){
        return loteRepository.findAll();
    }

    /**
     * Retorna todos os lotes de vacina de um determinado fabricante. Realiza verifição jwt para ver se o dono do Token passado é um funcionário
     *
     * @param nomeFabricante eh o nome do fabricante da vacina procurada
     * @return a lista de lotes da fabricante
     */
    @Override
    public List<Lote> listaLotesPorFabricante(String nomeFabricante){
        List<Lote> resultado = new ArrayList<>();

        for(Lote lote : loteRepository.findAll()){
            if(PadronizaString.padronizaString(lote.getNomeFabricante()).equals(nomeFabricante)){
                resultado.add(lote);
            }
        }

        return resultado;
    }



    /**
     * Verifica se o Lote está vazio.
     *
     * @param lote eh o lote
     * @return booleano informado se está vazio ou nao
     */
    private boolean isLoteVazio(Lote lote){
        return lote.getQtdDosesDisponiveis() == 0;
    }


    /**
     * Valida a quantidade de doses de vacina. Ela deve ser maior ou igual a 0 para não lançar exceção.
     *
     * @param qtdDoses eh a quantidade de doses a ser validada
     */
    private void validaDoseLotes(int qtdDoses) {
        if (qtdDoses <= 0) {
            throw new IllegalArgumentException("Quantidade de doses inválida!");
        }
    }

    /**
     * Valida a data de validade. Ela deve ser depois que hoje.
     *
     * @param date eh a data de valiade a ser validada
     */
    private void validaDataDeValidade(LocalDate date){
        if(date.isBefore(LocalDate.now())){
            throw new IllegalArgumentException("Data de validade inválida");
        }
    }

    /**
     * Verifica a data de validade. Se o Lote estiver vencido ele será removido e uma exceção será lançada");
     * @param lote eh o lote cuja data de validade sera verificada
     */
    private void verificaDataValidade(Lote lote) {
        if (!lote.getDataDeValidade().isAfter(LocalDate.now())) {
            loteRepository.delete(lote);
            throw new IllegalArgumentException("Lote com data de validade vencida! Lote removido. " +
                    "Adicione novo lote para continuar vacinação");
        }
    }


    /**
     * Metodo responsavel por calcular a quantidade total de doses no sistema
     * @return a quantidade inteira de doses de vacinas disponíveis
     *
     * @author Caetano Albuquerque
     */
    public int getQtdVacinaDisponivel(){
        int result = 0;
        List<Lote> lotesVacina = this.loteRepository.findAll();

        for (Lote lote : lotesVacina){
            this.verificaDataValidade(lote);
            result += lote.getQtdDosesDisponiveis();
        }
        return result;
    }

    @Override
    public LocalDate getMaiorValidadeLotes() {
        List<Lote> lotes = listaLotes();
        Collections.sort(lotes, new OrdenarPorData());

        return lotes.get(lotes.size()-1).getDataDeValidade() ;
    }

    /**
     * Metodo responsavel por retirar uma dose de um lote que contenha a vacina requisitada
     * @param tipoVacina - Representa o tipo da vacina
     * @return - A vacina requisitada
     *
     * @author Cartano Albuquerque
     */
    public Vacina retirarVacinaValidadeProxima(String tipoVacina) {
        if (!this.existeLoteDaVacina(tipoVacina)){
            throw new IllegalArgumentException("Sem lotes da vacina requisitada");
        }

        List<Lote> lotes = listaLotesPorFabricante(PadronizaString.padronizaString(tipoVacina));

        Collections.sort(lotes, new ComparatorLotePorValidade());

        Lote loteProxValidade = lotes.get(0);
        loteProxValidade.diminuiQtdDosesDisponiveis();

        if (this.isLoteVazio(loteProxValidade)) this.loteRepository.delete(loteProxValidade);

        return loteProxValidade.getVacina();
    }

    /**
     * Metodo responsavel por verificar a existencia de lotes de uma determinada vacina
     * @param tipoVacina - Tipo da vacina que queremos ver se existe algum lote
     * @return - true se existir algum lote daquele determinado tipo de vacina.
     *
     * @author Caetano Albuquerque
     */
    public boolean existeLoteDaVacina(String tipoVacina){
        List<Lote> lotes = listaLotesPorFabricante(PadronizaString.padronizaString(tipoVacina));
        return !lotes.isEmpty();
    }


}
