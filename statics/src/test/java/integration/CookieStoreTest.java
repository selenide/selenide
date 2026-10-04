package integration;

import com.codeborne.selenide.ex.ConditionMetError;
import com.codeborne.selenide.ex.ConditionNotMetError;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import java.util.Set;

import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.CookieStoreConditions.cookie;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.cookies;
import static java.time.Duration.ofMillis;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class CookieStoreTest extends IntegrationTest {
  private static final String NAME = "TEST_COOKIE";
  private static final String VALUE = "AF33892F98ABC39A";

  @AfterAll
  static void resetCookieStore() {
    cookies().clear();
  }

  @BeforeEach
  void openTestPage() {
    openFile("cookies.html");
  }

  @Test
  void addAndCheckCookie() {
    cookies().add("mouse", "Jerry");
    cookies().shouldHave(cookie("cat"), ofMillis(10000));
    cookies().shouldHave(cookie("cat", "Tom"), ofMillis(10000));
    cookies().shouldHave(cookie("mouse", "Jerry"));
  }

  @Test
  void getAll() {
    cookies().add("cat", "Tom");
    cookies().add("mouse", "Jerry");

    Set<Cookie> cookies = cookies().getAll();

    assertThat(cookies).hasSizeGreaterThanOrEqualTo(2);
    assertThat(cookies).contains(new Cookie("cat", "Tom"));
    assertThat(cookies).contains(new Cookie("mouse", "Jerry"));
  }

  @Test
  void assertPresenceOfSpecificCookie() {
    $("#button-put").click();
    cookies().shouldHave(cookie(NAME), ofMillis(2000));
    cookies().shouldHave(cookie(NAME, VALUE), ofMillis(2000));
  }

  @Test
  void assertAbsenceOfCookie() {
    cookies().add(NAME, VALUE);
    $("#button-remove").click();
    cookies().shouldNotHave(cookie(NAME), ofMillis(2000));
    cookies().shouldNotHave(cookie(NAME, VALUE), ofMillis(2000));
  }

  @Test
  void checkCookieValue() {
    $("#button-put").click();
    cookies().shouldNotHave(cookie(NAME, "another value"), ofMillis(2000));
  }

  @Test
  void errorMessageWhenCookieIsMissing() {
    assertThatThrownBy(() ->
      cookies().shouldHave(cookie("foo"), ofMillis(10))
    )
      .isInstanceOf(ConditionNotMetError.class)
      .hasMessageStartingWith("cookieStore should have cookie with name \"foo\"")
      .hasMessageContaining("Screenshot: ")
      .hasMessageContaining("Page source: ")
      .hasMessageContaining("Timeout: 10ms");
  }

  @Test
  void errorMessageWhenCookieHasWrongValue() {
    timeout = 1;
    $("#button-put").click();

    assertThatThrownBy(() ->
      cookies().shouldHave(cookie(NAME, "wrong value"))
    )
      .isInstanceOf(ConditionNotMetError.class)
      .hasMessageStartingWith("cookieStore should have cookie with name \"%s\" and value \"wrong value\"".formatted(NAME))
      .hasMessageContaining("Screenshot: ")
      .hasMessageContaining("Page source: ")
      .hasMessageContaining("Timeout: 1ms");
  }

  @Test
  void deleteCookie() {
    cookies().add("cat", "Tom");
    assertThat(cookies().get("cat").getValue()).isEqualTo("Tom");

    cookies().delete("cat");
    assertThat(cookies().get("cat")).isNull();
  }

  @Test
  void clearAndSize() {
    cookies().add("cat", "Tom");
    cookies().add("mouse", "Jerry");
    assertThat(cookies().size()).isGreaterThanOrEqualTo(3);
    assertThat(cookies().getAll()).contains(new Cookie("cat", "Tom"), new Cookie("mouse", "Jerry"));

    cookies().clear();
    assertThat(cookies().size()).isEqualTo(0);
  }


  @Test
  void errorMessageWhenCookieShouldNotExist() {
    timeout = 1;
    cookies().add("cat", "Tom");

    assertThatThrownBy(() ->
      cookies().shouldNotHave(cookie("cat"))
    )
      .isInstanceOf(ConditionMetError.class)
      .hasMessageStartingWith("cookieStore should not have cookie with name \"cat\"")
      .hasMessageContaining("Screenshot: ")
      .hasMessageContaining("Page source: ")
      .hasMessageContaining("Timeout: 1ms");
  }

  @Test
  void errorMessageWhenCookieShouldNotHaveGivenValue() {
    timeout = 1;
    cookies().add("cat", "Tom");

    assertThatThrownBy(() ->
      cookies().shouldNotHave(cookie("cat", "Tom"))
    )
      .isInstanceOf(ConditionMetError.class)
      .hasMessageStartingWith("cookieStore should not have cookie with name \"cat\" and value \"Tom\"")
      .hasMessageContaining("Screenshot: ")
      .hasMessageContaining("Page source: ")
      .hasMessageContaining("Timeout: 1ms");
  }
}
