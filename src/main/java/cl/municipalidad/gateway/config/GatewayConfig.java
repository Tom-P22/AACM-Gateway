package cl.municipalidad.gateway.config;

import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.web.servlet.function.RequestPredicates.path;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;
import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;

@Configuration
public class GatewayConfig {

    @Bean
    public RouterFunction<ServerResponse> gatewayRoutes() {
        return route("MS-usuarios")
                .route(path("/api/v1/usuarios/**"), http())
                .filter(lb("ms-usuarios"))
                .build()
                
            .and(route("MS-auth")
                .route(path("/api/v1/auth/**"), http())
                .filter(lb("ms-auth"))
                .build())

            .and(route("MS-canchas")
                .route(path("/api/v1/canchas/**"), http())
                .filter(lb("ms-canchas"))
                .build())
                
            .and(route("MS-reservas")
                .route(path("/api/v1/reservas/**"), http())
                .filter(lb("ms-reservas"))
                .build())

            .and(route("MS-soporte")
                .route(path("/api/v1/soporte/**"), http())
               .filter(lb("ms-soporte"))
                .build())

            .and(route("MS-reportes")
                .route(path("/api/v1/reportes/**"), http())
                .filter(lb("ms-reportes"))
                .build())

            .and(route("MS-inventario")
                .route(path("/api/v1/inventario/**"), http())
                .filter(lb("ms-inventario"))
                .build())

            .and(route("MS-pagos")
                .route(path("/api/v1/pagos/**"), http())
                .filter(lb("ms-pagos"))
                .build())

            .and(route("MS-reseñas")
                .route(path("/api/v1/reseñas/**"), http())
                .filter(lb("ms-reseñas"))
                .build())

            .and(route("MS-restricciones")
                .route(path("/api/v1/restricciones/**"), http())
                .filter(lb("ms-restricciones"))
                .build());





    }
}