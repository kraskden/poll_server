package org.kraskovsky.polls.secure.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;

@Slf4j
public class JwtTokenFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) throws IOException, ServletException {
        Optional<String> token = jwtTokenProvider.resolveToken((HttpServletRequest) req);

        token.ifPresent(jwt -> {
            Authentication auth = jwtTokenProvider.getAuthentication(jwt);
            log.info("Auth is: {}", auth);
            if (auth != null) {
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        });
        filterChain.doFilter(req, res);
    }
}
