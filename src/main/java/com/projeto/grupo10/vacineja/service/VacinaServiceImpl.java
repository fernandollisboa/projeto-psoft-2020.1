package com.projeto.grupo10.vacineja.service;


import com.projeto.grupo10.vacineja.model.vacina.Vacina;
import com.projeto.grupo10.vacineja.DTO.VacinaDTO;
import com.projeto.grupo10.vacineja.repository.VacinaRepository;
import com.projeto.grupo10.vacineja.util.PadronizaString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import java.util.List;
import java.util.Optional;

/**
 * Implemetação de VacinaService, realiza funções de cadastro, verificação, listagem e busca de Vacinas
 * no sistema.
 */
@Service
public class VacinaServiceImpl implements VacinaService {

    @Autowired
    private VacinaRepository vacinaRepository;

    @Autowired
    private CidadaoService cidadaoService;

    @Autowired
    private AdministradorService administradorService;


    /**
     * Cria uma nova Vacina a partir de VacinaDTO.
     * Caso já exista uma Vacina com o mesmo nome de Fabricante, uma exceção irá ser lançada.
     *
     * @param vacinaDTO eh o DTO da Vacina a ser criada
     * @return a vacina cadastrada
     */
    @Override
    public Vacina criaVacina(VacinaDTO vacinaDTO, String authToken) throws ServletException {

        administradorService.verificaLoginAdmin(authToken);

        Optional<Vacina> optionalVacina = vacinaRepository.findById(vacinaDTO.getNomeFabricante());

        if(optionalVacina.isPresent()){
            throw new IllegalArgumentException("Já existe vacina desse fabricante cadastrada!");
        }

        validaNumDoses(vacinaDTO.getNumDosesNecessarias());

        if(vacinaDTO.getNumDosesNecessarias() ==1) {
            validaDiasDoseUnica(vacinaDTO.getDiasEntreDoses());
        }
        else{
            validaDiasEntreDoses(vacinaDTO.getDiasEntreDoses());
        }

        Vacina vacina = new Vacina(PadronizaString.padronizaString(vacinaDTO.getNomeFabricante()), vacinaDTO.getNumDosesNecessarias(),
                vacinaDTO.getDiasEntreDoses());

        vacinaRepository.save(vacina);

        return vacina;
    }

    /**
     * Retorna uma lista das vacinas armazenadas
     * @return lista das vacinas armazenadas em VacinaRepository
     */
    @Override
    public List<Vacina> listarVacinas(String headerToken) throws ServletException {
        cidadaoService.verificaTokenFuncionario(headerToken);
        return vacinaRepository.findAll();
    }

    /**
     * Retorna um Optional<Vacina> baseada no seu Id (caso nao exista retornara empty)
     * @param nomeFabricante eh o nome do fabricante da Vacina procurada
     * @return optional da Vacina procurada
     */
    @Override
    public Optional<Vacina> getVacinaById(String nomeFabricante) {
        return this.vacinaRepository.findById(nomeFabricante);
    }



    /**
     * Busca a Vacina em VacinaRepository, se encontrar ele a retorna, se não lança uma exceção
     * @param nomeFabricante eh o nome do fabricante da Vacina
     * @return a vacina procurada
     */
    @Override
    public Vacina fetchVacina(String nomeFabricante) throws NullPointerException{
        Optional<Vacina> optionalVacina = getVacinaById(nomeFabricante);
        if(optionalVacina.isEmpty()){
            throw new NullPointerException("Não há Vacina desse fabricante cadastrada!");
        }
        return optionalVacina.get();
    }

    /**
     * Valida o número de doses informado na criação da vacina, ele deve ser 1 ou 2 para que não seja lançada a exceção.
     * @param numDosesNecessarias eh o numero de doses necessárias
     */
    private void validaNumDoses(int numDosesNecessarias){
        if(numDosesNecessarias > 2 || numDosesNecessarias <= 0){
            throw new IllegalArgumentException("Número de doses inválido! (o mínino é 1 e o máximo 2)");
        }
    }

    /**
     * Valida o periodo entre as doses (caso só tenha uma dose esse método não é chmadao).
     * O período entre as doses deve ser entre   28 e 90.
     * @param diasEntreDoses eh o num de dias entre doses
     */
    private void validaDiasEntreDoses(int diasEntreDoses){
        if(diasEntreDoses < 28 || diasEntreDoses > 90){
            throw new IllegalArgumentException("Quantidade de dias entre doses inválido! O mínimo é 30 e o máximo é 90");
        }
    }

    /**
     * Verifica se o dia entre doses da Vacina de dose única é 0, afinal não tem como tomar uma dose única espaçada.
     * @param diasEntreDoses eh o num de dias entre doses (que deve ser 0 para não lançar exceção)
     */
    private void validaDiasDoseUnica(int diasEntreDoses){
        if(diasEntreDoses != 0){
            throw new IllegalArgumentException("Dia entre doses inválido! Se a Vacina tem dose única, ela não tem " +
                    "intevalo entre doses");
        }

    }

}