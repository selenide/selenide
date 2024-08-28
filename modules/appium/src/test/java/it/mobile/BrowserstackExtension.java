package it.mobile;

import com.codeborne.selenide.WebDriverRunner;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Optional;

import static com.codeborne.selenide.Selenide.executeJavaScript;
import static it.mobile.BrowserstackUtils.isCi;

/***
 * Enhance console output and Browserstack dashboard with test result information
 */
public class BrowserstackExtension implements AfterTestExecutionCallback {
  private static final Logger log = LoggerFactory.getLogger(BrowserstackExtension.class);
  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static final String setSessionStatusTemplate = """
    browserstack_executor: {"action": "setSessionStatus", "arguments": {"status": "%s", "reason": ""}}""";
  private static final String nameTemplate = """
    browserstack_executor: {"action": "setSessionName", "arguments": {"name": "%s"}}""";
  private static final String getSessionDetailsTemplate = """
    browserstack_executor: {"action": "getSessionDetails"}""";

  @Override
  public void afterTestExecution(final ExtensionContext context) {
    if (!isCi()) {
      return;
    }
    if (!WebDriverRunner.hasWebDriverStarted()) {
      return;
    }
    var className = context.getTestClass().map(Class::getName).orElse("EmptyClass");
    if (className.startsWith("it.mobile.ios")) {
      setSessionStatus(context);
      setSessionName(context);
    }
    addPublicLink(context);
  }

  private void addPublicLink(ExtensionContext context) {
    context.getExecutionException().ifPresent(error -> {
      String details = executeJavaScript(getSessionDetailsTemplate);
      try {
        String publicUrl = objectMapper.readValue(details, SessionDetails.class).publicUrl;
        if (error instanceof WebDriverException e) {
          e.addInfo("Browserstack link", publicUrl);
        } else if (error.getCause() instanceof WebDriverException c) {
          c.addInfo("Browserstack link", publicUrl);
        } else {
          log.error("Browserstack link {}", publicUrl);
        }
      } catch (JsonProcessingException e) {
        log.error("Failed to get Public URL {}", e.getMessage());
      }
    });
  }

  private void setSessionStatus(ExtensionContext context) {
    context.getExecutionException().ifPresentOrElse(
      error -> executeJavaScript(setSessionStatusTemplate.formatted("failed")),
      () -> executeJavaScript(setSessionStatusTemplate.formatted("passed"))
    );

  }

  private void setSessionName(ExtensionContext context) {
    executeJavaScript(nameTemplate.formatted(getTestName(context)));
  }

  private String getTestName(ExtensionContext context) {
    final Optional<Class<?>> testClass = context.getTestClass();
    final String className = testClass.map(Class::getName).orElse("EmptyClass");

    final Optional<Method> testMethod = context.getTestMethod();
    final String methodName = testMethod.map(Method::getName).orElse("emptyMethod");
    return className + "." + methodName;
  }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class SessionDetails {
  @JsonProperty("public_url")
  public String publicUrl;
}

