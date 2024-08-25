package com.ochobits.security.config;

import com.ochobits.security.config.filter.JwtTokenValidator;
import com.ochobits.security.service.UserDetailServiceImpl;
import com.ochobits.security.util.JwtUtils;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private JwtUtils jwtUtils;

    public SecurityConfig(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return  httpSecurity
                .csrf(csrf -> csrf.disable())/*SE DESABILITA LOS CSRF PARA FORMS WEB*/
                .httpBasic(Customizer.withDefaults())/*PERMITE EL LOGEO DE FORMA BASICA USUARIO Y CLAVE SIN TOKEN*/
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) /*PERMITE TRABAJAR CON ESTADOS DE SESION, ES MEJOR TRABAJAR CON STATELESS*/
                .authorizeHttpRequests(auth->{
                    /*CONFIGURAR ENDPOINTS PUBLICOS*/
                    auth.requestMatchers(HttpMethod.POST,"/auth/**").permitAll();/*PERMITE EL ACCESO A TODOS*/

                    /*CONFIGURAR ENDPOINTS PRIVADOS*/
//                    auth.requestMatchers(HttpMethod.POST, "/auth/post").hasRole("ADMIN");/*PERMITE VALIDAR POR 1 ROL*/
                    auth.requestMatchers(HttpMethod.POST, "/auth/post").hasAnyRole("ADMIN","DEVELOPER");/*PERMITE VALIDAR POR VARIOS ROLES*/


//                    auth.requestMatchers(HttpMethod.POST,"/auth/post").hasAnyAuthority("CREATE", "READ");/*PERMITE EL ACCESO QUIEN TIENE AUTORIDADES*/
                    auth.requestMatchers(HttpMethod.PATCH,"/auth/patch").hasAnyAuthority("REFACTOR");

                    /*CONFIGURAR EL RESTO DE DE ENDPOINTS - NO ESPECIFICADOS*/
                    auth.anyRequest().denyAll();/*NIEGA EN ACCESO A CUALQUIER OTRO ENDPOINT NO ESPECIFICADO*/
                    /*auth.anyRequest().authenticated();/*PERMITE EL ACCESO A CUALQUIER ENDPOINT NO ESPECIFICADO PERO QUE EL USUARIO ESTE AUNTENTICADO, LOGEADO*/
                })
                .addFilterBefore(new JwtTokenValidator(jwtUtils), BasicAuthenticationFilter.class)/*VA A EJECUTAR EL FILTRO ANTES DE QUE SE VALIDE*/
                .build();
    }

    /*PARA TRABAJAR CON ANOTACIONES*/
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
//        return  httpSecurity
//                .csrf(csrf -> csrf.disable())/*SE DESABILITA LOS CSRF PARA FORMS WEB*/
//                .httpBasic(Customizer.withDefaults())/*PERMITE EL LOGEO DE FORMA BASICA USUARIO Y CLAVE SIN TOKEN*/
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) /*PERMITE TRABAJAR CON ESTADOS DE SESION, ES MEJOR TRABAJAR CON STATELESS*/
//                .build();
//    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    )throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailServiceImpl userDetailService){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
//        daoAuthenticationProvider.setUserDetailsService(userDetailsService());
        daoAuthenticationProvider.setUserDetailsService(userDetailService);
        return  daoAuthenticationProvider;
    }

//    @Bean
//    public UserDetailsService userDetailsService(){
//        List<UserDetails> userDetailsList = new ArrayList<>();
//        userDetailsList.add(
//                User.withUsername("esteban")
//                .password("1234")
//                .roles("ADMIN")
//                .authorities("READ","CREATE")
//                .build());
//        userDetailsList.add(
//                User.withUsername("gabriel")
//                        .password("1234")
//                        .roles("USER")
//                        .authorities("READ")
//                        .build());
//        return new InMemoryUserDetailsManager(userDetailsList);
//    }

    @Bean
    public PasswordEncoder passwordEncoder(){
//        return NoOpPasswordEncoder.getInstance(); SOLO PARA PRUEBAS
        return new  BCryptPasswordEncoder();
    }

}
