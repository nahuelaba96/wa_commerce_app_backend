package com.example.backend.config;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

final class MerchantScopeFilter extends OncePerRequestFilter {
    private static final Pattern MERCHANT_PATH = Pattern.compile("^/api/(?:merchants/|merchant-users/merchant/)([0-9]+)$");
    private static final long MAX_BODY_BYTES = 1024 * 1024;

    private final ObjectMapper objectMapper;

    MerchantScopeFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            jakarta.servlet.FilterChain filterChain) throws jakarta.servlet.ServletException, IOException {
        if (!request.getRequestURI().startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (isManagedInternally(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Use the authentication onboarding flow");
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken jwtAuthentication)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        Object merchantClaim = jwtAuthentication.getToken().getClaims().get("merchant_id");
        Long authorizedMerchantId = parseId(merchantClaim);
        if (authorizedMerchantId == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Token has no merchant scope");
            return;
        }
        if (request.getRequestURI().equals("/api/merchants/mine")) {
            filterChain.doFilter(request, response);
            return;
        }

        HttpServletRequest scopedRequest = request;
        List<Long> requestedMerchantIds = new ArrayList<>();
        for (String value : request.getParameterValues("merchantId") == null
                ? new String[0] : request.getParameterValues("merchantId")) {
            requestedMerchantIds.add(parseId(value));
        }
        Matcher pathMatcher = MERCHANT_PATH.matcher(request.getRequestURI());
        if (pathMatcher.matches()) {
            requestedMerchantIds.add(parseId(pathMatcher.group(1)));
        }

        if (hasJSONBody(request)) {
            byte[] body = request.getInputStream().readNBytes((int) MAX_BODY_BYTES + 1);
            if (body.length > MAX_BODY_BYTES) {
                response.sendError(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
                return;
            }
            JsonNode root;
            try {
                root = body.length == 0 ? null : objectMapper.readTree(body);
            } catch (IOException exception) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON request");
                return;
            }
            if (root != null) {
                collectMerchantIds(root, requestedMerchantIds);
            }
            scopedRequest = new CachedBodyRequest(request, body);
        }

        if (requestedMerchantIds.isEmpty() || requestedMerchantIds.stream().anyMatch(id ->
                id == null || !id.equals(authorizedMerchantId))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Request is outside the active merchant");
            return;
        }
        filterChain.doFilter(scopedRequest, response);
    }

    private static boolean isManagedInternally(HttpServletRequest request) {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return false;
        }
        String path = request.getRequestURI();
        return "/api/users".equals(path) || "/api/merchants".equals(path) || "/api/merchant-users".equals(path);
    }

    private static boolean hasJSONBody(HttpServletRequest request) {
        return request.getContentLengthLong() != 0
                && request.getContentType() != null
                && MediaType.APPLICATION_JSON.isCompatibleWith(MediaType.parseMediaType(request.getContentType()));
    }

    private static void collectMerchantIds(JsonNode node, List<Long> result) {
        if (node.isObject()) {
            node.fields().forEachRemaining(entry -> {
                if ("merchantId".equals(entry.getKey()) || "merchant_id".equals(entry.getKey())) {
                    result.add(parseId(entry.getValue()));
                } else {
                    collectMerchantIds(entry.getValue(), result);
                }
            });
        } else if (node.isArray()) {
            node.forEach(child -> collectMerchantIds(child, result));
        }
    }

    private static Long parseId(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value instanceof JsonNode node && node.canConvertToLong()) {
            return node.longValue();
        }
        if (value instanceof String string) {
            try {
                return Long.parseLong(string);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private static final class CachedBodyRequest extends HttpServletRequestWrapper {
        private final byte[] body;

        CachedBodyRequest(HttpServletRequest request, byte[] body) {
            super(request);
            this.body = body;
        }

        @Override
        public ServletInputStream getInputStream() {
            ByteArrayInputStream input = new ByteArrayInputStream(body);
            return new ServletInputStream() {
                @Override
                public boolean isFinished() {
                    return input.available() == 0;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(ReadListener listener) {
                    throw new UnsupportedOperationException("Asynchronous request bodies are not supported");
                }

                @Override
                public int read() {
                    return input.read();
                }
            };
        }

        @Override
        public BufferedReader getReader() {
            return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
        }
    }
}
