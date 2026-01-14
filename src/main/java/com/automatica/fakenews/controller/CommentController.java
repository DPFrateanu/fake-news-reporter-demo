package com.automatica.fakenews.controller;

import com.automatica.fakenews.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/reports/add-comment")
    public String addComment(@RequestParam("reportId") Long reportId,
                             @RequestParam("comment") String comment,
                             Principal principal) {
        commentService.addComment(reportId, comment, principal);
        return "redirect:/reports/" + reportId;
    }
}
