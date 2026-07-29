package api.specs;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import org.apache.http.HttpHeaders;
import api.configs.Config;
import api.configs.PROPERTY;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import api.models.LoginUserRequest;
import api.requests.LoginUserRequester;

public class RequestSpecs {
  private RequestSpecs() {
  }

  private static RequestSpecBuilder baseRequestBuilder() {
    return new RequestSpecBuilder()
        .setContentType(ContentType.JSON)
        .setAccept(ContentType.JSON)
        .setBaseUri(Config.getProperty(PROPERTY.SERVER) + Config.getProperty(PROPERTY.API_VERSION));
  }

  private static RequestSpecBuilder defaultRequestBuilder() {
    return baseRequestBuilder()
        .addFilters(List.of(new RequestLoggingFilter(), new ResponseLoggingFilter()));
  }

  public static RequestSpecification unauthSpec() {
    return defaultRequestBuilder().build();
  }

  public static RequestSpecification adminSpec() {
    return authenticated(adminAuthHeader());
  }

  public static RequestSpecification adminSpecWithoutLogging() {
    return baseRequestBuilder()
        .addHeader(HttpHeaders.AUTHORIZATION, adminAuthHeader())
        .build();
  }

  private static String adminAuthHeader() {
    String credentials = Config.getProperty(PROPERTY.ADMIN_USERNAME)
        + ":"
        + Config.getProperty(PROPERTY.ADMIN_PASSWORD);
    return "Basic " + Base64.getEncoder()
        .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
  }

  public static RequestSpecification authenticated(String authHeader) {
    return defaultRequestBuilder()
        .addHeader(HttpHeaders.AUTHORIZATION, authHeader)
        .build();
  }

  public static RequestSpecification authAsUser(String username, String password) {
    return authenticated(getUserAuthHeader(username, password));
  }

  public static String loginAuthHeader(String username, String password) {
    return getUserAuthHeader(username, password);
  }

  public static String getUserAuthHeader(String username, String password) {
    return new LoginUserRequester(
        unauthSpec(),
        ResponseSpecs.requestReturnsOK())
        .post(LoginUserRequest.builder()
            .username(username)
            .password(password)
            .build())
        .extract()
        .header(HttpHeaders.AUTHORIZATION);
  }
}
