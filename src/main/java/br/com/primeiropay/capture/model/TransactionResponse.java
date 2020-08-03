package br.com.primeiropay.capture.model;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter = true, publicConverter = false)
public class TransactionResponse {

  private String id;
  private String result_code;
  private String description;


  public TransactionResponse(String id, String result_code, String description) {
    this.id = id;
    this.setResult_code(result_code);
    this.setDescription(description);
  }

  public TransactionResponse(JsonObject json) {
    TransactionResponseConverter.fromJson(json, this);
  }

  public TransactionResponse(TransactionResponse other) {
    this.id = other.getId();
    this.setResult_code(other.getResult_code());
    this.setDescription(other.getDescription());
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    TransactionResponseConverter.toJson(this, json);
    return json;
  }

  @Fluent public TransactionResponse setId(String id){
    this.id = id;
    return this;
  }

  public String getId() {
    return this.id;
  }

  public String getResult_code() {
    return result_code;
  }

  @Fluent public TransactionResponse setResult_code(String result_code) {
    this.result_code = result_code;
    return this;
  }

  public String getDescription() {
    return description;
  }

  @Fluent public TransactionResponse setDescription(String description) {
    this.description = description;
    return this;
  }
}
