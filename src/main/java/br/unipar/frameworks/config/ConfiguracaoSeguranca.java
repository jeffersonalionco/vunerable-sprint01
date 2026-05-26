package br.unipar.frameworks.config;

import br.unipar.frameworks.security.FiltroAutenticacaoJwt;
import br.unipar.frameworks.security.ServicoDetalhesUsuario;
import br.unipar.frameworks.security.ServicoHashSenha;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * API stateless: login devolve JWT; demais rotas exigem Bearer token.
 * Roles continuam no @PreAuthorize dos controllers.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@EnableConfigurationProperties(PropriedadesJwt.class)
public class ConfiguracaoSeguranca {

    private final ServicoDetalhesUsuario servicoDetalhesUsuario;
    private final ServicoHashSenha servicoHashSenha;
    private final FiltroAutenticacaoJwt filtroAutenticacaoJwt;

    public ConfiguracaoSeguranca(
            ServicoDetalhesUsuario servicoDetalhesUsuario,
            ServicoHashSenha servicoHashSenha,
            FiltroAutenticacaoJwt filtroAutenticacaoJwt
    ) {
        this.servicoDetalhesUsuario = servicoDetalhesUsuario;
        this.servicoHashSenha = servicoHashSenha;
        this.filtroAutenticacaoJwt = filtroAutenticacaoJwt;
    }

    @Bean
    public SecurityFilterChain cadeiaFiltrosSeguranca(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(sessao -> sessao
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(autorizacao -> autorizacao
                        .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .addFilterBefore(filtroAutenticacaoJwt, UsernamePasswordAuthenticationFilter.class)
                .headers(cabecalhos -> cabecalhos.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    @Bean
    public AuthenticationManager gerenciadorAutenticacao() {
        DaoAuthenticationProvider provedor = new DaoAuthenticationProvider();
        provedor.setUserDetailsService(servicoDetalhesUsuario);
        provedor.setPasswordEncoder(servicoHashSenha);
        return new ProviderManager(provedor);
    }
}
