package api.requests.skelethon.requesters;

import static io.restassured.RestAssured.given;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import api.models.BaseModel;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.HttpRequest;
import api.requests.skelethon.interfaces.CrudEndpointInterface;
import api.requests.skelethon.interfaces.GetAllEndpointInterface;

public class CrudRequester extends HttpRequest
    implements CrudEndpointInterface, GetAllEndpointInterface {
  public CrudRequester(RequestSpecification requestSpecification, Endpoint endpoint,
      ResponseSpecification responseSpecification) {
    super(requestSpecification, endpoint, responseSpecification);
  }

  public ValidatableResponse post() {
    return post(null);
  }

  @Override
  public ValidatableResponse post(BaseModel model) {
    var body = model == null ? "" : model;
    return given()
        .spec(requestSpecification)
        .body(body)
        .post(endpoint.getUrl())
        .then()
        .assertThat()
        .spec(responseSpecification);
  }

  @Override
  public ValidatableResponse get(int id) {
    return given()
        .spec(requestSpecification)
        .get(endpoint.getUrl())
        .then()
        .assertThat()
        .spec(responseSpecification);
  }

  public ValidatableResponse get() {
    return get(0);
  }

  @Override
  public ValidatableResponse put(int id, BaseModel model) {
    var body = model == null ? "" : model;
    return given()
        .spec(requestSpecification)
        .body(body)
        .put(endpoint.getUrl())
        .then()
        .assertThat()
        .spec(responseSpecification);
  }

  public ValidatableResponse put(BaseModel model) {
    return put(0, model);
  }

  @Override
  public Object delete(int id) {
    return null;
  }

  @Override
  public ValidatableResponse getAll(Class<?> clazz) {
    return given()
        .spec(requestSpecification)
        .get(endpoint.getUrl())
        .then()
        .assertThat()
        .spec(responseSpecification);
  }
}
