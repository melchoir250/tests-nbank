package api.requests.steps;

import org.assertj.core.api.Assertions;
import api.models.CustomerProfile;
import api.models.UpdateProfileNameRequest;
import api.models.UpdateProfileNameResponse;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.ApiRequester;
import api.specs.ResponseSpecs;
import io.restassured.specification.RequestSpecification;

public final class ProfileSteps {
  private ProfileSteps() {
  }

  public static CustomerProfile getProfile(RequestSpecification userSpec) {
    return new ApiRequester(userSpec, Endpoint.GET_PROFILE, ResponseSpecs.requestReturnsOK())
        .getAndExtract();
  }

  public static UpdateProfileNameRequest updateProfileNameRequest(String name) {
    return UpdateProfileNameRequest.builder()
        .name(name)
        .build();
  }

  public static UpdateProfileNameResponse updateProfileName(
      RequestSpecification userSpec,
      UpdateProfileNameRequest request) {
    return new ApiRequester(userSpec, Endpoint.UPDATE_PROFILE, ResponseSpecs.profileUpdated())
        .putAndExtract(request);
  }

  public static void updateProfileNameExpectingBadRequest(
      RequestSpecification userSpec,
      UpdateProfileNameRequest request) {
    new ApiRequester(userSpec, Endpoint.UPDATE_PROFILE, ResponseSpecs.invalidProfileName())
        .put(request);
  }

  public static void assertProfile(
      RequestSpecification userSpec,
      String expectedUsername,
      String expectedName) {
    CustomerProfile profile = getProfile(userSpec);

    Assertions.assertThat(profile.getUsername())
        .as("Profile username via GET /customer/profile")
        .isEqualTo(expectedUsername);
    Assertions.assertThat(profile.getName())
        .as("Profile name via GET /customer/profile")
        .isEqualTo(expectedName);
  }
}
