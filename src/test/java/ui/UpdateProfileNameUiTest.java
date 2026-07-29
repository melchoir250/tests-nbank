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
    private static final String INITIAL_NAME = "Current User";

    @ParameterizedTest
    @MethodSource("positiveNames")
    @UserSession
    @DisplayName("Изменяет имя профиля")
    void shouldUpdateProfileName(String newName) {
        SessionStorage.getSteps().updateProfileName(INITIAL_NAME);

        new UserDashboard().open()
                .openEditProfile()
                .waitUntilNameLoaded(INITIAL_NAME)
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
    @MethodSource("negativeNames")
    @UserSession
    @DisplayName("Отклоняет недопустимое имя профиля")
    void shouldRejectInvalidProfileName(String newName) {
        SessionStorage.getSteps().updateProfileName(INITIAL_NAME);

        new UserDashboard().open()
                .openEditProfile()
                .waitUntilNameLoaded(INITIAL_NAME)
                .updateName(newName)
                .checkAlertMessageAndAccept(BankAlert.NAME_MUST_CONTAIN_TWO_WORDS.getMessage());

        SessionStorage.getSteps().assertProfileName(INITIAL_NAME);
    }

    static Stream<Arguments> negativeNames() {
        return Stream.of(
                Arguments.of(ProfileLimits.WITH_DIGIT),
                Arguments.of(ProfileLimits.WITH_SPECIAL),
                Arguments.of(ProfileLimits.THREE_WORDS));
    }
}
