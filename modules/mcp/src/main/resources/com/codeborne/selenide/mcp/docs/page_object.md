# Selenide Page Objects

## Basic pattern
```java
public class LoginPage {
  private final SelenideElement usernameInput = $("#username");
  private final SelenideElement passwordInput = $("#password");
  private final SelenideElement loginButton = $("button[type=submit]");
  private final SelenideElement errorMessage = $(".error-message");

  public LoginPage open() {
    Selenide.open("/login");
    return this;
  }

  public void loginAs(String username, String password) {
    usernameInput.setValue(username);
    passwordInput.setValue(password);
    loginButton.click();
  }

  public void shouldShowError(String message) {
    errorMessage.shouldHave(text(message));
  }
}
```

## Using page objects in tests
```java
@Test
void userCanLogin() {
  LoginPage page = new LoginPage().open();
  page.loginAs("admin", "secret");
  // assertions on next page...
}
```

## With collections
```java
public class TodoPage {
  private final ElementsCollection todos = $$(".todo-item");
  private final SelenideElement newTodoInput = $(".new-todo");

  public void addTodo(String text) {
    newTodoInput.setValue(text).pressEnter();
  }

  public void shouldHaveTodos(String... texts) {
    todos.shouldHave(exactTexts(texts));
  }

  public void completeTodo(String text) {
    todos.findBy(text(text)).$(".toggle").click();
  }
}
```

## Page components (reusable parts)
```java
public class Header {
  private final SelenideElement root = $("header");
  private final SelenideElement logo = root.$(".logo");
  private final SelenideElement userMenu = root.$(".user-menu");

  public void openUserMenu() {
    userMenu.click();
  }
}

public class DashboardPage {
  private final Header header = new Header();

  public Header header() {
    return header;
  }
}
```
