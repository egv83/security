package com.ochobits.security.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@PreAuthorize("denyAll()")/*PARA TRABAJAR CON RESTRICCIONES POR ANOTACIONES, ESTE NIEGA TODO*/
public class TestAuthController {

    @GetMapping("/hello")
    @PreAuthorize("permitAll()")
    public String hello(){
        return "Hello test";
    }

    @GetMapping("/hello-secured")
    @PreAuthorize("hasAnyAuthority('READ')")
    public String helloSecured(){
        return "Hello Test Secured";
    }

    @GetMapping("/hello-secured2")
    @PreAuthorize("hasAnyAuthority('CREATE')")
    public String helloSecured2(){
        return "Hello Test Secured2";
    }
}
