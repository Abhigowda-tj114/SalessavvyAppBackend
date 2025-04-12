package com.example.demo.filter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.GenericFilterBean;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class AuthenticationFilter extends GenericFilterBean {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Autowired
    private AuthService authService; // Service to handle JWT validation

    @Autowired
    private UserRepository userRepository; // Database repository to fetch user info

    // Paths that don't require authentication
    private static final List<String> UNAUTHENTICATED_PATHS = Arrays.asList(
        "/api/auth/login",
        "/api/users/register",
        "/public/"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        setCORSHeaders(httpResponse); // Add CORS headers to all responses

        // 🛑 Handle CORS Preflight Requests (OPTIONS)
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return; // Stop further processing
        }

        String requestURI = httpRequest.getRequestURI();
        logger.info("Request URI: {}", requestURI);

        // ✅ Allow unauthenticated routes to pass through
        if (UNAUTHENTICATED_PATHS.stream().anyMatch(requestURI::startsWith)) {
            chain.doFilter(request, response);
            return;
        }

        // 🔍 Extract token from cookies
        String token = getAuthTokenFromCookie(httpRequest);
        logger.info("Extracted Token: {}", token);

        // 🛑 If token is missing or invalid, return Unauthorized response
        if (token == null || !authService.validateToken(token)) {
            sendErrorResponse(httpResponse, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Invalid Token");
            return;
        }

        // 🔑 Get username from token
        String username = authService.extractUsername(token);
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            sendErrorResponse(httpResponse, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: User Not Found");
            return;
        }

        // ✅ User authenticated
        User authenticatedUser = userOptional.get();
        Role role = authenticatedUser.getRole();
        logger.info("Authenticated User: {}, Role: {}", authenticatedUser.getUsername(), role);

        // 🚀 Role-Based Access Control (RBAC)
        if (requestURI.startsWith("/api/") && (role != Role.CUSTOMER && role != Role.ADMIN)) {
            sendErrorResponse(httpResponse, HttpServletResponse.SC_FORBIDDEN, "Unauthorized: Access Denied for Role");
            return;
        }

        if (requestURI.startsWith("/admin/") && role != Role.ADMIN) {
            sendErrorResponse(httpResponse, HttpServletResponse.SC_FORBIDDEN, "Unauthorized: Admin Access Only");
            return;
        }

        // ✅ User is authorized, continue processing request
        httpRequest.setAttribute("authenticatedUser", authenticatedUser);
        chain.doFilter(request, response);
    }

    // 🔍 Extract JWT Token from cookies
    private String getAuthTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("authToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null; // No token found
    }

    // 🚀 Helper method to send error responses
    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }

    // 🌍 Set CORS headers for all responses
    private void setCORSHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Max-Age", "3600");
    }
}

