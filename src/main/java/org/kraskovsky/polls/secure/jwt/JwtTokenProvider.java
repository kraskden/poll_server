package org.kraskovsky.polls.secure.jwt;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.kraskovsky.polls.secure.JwtUserDetailService;
import org.kraskovsky.polls.secure.jwt.exception.JwtAuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Optional;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.token.secret}")
    private String secret;

    @Value("${jwt.token.validity}")
    private Long validity;

    private JwtUserDetailService userDetailsService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public String createToken(Long id) {
        Claims claims = Jwts.claims().setSubject(id.toString());

        Date now = new Date();
        Date validityDate = new Date(now.getTime() + this.validity);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(validityDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        String payload = getPayload(token);
        try {
            Long id = Long.parseLong(payload);
            UserDetails userDetails = this.userDetailsService.loadUserById(id);
            return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("User not found");
        }
    }

    public String getPayload(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

    public Optional<String> resolveBearer(String token) {
        if (token != null && token.startsWith("Bearer")) {
            return Optional.of(token.substring(7, token.length()));
        }
        return Optional.empty();
    }

    public Optional<String> resolveToken(HttpServletRequest req) {
        String token = req.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer")) {
            return Optional.of(token.substring(7, token.length()));
        }
        return Optional.empty();
    }

    public void validateToken(String token) {
        try {
            Jws<Claims> claimsJws =  Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            if (claimsJws.getBody().getExpiration().before(new Date())) {
                throw new JwtAuthException("Jwt token is expired");
            }
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthException("Jwt token is invalid");
        }
    }


    @Autowired
    public void setUserDetailsService(JwtUserDetailService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
}
