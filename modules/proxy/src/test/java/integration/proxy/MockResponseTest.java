package integration.proxy;

import com.codeborne.selenide.proxy.MockResponseFilter;
import integration.ProxyIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.remote.http.HttpResponse;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getSelenideProxy;
import static com.codeborne.selenide.proxy.RequestMatcher.HttpMethod.GET;
import static com.codeborne.selenide.proxy.RequestMatchers.urlContains;
import static java.util.Objects.requireNonNull;
import static org.openqa.selenium.remote.http.Contents.utf8String;

final class MockResponseTest extends ProxyIntegrationTest {
  @BeforeEach
  void setUp() {
    timeout = 1000;
    open();
    proxyMocker().resetAll();
  }

  @AfterEach
  void tearDown() {
    proxyMocker().resetAll();
  }

  @Test
  void withoutMock() {
    open("/page_with_json_request.html");
    verifyRealProduct();

    $("#status").shouldHave(text("200"));
    $("#content-length").shouldHave(text("89"));
    $("#content-type").shouldHave(text("application/json"));
  }

  @Test
  void canMockServerResponse() {
    proxyMocker().mockText("product-mock", urlContains(GET, "/product.json"), this::mockedResponse);
    open("/page_with_json_request.html");
    verifyMockedProduct();

    $("#status").shouldHave(text("200"));
    $("#content-length").shouldHave(text("98"));
    $("#content-type").shouldHave(exactText(""));
  }

  @Test
  void canMockServerResponseWithAnyHttpStatus() {
    proxyMocker().mockText("product-mock", urlContains(GET, "/product.json"), 429, this::mockedResponse);
    open("/page_with_json_request.html");

    verifyMockedProduct();
    $("#status").shouldHave(text("429"));
    $("#content-length").shouldHave(text("98"));
    $("#content-type").shouldHave(exactText(""));
  }

  @Test
  void canMockServerResponseWithAnyHttpStatusAndContentType() {
    proxyMocker().mockResponse("product-mock", urlContains(GET, "/product.json"), () -> new HttpResponse()
      .setStatus(429)
      .addHeader("Content-Type", "image/jpeg")
      .setContent(utf8String(mockedResponse()))
    );
    open("/page_with_json_request.html");
    verifyMockedProduct();
    $("#status").shouldHave(text("429"));
    $("#content-length").shouldHave(text("98"));
    $("#content-type").shouldHave(text("image/jpeg"));
  }

  private void verifyRealProduct() {
    verifyResponse("Fridge", "999.99 USD", "yes");
  }

  private void verifyMockedProduct() {
    verifyResponse("Vacuum cleaner", "222.22 EUR", "no");
  }

  private void verifyResponse(String name, String price, String availability) {
    $("#name").shouldHave(text(name));
    $("#price").shouldHave(text(price));
    $("#availability").shouldHave(text(availability));
  }

  private String mockedResponse() {
    return """
      {
        "name": "Vacuum cleaner",
        "price": "222.22",
        "currency": "EUR",
        "availability": false
      }
      """;
  }

  private static MockResponseFilter proxyMocker() {
    return requireNonNull(getSelenideProxy()).responseMocker();
  }
}
