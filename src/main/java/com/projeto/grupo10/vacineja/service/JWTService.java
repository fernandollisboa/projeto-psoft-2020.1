package com.projeto.grupo10.vacineja.service;

import com.projeto.grupo10.vacineja.DTO.CidadaoLoginDTO;
import com.projeto.grupo10.vacineja.filtros.TokenFilter;
import com.projeto.grupo10.vacineja.util.ErroLogin;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JWTService {

    @Autowired
    private CidadaoService cidadaoService;
    private final String TOKEN_KEY = "login correto";

    /**
     * Método responsável por autenticar um login. Verifica se a senha pertence ao usuário que efetuou o login. Também
     * verifica se o login foi feito por um funcionario ou administrador e se aqueles valores de login sao realmente
     * de algum usuário desse tipo.
     * @param cidadaoLogin informações do login
     * @return retorna um token para ser utilizado ao executar ações no sistema
     */
    public ResponseEntity<?> autentica(CidadaoLoginDTO cidadaoLogin){
        if(!cidadaoService.validaCidadaoSenha(cidadaoLogin)){
            return ErroLogin.erroLoginSenhaUsuarioErrado();
        }
        if(cidadaoService.validaLoginComoFuncionario(cidadaoLogin)){
            return ErroLogin.erroLoginNaoAutorizadoFuncionario();
        }
        if(cidadaoService.validaLoginComoAdministrador(cidadaoLogin)){
            return ErroLogin.erroLoginNaoAutorizadoAdministrador();
        }

        String token = geraToken(cidadaoLogin.getCpfLogin(), geraTipoLogin(cidadaoLogin));
        return new ResponseEntity<String>(token, HttpStatus.OK);
    }

    /**
     * Gera uma parte do token que será retornado no método autentica
     * @param cidadaoLoginDTO informações do login
     * @return a parte do token responsável pelo tipo do login realizado
     */
    private Map<String,Object> geraTipoLogin(CidadaoLoginDTO cidadaoLoginDTO){
        Map mapaTipoLogin = new HashMap<String,Object>();
        mapaTipoLogin.put("tipoLogin",cidadaoLoginDTO.getTipoLogin());

        return mapaTipoLogin;
    }

    /**
     * Gera o token para com informações do cpf do usuario, do tipoLogin. Todas essas informações são criptografadas
     * e concatenada com a duração do token
     * @param cpf cpf do usuario que executou o login e que será dono daquele token
     * @param tipoLogin String montada pelo método geraTipoLogin
     * @return o token gerado
     */
    private String geraToken(String cpf, Map<String,Object> tipoLogin){
        return Jwts.builder().setHeaderParam("typ","JWT").setClaims(tipoLogin).setSubject(cpf)
                .signWith(SignatureAlgorithm.HS512, TOKEN_KEY)
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000)).compact();
    }

    /**
     * Resgata um cidadao pelo ao token inserido ao executar uma requisição ao sistema
     * @param authoHeader token inserido para o método resgatar o cidadão
     * @return cpf do cidadão "dono" desse token
     * @throws ServletException caso o token esteja errado ou já expirado
     */
    public String getCidadaoDoToken(String authoHeader) throws ServletException{
        String authorizationHeader = "Bearer "+ authoHeader;
        this.verificaToken(authorizationHeader);

        // Extraindo apenas o token do cabecalho.
        String token = authorizationHeader.substring(TokenFilter.TOKEN_INDEX);

        String subject = null;
        try {
            subject = Jwts.parser().setSigningKey(TOKEN_KEY).parseClaimsJws(token).getBody().getSubject();
        } catch (SignatureException e) {
            throw new ServletException("Token invalido ou expirado!");
        }
        return subject;
    }

    /**
     * Método responsável por resgatar o tipo do login que foi feito através do token inserido em uma requisição ao sistema
     * @param authoHeader token inserido para o método resgatar o tipo de login
     * @return tipo do login que vem no token inserido como parametro
     * @throws ServletException caso o token esteja errado ou já expirado
     */
    public String getTipoLogin(String authoHeader) throws ServletException {
        String authorizationHeader = "Bearer "+ authoHeader;
        this.verificaToken(authorizationHeader);

        // Extraindo apenas o token do cabecalho.
        String token = authorizationHeader.substring(TokenFilter.TOKEN_INDEX);

        String subject = null;
        try {
            Claims claims = Jwts.parser().setSigningKey(TOKEN_KEY).parseClaimsJws(token).getBody();
            subject = claims.get("tipoLogin").toString();
        } catch (SignatureException e) {
            throw new ServletException("Token invalido ou expirado!");
        }
        return subject;
    }

    /**
     * Verifica se o token não é nulo ou se ele esta em um formato válido
     * @param authorizationHeader token a ser verificado
     * @throws ServletException caso o token seja nulo ou não tenha um formato válido
     */
    private void verificaToken(String authorizationHeader)  throws ServletException{
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new ServletException("Token inexistente ou mal formatado!");
        }
    }



}
