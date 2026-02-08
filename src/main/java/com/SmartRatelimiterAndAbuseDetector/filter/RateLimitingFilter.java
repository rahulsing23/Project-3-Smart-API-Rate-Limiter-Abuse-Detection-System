package com.SmartRatelimiterAndAbuseDetector.filter;

import com.SmartRatelimiterAndAbuseDetector.service.AbuseDetectionService;
import com.SmartRatelimiterAndAbuseDetector.service.RateLimiterService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RateLimitingFilter implements Filter {

    private final RateLimiterService rateLimiter;
    private final AbuseDetectionService abuseService;

    public RateLimitingFilter(
            RateLimiterService rateLimiter,
            AbuseDetectionService abuseService
    ) {
        this.rateLimiter = rateLimiter;
        this.abuseService = abuseService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String clientId = request.getRemoteAddr();

        if (abuseService.isBlocked(clientId)) {
            block(response, "Client temporarily blocked");
            return;
        }

        if (abuseService.isRapidFire(clientId)) {
            abuseService.block(clientId, java.time.Duration.ofMinutes(5));
            block(response, "Suspicious request behavior");
            return;
        }

        if (!rateLimiter.allowRequest(clientId)) {
            block(response, "Rate limit exceeded");
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);

        if (response.getStatus() >= 400) {
            abuseService.recordError(clientId);
        }
    }

    private void block(HttpServletResponse response, String msg)
            throws IOException {
        response.setStatus(429);
        response.setContentType("application/json");
        response.getWriter().write("""
            {
              "error": "BLOCKED",
              "message": "%s"
            }
        """.formatted(msg));
    }
}
