package br.unipar.frameworks.repository;

import br.unipar.frameworks.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByProductId(Long productId, Pageable paginacao);
}
