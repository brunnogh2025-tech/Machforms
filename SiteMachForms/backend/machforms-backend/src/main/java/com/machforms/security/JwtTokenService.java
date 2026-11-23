package com.machforms.security;

import com.machforms.config.JwtProperties;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtTokenService implements TokenService {

    private final SecretKey chave;
    private final Duration validade;

    public JwtTokenService(JwtProperties properties) {
        this.chave = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
        this.validade = properties.expiration();
    }

    @Override
    public String gerar(String emailOrganizador) {
        Date agora = new Date();
        return Jwts.builder()
                .subject(emailOrganizador)
                .issuedAt(agora)
                .expiration(new Date(agora.getTime() + validade.toMillis()))
                .signWith(chave)
                .compact();
    }

    @Override
    public Optional<String> extrairEmail(String token) {
        try {
            String email = Jwts.parser()
                    .verifyWith(chave)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
            return Optional.ofNullable(email);
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    @Override
    public long validadeEmSegundos() {
        return validade.toSeconds();
    }
}
