package co.com.bancolombia.api;

import co.com.bancolombia.api.request.CreateBootcampRequest;
import co.com.bancolombia.model.bootcamp.exception.DomainException;
import co.com.bancolombia.usecase.CreateBootcampUseCase;
import co.com.bancolombia.usecase.DeleteBootcampUseCase;
import co.com.bancolombia.usecase.GetBootcampByIdUseCase;
import co.com.bancolombia.usecase.GetPaginatedBootcampsUseCase;
import co.com.bancolombia.usecase.command.CreateBootcampCommand;
import co.com.bancolombia.usecase.exception.BussinessException;
import co.com.bancolombia.usecase.response.BootcampsWithTechnologiesResponse;
import co.com.bancolombia.usecase.response.CreateBootcampResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class HandlerTest {

    private CreateBootcampUseCase createBootcampUseCase;
    private GetPaginatedBootcampsUseCase getPaginatedBootcampsUseCase;
    private GetBootcampByIdUseCase getBootcampByIdUseCase;
    private DeleteBootcampUseCase deleteBootcampUseCase;
    private Handler handler;

    @BeforeEach
    void setUp() {
        createBootcampUseCase = mock(CreateBootcampUseCase.class);
        getPaginatedBootcampsUseCase = mock(GetPaginatedBootcampsUseCase.class);
        getBootcampByIdUseCase = mock(GetBootcampByIdUseCase.class);
        deleteBootcampUseCase = mock(DeleteBootcampUseCase.class);
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        handler = new Handler(createBootcampUseCase, getPaginatedBootcampsUseCase, getBootcampByIdUseCase, validator, deleteBootcampUseCase);
    }

    @Test
    void createBootcamp_success() {
        CreateBootcampResponse fakeResponse = new CreateBootcampResponse(1L, "Bootcamp", "Desc", LocalDate.now().plusDays(1), 10, List.of());
        when(createBootcampUseCase.execute(any(CreateBootcampCommand.class))).thenReturn(Mono.just(fakeResponse));

        CreateBootcampRequest request = new CreateBootcampRequest();
        request.setName("Bootcamp");
        request.setDescription("Desc");
        request.setLaunchDate(LocalDate.now().plusDays(1));
        request.setDuration(10);
        request.setCapacityIds(List.of(1L));

        ServerRequest serverRequest = Mockito.mock(ServerRequest.class);
        when(serverRequest.bodyToMono(CreateBootcampRequest.class)).thenReturn(Mono.just(request));

        Mono<ServerResponse> response = handler.createBootcamp(serverRequest);

        StepVerifier.create(response)
                .expectNextMatches(r -> r.statusCode().is2xxSuccessful())
                .verifyComplete();

        ArgumentCaptor<CreateBootcampCommand> captor = ArgumentCaptor.forClass(CreateBootcampCommand.class);
        verify(createBootcampUseCase).execute(captor.capture());
    }

    @Test
    void createBootcamp_validationError() {
        CreateBootcampRequest invalid = new CreateBootcampRequest();
        ServerRequest serverRequest = Mockito.mock(ServerRequest.class);
        when(serverRequest.bodyToMono(CreateBootcampRequest.class)).thenReturn(Mono.just(invalid));

        Mono<ServerResponse> response = handler.createBootcamp(serverRequest);

        StepVerifier.create(response)
                .expectNextMatches(r -> r.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void getBootcampById_badRequest_whenNotNumber() {
        ServerRequest req = Mockito.mock(ServerRequest.class);
        when(req.pathVariable("bootcampId")).thenReturn("abc");

        StepVerifier.create(handler.getBootcampById(req))
                .expectNextMatches(r -> r.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void getBootcampById_success() {
        BootcampsWithTechnologiesResponse payload = new BootcampsWithTechnologiesResponse(1L, "N", "D", LocalDate.now().plusDays(1), 1, List.of());
        when(getBootcampByIdUseCase.execute(1L)).thenReturn(Mono.just(payload));
        ServerRequest req = Mockito.mock(ServerRequest.class);
        when(req.pathVariable("bootcampId")).thenReturn("1");

        StepVerifier.create(handler.getBootcampById(req))
                .expectNextMatches(r -> r.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void getAllBootcamps_domainError() {
        when(getPaginatedBootcampsUseCase.execute(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(Mono.error(new DomainException("bad domain")));
        ServerRequest req = Mockito.mock(ServerRequest.class);
        when(req.queryParam("page")).thenReturn(java.util.Optional.empty());
        when(req.queryParam("size")).thenReturn(java.util.Optional.empty());
        when(req.queryParam("sortBy")).thenReturn(java.util.Optional.empty());
        when(req.queryParam("order")).thenReturn(java.util.Optional.empty());

        StepVerifier.create(handler.getAllBootcamps(req))
                .expectNextMatches(r -> r.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void deleteBootcamp_businessError() {
        when(deleteBootcampUseCase.execute(2L)).thenReturn(Mono.error(new BussinessException("not found")));
        ServerRequest req = Mockito.mock(ServerRequest.class);
        when(req.pathVariable("bootcampId")).thenReturn("2");

        StepVerifier.create(handler.deleteBootcamp(req))
                .expectNextMatches(r -> r.statusCode().is4xxClientError())
                .verifyComplete();
    }
}


