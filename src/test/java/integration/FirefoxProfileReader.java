package integration;

import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.File;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.assertj.core.api.Assertions.assertThat;

class FirefoxProfileReader {
  FirefoxProfileChecker readProfile(FirefoxDriver firefox) throws IOException {
    String profileFolder = (String) firefox.getCapabilities().getCapability("moz:profile");
    String profileFile = readFileToString(new File(profileFolder, "user.js"), UTF_8);
    return new FirefoxProfileChecker(profileFile);
  }

  static class FirefoxProfileChecker {
    private final String profileFile;

    FirefoxProfileChecker(String profileFile) {
      this.profileFile = profileFile;
    }

    public void assertPreference(String name, String value) {
      assertThat(profileFile).contains(String.format("user_pref(\"%s\", \"%s\");", name, value));
    }

    public void assertPreference(String name, Object value) {
      assertThat(profileFile).contains(String.format("user_pref(\"%s\", %s);", name, value));
    }
  }
}
