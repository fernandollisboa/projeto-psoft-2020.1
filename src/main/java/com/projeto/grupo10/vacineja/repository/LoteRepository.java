package com.projeto.grupo10.vacineja.repository;

import com.projeto.grupo10.vacineja.model.lote.Lote;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface LoteRepository extends JpaRepository<Lote,Long> {
    List<Lote> findAllByNomeFabricanteVacina(String nomeFabricante);
    Optional<Lote> findAllByQtdDosesDisponiveisGreaterThanAndDataDeValidadeBeforeOrderByDataDeValidadeAsc(int greaterThan,LocalDate data);
    List<Lote> findAllByQtdDosesDisponiveisGreaterThanAndNomeFabricanteVacina(int greaterThan, String nomeFabricante);
    Optional<Lote> findFirstByQtdDosesDisponiveisGreaterThanAndNomeFabricanteVacina(int greaterThan, String nomeFabricante);
}
