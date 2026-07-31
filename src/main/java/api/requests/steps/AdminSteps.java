package api.requests.steps;

import api.generators.RandomModelGenerator;
import api.models.CreateUserRequest;
import api.models.CreateUserResponse;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.ApiRequester;
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

        new ApiRequester(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_CREATE_USER,
                ResponseSpecs.entityWasCreated())
                .post(userRequest);

        return userRequest;
    }

    public static void createUserExpectingInvalidUsername(CreateUserRequest userRequest) {
        new ApiRequester(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_CREATE_USER,
                ResponseSpecs.invalidUsernameErrors())
                .post(userRequest);
    }

    public static List<CreateUserResponse> getAllUsers() {
        return new ApiRequester(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_CREATE_USER,
                ResponseSpecs.requestReturnsOK())
                .getAllAndExtract(CreateUserResponse[].class);
    }

    public static void waitUntilAccountVisible(String accountNumber) {
        waitUntil(
                () -> DataBaseSteps.getAccountByAccountNumber(accountNumber) != null,
                "account " + accountNumber + " to appear in database");
    }

    public static void waitUntilAccountHasTransactions(String accountNumber, int minimumCount) {
        waitUntil(
                () -> {
                    var account = DataBaseSteps.getAccountByAccountNumber(accountNumber);
                    return account != null
                            && DataBaseSteps.countTransactions(account.getId()) >= minimumCount;
                },
                "account " + accountNumber + " to have at least " + minimumCount
                        + " transactions in database");
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
