package com.automatica.fakenews.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProxyController {

    @GetMapping("/api/who")
    public String sesiuneProxy(@RequestHeader(value="X-Forwarded-Email",required = false) String userEmail){
        if(userEmail==null || userEmail.isEmpty()){
            return "You did not pass the proxy first!";
        }

        return "Welcome! You are logged in as: "+userEmail;
    }
}
