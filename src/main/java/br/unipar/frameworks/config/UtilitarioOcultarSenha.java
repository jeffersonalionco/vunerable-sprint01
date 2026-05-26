package br.unipar.frameworks.config;

import br.unipar.frameworks.model.User;
import org.springframework.http.ResponseEntity;

import java.util.Collection;
import java.util.Map;


public final class UtilitarioOcultarSenha {

    private UtilitarioOcultarSenha() {
        // classe só de helper, ninguém instancia isso aqui
    }

    /**
     * Entra no objeto de retorno ( e zera password onde achar.
     */
    public static void limparSenhasDoCorpo(Object corpo) {
        if (corpo == null) {
            return;
        }

        if (corpo instanceof User usuario) {
            usuario.setPassword(null);
            return;
        }

        if (corpo instanceof ResponseEntity<?> resposta) {
            limparSenhasDoCorpo(resposta.getBody());
            return;
        }

        if (corpo instanceof Collection<?> colecao) {
            for (Object item : colecao) {
                limparSenhasDoCorpo(item);
            }
            return;
        }

        if (corpo instanceof Map<?, ?> mapa) {
            for (Object valor : mapa.values()) {
                limparSenhasDoCorpo(valor);
            }
        }
    }
}
