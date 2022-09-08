package example.micronaut;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import io.micronaut.data.exceptions.EmptyResultException;
import jakarta.inject.Inject;

//@MicronautTest
@Transactional(rollbackFor = EmptyResultException.class)
public class FunctionRequestHandlerTest {

    @Inject
    private static FunctionRequestHandler handler;

    @BeforeAll
    public static void setupSpec() {
        handler = new FunctionRequestHandler();
    }

    @AfterAll
    public static void cleanupSpec() {
        handler.getApplicationContext().close();
    }

    @Test
    //    @Transactional(rollbackFor = EmptyResultException.class)
    public void testHandler() {
        /* 引数設定 */
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setHttpMethod("GET");
        request.setPath("/");
        request.setBody("{\"message\":\"Hello World\"}");

        /* 呼び出し */
        APIGatewayProxyResponseEvent response = handler.execute(request);

        /* 判定 */
        System.out.println(response.getBody());
        assertEquals(200, response.getStatusCode().intValue());
    }
}
