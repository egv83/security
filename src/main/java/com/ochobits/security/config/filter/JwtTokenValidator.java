package com.ochobits.security.config.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.ochobits.security.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;

public class JwtTokenValidator extends OncePerRequestFilter {

    private JwtUtils jwtUtils;

    public JwtTokenValidator(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    /*METODO QUE VALIDA A CADA MOMENTO LOS FILTERCHAG
    * SE USA @NonNull PARA QUE NO VENGAN LOS PARAMETROS EN NULL*/
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION); /*OBTENER TOKEN DEL HEADER QUE VIENE EN EL REQUEST*/

        /*VALIDA QUE EL TOKEN NO VENGA NULL*/
        if(Objects.nonNull(jwtToken)){
            //Bearer dflsdksadlk{asls
            jwtToken = jwtToken.substring(7); /*Extrae el token despues de los 7 primero caracteres esto corresponde a Bearer con el espacio*/

            DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);

            String userName = jwtUtils.extractUserName(decodedJWT);
            String stringAuthorities = jwtUtils.getSpecificCalim(decodedJWT,"authorities").asString();

            Collection<? extends GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(stringAuthorities); /*CONVIERTE A UNA LISTA DE PERMISOS EL LISTADO DE PERMISOS CON COMA*/

            SecurityContext context = SecurityContextHolder.getContext();
            Authentication authentication = new UsernamePasswordAuthenticationToken(userName,null,authorities); /*INSERTO EL USURIO Y PERMISOS PARA EL AUTHORITIES DE SPRING*/
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
        }

        filterChain.doFilter(request,response);
    }
}
