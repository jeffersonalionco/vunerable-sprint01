package br.unipar.frameworks.controller;

import br.unipar.frameworks.model.Product;
import br.unipar.frameworks.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository repositorioProduto;

    public ProductController(ProductRepository repositorioProduto) {
        this.repositorioProduto = repositorioProduto;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Page<Product> listarProdutos(Pageable paginacao) {
        return repositorioProduto.findAll(paginacao);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Product criarProduto(@RequestBody Product produto) {
        return repositorioProduto.save(produto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Product atualizarProduto(@PathVariable Long id, @RequestBody Product produto) {
        produto.setId(id);
        return repositorioProduto.save(produto);
    }
}
