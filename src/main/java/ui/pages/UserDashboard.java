package ui.pages;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.refresh;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

@Getter
public class UserDashboard extends BasePage<UserDashboard> {
    private SelenideElement welcomeText = $(Selectors.byClassName("welcome-text"));
    private SelenideElement userName = $(Selectors.byClassName("user-name"));
    private SelenideElement profileHeader = $(Selectors.byClassName("profile-header"));
    private SelenideElement createNewAccount = $(Selectors.byText("➕ Create New Account"));
    private SelenideElement depositMoney = $(Selectors.byText("💰 Deposit Money"));
    private SelenideElement makeATransfer = $(Selectors.byText("🔄 Make a Transfer"));

    @Override
    public String url() {
        return "/dashboard";
    }

    public UserDashboard createNewAccount() {
        createNewAccount.click();
        return this;
    }

    public DepositPage openDeposit() {
        depositMoney.click();
        return getPage(DepositPage.class);
    }

    public TransferPage openTransfer() {
        makeATransfer.click();
        return getPage(TransferPage.class);
    }

    public EditProfilePage openEditProfile() {
        profileHeader.click();
        return getPage(EditProfilePage.class);
    }

    public UserDashboard checkNameDisplayed(String name) {
        refresh();
        userName.shouldHave(exactText(name));
        welcomeText.shouldHave(text(name));
        return this;
    }
}
