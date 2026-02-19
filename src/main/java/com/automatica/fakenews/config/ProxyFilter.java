package com.automatica.fakenews.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ProxyFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        String proxyEmail=request.getHeader("X-Forwarded-Email");

        if(proxyEmail==null||proxyEmail.isEmpty()){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().println("Access Denied! Access through 4180!");
            return;
        }

        filterChain.doFilter(request,response);
        }
}
