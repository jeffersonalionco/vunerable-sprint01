package br.unipar.frameworks.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Config do JWT lida do application.properties (prefixo jwt.).
 */
@ConfigurationProperties(prefix = "jwt")
public class PropriedadesJwt {

    private String segredo;
    private long expiracaoMs = 86_400_000L;

    public String getSegredo() {
        return segredo;
    }

    public void setSegredo(String segredo) {
        this.segredo = segredo;
    }

    public long getExpiracaoMs() {
        return expiracaoMs;
    }

    public void setExpiracaoMs(long expiracaoMs) {
        this.expiracaoMs = expiracaoMs;
    }
}
