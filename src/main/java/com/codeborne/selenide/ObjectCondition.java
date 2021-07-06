package com.codeborne.selenide;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ObjectCondition<T> {
  @Nonnull
  @CheckReturnValue
  String description();

  @CheckReturnValue
  boolean test(T object);

  @Nullable
  @CheckReturnValue
  Object actualValue(T object);

  @Nonnull
  @CheckReturnValue
  default ObjectCondition<T> negate() {
    ObjectCondition<T> self = this;

    return new ObjectCondition<T>() {
      @Nonnull
      @Override
      public String description() {
        return "not " + self.description();
      }

      @Override
      public boolean test(T object) {
        return !self.test(object);
      }

      @Nullable
      @Override
      public Object actualValue(T object) {
        return self.actualValue(object);
      }

      @Nonnull
      @Override
      public ObjectCondition<T> negate() {
        return self;
      }
    };
  }
}
