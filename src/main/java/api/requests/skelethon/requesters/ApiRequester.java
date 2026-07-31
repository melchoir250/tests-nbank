package api.requests.skelethon.requesters;

import static io.restassured.RestAssured.given;

import api.models.BaseModel;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.HttpRequest;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import java.util.Arrays;
import java.util.List;

public final class ApiRequester extends HttpRequest {

  public ApiRequester(
      RequestSpecification requestSpecification,
      Endpoint endpoint,
      ResponseSpecification responseSpecification) {
    super(requestSpecification, endpoint, responseSpecification);
  }

  public ValidatableResponse post() {
    return post(null);
  }

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

  public ValidatableResponse get() {
    return given()
        .spec(requestSpecification)
        .get(endpoint.getUrl())
        .then()
        .assertThat()
        .spec(responseSpecification);
  }

  public ValidatableResponse getById(long id) {
    return given()
        .spec(requestSpecification)
        .pathParam("id", id)
        .get(endpoint.getUrl())
        .then()
        .assertThat()
        .spec(responseSpecification);
  }

  public ValidatableResponse put(BaseModel model) {
    var body = model == null ? "" : model;
    return given()
        .spec(requestSpecification)
        .body(body)
        .put(endpoint.getUrl())
        .then()
        .assertThat()
        .spec(responseSpecification);
  }

  @SuppressWarnings("unchecked")
  public <T extends BaseModel> T postAndExtract(BaseModel model) {
    return (T) post(model).extract().as(endpoint.getResponseModel());
  }

  @SuppressWarnings("unchecked")
  public <T extends BaseModel> T postAndExtract() {
    return (T) post().extract().as(endpoint.getResponseModel());
  }

  @SuppressWarnings("unchecked")
  public <T extends BaseModel> T getAndExtract() {
    return (T) get().extract().as(endpoint.getResponseModel());
  }

  @SuppressWarnings("unchecked")
  public <T extends BaseModel> T getByIdAndExtract(long id) {
    return (T) getById(id).extract().as(endpoint.getResponseModel());
  }

  @SuppressWarnings("unchecked")
  public <T extends BaseModel> T putAndExtract(BaseModel model) {
    return (T) put(model).extract().as(endpoint.getResponseModel());
  }

  public <T> List<T> getAllAndExtract(Class<T[]> arrayClass) {
    T[] array = get().extract().as(arrayClass);
    return Arrays.asList(array);
  }
}
