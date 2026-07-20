package ui;

import api.generators.RandomData;
import api.requests.steps.CustomerContext;
import constants.ProfileLimits;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ui.pages.BankAlert;
import ui.pages.UserDashboard;

@DisplayName("UI / Edit Profile")
class UpdateProfileNameUiTest extends BaseUiTest {

    @ParameterizedTest
    @MethodSource("positiveNames")
    @DisplayName("изменение имени requests/ui/profile_tests")
    void shouldUpdateProfileName(String newName) {
        CustomerContext customer = CustomerContext.create();

        authAsUser(customer.user());
        new UserDashboard().open()
                .openEditProfile()
                .updateName(newName)
                .checkAlertMessageAndAccept(BankAlert.NAME_UPDATED_SUCCESSFULLY.getMessage())
                .openDashboard()
                .checkNameDisplayed(newName);

        customer.assertProfileName(newName);
    }

    static Stream<Arguments> positiveNames() {
        return Stream.of(
                Arguments.of(RandomData.validProfileName()),
                Arguments.of(ProfileLimits.VALID_ONE_CHAR),
                Arguments.of(ProfileLimits.VALID_MAX_LENGTH));
    }

    @ParameterizedTest
    @MethodSource("negativeNames")
    @DisplayName("негативный кейс изменение имени requests/ui/profile_tests")
    void shouldRejectInvalidProfileName(String newName) {
        CustomerContext customer = CustomerContext.create();

        authAsUser(customer.user());
        new UserDashboard().open()
                .openEditProfile()
                .updateName(newName)
                .checkAlertMessageAndAccept(BankAlert.NAME_MUST_CONTAIN_TWO_WORDS.getMessage());

        customer.assertProfileName(null);
    }

    static Stream<Arguments> negativeNames() {
        return Stream.of(
                Arguments.of(ProfileLimits.WITH_DIGIT),
                Arguments.of(ProfileLimits.WITH_SPECIAL),
                Arguments.of(ProfileLimits.THREE_WORDS));
    }
}
