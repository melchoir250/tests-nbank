package api.requests.skelethon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import api.models.BaseModel;
import api.models.CreateAccountRequest;
import api.models.CreateAccountResponse;
import api.models.CreateUserRequest;
import api.models.CreateUserResponse;
import api.models.CustomerProfile;
import api.models.DepositRequest;
import api.models.DepositResponse;
import api.models.DepositTransferRequest;
import api.models.DepositTransferResponse;
import api.models.LoginUserRequest;
import api.models.LoginUserResponse;
import api.models.UpdateProfileNameRequest;
import api.models.UpdateProfileNameResponse;

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

  private final String url;
  private final Class<? extends BaseModel> requestModel;
  private final Class<? extends BaseModel> responseModel;
}
