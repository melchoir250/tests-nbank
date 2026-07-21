package ui;

import api.generators.RandomData;
import common.annotations.UserSession;
import common.storage.SessionStorage;
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
    @UserSession
    @MethodSource("positiveNames")
    @DisplayName("изменение имени requests/ui/profile_tests")
    void shouldUpdateProfileName(String newName) {

        new UserDashboard().open()
                .openEditProfile()
                .updateName(newName)
                .checkAlertMessageAndAccept(BankAlert.NAME_UPDATED_SUCCESSFULLY.getMessage())
                .openDashboard()
                .checkNameDisplayed(newName);

        SessionStorage.getSteps().assertProfileName(newName);
    }

    static Stream<Arguments> positiveNames() {
        return Stream.of(
                Arguments.of(RandomData.validProfileName()),
                Arguments.of(ProfileLimits.VALID_ONE_CHAR),
                Arguments.of(ProfileLimits.VALID_MAX_LENGTH));
    }

    @ParameterizedTest
    @UserSession
    @MethodSource("negativeNames")
    @DisplayName("негативный кейс изменение имени requests/ui/profile_tests")
    void shouldRejectInvalidProfileName(String newName) {
        new UserDashboard().open()
                .openEditProfile()
                .updateName(newName)
                .checkAlertMessageAndAccept(BankAlert.NAME_MUST_CONTAIN_TWO_WORDS.getMessage());

        SessionStorage.getSteps().assertProfileName(null);
    }

    static Stream<Arguments> negativeNames() {
        return Stream.of(
                Arguments.of(ProfileLimits.WITH_DIGIT),
                Arguments.of(ProfileLimits.WITH_SPECIAL),
                Arguments.of(ProfileLimits.THREE_WORDS));
    }
}
