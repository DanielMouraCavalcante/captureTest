package br.com.primeiropay.capture;

import br.com.primeiropay.capture.service.CaptureService;
import br.com.primeiropay.capture.util.ApplicationProperties;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;
import io.vertx.serviceproxy.ServiceBinder;

public class CaptureTestApplication extends AbstractVerticle {

	HttpServer server;
	ServiceBinder serviceBinder;

	MessageConsumer<JsonObject> consumer;

	private void startCaptureService() {
		serviceBinder = new ServiceBinder(vertx);

		CaptureService captureService = CaptureService.create();
		consumer = serviceBinder
				.setAddress("transactions_manager.capture")
				.register(CaptureService.class, captureService);
	}

	private Future<Void> startHttpServer() {
		Promise<Void> promise = Promise.promise();
		OpenAPI3RouterFactory.create(this.vertx, "/openapi.json", openAPI3RouterFactoryAsyncResult -> {
			if (openAPI3RouterFactoryAsyncResult.succeeded()) {
				OpenAPI3RouterFactory routerFactory = openAPI3RouterFactoryAsyncResult.result();

				routerFactory.mountServicesFromExtensions();

				Router router = routerFactory.getRouter();
				server = vertx.createHttpServer(new HttpServerOptions().setPort
						(Integer.parseInt(ApplicationProperties.getProperty("server.port"))).setHost("localhost"));
				server.requestHandler(router).listen(ar -> {
					if (ar.succeeded()) promise.complete();
					else promise.fail(ar.cause());
				});
			} else {
				promise.fail(openAPI3RouterFactoryAsyncResult.cause());
			}
		});
		return promise.future();
	}

	@Override
	public void start(Promise<Void> promise) {
		startCaptureService();
		startHttpServer().onComplete(promise);
	}

	@Override
	public void stop(){
		this.server.close();
		consumer.unregister();
	}

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new CaptureTestApplication());
	}

}
