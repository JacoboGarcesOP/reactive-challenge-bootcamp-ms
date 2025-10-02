package co.com.bancolombia.api;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.assertj.core.api.Assertions.assertThat;

public class RouterRestTest {

    @Test
    void routerBeans_areCreated() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(RouterRest.class);
        ctx.register(TestHandlerConfig.class);
        ctx.refresh();

        RouterFunction<ServerResponse> create = ctx.getBean("createBootcampRoute", RouterFunction.class);
        RouterFunction<ServerResponse> getById = ctx.getBean("getBootcampByIdRoute", RouterFunction.class);
        RouterFunction<ServerResponse> delete = ctx.getBean("deleteBootcamp", RouterFunction.class);
        RouterFunction<ServerResponse> getAll = ctx.getBean("getAllBootcampsRoute", RouterFunction.class);

        assertThat(create).isNotNull();
        assertThat(getById).isNotNull();
        assertThat(delete).isNotNull();
        assertThat(getAll).isNotNull();

        ctx.close();
    }
}
