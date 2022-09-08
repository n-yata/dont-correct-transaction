package example.micronaut;

import org.json.JSONObject;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import example.micronaut.domain.Mutter;
import example.micronaut.repository.MutterRepository;
import io.micronaut.function.aws.MicronautRequestHandler;
import jakarta.inject.Inject;

//@Transactional(rollbackFor = EmptyResultException.class)
public class FunctionRequestHandler
        extends MicronautRequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Inject
    ObjectMapper objectMapper;
    @Inject
    MutterRepository mutterRepo;

    private static APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

    @Override
    public APIGatewayProxyResponseEvent execute(APIGatewayProxyRequestEvent input) {

        // insert
        saveMutter();

        // select(throw exception)
        String str = selectMutter();

        JSONObject body = new JSONObject();
        body.put("res", str);
        response.setStatusCode(200);
        response.setBody(str);

        return response;
    }

    private void saveMutter() {
        Mutter mu = new Mutter();
        mu.setName("test");
        mu.setText("text");
        mutterRepo.save(mu);
    }

    private String selectMutter() {
        // throw exception
        Mutter mutter = mutterRepo.findById(0);
        System.out.println(mutter);
        System.out.println(mutter.getName());
        System.out.println(mutter.getText());

        return "name:" + mutter.getName() + " text:" + mutter.getText();
    }
}
