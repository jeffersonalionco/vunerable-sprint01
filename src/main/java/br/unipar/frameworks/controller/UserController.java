package br.unipar.frameworks.controller;

import br.unipar.frameworks.model.User;
import br.unipar.frameworks.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<User> listUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    
    @GetMapping("/search")
    public List<User> buscarPorNome(@RequestParam String term) {
        return userRepository.buscarPorNomeContendo(term);
    }

    @GetMapping("/search-safe")
    public List<User> buscarPorNomeAlias(@RequestParam String term) {
        return userRepository.buscarPorNomeContendo(term);
    }
}
