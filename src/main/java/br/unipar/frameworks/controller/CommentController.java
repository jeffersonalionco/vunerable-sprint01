package br.unipar.frameworks.controller;

import br.unipar.frameworks.dto.CommentRequest;
import br.unipar.frameworks.model.Comment;
import br.unipar.frameworks.model.Product;
import br.unipar.frameworks.repository.CommentRepository;
import br.unipar.frameworks.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class CommentController {

    private final CommentRepository repositorioComentario;
    private final ProductRepository repositorioProduto;

    public CommentController(CommentRepository repositorioComentario, ProductRepository repositorioProduto) {
        this.repositorioComentario = repositorioComentario;
        this.repositorioProduto = repositorioProduto;
    }

    @GetMapping("/product/{productId}")
    public Page<Comment> listarPorProduto(@PathVariable Long productId, Pageable paginacao) {
        return repositorioComentario.findByProductId(productId, paginacao);
    }

    @PostMapping
    public Comment criar(@RequestBody CommentRequest requisicao) {
        Product produto = repositorioProduto.findById(requisicao.productId()).orElseThrow();
        Comment comentario = new Comment();
        comentario.setText(requisicao.text());
        comentario.setProduct(produto);
        return repositorioComentario.save(comentario);
    }
}
