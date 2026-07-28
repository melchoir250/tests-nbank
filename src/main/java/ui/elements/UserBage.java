package ui.elements;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

@Getter
public class UserBage extends BaseElement {
  private final String username;
  private final String role;

  public UserBage(SelenideElement element) {
    super(element);
    String[] lines = element.getText().split("\n");
    username = lines[0];
    role = lines.length > 1 ? lines[1] : "";
  }
}
