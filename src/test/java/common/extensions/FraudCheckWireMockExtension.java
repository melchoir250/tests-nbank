package common.extensions;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

import api.models.fraud.FraudCheckServiceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import common.annotations.FraudCheckMock;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public final class FraudCheckWireMockExtension implements BeforeEachCallback, AfterEachCallback {
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private WireMockServer wireMockServer;

  @Override
  public void beforeEach(ExtensionContext context) {
    FraudCheckMock mockConfig = context.getTestMethod()
        .map(method -> method.getAnnotation(FraudCheckMock.class))
        .orElseGet(() -> context.getTestClass()
            .map(clazz -> clazz.getAnnotation(FraudCheckMock.class))
            .orElse(null));

    if (mockConfig != null) {
      setupWireMock(mockConfig);
    }
  }

  private void setupWireMock(FraudCheckMock config) {
    wireMockServer = new WireMockServer(
        WireMockConfiguration.wireMockConfig().port(config.port()));
    wireMockServer.start();

    wireMockServer.stubFor(
        post(urlPathEqualTo(config.endpoint()))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(createResponseBody(config))));
  }

  private static String createResponseBody(FraudCheckMock config) {
    FraudCheckServiceResponse response = FraudCheckServiceResponse.builder()
        .status(config.status())
        .decision(config.decision())
        .riskScore(config.riskScore())
        .reason(config.reason())
        .requiresManualReview(config.requiresManualReview())
        .additionalVerificationRequired(config.additionalVerificationRequired())
        .build();

    try {
      return OBJECT_MAPPER.writeValueAsString(response);
    } catch (JsonProcessingException exception) {
      throw new IllegalStateException("Failed to serialize fraud-check mock response", exception);
    }
  }

  @Override
  public void afterEach(ExtensionContext context) {
    if (wireMockServer != null) {
      wireMockServer.stop();
      wireMockServer = null;
    }
  }
}
