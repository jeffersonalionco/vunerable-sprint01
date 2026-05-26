package br.unipar.frameworks.security;

import br.unipar.frameworks.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Carrega usuário do banco quando alguém tenta logar Basic ou /api/auth/login
 */

@Service
public class ServicoDetalhesUsuario implements UserDetailsService {

    private final UserRepository repositorioUsuario;

    public ServicoDetalhesUsuario(UserRepository repositorioUsuario) {
        this.repositorioUsuario = repositorioUsuario;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return repositorioUsuario.findByEmail(email)
                .map(DetalhesUsuarioLab::new)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não achado: " + email));
    }
}
