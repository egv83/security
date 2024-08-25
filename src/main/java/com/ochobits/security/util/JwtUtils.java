package com.ochobits.security.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.hibernate.annotations.Comment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    @Value("${security.jwt.key}")
    private String privateKey;

    @Value("${security.jwt.user.generator}")
    private String userGenerator;

    /*METODO PARA CREAR EL TOKEN*/
    public String createToken(Authentication authentication){
        Algorithm algorithm = Algorithm.HMAC256(this.privateKey);

        String userNme = authentication.getPrincipal().toString();/*con getPrincipal obtiene el usuario*/

        String authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)/*es lo mimsmo que tener esto: grantedAuthority -> grantedAuthority.getAuthority()*/
                .collect(Collectors.joining(",")); /*COGE CADA PERMISO Y LO SEPARAR CON UNA ,: READ, WRITE, DELETE, ETC */

        String jwtToken = JWT.create()
                .withIssuer(this.userGenerator) /*USUARIO QUE GENERO EL TOKEN: EJM BACKEND*/
                .withSubject(userNme)/*NOMBRE DE USUARIO*/
                .withClaim("authorities",authorities) /*LOS PERMISOS*/
                .withIssuedAt(new Date())/*TIEMPO DE INICIO*/
                .withExpiresAt(new Date(System.currentTimeMillis()+1800000))/*TIEMPO DE EXPIRACIÓN EN MILISEGUNDOS*/
                .withJWTId(UUID.randomUUID().toString())/*ID DE USUARIO RANDOM*/
                .withNotBefore(new Date(System.currentTimeMillis()))/*DISPONOBILIDAD DEL TOKEN, PUEDO ESPECIFICAR QUE ESTE DISPONIBLE DESDES DE 2 HORAS*/
                .sign(algorithm); /*firma*/
        return jwtToken;
    }

    /*METODO PARA VERIFICAR EL TOKEN*/
    public DecodedJWT validateToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(this.privateKey);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(this.userGenerator) /*USUARIO QUE GENERO EL TOKEN: EJM BACKEND*/
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT;
        }catch (JWTVerificationException e){
            throw new JWTVerificationException("Token invalid, not Authorized");
        }
    }

    /*OBTENER USER NAME DEL TOKEN*/
    public String extractUserName(DecodedJWT decodedJWT){
        return decodedJWT.getSubject().toString();
    }

    /*OBTENER CLAIM ESPECIFICO DEL TOKEN*/
    public Claim getSpecificCalim(DecodedJWT decodedJWT, String claimName){
        return decodedJWT.getClaim(claimName);
    }

    /*OBTENER TODOS LOCA CLAIM DEL TOKEN*/
    public Map<String,Claim> getAllClaims(DecodedJWT decodedJWT){
        return decodedJWT.getClaims();
    }
}
