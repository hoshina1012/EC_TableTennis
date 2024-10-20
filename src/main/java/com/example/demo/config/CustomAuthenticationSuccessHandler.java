package com.example.demo.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.demo.entity.Users;
import com.example.demo.repository.UsersRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UsersRepository usersRepository;

    public CustomAuthenticationSuccessHandler(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Users user = usersRepository.findByMailAddress(userDetails.getUsername());
        if (user != null) {
            response.sendRedirect("/users/" + user.getId());
        } else {
            response.sendRedirect("/");
        }
    }
}
