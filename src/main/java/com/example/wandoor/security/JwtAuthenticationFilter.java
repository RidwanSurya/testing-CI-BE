package com.example.wandoor.security;

import com.example.wandoor.config.RequestContext;
import com.example.wandoor.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final RequestContext requestContext;

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                              HttpServletResponse response,
                              FilterChain filterChain)
        throws ServletException, IOException {

        String path = req.getRequestURI();

        if (path.startsWith("/api/auth/")) {
            System.out.println("JWT Filter skipped for: " + path);
            filterChain.doFilter(req, response);
            return;
        }

        var header = req.getHeader("Authorization");
        var userIdHeader = req.getHeader("User-Id");
        var cifHeader = req.getHeader("Customer-Id");

        if (header == null || !header.startsWith("Bearer ")){
            unauthorized(response, "Unauthorized - Token JWT tidak valid");
            return;
        }

        if (userIdHeader == null || cifHeader == null) {
            unauthorized(response, "Unauthorized - Missing userId or cif header");
            return;
        }

        var token = header.substring(7);
        try {
            var jwt = jwtUtils.validateToken(token);
            var userId = jwt.getSubject();
            var role = jwt.getClaim("role").asString();

            var auth = new UsernamePasswordAuthenticationToken(userId, null, List.of(() -> "ROLE_" + role));
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
            SecurityContextHolder.getContext().setAuthentication(auth);

            // simpan ke context global
            RequestContext ctx = RequestContext.get();
            ctx.setUserId(userIdHeader);
            ctx.setCif(cifHeader);

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            unauthorized(response, "Unauthorized - Token JWT tidak valid");
            return;
        }

        try {
            filterChain.doFilter(req, response);
        } finally {
            RequestContext.clear();
        }

        filterChain.doFilter(req, response);
    }

    private void unauthorized(HttpServletResponse res, String msg) throws IOException {
        res.setStatus(HttpStatus.UNAUTHORIZED.value());
        res.setContentType("application/json");
        res.getWriter().write("{\"status\":false,\"message\":\"" + msg + "\"}");
    }
}
