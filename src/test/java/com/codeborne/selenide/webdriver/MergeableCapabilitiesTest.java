package com.codeborne.selenide.webdriver;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

final class MergeableCapabilitiesTest {
  @Test
  void mergesChromeOptions() {
    ChromeOptions base = new ChromeOptions();
    base.addArguments("--a", "--b");
    ChromeOptions extra = new ChromeOptions();
    extra.addArguments("--c", "--d");
    extra.setBinary("/usr/local/chrome.exe");

    MergeableCapabilities result = new MergeableCapabilities(base, extra);

    assertThat(result.asMap()).containsEntry("goog:chromeOptions", ImmutableMap.of(
      "args", asList("--a", "--b", "--c", "--d"),
      "binary", "/usr/local/chrome.exe",
      "extensions", emptyList()
    ));
  }

  @Test
  @SuppressWarnings("unchecked")
  void mergesExperimentalArguments() {
    ChromeOptions base = new ChromeOptions();
    base.setExperimentalOption("excludeSwitches", new String[]{"foo"});
    ChromeOptions extra = new ChromeOptions();
    extra.setExperimentalOption("excludeSwitches", new String[]{"bar", "zzz"});

    MergeableCapabilities result = new MergeableCapabilities(base, extra);

    assertThat(result.asMap()).containsKeys("goog:chromeOptions");
    Object excludeSwitches = ((Map<String, Object>) result.asMap().get("goog:chromeOptions")).get("excludeSwitches");
    assertThat(excludeSwitches).isEqualTo(new String[]{"foo", "bar", "zzz"});
  }

  @Test
  @SuppressWarnings("unchecked")
  void mergesArrayAndList() {
    ChromeOptions base = new ChromeOptions();
    base.setExperimentalOption("excludeSwitches", new String[]{"foo", "bar"});
    ChromeOptions extra = new ChromeOptions();
    extra.setExperimentalOption("excludeSwitches", asList("blah", "bluh"));

    MergeableCapabilities result = new MergeableCapabilities(base, extra);

    assertThat(result.asMap()).containsKeys("goog:chromeOptions");
    Object excludeSwitches = ((Map<String, Object>) result.asMap().get("goog:chromeOptions")).get("excludeSwitches");
    assertThat(excludeSwitches).isEqualTo(asList("foo", "bar", "blah", "bluh"));
  }

  @Test
  @SuppressWarnings("unchecked")
  void mergesListAndArray() {
    ChromeOptions base = new ChromeOptions();
    base.setExperimentalOption("excludeSwitches", asList("foo", "bar"));
    ChromeOptions extra = new ChromeOptions();
    extra.setExperimentalOption("excludeSwitches", new String[]{"blah", "bluh"});

    MergeableCapabilities result = new MergeableCapabilities(base, extra);

    assertThat(result.asMap()).containsKeys("goog:chromeOptions");
    Object excludeSwitches = ((Map<String, Object>) result.asMap().get("goog:chromeOptions")).get("excludeSwitches");
    assertThat(excludeSwitches).isEqualTo(asList("foo", "bar", "blah", "bluh"));
  }
}
