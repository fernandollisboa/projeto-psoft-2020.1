package com.projeto.grupo10.vacineja.util;

import com.projeto.grupo10.vacineja.model.lote.Lote;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.function.ToLongFunction;

public class OrdenarPorData implements Comparator<Lote> {
    @Override
    public int compare(Lote lote, Lote lote1) {
        return lote.getDataDeValidade().compareTo(lote1.getDataDeValidade());
    }

}
