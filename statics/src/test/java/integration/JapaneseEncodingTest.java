package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.ClickOptions.withTimeout;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static java.time.Duration.ofHours;

final class JapaneseEncodingTest extends IntegrationTest {
  @BeforeEach
  void setUp() {
    openFile("page_in_japanese_encoding.html");
  }

  @Test
  void usesProperEncoding() {
    $("h1").shouldHave(text("Page in Japanese"));
    $("#description").shouldHave(text("文藝春秋が運営するニュースサイト。文春オンラインは世の中を驚かせるスクープから、毎日の仕事や生活に役立つ話題までお届けする情報メディアです。"));

    $("[name=username]").val("文春オンライン | 世の中の「ほんとう」がわかります");
    $("[name=password]").val("secret in latin");
    $("#login").click(withTimeout(ofHours(100)));
    $("body").shouldHave(exactText("Submitted %1 parameters:username=文春オンライン | 世の中の「ほんとう」がわかります"));
  }
}
