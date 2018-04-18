package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ex.ExplainedUIAssertionError;
import com.codeborne.selenide.ex.ListSizeMismatch;
import com.codeborne.selenide.ex.UIAssertionError;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.CollectionCondition.*;
import static com.codeborne.selenide.Selenide.$$;
import static org.junit.Assert.fail;

public class CollectionSizeTest extends IntegrationTest {
  @Before
  public void openTestPage() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  public void size_equals() {
    $$("#radioButtons input").shouldHave(size(4));
  }

  @Test
  public void size_greaterThan() {
    $$("#radioButtons input").shouldHave(sizeGreaterThan(3));
    $$("#radioButtons input").shouldHave(sizeGreaterThan(2));
    $$("#radioButtons input").shouldHave(sizeGreaterThan(1));
  }

  @Test
  public void size_greaterThan_failure() {
    thrown.expect(ListSizeMismatch.class);
    thrown.expectMessage("expected: > 4, actual: 4");
    $$("#radioButtons input").shouldHave(sizeGreaterThan(4));
  }

  @Test
  public void size_greaterThanWithReason_failure() {
    String reason = "the page should have more than 4 radio buttons";
    try {
      $$("#radioButtons input").shouldHave(sizeGreaterThan(4));
      fail("Should have thrown exception");
    } catch (UIAssertionError e) {
      thrown.expect(ExplainedUIAssertionError.class);
      thrown.expectMessage(e.getMessage() + " (because " + reason + ")");
    }
    $$("#radioButtons input").shouldHave(sizeGreaterThan(4).because(reason));
  }

  @Test
  public void size_greaterThanOrEqual() {
    $$("#radioButtons input").shouldHave(sizeGreaterThanOrEqual(4));
    $$("#radioButtons input").shouldHave(sizeGreaterThanOrEqual(3));
  }

  @Test
  public void size_greaterThanOrEqual_failure() {
    thrown.expect(ListSizeMismatch.class);
    thrown.expectMessage("expected: >= 5, actual: 4");
    $$("#radioButtons input").shouldHave(sizeGreaterThanOrEqual(5));
  }

  @Test
  public void size_lessThan() {
    $$("#radioButtons input").shouldHave(sizeLessThan(5));
    $$("#radioButtons input").shouldHave(sizeLessThan(6));
    $$("#radioButtons input").shouldHave(sizeLessThan(7));
  }

  @Test
  public void size_lessThan_failure() {
    thrown.expect(ListSizeMismatch.class);
    thrown.expectMessage("expected: < 4, actual: 4");
    $$("#radioButtons input").shouldHave(sizeLessThan(4));
  }

  @Test
  public void size_lessThanOrEqual() {
    $$("#radioButtons input").shouldHave(sizeLessThanOrEqual(4));
    $$("#radioButtons input").shouldHave(sizeLessThanOrEqual(5));
  }

  @Test
  public void size_lessThanOrEqual_failure() {
    thrown.expect(ListSizeMismatch.class);
    thrown.expectMessage("expected: <= 3, actual: 4");
    $$("#radioButtons input").shouldHave(sizeLessThanOrEqual(3));
  }

  @Test
  public void size_notEqual() {
    $$("#radioButtons input").shouldHave(sizeNotEqual(3));
    $$("#radioButtons input").shouldHave(sizeNotEqual(5));
  }

  @Test
  public void size_notEqual_failure() {
    thrown.expect(ListSizeMismatch.class);
    thrown.expectMessage("expected: <> 4, actual: 4");
    $$("#radioButtons input").shouldHave(sizeNotEqual(4));
  }

  @Test
  public void sizeCheck_forSpecialCornerCase_timeoutHappensAfterFirstCheck() {
    Configuration.collectionsTimeout = 1000;
    Configuration.collectionsPollingInterval = 1100;

    try {
      $$("#radioButtons input").shouldHave(size(4));
    }
    finally {
      Configuration.collectionsTimeout = 6000;
      Configuration.collectionsPollingInterval = 200;
    }
  }
}
