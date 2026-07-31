package api.requests.steps;

import api.models.DepositTransferRequest;
import api.models.DepositTransferResponse;
import api.models.fraud.FraudCheckStatusResponse;
import api.models.fraud.FraudTransferRequest;
import api.models.fraud.FraudTransferResponse;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.ApiRequester;
import api.specs.ResponseSpecs;
import constants.FraudMessages;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public final class TransferSteps {
  private TransferSteps() {
  }

  public static DepositTransferRequest transferRequest(
      int senderAccountId,
      int receiverAccountId,
      double amount) {
    return DepositTransferRequest.builder()
        .senderAccountId(senderAccountId)
        .receiverAccountId(receiverAccountId)
        .amount(amount)
        .build();
  }

  public static DepositTransferResponse transfer(
      RequestSpecification userSpec,
      DepositTransferRequest request) {
    return new ApiRequester(userSpec, Endpoint.TRANSFER, ResponseSpecs.transferSuccessful())
        .postAndExtract(request);
  }

  public static void transferExpectingMinAmountError(
      RequestSpecification userSpec,
      DepositTransferRequest request) {
    transferExpecting(userSpec, request, ResponseSpecs.transferAmountTooLow());
  }

  public static void transferExpectingMaxAmountError(
      RequestSpecification userSpec,
      DepositTransferRequest request) {
    transferExpecting(userSpec, request, ResponseSpecs.transferAmountTooHigh());
  }

  private static void transferExpecting(
      RequestSpecification userSpec,
      DepositTransferRequest request,
      ResponseSpecification responseSpec) {
    new ApiRequester(userSpec, Endpoint.TRANSFER, responseSpec)
        .post(request);
  }

  public static FraudTransferRequest fraudTransferRequest(
      int senderAccountId,
      int receiverAccountId,
      double amount) {
    return FraudTransferRequest.builder()
        .senderAccountId(senderAccountId)
        .receiverAccountId(receiverAccountId)
        .amount(amount)
        .description(FraudMessages.TRANSFER_DESCRIPTION)
        .build();
  }

  public static FraudTransferResponse transferWithFraudCheck(
      RequestSpecification userSpec,
      FraudTransferRequest request) {
    return new ApiRequester(
        userSpec,
        Endpoint.TRANSFER_WITH_FRAUD_CHECK,
        ResponseSpecs.requestReturnsOK())
        .postAndExtract(request);
  }

  public static FraudCheckStatusResponse getFraudCheckStatus(
      RequestSpecification userSpec,
      long transactionId) {
    return new ApiRequester(userSpec, Endpoint.FRAUD_CHECK_STATUS, ResponseSpecs.requestReturnsOK())
        .getByIdAndExtract(transactionId);
  }
}
