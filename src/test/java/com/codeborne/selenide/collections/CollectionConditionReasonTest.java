package com.codeborne.selenide.collections;

import com.codeborne.selenide.CollectionCondition;
import org.junit.Assert;
import org.junit.Test;

public class CollectionConditionReasonTest {

  @Test
  public void testCollectionConditionWithBecause() {
    CollectionCondition condition = CollectionCondition.sizeGreaterThanOrEqual(1).because("size is lower than expected");
    Assert.assertEquals("size >= 1 (because size is lower than expected)", condition.toString());
  }

  @Test
  public void testCollectionConditionWithoutBecause() {
    CollectionCondition condition = CollectionCondition.sizeGreaterThanOrEqual(1);
    Assert.assertEquals("size >= 1", condition.toString());
  }

}
