package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.SelenideConfig;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.UsernameAndPassword;
import org.openqa.selenium.remote.http.ClientConfig;

import java.net.URI;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class RemoteDriverFactoryTest {
  private final RemoteDriverFactory factory = new RemoteDriverFactory();

  @Test
  void clientConfig_withoutCredentials() {
    ClientConfig clientConfig = factory.clientConfig(new SelenideConfig()
      .remote("https://grid.example.com:4444/wd/hub")
      .remoteReadTimeout(12_000)
      .remoteConnectionTimeout(3_000));

    assertThat(clientConfig.baseUri()).isEqualTo(URI.create("https://grid.example.com:4444/wd/hub"));
    assertThat(clientConfig.credentials()).isNull();
    assertThat(clientConfig.readTimeout()).isEqualTo(Duration.ofSeconds(12));
    assertThat(clientConfig.connectionTimeout()).isEqualTo(Duration.ofSeconds(3));
  }

  @Test
  void clientConfig_movesCredentialsFromUrlToAuthentication() {
    ClientConfig clientConfig = factory.clientConfig(new SelenideConfig()
      .remote("https://john%40mail.com:Love.Is%3AStronger@grid.example.com/wd/hub?a=b"));

    assertThat(clientConfig.baseUri()).isEqualTo(URI.create("https://grid.example.com/wd/hub?a=b"));
    assertThat(clientConfig.credentials()).isInstanceOf(UsernameAndPassword.class);
    UsernameAndPassword credentials = (UsernameAndPassword) clientConfig.credentials();
    assertThat(credentials.username()).isEqualTo("john@mail.com");
    assertThat(credentials.password()).isEqualTo("Love.Is:Stronger");
  }

  @Test
  void clientConfig_usernameWithoutPassword() {
    ClientConfig clientConfig = factory.clientConfig(new SelenideConfig().remote("http://john@localhost:4444"));

    assertThat(clientConfig.baseUri()).isEqualTo(URI.create("http://localhost:4444"));
    UsernameAndPassword credentials = (UsernameAndPassword) clientConfig.credentials();
    assertThat(credentials.username()).isEqualTo("john");
    assertThat(credentials.password()).isEmpty();
  }

  @Test
  void clientConfig_invalidUrl() {
    assertThatThrownBy(() -> factory.clientConfig(new SelenideConfig().remote("not a url")))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Invalid 'remote' parameter: not a url");
  }
}
