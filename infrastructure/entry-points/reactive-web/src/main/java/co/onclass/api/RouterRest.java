package co.onclass.api;

import co.onclass.api.dto.ApiErrorResponse;
import co.onclass.api.dto.ApiSuccessResponse;
import co.onclass.api.dto.bootcamp.BootcampRequestDto;
import io.swagger.v3.oas.annotations.Operation;
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

import static co.onclass.api.constants.ApiConstants.GUARDAR_BOOTCAMP;
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
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST(GUARDAR_BOOTCAMP), handler::listenPOSTGuardarBootcamp);
    }
}
