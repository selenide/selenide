package com.codeborne.selenide.impl;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

final class HttpHelperTest {
  private final HttpHelper helper = new HttpHelper();

  @Test
  void extractsFileNameFromHttpHeader() {
    assertThat(helper.getFileNameFromContentDisposition(
      "Content-Disposition", "Content-Disposition=attachment; filename=statement.xls"))
      .contains("statement.xls");

    assertThat(helper.getFileNameFromContentDisposition(
      "Content-Disposition", "Content-Disposition=attachment; filename = statement.xls"))
      .contains("statement.xls");

    assertThat(helper.getFileNameFromContentDisposition(
      "Content-Disposition", "Content-Disposition=attachment; filename =statement.xls"))
      .contains("statement.xls");

    assertThat(helper.getFileNameFromContentDisposition(
      "Content-Disposition", "Content-Disposition=attachment; filename   =   statement.xls"))
      .contains("statement.xls");

    assertThat(helper.getFileNameFromContentDisposition(
      "Content-Disposition", "Content-Disposition=attachment; filename= statement.xls"))
      .contains("statement.xls");

    assertThat(helper.getFileNameFromContentDisposition(
      "Content-Disposition", "Content-Disposition=inline; filename=\"statement-40817810048000102279.pdf\""))
      .contains("statement-40817810048000102279.pdf");

    assertThat(helper.getFileNameFromContentDisposition(
      "content-disposition", "attachement; filename=selenide-2.6.1.jar"))
      .contains("selenide-2.6.1.jar");

    assertThat(helper.getFileNameFromContentDisposition(
      "Content-Disposition", "attachement; filename=selenide-4.11.5.md?sessioncookie=12345%2323"))
      .contains("selenide-4.11.5.md");

    assertThat(helper.getFileNameFromContentDisposition(
      "Content-Disposition", "attachement; filename=random_file.txt?auth=5v1kij42xXSsc;charset=CP1251"))
      .contains("random_file.txt");

    assertThat(helper.getFileNameFromContentDisposition(
      "Content-Disposition", "attachment; filename=Prices.csv;charset=UTF-8"))
      .contains("Prices.csv");

    assertThat(helper.getFileNameFromContentDisposition(
      "Content-Disposition", "attachment; filename=%D1%86%D0%B5%D0%BD%D1%8B%27.csv;charset=UTF-8"))
      .contains("цены'.csv");

    assertThat(helper.getFileNameFromContentDisposition(
      "Content-Disposition", "attachment; filename=%F6%E5%ED%FB%27.csv;charset=CP1251"))
      .contains("цены'.csv");

    assertThat(helper.getFileNameFromContentDisposition(
      "Content-Disposition", "attachment; filename*=UTF-8''%D1%86%D0%B5%D0%BD%D1%8B%27.csv"))
      .contains("цены'.csv");

    assertThat(helper.getFileNameFromContentDisposition(
      "Content-Disposition", "attachment; filename*=cp1251''%F6%E5%ED%FB%27.csv"))
      .contains("цены'.csv");

    assertThat(helper.getFileNameFromContentDisposition(
      "Content-Disposition", "attachment; filename*=UTF-8''Na%C3%AFve%20file.txt"))
      .contains("Naïve file.txt");

    assertThat(helper.getFileNameFromContentDisposition(
      "content-disposition",
      "attachement; filename=UTF-8''%D1%84%D0%B0%D0%B9%D0%BB-%D1%81-%D1%80%D1%83%D1%81%D1%81%D0%BA%D0%B8%D0%BC.txt"))
      .contains("файл-с-русским.txt");
  }

  @Test
  void decodesBase64EncodedFileName() {
    assertThat(helper.getFileNameFromContentDisposition(
      "Content-Disposition", "attachment; " +
        "filename=\"4oSWIDg0MjMg0L7RgiAxMSDQvNCw0Y8gMjAyMiDQsy5wZGY=\"; " +
        "filename*=UTF-8''4oSWIDg0MjMg0L7RgiAxMSDQvNCw0Y8gMjAyMiDQsy5wZGY%3D"))
      .contains("№ 8423 от 11 мая 2022 г.pdf");
  }

  @Test
  void fileNameIsNone_ifContentDispositionHeaderIsEmpty() {
    assertThat(helper.getFileNameFromContentDisposition("Content-Disposition", null).isPresent())
      .isFalse();
    assertThat(helper.getFileNameFromContentDisposition("Content-Disposition", "").isPresent())
      .isFalse();
  }

  @Test
  void fileNameIsNone_ifContentDispositionHeaderIsNotFound() {
    assertThat(helper.getFileNameFromContentDisposition("another-header", "some.png").isPresent())
      .isFalse();
  }

  @Test
  void getsFileNameFromResponseHeader() {
    Map<String, String> headers = new HashMap<>();
    headers.put("content-disposition", "attachement; filename=report.pdf");
    headers.put("referrer", "http://google.kz");

    assertThat(helper.getFileNameFromContentDisposition(headers))
      .contains("report.pdf");
  }

  @Test
  void fileNameIsNull_ifResponseDoesNotContainDispositionHeader() {
    Map<String, String> headers = new HashMap<>();
    headers.put("location", "/downloads");
    headers.put("referrer", "http://google.kz");

    assertThat(helper.getFileNameFromContentDisposition(headers))
      .isEmpty();
  }

  @Test
  void extractsFileNameFromUrl() {
    assertThat(helper.getFileName("/blah.jpg")).isEqualTo("blah.jpg");
    assertThat(helper.getFileName("/blah.jpg?foo")).isEqualTo("blah.jpg");
    assertThat(helper.getFileName("https://blah.org/blah.jpg")).isEqualTo("blah.jpg");
    assertThat(helper.getFileName("https://blah.org/with spaces`and'forbidden\"characters+&.jpg"))
      .isEqualTo("with+spaces_and_forbidden_characters__.jpg");

    assertThat(helper.getFileName("https://some.com/foo/bar/")).isEqualTo("");
  }

  @Test
  void removesAllForbiddenCharactersFromFileName() {
    assertThat(helper.normalize("имя с #pound,%percent,&ampersand,{left,}right,/slash,\\backslash," +
      "<left,>right,*asterisk,?question,$dollar,!exclamation,'quote,\"quotes," +
      ":colon,@at,+plus,`backtick,|pipe,=equal.winrar"))
      .isEqualTo("имя+с+_pound,_percent,_ampersand,_left,_right,_slash,_backslash," +
        "_left,_right,_asterisk,_question,_dollar,_exclamation,_quote,_quotes," +
        "_colon,_at,_plus,_backtick,_pipe,_equal.winrar");
  }

  @Test
  void slashIsAlsoReplacedByUnderscore() {
    assertThat(helper.normalize("имя с /slash.winzip"))
      .isEqualTo("имя+с+_slash.winzip");
  }
}
