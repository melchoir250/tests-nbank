package api.requests.steps;

import api.generators.RandomModelGenerator;
import api.models.CreateAccountResponse;
import api.models.CreateUserRequest;
import api.models.CreateUserResponse;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.skelethon.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import java.time.Duration;
import java.util.List;
import java.util.function.BooleanSupplier;

public final class AdminSteps {
    private static final Duration ADMIN_VISIBILITY_TIMEOUT = Duration.ofSeconds(15);
    private static final Duration POLL_INTERVAL = Duration.ofMillis(250);

    private AdminSteps() {
    }

    public static CreateUserRequest createUser() {
        CreateUserRequest userRequest = RandomModelGenerator.generate(CreateUserRequest.class);

        new ValidatedCrudRequester<CreateUserResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_CREATE_USER,
                ResponseSpecs.entityWasCreated())
                .post(userRequest);

        return userRequest;
    }

    public static void createUserExpectingInvalidUsername(CreateUserRequest userRequest) {
        new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_CREATE_USER,
                ResponseSpecs.invalidUsernameErrors())
                .post(userRequest);
    }

    public static List<CreateUserResponse> getAllUsers() {
        return new ValidatedCrudRequester<CreateUserResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_CREATE_USER,
                ResponseSpecs.requestReturnsOK())
                .getAll(CreateUserResponse[].class);
    }

    public static void waitUntilAccountVisible(String accountNumber) {
        waitUntil(
                () -> getAllAccountsWithoutLogging()
                        .stream()
                        .anyMatch(account -> accountNumber.equals(account.getAccountNumber())),
                "account " + accountNumber + " to appear in GET /admin/users");
    }

    public static void waitUntilAccountHasTransactions(String accountNumber, int minimumCount) {
        waitUntil(
                () -> getAllAccountsWithoutLogging()
                        .stream()
                        .filter(account -> accountNumber.equals(account.getAccountNumber()))
                        .map(CreateAccountResponse::getTransactions)
                        .anyMatch(transactions -> transactions != null && transactions.size() >= minimumCount),
                "account " + accountNumber + " to have at least " + minimumCount
                        + " transactions in GET /admin/users");
    }

    private static List<CreateAccountResponse> getAllAccountsWithoutLogging() {
        return new ValidatedCrudRequester<CreateUserResponse>(
                RequestSpecs.adminSpecWithoutLogging(),
                Endpoint.ADMIN_CREATE_USER,
                ResponseSpecs.requestReturnsOK())
                .getAll(CreateUserResponse[].class)
                .stream()
                .filter(user -> user.getAccounts() != null)
                .flatMap(user -> user.getAccounts().stream())
                .toList();
    }

    private static void waitUntil(BooleanSupplier condition, String description) {
        long deadline = System.nanoTime() + ADMIN_VISIBILITY_TIMEOUT.toNanos();
        RuntimeException lastFailure = null;

        while (System.nanoTime() < deadline) {
            try {
                if (condition.getAsBoolean()) {
                    return;
                }
            } catch (RuntimeException error) {
                lastFailure = error;
            }

            try {
                Thread.sleep(POLL_INTERVAL.toMillis());
            } catch (InterruptedException error) {
                Thread.currentThread().interrupt();
                throw new AssertionError("Interrupted while waiting for " + description, error);
            }
        }

        throw new AssertionError(
                "Timed out after " + ADMIN_VISIBILITY_TIMEOUT.toSeconds()
                        + " seconds waiting for " + description,
                lastFailure);
    }
}
