package integration.helpers;

import static com.codeborne.selenide.Selenide.executeJavaScript;
import static com.codeborne.selenide.Selenide.open;

public class HTMLBuilderForTestPreconditions {

  private static void execute(String jsCommand) {
    executeJavaScript(jsCommand);
  }

  static class When {
    static void withBody(String... html) {
      execute(
        String.join(" ",
          "document.getElementsByTagName('body')[0].innerHTML =",
          "\"",
          String.join(" ", html).replace("\n", " "),
          "\";"
        )
      );
    }

    public static void withBodyTimedOut(int timeout, String... html) {
      execute(
        String.join(" ",
          "setTimeout(",
          "function(){",
          "document.getElementsByTagName('body')[0].innerHTML = \"",
          String.join(" ", html).replace("\n", " "),
          "\" },",
          timeout + ");"
        )
      );
    }

    public static void executeScriptWithTimeout(int timeout, String... js) {
      execute(
        String.join(" ",
          "setTimeout(",
          "function(){",
          String.join(" ", js),
          "},",
          timeout + ");"
        )
      );
    }
  }

  public static class Given {
    public static void openedPageWithBody(String... html) {
      Given.openedEmptyPage();
      When.withBody(html);
    }

    static void openedEmptyPage() {
      open("/empty.html");
    }
  }
}
