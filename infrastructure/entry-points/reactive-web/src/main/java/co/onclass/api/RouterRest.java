package co.onclass.api;

import co.onclass.api.dto.ApiErrorResponse;
import co.onclass.api.dto.ApiSuccessResponse;
import co.onclass.api.dto.bootcamp.BootcampRequestDto;
import co.onclass.api.dto.bootcamp.CapacidadesBootcampRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static co.onclass.api.constants.ApiConstants.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = GUARDAR_BOOTCAMP,
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "listenPOSTGuardarBootcamp",
                    operation = @Operation(
                            operationId = "guardarBootcamp",
                            summary = "Guardar un nuevo Bootcamp",
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Datos del nuevo Bootcamp a crear",
                                    content = @Content(schema = @Schema(implementation = BootcampRequestDto.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Bootcamp guardado exitosamente",
                                            content = @Content(schema = @Schema(implementation = ApiSuccessResponse.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Datos invalidos",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = ASIGNAR_CAPACIDADES,
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "listenPOSTAsignarCapacidades",
                    operation = @Operation(
                            operationId = "asignarCapacidades",
                            summary = "Asigna las capacidades de un Bootcamp",
                            parameters = {
                                    @Parameter(
                                            in = ParameterIn.PATH,
                                            name = ID_BOOTCAMP_PATH_VARIABLE,
                                            description = "Id del Bootcamp",
                                            required = true
                                    )
                            },
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Lista con las capacidades a relacionar",
                                    content = @Content(schema = @Schema(implementation = CapacidadesBootcampRequestDto.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Capacidades relacionadas al Bootcamp exitosamente",
                                            content = @Content(schema = @Schema(implementation = ApiSuccessResponse.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Datos invalidos",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = OBTENER_BOOTCAMPS,
                    method = RequestMethod.GET,
                    beanClass = Handler.class,
                    beanMethod = "listenGETBootcamps",
                    operation = @Operation(
                            operationId = "obtenerBootcampsPaginados",
                            summary = "Lista de los Bootcamps paginados y filtrados",
                            parameters = {
                                    @Parameter(
                                            in = ParameterIn.QUERY,
                                            name = "page",
                                            description = "Página de la petición"
                                    ),
                                    @Parameter(
                                            in = ParameterIn.QUERY,
                                            name = "size",
                                            description = "Cantidad de elementos a mostrar en la página"
                                    ),
                                    @Parameter(
                                            in = ParameterIn.QUERY,
                                            name = "sortBy",
                                            description = "Campo por el cual se ordenarán los elementos"
                                    ),
                                    @Parameter(
                                            in = ParameterIn.QUERY,
                                            name = "order",
                                            description = "Orden en que se mostrarán los elementos"
                                    )
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Bootcamps paginados y filtrados exitosamente"
//                                            content = @Content(schema = @Schema(implementation = PaginaDto.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Datos invalidos",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST(GUARDAR_BOOTCAMP), handler::listenPOSTGuardarBootcamp)
                .andRoute(POST(ASIGNAR_CAPACIDADES), handler::listenPOSTAsignarCapacidades)
                .andRoute(GET(OBTENER_BOOTCAMPS), handler::listenGETBootcamps);
    }
}
