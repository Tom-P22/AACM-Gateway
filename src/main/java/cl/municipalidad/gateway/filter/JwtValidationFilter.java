package cl.municipalidad.gateway.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(-1) //Pone en la lista de prioridad que este filtro se ejecute primero
public class JwtValidationFilter extends OncePerRequestFilter {

    private final SecretKey CLAVE_SECRETA = Keys.hmacShaKeyFor(
            "ClaveUltraSecretaEInviolableParaLaMunicipalidad2026!".getBytes()
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        System.out.println("[JWT FILTER] " + method + " " + path);
        //  Rutas públicas que se saltan la validación

        if (path.startsWith("/swagger-ui") 
        || path.contains("v3/api-docs") 
        || path.contains("/doc/")
        || path.equals("/favicon.ico")) {
            
            filterChain.doFilter(request, response);
            return;
        }

        if (path.startsWith("/api/v1/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (path.startsWith("/api/v1/usuarios") && "POST".equalsIgnoreCase(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (path.contains("/internal/")) {
            System.out.println("[GATEWAY] Comunicacion interna autorizada");
            filterChain.doFilter(request, response);
            return;
        }

        // Revisión de cabeceras
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            rebotarPeticion(response, "Falta cabecera Authorization", HttpStatus.UNAUTHORIZED);
            return;
        }

        if (!authHeader.startsWith("Bearer ")) {
            rebotarPeticion(response, "Formato de token no es Bearer", HttpStatus.UNAUTHORIZED);
            return;
        }

        String tokenPuro = authHeader.substring(7);

        // Validación JWT
        try {
            var claims = Jwts.parser()
                    .verifyWith(CLAVE_SECRETA)
                    .build()
                    .parseSignedClaims(tokenPuro)
                    .getPayload();

            String userEmail = claims.get("email", String.class);


            HttpServletRequest wrappedRequest = new HttpServletRequestWrapper(request) {
                @Override
                public String getHeader(String name) {
                    if ("X-User-Email".equalsIgnoreCase(name)) {
                        return userEmail;
                    }
                    return super.getHeader(name);
                }

                @Override
                public Enumeration<String> getHeaders(String name) {
                    if ("X-User-Email".equalsIgnoreCase(name)) {
                        return Collections.enumeration(Collections.singletonList(userEmail));
                    }
                    return super.getHeaders(name);
                }

                @Override
                public Enumeration<String> getHeaderNames() {
                    List<String> names = Collections.list(super.getHeaderNames());
                    names.add("X-User-Email");
                    return Collections.enumeration(names);
                }
            };

            filterChain.doFilter(wrappedRequest, response);

        } catch (Exception e) {
            System.err.println("[GATEWAY ERROR] Falló la validación del Token: " + e.getMessage());
            rebotarPeticion(response, "Token invalido o vencido: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    // Método auxiliar para rechazar la petición síncrona
    private void rebotarPeticion(HttpServletResponse response, String mensaje, HttpStatus status) throws IOException {
        response.setStatus(status.value());
        response.setHeader("X-Gateway-Error", mensaje);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + mensaje + "\"}");
        
        System.out.println("[GATEWAY SHIELD] Acceso Denegado: " + mensaje);
    }
}