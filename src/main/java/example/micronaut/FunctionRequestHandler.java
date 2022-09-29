package example.micronaut;

import java.sql.Connection;

import org.json.JSONObject;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import example.micronaut.domain.Mutter;
import example.micronaut.repository.MutterRepository;
import io.micronaut.data.exceptions.DataAccessException;
import io.micronaut.function.aws.MicronautRequestHandler;
import io.micronaut.transaction.SynchronousTransactionManager;
import io.micronaut.transaction.TransactionDefinition;
import io.micronaut.transaction.TransactionDefinition.Propagation;
import io.micronaut.transaction.TransactionStatus;
import jakarta.inject.Inject;

public class FunctionRequestHandler
        extends MicronautRequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Inject
    ObjectMapper objectMapper;
    @Inject
    MutterRepository mutterRepo;
    @Inject
    Connection connection; // ★transaction取得のため追加
    @Inject
    SynchronousTransactionManager<Connection> transactionManager; // ★transaction取得のため追加

    private static APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

    @Override
    public APIGatewayProxyResponseEvent execute(APIGatewayProxyRequestEvent input) {

        // ★transactionの取得
        TransactionStatus<Connection> status = transactionManager
                .getTransaction(TransactionDefinition.of(Propagation.REQUIRED));

        try {
            // メイン処理
            runProcess(input);
            response.setStatusCode(200);

        } catch (DataAccessException e) {
            e.printStackTrace();
            response.setStatusCode(500);
        }

        // ★transaction操作
        if (200 == response.getStatusCode()) {
            transactionManager.commit(status);
        } else {
            // これは書かなくても明示的にtransactionManager.commitしない限りコミットされない？
            transactionManager.rollback(status);
        }

        return response;
    }

    /**
     * メイン処理
     * @param input
     * @return
     */
    private APIGatewayProxyResponseEvent runProcess(APIGatewayProxyRequestEvent input) {

        /* insert */
        saveMutter();

        /* select */
        String str = selectMutter(1);
        /* select(throw exception) */
        //        String str = selectMutter(-1);

        JSONObject body = new JSONObject();
        body.put("res", str);
        response.setBody(str);

        return response;
    }

    private void saveMutter() {
        Mutter mu = new Mutter();
        mu.setName("test");
        mu.setText("text");
        mutterRepo.save(mu);
    }

    private String selectMutter(int id) {
        Mutter mutter = mutterRepo.findById(id);
        System.out.println(mutter);
        System.out.println(mutter.getName());
        System.out.println(mutter.getText());

        return "name:" + mutter.getName() + " text:" + mutter.getText();
    }
}
