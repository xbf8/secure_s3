package com.xbf8.secure_s3.session;

import java.io.IOException;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.xbf8.secure_s3.session.dto.SessionRedisValue;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SessionKeyAuthFilter extends OncePerRequestFilter {
    private final SessionService sessionService;
    public SessionKeyAuthFilter(SessionService sessionService) {
        this.sessionService = sessionService;
    }

	@Override
	protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        String sessionKey = request.getHeader("X-Session-Key");
        if (sessionKey == null || sessionKey.length() < 1) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        Optional<SessionRedisValue> value = this.sessionService.get(sessionKey);
        if (value.isEmpty()) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        request.setAttribute("fid", value.get().fileId());
        request.setAttribute("operation", value.get().operation());
        filterChain.doFilter(request, response);
	}

}
