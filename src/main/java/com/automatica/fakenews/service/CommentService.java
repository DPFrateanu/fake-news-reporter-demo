package com.automatica.fakenews.service;

import com.automatica.fakenews.model.Comment;
import com.automatica.fakenews.model.FakeNewsReport;
import com.automatica.fakenews.model.User;
import com.automatica.fakenews.repository.CommentRepository;
import com.automatica.fakenews.repository.FakeNewsReportRepository;
import com.automatica.fakenews.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private FakeNewsReportRepository fakeNewsReportRepository;

    @Autowired
    private UserRepository userRepository;

    public void addComment(Long reportId, String content, Principal principal) {
        Optional<FakeNewsReport> reportOptional = fakeNewsReportRepository.findById(reportId);
        if (reportOptional.isPresent()) {
            FakeNewsReport report = reportOptional.get();
            Optional<User> userOptional = userRepository.findByUsername(principal.getName());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                Comment comment = new Comment();
                comment.setContent(content);
                comment.setReport(report);
                comment.setUser(user);
                commentRepository.save(comment);
            }
        }
    }
}
