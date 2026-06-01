package cl.municipalidad.gateway.config;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.web.servlet.function.RequestPredicates.path;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class GatewayConfig {

    @Bean
    public RouterFunction<ServerResponse> gatewayRoutes() {
        return route("MS-usuarios")
                .route(path("/api/v1/usuarios/**"), http())
                .before(uri("http://localhost:8081"))
                .build()
                
            .and(route("MS-auth")
                .route(path("/api/v1/auth/**"), http())
                .before(uri("http://localhost:8082"))
                .build())

            .and(route("MS-canchas")
                .route(path("/api/v1/canchas/**"), http())
                .before(uri("http://localhost:8083"))
                .build())
                
            .and(route("MS-reservas")
                .route(path("/api/v1/reservas/**"), http())
                .before(uri("http://localhost:8084"))
                .build())

            .and(route("MS-soporte")
                .route(path("/api/v1/soporte/**"), http())
                .before(uri("http://localhost:8085"))
                .build())

            .and(route("MS-reportes")
                .route(path("/api/v1/reportes/**"), http())
                .before(uri("http://localhost:8086"))
                .build())

            .and(route("MS-inventario")
                .route(path("/api/v1/inventario/**"), http())
                .before(uri("http://localhost:8087"))
                .build())

            .and(route("MS-pagos")
                .route(path("/api/v1/pagos/**"), http())
                .before(uri("http://localhost:8088"))
                .build())

            .and(route("MS-reseñas")
                .route(path("/api/v1/reseñas/**"), http())
                .before(uri("http://localhost:8089"))
                .build())

            .and(route("MS-restricciones")
                .route(path("/api/v1/restricciones/**"), http())
                .before(uri("http://localhost:8090"))
                .build());





    }
}