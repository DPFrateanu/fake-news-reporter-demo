package com.automatica.fakenews.repository;

import com.automatica.fakenews.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
