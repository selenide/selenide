package com.codeborne.selenide.appium;

import java.time.Duration;

import com.codeborne.selenide.ClickOptions;
import com.codeborne.selenide.DragAndDropOptions;
import com.codeborne.selenide.HighlightOptions;
import com.codeborne.selenide.HoverOptions;
import com.codeborne.selenide.ScrollIntoViewOptions;
import com.codeborne.selenide.ScrollOptions;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.SetValueOptions;
import com.codeborne.selenide.TypeOptions;
import com.codeborne.selenide.WebElementCondition;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.By;

public interface SelenideAppiumElement extends SelenideElement {
  @CanIgnoreReturnValue
  @Override
  SelenideAppiumElement as(String alias);

  /**
   * @see com.codeborne.selenide.appium.commands.HideKeyboard
   */
  @CanIgnoreReturnValue
  SelenideAppiumElement hideKeyboard();

  /**
   * @see com.codeborne.selenide.appium.commands.AppiumScrollTo
   */
  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement scrollTo();

  /**
   * @see com.codeborne.selenide.appium.commands.AppiumScrollTo
   */
  @CanIgnoreReturnValue
  SelenideAppiumElement scroll(AppiumScrollOptions appiumScrollOptions);

  /**
   * @see com.codeborne.selenide.appium.commands.AppiumSwipeTo
   */
  @CanIgnoreReturnValue
  SelenideAppiumElement swipeTo();

  /**
   * @see com.codeborne.selenide.appium.commands.AppiumSwipeTo
   */
  @CanIgnoreReturnValue
  SelenideAppiumElement swipe(AppiumSwipeOptions appiumSwipeOptions);

  /**
   * @see com.codeborne.selenide.appium.commands.AppiumTap
   */
  @CanIgnoreReturnValue
  SelenideAppiumElement tap();

  /**
   * @see com.codeborne.selenide.appium.commands.AppiumTap
   */
  @CanIgnoreReturnValue
  SelenideAppiumElement tap(AppiumClickOptions appiumClickOptions);

  /**
   * @see com.codeborne.selenide.appium.commands.AppiumDoubleTap
   */
  @CanIgnoreReturnValue
  SelenideAppiumElement doubleTap();

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement shouldNotBe(WebElementCondition condition, Duration timeout);

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement shouldNotBe(WebElementCondition... condition);

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement shouldNotHave(WebElementCondition condition, Duration timeout);

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement shouldNotHave(WebElementCondition... condition);

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement shouldNot(WebElementCondition condition, Duration timeout);

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement shouldNot(WebElementCondition... condition);

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement shouldBe(WebElementCondition condition, Duration timeout);

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement shouldBe(WebElementCondition... condition);

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement shouldHave(WebElementCondition condition, Duration timeout);

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement shouldHave(WebElementCondition... condition);

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement should(WebElementCondition condition, Duration timeout);

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement should(WebElementCondition... condition);

  /**
   * @see com.codeborne.selenide.appium.commands.AppiumFind
   */
  @Override
  SelenideAppiumElement find(String cssSelector);

  @Override
  SelenideAppiumElement find(String cssSelector, int index);

  @Override
  SelenideAppiumElement find(By selector);

  @Override
  SelenideAppiumElement find(By selector, int index);

  @Override
  SelenideAppiumElement $(String cssSelector);

  @Override
  SelenideAppiumElement $(String cssSelector, int index);

  @Override
  SelenideAppiumElement $(By selector);

  @Override
  SelenideAppiumElement $(By selector, int index);

  /**
   * @see com.codeborne.selenide.appium.commands.AppiumFindByXpath
   */
  @Override
  SelenideAppiumElement $x(String xpath);

  @Override
  SelenideAppiumElement $x(String xpath, int index);

  /**
   * @see com.codeborne.selenide.appium.commands.AppiumFindAll
   */
  @Override
  SelenideAppiumCollection findAll(String cssSelector);

  @Override
  SelenideAppiumCollection findAll(By selector);

  @Override
  SelenideAppiumCollection $$(String cssSelector);

  @Override
  SelenideAppiumCollection $$(By selector);

  /**
   * @see com.codeborne.selenide.appium.commands.AppiumFindAllByXpath
   */
  @Override
  SelenideAppiumCollection $$x(String xpath);

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement setValue(@Nullable String text);

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement setValue(SetValueOptions options);

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement val(@Nullable String text);

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement val(SetValueOptions options);

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement type(CharSequence text);

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement type(TypeOptions options);

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement append(String text);

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement paste();

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement press(CharSequence... keysToPress);

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement pressEnter();

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement pressEscape();

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement pressTab();

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement unfocus();

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement setSelected(boolean selected);

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement click(ClickOptions clickOption);

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement contextClick();

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement doubleClick();

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement doubleClick(ClickOptions clickOption);

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement hover();

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement hover(HoverOptions options);

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement dragAndDrop(DragAndDropOptions options);

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement scroll(ScrollOptions scrollOptions);

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement scrollIntoView(ScrollIntoViewOptions options);

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement scrollIntoCenter();

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement highlight();

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement highlight(HighlightOptions options);

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement unhighlight();
}
