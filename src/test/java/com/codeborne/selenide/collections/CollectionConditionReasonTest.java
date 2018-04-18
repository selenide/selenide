package com.codeborne.selenide.collections;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ex.UIAssertionError;
import com.codeborne.selenide.impl.WebElementsCollection;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.Callable;

import static org.junit.Assert.assertEquals;

public class CollectionConditionReasonTest {

  private static final String REASON = "<CollectionCondition::because provided reason>";
  private static final Callable<Exception> EXCEPTION_PRODUCER = () -> new Exception("exception message");

  @Test
  public void reasonTest() throws Exception {
    String messageWithoutReason = getConditionFailMessage(new MockCollectionCondition(), EXCEPTION_PRODUCER.call());
    String messageWithReason = getConditionFailMessage(new MockCollectionCondition().because(REASON),
                                                       EXCEPTION_PRODUCER.call());
    assertEquals(messageWithoutReason + " (because " + REASON + ")", messageWithReason);
  }

  private String getConditionFailMessage(CollectionCondition condition, Exception lastError) {
    try {
      condition.fail(null, null, lastError, 0);
    } catch (UIAssertionError expected) {
      return expected.getMessage();
    }
    throw new IllegalStateException("CollectionCondition::fail did not throw an UIAssertionError");
  }

  private final class MockCollectionCondition extends CollectionCondition {

    @Override
    public void fail(WebElementsCollection collection, List<WebElement> elements, Exception lastError, long timeoutMs) {
      throw new UIAssertionError(lastError);
    }

    @Override
    public boolean apply(@Nullable List<WebElement> input) {
      return false;
    }
  }

}
