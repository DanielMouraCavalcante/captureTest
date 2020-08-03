package br.com.primeiropay.capture.service.impl;

import br.com.primeiropay.capture.model.TransactionResponse;
import br.com.primeiropay.capture.service.CaptureService;
import br.com.primeiropay.capture.util.ApplicationProperties;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.api.OperationRequest;
import io.vertx.ext.web.api.OperationResponse;
import org.apache.commons.io.IOUtils;

import javax.net.ssl.HttpsURLConnection;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Logger;

public class CaptureServiceImpl implements CaptureService {

    private Logger logger = Logger.getLogger(CaptureServiceImpl.class.getName());

    @Override
    public void doCapture(String cpf,
                          String amount,
                          String currency,
                          String paymentType,
                          String id,
                          OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler) {
        String responseDescription = "";
        String responseId = "";
        String resultCode = "";

        try {
            String primeiroPayResponse = request(amount, currency, paymentType, id);
            logger.info(primeiroPayResponse);
            JsonObject jobj = new Gson().fromJson(primeiroPayResponse, JsonObject.class);
            logger.info(jobj.toString());
            resultCode = getResultCode(jobj);
            responseDescription = getResonseDescription(jobj);
            responseId = getResonseId(jobj);
        } catch (IOException e) {
            e.printStackTrace();
        }
        TransactionResponse response = new TransactionResponse(responseId, resultCode, responseDescription);
        resultHandler.handle(Future.succeededFuture(OperationResponse.completedWithJson(response.toJson())));
    }

    private String request(String amount, String currency, String paymentType, String id) throws IOException {
        URL url = new URL(ApplicationProperties.getProperty("capture.url") + id);

        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + ApplicationProperties.getProperty("api.key"));
        conn.setDoInput(true);
        conn.setDoOutput(true);

        String data = ""
                + "entityId=" + ApplicationProperties.getProperty("entity_id")
                + "&amount=" + amount
                + "&currency=" + currency
                + "&paymentType=" + paymentType;

        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(data);
        wr.flush();
        wr.close();
        int responseCode = conn.getResponseCode();
        InputStream is;

        if (responseCode >= 400) is = conn.getErrorStream();
        else is = conn.getInputStream();

        return IOUtils.toString(is);
    }

    private String getResultCode(JsonObject jsonObject) {
        JsonObject jobj2 = new Gson().fromJson(jsonObject.get("result").toString(), JsonObject.class);
        return removeDoubleQuotes(jobj2.get("code").toString());
    }

    private String getResonseId(JsonObject jsonObject) {
        return removeDoubleQuotes(jsonObject.get("id").toString());
    }

    private String getResonseDescription(JsonObject jsonObject) {
        JsonObject jobj2 = new Gson().fromJson(jsonObject.get("result").toString(), JsonObject.class);
        return removeDoubleQuotes(jobj2.get("description").toString());
    }

    private String removeDoubleQuotes(String str) {
        return str.replaceAll("^\"+|\"+$", "");
    }
}
