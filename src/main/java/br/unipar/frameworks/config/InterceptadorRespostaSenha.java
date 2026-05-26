package br.unipar.frameworks.config;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * Intercepta TODA resposta dos controllers antes de virar JSON.
 * Se voltar entidade User (ou lista/map com User), a senha não vai no payload.
 *
 * Obs: isso não criptografa nada no banco — só evita vazar password na API.
 */
@RestControllerAdvice
public class InterceptadorRespostaSenha implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter tipoRetorno, Class<? extends HttpMessageConverter<?>> conversor) {
        // roda em tudo que sai dos @RestController, sem frescura de filtrar método por método
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object corpo,
            MethodParameter tipoRetorno,
            MediaType tipoMidia,
            Class<? extends HttpMessageConverter<?>> conversor,
            ServerHttpRequest requisicao,
            ServerHttpResponse resposta
    ) {
        UtilitarioOcultarSenha.limparSenhasDoCorpo(corpo);
        return corpo;
    }
}
