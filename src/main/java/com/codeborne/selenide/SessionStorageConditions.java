package com.codeborne.selenide;

import com.codeborne.selenide.conditions.sessionstorage.Item;
import com.codeborne.selenide.conditions.sessionstorage.ItemWithValue;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @since 5.23.0
 */
@ParametersAreNonnullByDefault
public class SessionStorageConditions {

  @CheckReturnValue
  @Nonnull
  public static ObjectCondition<SessionStorage> item(String item) {
    return new Item(item);
  }

  @CheckReturnValue
  @Nonnull
  public static ObjectCondition<SessionStorage> itemWithValue(String item, String value) {
    return new ItemWithValue(item, value);
  }
}
