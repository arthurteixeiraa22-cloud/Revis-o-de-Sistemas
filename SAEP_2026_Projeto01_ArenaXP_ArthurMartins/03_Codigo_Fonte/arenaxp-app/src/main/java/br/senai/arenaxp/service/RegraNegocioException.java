package br.senai.arenaxp.service;

/** Violação de regra de negócio com mensagem amigável para o usuário. */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
