package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ExBy;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.name;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class SelectorsExtendedTest extends IntegrationTest {

  int expectantCalls = 0;

  @Before
  public void openPage() {
    openFile("page_with_frames.html");
    setDefaultFrame((String[])null);
    Configuration.timeout = 4000;
  }

  @Test
  public void descriptionOfItemIsDisplayed() {
    assertEquals("Test::frames", title());

    By childFrame2 = byName("childFrame_2").as("Frame in parentFrame").inRoot();
    Configuration.timeout = 0;
    try {
      $(childFrame2).getText();
    }
    catch (ElementNotFound ex) {
      assertThat(ex.getLocalizedMessage(), containsString("Element not found {Frame in parentFrame}"));
    }

    childFrame2 = byName("childFrame_2").inRoot();
    try {
      $(childFrame2).getText();
    }
    catch (ElementNotFound ex) {
      assertThat(ex.getLocalizedMessage(), containsString("Element not found {By.name: childFrame_2}"));
    }
  }

  @Test
  public void parametersWorks() {
    assertEquals("Test::frames", title());

    ExBy frame = byXpath("//frame[@src='{0}{1}{2}']").as("Some frame");
    $(frame.with("page_with_dynamic", "_select", ".html")).shouldHave(name("leftFrame"));
    try {
      $(frame.with("page_with_dynamic", "_select")).shouldHave(name("leftFrame"));
    }
    catch (IllegalArgumentException ex) {
      assertThat(ex.getLocalizedMessage(), containsString("Incorrect number of parameters (expected 3 , but received 2)"));
    }

    try {
      $(frame.with("page_with_dynamic", "_select", ".html", ".html")).shouldHave(name("leftFrame"));
    }
    catch (IllegalArgumentException ex) {
      assertThat(ex.getLocalizedMessage(), containsString("Incorrect number of parameters (expected 3 , but received 4)"));
    }
  }

  @Test
  public void frameSwitchingWorksViaLinks() {
    assertEquals("Test::frames", title());

    By parentFrame = byName("parentFrame").inRoot();
    By childFrame2 = byName("childFrame_2").inFrame(parentFrame);
    By childFrame21 = byName("childFrame_2_1").inFrame(childFrame2);

    $(childFrame21).shouldHave(name("childFrame_2_1"));
    assertThat(currentFrameUrl(), equalTo(Configuration.baseUrl + "/page_with_child_frame.html"));
  }


  @Test
  public void frameSwitchingWorksViaText() {
    assertEquals("Test::frames", title());

    By childFrame1 = byTagName("frame").inFrame("parentFrame");
    $(childFrame1).shouldHave(name("childFrame_1"));
    assertThat(currentFrameUrl(), equalTo(Configuration.baseUrl + "/page_with_parent_frame.html"));

    childFrame1 = byTagName("frame");
    $(childFrame1).shouldHave(name("childFrame_1"));
    assertThat(currentFrameUrl(), equalTo(Configuration.baseUrl + "/page_with_parent_frame.html"));

    switchTo().defaultContent();
    setDefaultFrame("parentFrame");

    childFrame1 = byTagName("frame");
    $(childFrame1).shouldHave(name("childFrame_1"));
    assertThat(currentFrameUrl(), equalTo(Configuration.baseUrl + "/page_with_parent_frame.html"));

    By topFrame = byId("top-frame").inRoot();
    $(topFrame).shouldHave(name("topFrame"));

    By childFrame21 = byTagName("frame").inFrame("parentFrame", "childFrame_2");
    $(childFrame21).shouldHave(name("childFrame_2_1"));
    assertThat(currentFrameUrl(), equalTo(Configuration.baseUrl + "/page_with_child_frame.html"));
  }

  @Test
  public void frameSwitchingWorksViaCriteria() {
    assertEquals("Test::frames", title());

    By parentFrame = byName("parentFrame").inRoot();
    By childFrame1 = byName("childFrame_1").inFrame(parentFrame);
    By childFrame2 = byName("childFrame_2");

    $(childFrame1).shouldHave(name("childFrame_1"));
    assertThat(currentFrameUrl(), equalTo(Configuration.baseUrl + "/page_with_parent_frame.html"));

    By childFrame21 = byTagName("frame").inFrame(parentFrame, childFrame2);
    $(childFrame21).shouldHave(name("childFrame_2_1"));
    assertThat(currentFrameUrl(), equalTo(Configuration.baseUrl + "/page_with_child_frame.html"));

    switchTo().defaultContent();
    setDefaultFrame(parentFrame, childFrame2);

    childFrame21 = byTagName("frame");
    $(childFrame21).shouldHave(name("childFrame_2_1"));
    assertThat(currentFrameUrl(), equalTo(Configuration.baseUrl + "/page_with_child_frame.html"));
  }

  @Test
  public void expectantWorks() {
    assertEquals("Test::frames", title());

    Selectors.setExpectant(new ExBy.IExpectant() {
      @Override
      public void actionsForWait() {
        expectantCalls++;
      }
    });

    int currCalls = expectantCalls;
    By someLocator = byId("top-frame");
    $(someLocator).getText();
    assertThat(expectantCalls, is(currCalls));

    Selectors.setWaitByDefault(true);
    currCalls = expectantCalls;
    someLocator = byId("top-frame");
    $(someLocator).getText();
    assertThat(expectantCalls, is(++currCalls));

    Selectors.setWaitByDefault(false);
    currCalls = expectantCalls;
    someLocator = byId("top-frame");
    $(someLocator).getText();
    assertThat(expectantCalls, is(currCalls));

    currCalls = expectantCalls;
    someLocator = byId("top-frame").withWait();
    $(someLocator).getText();
    assertThat(expectantCalls, is(++currCalls));

    currCalls = expectantCalls;
    someLocator = byId("top-frame").withNoWait();
    $(someLocator).getText();
    assertThat(expectantCalls, is(currCalls));
  }

  @AfterClass
  public static void tearDown() {
    close();
  }
}
