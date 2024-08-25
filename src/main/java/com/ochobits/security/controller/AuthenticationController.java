package com.ochobits.security.controller;

import com.ochobits.security.controller.dto.AuthCreateUserRequest;
import com.ochobits.security.controller.dto.AuthLoginRequest;
import com.ochobits.security.controller.dto.AuthResponse;
import com.ochobits.security.service.UserDetailServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private UserDetailServiceImpl userDetailsService;

    public AuthenticationController(UserDetailServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/log-in")
    public ResponseEntity<AuthResponse> login(
            @RequestBody @Valid AuthLoginRequest request){

        return new ResponseEntity<>(userDetailsService.loginUser(request), HttpStatus.OK);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid AuthCreateUserRequest request){
        return new ResponseEntity<>(this.userDetailsService.createUser(request),HttpStatus.CREATED);
    }

}
