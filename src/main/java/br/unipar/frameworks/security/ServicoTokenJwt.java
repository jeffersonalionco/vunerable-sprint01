package br.unipar.frameworks.security;

import br.unipar.frameworks.config.PropriedadesJwt;
import br.unipar.frameworks.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * Gera e valida JWT assinado com HS256.
 * Claims: subject=email, userId, role.
 */

@Service
public class ServicoTokenJwt {

    private final SecretKey chaveAssinatura;
    private final long expiracaoMs;

    public ServicoTokenJwt(PropriedadesJwt propriedadesJwt) {
        String segredo = propriedadesJwt.getSegredo();
        if (segredo == null || segredo.isBlank()) {
            throw new IllegalStateException("jwt.segredo não pode ficar vazio no application.properties");
        }
        this.chaveAssinatura = criarChaveHmac(segredo);
        this.expiracaoMs = propriedadesJwt.getExpiracaoMs();
    }

    /**
     * HS256 exige chave >= 256 bits. Se o segredo no .properties for curto, a gente estica com SHA-256.
     */
    private static SecretKey criarChaveHmac(String segredo) {
        byte[] bytes = segredo.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            try {
                bytes = MessageDigest.getInstance("SHA-256").digest(bytes);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException("SHA-256 indisponível na JVM", e);
            }
        }
        return Keys.hmacShaKeyFor(bytes);
    }

    public String gerarToken(DetalhesUsuarioLab detalhesUsuario) {
        User usuario = detalhesUsuario.getUsuario();
        Date agora = new Date();
        Date expiraEm = new Date(agora.getTime() + expiracaoMs);

        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("userId", usuario.getId())
                .claim("role", usuario.getRole())
                .issuedAt(agora)
                .expiration(expiraEm)
                .signWith(chaveAssinatura)
                .compact();
    }

    public Claims extrairClaims(String token) {
        return Jwts.parser()
                .verifyWith(chaveAssinatura)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean tokenValido(String token) {
        try {
            extrairClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public long getExpiracaoMs() {
        return expiracaoMs;
    }
}
