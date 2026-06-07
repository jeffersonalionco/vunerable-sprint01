package br.unipar.frameworks.controller;

import br.unipar.frameworks.dto.LoginRequest;
import br.unipar.frameworks.dto.RegisterRequest;
import br.unipar.frameworks.model.User;
import br.unipar.frameworks.repository.UserRepository;
import br.unipar.frameworks.security.DetalhesUsuarioLab;
import br.unipar.frameworks.security.ServicoHashSenha;
import br.unipar.frameworks.security.ServicoTokenJwt;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final AuthenticationManager gerenciadorAutenticacao;
    private final ServicoHashSenha servicoHashSenha;
    private final ServicoTokenJwt servicoTokenJwt;

    public AuthController(
            UserRepository userRepository,
            AuthenticationManager gerenciadorAutenticacao,
            ServicoHashSenha servicoHashSenha,
            ServicoTokenJwt servicoTokenJwt
    ) {
        this.userRepository = userRepository;
        this.gerenciadorAutenticacao = gerenciadorAutenticacao;
        this.servicoHashSenha = servicoHashSenha;
        this.servicoTokenJwt = servicoTokenJwt;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) {
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(servicoHashSenha.criptografarSenha(request.password()));
        user.setRole("USER");
        return ResponseEntity.ok(userRepository.save(user));
    }



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication autenticacao = gerenciadorAutenticacao.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(
                            request.email(),
                            request.password()
                    )
            );

            DetalhesUsuarioLab detalhes = (DetalhesUsuarioLab) autenticacao.getPrincipal();
            String token = servicoTokenJwt.gerarToken(detalhes);
            long expiraEmSegundos = servicoTokenJwt.getExpiracaoMs() / 1000;

            return ResponseEntity.ok(Map.of(
                    "message", "Login OK envie o token no header Authorization",
                    "token", token,
                    "tipoToken", "Bearer",
                    "expiresIn", expiraEmSegundos,
                    "roles", autenticacao.getAuthorities(),
                    "user", detalhes.getUsuario()
            ));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).body(Map.of(
                    "error", "Email ou senha inválidos"
            ));
        }
    }
}
