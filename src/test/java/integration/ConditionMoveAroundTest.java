package integration;

import com.codeborne.selenide.ex.ElementShouldNot;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.moveAround;
import static com.codeborne.selenide.Selenide.$;

public class ConditionMoveAroundTest extends IntegrationTest {
  @Before
  public void openTestPageWithMovingElements() {
    openFile("page_with_moving_elements.html");
  }

  @Test(expected = ElementShouldNot.class)
  public void foreverMovingElement() {
    $("#forever_moving_element").click();
    $("#forever_moving_element").shouldNot(moveAround);
  }

  @Test
  public void movingElementMoves() {
    $("#forever_moving_element").click();
    $("#forever_moving_element").should(moveAround);
  }


  @Test
  public void notMovingElement() {
    $("#not_moving_element").shouldNot(moveAround);
  }


  @Test
  public void movingAndFrozenElement() {
    $("#moving_then_frozen_element").click();
    $("#moving_then_frozen_element").shouldNot(moveAround);
  }
}
