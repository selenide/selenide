package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.Mocks.mockElement;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

final class HrefTest {
  private final Driver driver = new DriverStub();

  @Test
  void hrefContainingFullUrl() {
    assertThat(new Href("http://google.ek").check(driver, link("http://google.ek/")).verdict()).isEqualTo(ACCEPT);
    assertThat(new Href("http://google.ek/").check(driver, link("http://google.ek/")).verdict()).isEqualTo(ACCEPT);
  }

  @Test
  void hrefContainingRelativeUrlToFile() {
    assertThat(new Href("cv.yml").check(driver, link("https://cv.ee/cv.yml")).verdict()).isEqualTo(ACCEPT);
    assertThat(new Href("/people/cv.yml").check(driver, link("https://cv.ee/people/cv.yml")).verdict()).isEqualTo(ACCEPT);
  }

  @Test
  void hrefContainingCyrillicSymbols() {
    Href condition = new Href("/файл/cv.yml");
    assertThat(condition.check(driver, link("https://cv.ee/%D1%84%D0%B0%D0%B9%D0%BB/cv.yml")).verdict()).isEqualTo(ACCEPT);
  }

  @Test
  void hrefContainingAnchor() {
    assertThat(new Href("#").check(driver, link("https://cv.ee/people/jaan?source=23213#")).verdict()).isEqualTo(ACCEPT);
  }

  @Test
  void hrefContainingEncodedSpace() {
    assertThat(new Href("some%20file.pdf").check(driver, link("https://dropbox.com/some%20file.pdf")).verdict()).isEqualTo(ACCEPT);
  }

  @Test
  void decodesUrl() {
    Href condition = new Href("");
    assertThat(condition.decode("https://www.eki.ee/dict/evs")).isEqualTo("https://www.eki.ee/dict/evs");
    assertThat(condition.decode("https://dropbox.ee/some/folder/cv.pdf")).isEqualTo("https://dropbox.ee/some/folder/cv.pdf");
    assertThat(condition.decode("https://127.0.0.1:42042/files/%D1%84%D0%B0%D0%B9%D0%BB-%D1%81-" +
      "%D1%80%D1%83%D1%81%D1%81%D0%BA%D0%B8%D0%BC-%D0%BD%D0%B0%D0%B7%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5%D0%BC.txt"))
      .isEqualTo("https://127.0.0.1:42042/files/файл-с-русским-названием.txt");
  }

  private WebElement link(String href) {
    WebElement link = mockElement("click me");
    when(link.getAttribute("href")).thenReturn(href);
    return link;
  }
}
