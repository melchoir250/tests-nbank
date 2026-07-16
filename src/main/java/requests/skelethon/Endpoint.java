package requests.skelethon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import models.BaseModel;
import models.CreateAccountRequest;
import models.CreateAccountResponse;
import models.CreateUserRequest;
import models.CreateUserResponse;
import models.CustomerProfile;
import models.DepositRequest;
import models.DepositResponse;
import models.DepositTransferRequest;
import models.DepositTransferResponse;
import models.LoginUserRequest;
import models.LoginUserResponse;
import models.UpdateProfileNameRequest;
import models.UpdateProfileNameResponse;

@Getter
@AllArgsConstructor
public enum Endpoint {
  ADMIN_CREATE_USER(
    "/admin/users",
    CreateUserRequest.class,
    CreateUserResponse.class),
  LOGIN(
    "/auth/login",
    LoginUserRequest.class,
    LoginUserResponse.class),
  CREATE_ACCOUNT(
    "/accounts",
    CreateAccountRequest.class,
    CreateAccountResponse.class),
  DEPOSIT(
    "/accounts/deposit",
    DepositRequest.class,
    DepositResponse.class),
  TRANSFER(
    "/accounts/transfer",
    DepositTransferRequest.class,
    DepositTransferResponse.class),
  CUSTOMER_ACCOUNTS(
    "/customer/accounts",
    BaseModel.class,
    CreateAccountResponse.class),
  GET_PROFILE(
    "/customer/profile",
    BaseModel.class,
    CustomerProfile.class),
  UPDATE_PROFILE(
    "/customer/profile",
    UpdateProfileNameRequest.class,
    UpdateProfileNameResponse.class);
  // @formatter:on

  private final String url;
  private final Class<? extends BaseModel> requestModel;
  private final Class<? extends BaseModel> responseModel;
}
