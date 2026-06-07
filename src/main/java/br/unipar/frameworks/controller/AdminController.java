package br.unipar.frameworks.controller;

import br.unipar.frameworks.model.User;
import br.unipar.frameworks.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository repositorioUsuario;

    public AdminController(UserRepository repositorioUsuario) {
        this.repositorioUsuario = repositorioUsuario;
    }

    @GetMapping("/users")
    public Page<User> listarUsuariosAdmin(Pageable paginacao) {
        return repositorioUsuario.findAll(paginacao);
    }

    @DeleteMapping("/users/{id}")
    public void excluirUsuario(@PathVariable Long id) {
        repositorioUsuario.deleteById(id);
    }
}
