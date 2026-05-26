package br.unipar.frameworks.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Lê Authorization Bearer ... e monta o SecurityContext.
 * Sem token válido rotas protegidas caem no 401 do Spring.
 */


@Component
public class FiltroAutenticacaoJwt extends OncePerRequestFilter {

    private static final String PREFIXO_BEARER = "Bearer ";

    private final ServicoTokenJwt servicoTokenJwt;

    public FiltroAutenticacaoJwt(ServicoTokenJwt servicoTokenJwt) {
        this.servicoTokenJwt = servicoTokenJwt;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest requisicao,
            HttpServletResponse resposta,
            FilterChain cadeia
    ) throws ServletException, IOException {
        String token = extrairTokenBearer(requisicao);

        if (token != null && servicoTokenJwt.tokenValido(token)) {
            Claims claims = servicoTokenJwt.extrairClaims(token);
            String role = claims.get("role", String.class);

            UsernamePasswordAuthenticationToken autenticacao = new UsernamePasswordAuthenticationToken(
                    claims.getSubject(),
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + role))
            );
            SecurityContextHolder.getContext().setAuthentication(autenticacao);
        }

        cadeia.doFilter(requisicao, resposta);
    }

    private String extrairTokenBearer(HttpServletRequest requisicao) {
        String cabecalho = requisicao.getHeader("Authorization");
        if (cabecalho == null || !cabecalho.startsWith(PREFIXO_BEARER)) {
            return null;
        }
        return cabecalho.substring(PREFIXO_BEARER.length()).trim();
    }
}
