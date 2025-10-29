package it.mobile;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class BrowserstackUtils {
  public static Map<String, Object> getBrowserstackOptions() {
    return Map.of(
      "userName", "githubactions_qxmgVeB",
      "accessKey", System.getProperty("selenide.bs_key"),
      "appiumVersion", "2.6.0",
      "projectName", "Selenide-Appium",
      "buildName", getPrettyJobName(),
      "interactiveDebugging", true
    );
  }

  public static URL browserstackUrl() {
    try {
      return new URL("https://hub.browserstack.com/wd/hub");
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  private static String getPrettyJobName() {
    String prBranch = System.getenv("GITHUB_REF");
    String githubJob = System.getenv("GITHUB_JOB");
    String runId = System.getenv("GITHUB_RUN_ID");
    if (prBranch != null) {
      return "%s - %s".formatted(prBranch, githubJob);
    }
    return githubJob == null ? "default" : "%s-%s".formatted(githubJob, runId);
  }

  public static boolean isCi() {
    return System.getenv().containsKey("CI");
  }
}
