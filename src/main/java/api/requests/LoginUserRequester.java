package api.requests;

import static io.restassured.RestAssured.given;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import api.models.LoginUserRequest;
import api.requests.skelethon.Endpoint;

public class LoginUserRequester extends Request<LoginUserRequest> {
  public LoginUserRequester(
    RequestSpecification requestSpecification,
    ResponseSpecification responseSpecification) {
    super(requestSpecification, responseSpecification);
  }

  @Override
  public ValidatableResponse post(LoginUserRequest model) {
    return given()
      .spec(requestSpecification)
      .body(model)
      .post(Endpoint.LOGIN.getUrl())
      .then()
      .assertThat()
      .spec(responseSpecification);
  }
}
