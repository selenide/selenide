package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

final class JapaneseEncodingTest extends IntegrationTest {
  @BeforeEach
  void setUp() {
    openFile("page_in_japanese_encoding.html");
  }

  @Test
  void usesProperEncoding() {
    $("h1").shouldHave(text("Page in Japanese"));
    $("#description").shouldHave(text("文藝春秋が運営するニュースサイト。文春オンラインは世の中を驚かせるスクープから、毎日の仕事や生活に役立つ話題までお届けする情報メディアです。"));
  }
}
