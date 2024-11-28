package it.mobile;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class BrowserstackUtils {
  private static final HashMap<String, Object> bstackOptions = new HashMap<>();

  static {
    bstackOptions.put("userName", "githubactions_qxmgVeB");
    bstackOptions.put("accessKey", System.getProperty("selenide.bs_key"));
    bstackOptions.put("appiumVersion", "2.6.0");
    bstackOptions.put("projectName", "Selenide-Appium");
    bstackOptions.put("buildName", getPrettyJobName());
    bstackOptions.put("interactiveDebugging", true);
  }

  public static URL browserstackUrl() {
    try {
      return new URL("https://hub.browserstack.com/wd/hub");
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  public static HashMap<String, Object> getBrowserstackOptions() {
    return bstackOptions;
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
