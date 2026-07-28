package ui.pages;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.confirm;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static org.assertj.core.api.Assertions.assertThat;

import api.models.CreateUserRequest;
import api.specs.RequestSpecs;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import java.util.List;
import java.util.function.Function;
import ui.elements.BaseElement;

public abstract class BasePage<T extends BasePage> {
  protected SelenideElement usernameInput = $(Selectors.byAttribute("placeholder", "Username"));
  protected SelenideElement passwordInput = $(Selectors.byAttribute("placeholder", "Password"));

  public abstract String url();

  public T open() {
    return Selenide.open(url(), (Class<T>) this.getClass());
  }

  public <P extends BasePage> P getPage(Class<P> pageClass) {
    return Selenide.page(pageClass);
  }

  public T checkAlertMessageAndAccept(String bankAlert) {
    String actualAlert = confirm();
    assertThat(actualAlert).contains(bankAlert);
    return (T) this;
  }

  public static void authAsUser(String username, String password) {
    Selenide.open("/");
    String userAuthHeader = RequestSpecs.getUserAuthHeader(username, password);
    executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);
  }

  public static void authAsUser(CreateUserRequest createUserRequest) {
    authAsUser(createUserRequest.getUsername(), createUserRequest.getPassword());
  }

  protected <E extends BaseElement> List<E> generatePageElements(
      ElementsCollection elementsCollection,
      Function<SelenideElement, E> constructor) {
    return elementsCollection.stream().map(constructor).toList();
  }
}
