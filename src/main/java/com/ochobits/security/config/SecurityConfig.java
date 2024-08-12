package com.ochobits.security.config;

import org.springframework.cglib.proxy.NoOp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
//        return  httpSecurity
//                .csrf(csrf -> csrf.disable())/*SE DESABILITA LOS CSRF PARA FORMS WEB*/
//                .httpBasic(Customizer.withDefaults())/*PERMITE EL LOGEO DE FORMA BASICA USUARIO Y CLAVE SIN TOKEN*/
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) /*PERMITE TRABAJAR CON ESTADOS DE SESION, ES MEJOR TRABAJAR CON STATELESS*/
//                .authorizeHttpRequests(auth->{
//                    /*CONFIGURAR ENDPOINTS PUBLICOS*/
//                    auth.requestMatchers(HttpMethod.GET,"/auth/hello").permitAll();/*PERMITE EL ACCESO A TODOS*/
//                    /*CONFIGURAR ENDPOINTS PRIVADOS*/
//                    auth.requestMatchers(HttpMethod.GET,"/auth/hello-secured").hasAnyAuthority("CREATE");/*PERMITE EL ACCESO QUIEN TIENE AUTORIDADES*/
//                    /*CONFIGURAR EL RESTO DE DE ENDPOINTS - NO ESPECIFICADOS*/
//                    auth.anyRequest().denyAll();/*NIEGA EN ACCESO A CUALQUIER OTRO ENDPOINT NO ESPECIFICADO*/
//                    /*auth.anyRequest().authenticated();/*PERMITE EL ACCESO A CUALQUIER ENDPOINT NO ESPECIFICADO PERO QUE EL USUARIO ESTE AUNTENTICADO, LOGEADO*/
//                })
//                .build();
//    }

    /*PARA TRABAJAR CON ANOTACIONES*/
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return  httpSecurity
                .csrf(csrf -> csrf.disable())/*SE DESABILITA LOS CSRF PARA FORMS WEB*/
                .httpBasic(Customizer.withDefaults())/*PERMITE EL LOGEO DE FORMA BASICA USUARIO Y CLAVE SIN TOKEN*/
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) /*PERMITE TRABAJAR CON ESTADOS DE SESION, ES MEJOR TRABAJAR CON STATELESS*/
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    )throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userDetailsService());
        return  daoAuthenticationProvider;
    }

    @Bean
    public UserDetailsService userDetailsService(){
        List<UserDetails> userDetailsList = new ArrayList<>();
        userDetailsList.add(
                User.withUsername("esteban")
                .password("1234")
                .roles("ADMIN")
                .authorities("READ","CREATE")
                .build());
        userDetailsList.add(
                User.withUsername("gabriel")
                        .password("1234")
                        .roles("USER")
                        .authorities("READ")
                        .build());
        return new InMemoryUserDetailsManager(userDetailsList);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }

}
