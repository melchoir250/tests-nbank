package ui.pages;

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

    public EditProfilePage updateName(String newName) {
        nameInput.setValue(newName);
        saveChangesButton.click();
        return this;
    }
}
