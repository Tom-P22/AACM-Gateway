package cl.municipalidad.gateway.filter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class JwtValidationFilterTest {

    @Autowired
    private MockMvc mockMvc;

    // TEST 1: Permite libre acceso si el path corresponde a la documentación de Swagger
    @Test
    @DisplayName("Debe permitir el libre acceso si el path corresponde a la documentación de Swagger")
    void doFilterInternal_CuandoEsRutaPublica_DebePermitirAccesoSinToken() throws Exception {
        String rutaPublica = "/swagger-ui.html";

        mockMvc.perform(get(rutaPublica))
                .andExpect(status().isNotFound()); 
    }

    // TEST 2: Deniega el acceso si la petición a un endpoint protegido no cuenta con cabecera Authorization
    @Test
    @DisplayName("Debe denegar el acceso y retornar 401 si la petición a un endpoint protegido no cuenta con cabecera Authorization")
    void doFilterInternal_CuandoNoHayTokenEnRutaProtegida_DebeRebotarCon401() throws Exception {
        String rutaProtegida = "/api/v1/soporte/tickets";

        mockMvc.perform(get(rutaProtegida)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(header().exists("X-Gateway-Error"));
    }

    // TEST 3: Deniega el acceso si la cabecera existe pero no cumple con el formato estructurado Bearer
    @Test
    @DisplayName("Debe denegar el acceso si la cabecera existe pero no cumple con el formato estructurado Bearer")
    void doFilterInternal_CuandoTokenTieneFormatoInvalido_DebeRetornar401() throws Exception {
        String tokenInvalido = "TokenFormatoInvalidoSinPrefijo";

        mockMvc.perform(get("/api/v1/soporte/tickets")
                        .header("Authorization", tokenInvalido)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("X-Gateway-Error", "Token invalido o vencido: Formato de token incorrecto."));
    }

    // TEST 4: Deniega el acceso si el token posee una firma corrupta o alterada maliciosamente
    @Test
    @DisplayName("Debe denegar el acceso si el token posee una firma corrupta o alterada")
    void doFilterInternal_CuandoTokenEstaCorrupto_DebeRetornar401() throws Exception {
        String tokenCorrupto = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ2ZWNpbm9AZ21haWwuY29tIn0.FirmaFalsaFalsaFalsa";

        mockMvc.perform(get("/api/v1/soporte/tickets")
                        .header("Authorization", tokenCorrupto)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(header().exists("X-Gateway-Error"));
    }

    // TEST 5: Permite el libre acceso perimetral a recursos estáticos del Gateway
    @Test
    @DisplayName("Debe ignorar la validación y permitir el paso libre a recursos estáticos del sistema")
    void doFilterInternal_CuandoEsRecursoEstatico_DebePermitirPasoLimpio() throws Exception {
        String recursoEstatico = "/favicon.ico";

        mockMvc.perform(get(recursoEstatico))
                .andExpect(status().isNotFound());
    }
}