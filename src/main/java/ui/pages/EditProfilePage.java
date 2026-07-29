package ui.pages;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

@Getter
public class EditProfilePage extends BasePage<EditProfilePage> {
    private SelenideElement nameInput = $(Selectors.byAttribute("placeholder", "Enter new name"));
    private SelenideElement saveChangesButton = $(Selectors.byText("💾 Save Changes"));

    @Override
    public String url() {
        return "/edit-profile";
    }

    public EditProfilePage waitUntilNameLoaded(String currentName) {
        nameInput.shouldHave(value(currentName));
        return this;
    }

    public EditProfilePage updateName(String newName) {
        nameInput.shouldBe(visible, enabled).setValue(newName);
        saveChangesButton.shouldBe(visible, enabled).click();
        return this;
    }

    public UserDashboard openDashboard() {
        return new UserDashboard().open();
    }
}
