package com.dengzhanglin.favoriteapi.security;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Created by rajeevkumarsingh on 19/08/17.
 */
@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwt.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwt.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    public String generateToken(Authentication authentication) {

        UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        try {
            Algorithm algorithm = Algorithm.HMAC512(jwtSecret);
            return JWT.create()
                    .withIssuer(userDetails.getName())
                    .withIssuedAt(new Date())
                    .withSubject(Long.toString(userDetails.getId()))
                    .withExpiresAt(expiryDate)
                    .sign(algorithm);
        } catch (UnsupportedEncodingException exception) {
            //UTF-8 encoding not supported
            System.out.println(exception.getMessage());
        } catch (JWTCreationException exception) {
            //Invalid Signing configuration / Couldn't convert Claims.
            System.out.println(exception.getMessage());
        }

        return "";
    }

    public Long getUserIdFromJWT(String token) {
        return Long.parseLong(JWT.decode(token).getId());
    }

    public boolean validateToken(String authToken) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
            JWTVerifier verifier = JWT.require(algorithm).build(); //Reusable verifier instance
            DecodedJWT jwt = verifier.verify(authToken);
            System.out.println(jwt.getIssuer());
            return true;
        } catch (UnsupportedEncodingException exception) {
            //UTF-8 encoding not supported
            System.out.println(exception.getMessage());
        } catch (JWTVerificationException exception) {
            //Invalid signature/claims
            System.out.println(exception.getMessage());
        }

        return false;
    }
}
