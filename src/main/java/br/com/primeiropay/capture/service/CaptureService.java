package br.com.primeiropay.capture.service;

import br.com.primeiropay.capture.service.impl.CaptureServiceImpl;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.web.api.OperationRequest;
import io.vertx.ext.web.api.OperationResponse;
import io.vertx.ext.web.api.generator.WebApiServiceGen;

@WebApiServiceGen
public interface CaptureService {

    static CaptureService create() {
        return new CaptureServiceImpl();
    }

    void doCapture(String cpf,
                   String amount,
                   String currency,
                   String paymentType,
                   String id,
                   OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler);
}
