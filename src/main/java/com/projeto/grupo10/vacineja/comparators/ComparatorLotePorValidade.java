package com.projeto.grupo10.vacineja.comparators;

import com.projeto.grupo10.vacineja.model.lote.Lote;

import java.util.Comparator;

public class ComparatorLotePorValidade implements Comparator<Lote> {

    /**
     * Compara dois lotes a partir da data de validade
     * @param lote1
     * @param lote2
     * @return
     *
     * @author Caetano Albuquerque
     */
    @Override
    public int compare(Lote lote1, Lote lote2) {
        return lote1.getDataDeValidade().compareTo(lote2.getDataDeValidade());
    }
}
