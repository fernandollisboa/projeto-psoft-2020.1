package com.projeto.grupo10.vacineja.job;

import com.projeto.grupo10.vacineja.repository.AgendaRepository;
import com.projeto.grupo10.vacineja.repository.CidadaoRepository;
import com.projeto.grupo10.vacineja.service.AgendaService;
import com.projeto.grupo10.vacineja.service.CidadaoService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Verificador programado responsável por checar as Data de Segunda dose dos Cidadãos. Quando a data chegar o state do cidadão
 * é automáticamente modificado (isso acontece toda vez que o programa é executado ou às 00hrs todos os dias).
 * @author fernandollisboa
 */
@Component
@EnableScheduling
public class Verificador implements InitializingBean {
    @Autowired
    CidadaoService cidadaoService;

    @Autowired
    CidadaoRepository cidadaoRepository;

    @Autowired
    AgendaService agendaService;

    @Autowired
    AgendaRepository agendaRepository;


    /**
     * Método que verifica se a data da segunda dose dos cidadãos chegou. É agendada para todos os dia a meia-noite
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void verificadorDataSegundaDose() {
        cidadaoService.habilitarSegundaDose();
    }

    /**
     * Método que verifica se a data da segunda dose dos cidadãos chegou. É agendada para todos os dia a meia-noite
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void verificadorDataAgendada()  {
        cidadaoService.verificaDataMarcadaVacinacao();
    }


    /**
     * Método que roda toda vez que o Programa
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        verificadorDataSegundaDose();
        verificadorDataAgendada();
    }
}
