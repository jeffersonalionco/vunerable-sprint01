package br.unipar.frameworks.controller;

import br.unipar.frameworks.model.User;
import br.unipar.frameworks.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class UserController {

    private final UserRepository repositorioUsuario;

    public UserController(UserRepository repositorioUsuario) {
        this.repositorioUsuario = repositorioUsuario;
    }

    @GetMapping
    public Page<User> listarUsuarios(Pageable paginacao) {
        return repositorioUsuario.findAll(paginacao);
    }

    @GetMapping("/{id}")
    public User buscarPorId(@PathVariable Long id) {
        return repositorioUsuario.findById(id).orElseThrow();
    }

    @GetMapping("/search")
    public Page<User> buscarPorNome(@RequestParam String term, Pageable paginacao) {
        return repositorioUsuario.buscarPorNomeContendo(term, paginacao);
    }

    @GetMapping("/search-safe")
    public Page<User> buscarPorNomeAlias(@RequestParam String term, Pageable paginacao) {
        return repositorioUsuario.buscarPorNomeContendo(term, paginacao);
    }
}
