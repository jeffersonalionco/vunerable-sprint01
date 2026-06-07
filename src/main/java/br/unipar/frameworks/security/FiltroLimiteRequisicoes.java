package br.unipar.frameworks.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FiltroLimiteRequisicoes extends OncePerRequestFilter {

    private final int limitePorMinuto;
    private final Map<String, List<Long>> requisicoesPorIp = new ConcurrentHashMap<>();

    public FiltroLimiteRequisicoes(@Value("${limite.requisicoes-por-minuto}") int limitePorMinuto) {
        this.limitePorMinuto = limitePorMinuto;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest requisicao,
            HttpServletResponse resposta,
            FilterChain cadeia
    ) throws ServletException, IOException {
        String ipCliente = requisicao.getRemoteAddr();

        if (passouDoLimite(ipCliente)) {
            resposta.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            resposta.setContentType("application/json");
            resposta.getWriter().write("{\"mensagem\":\"Limite de requisições excedido. Aguarde um minuto.\"}");
            return;
        }

        registrarRequisicao(ipCliente);
        cadeia.doFilter(requisicao, resposta);
    }

    private boolean passouDoLimite(String ipCliente) {
        long agora = System.currentTimeMillis();
        long umMinutoAtras = agora - 60_000;

        List<Long> horarios = requisicoesPorIp.getOrDefault(ipCliente, List.of());
        long totalNoMinuto = horarios.stream().filter(hora -> hora > umMinutoAtras).count();

        return totalNoMinuto >= limitePorMinuto;
    }

    private void registrarRequisicao(String ipCliente) {
        long agora = System.currentTimeMillis();
        long umMinutoAtras = agora - 60_000;

        List<Long> horariosAtuais = new ArrayList<>(requisicoesPorIp.getOrDefault(ipCliente, List.of()));
        horariosAtuais.removeIf(hora -> hora <= umMinutoAtras);
        horariosAtuais.add(agora);
        requisicoesPorIp.put(ipCliente, horariosAtuais);
    }
}
