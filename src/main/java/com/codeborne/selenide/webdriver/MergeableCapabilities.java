package com.codeborne.selenide.webdriver;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

/**
 * A subclass of MutableCapabilities which has fixed `merge` method:
 * it can properly merge all these ChromeOptions etc. with their Maps inside of Maps.
 */
public class MergeableCapabilities extends MutableCapabilities {
  public MergeableCapabilities(Capabilities base, Capabilities extraCapabilities) {
    merge(base);
    merge(extraCapabilities);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void setCapability(String key, Object value) {
    if (value instanceof Map) {
      setCapabilityMap(key, (Map<String, Object>) value);
    }
    else {
      super.setCapability(key, value);
    }
  }

  @SuppressWarnings("unchecked")
  private void setCapabilityMap(String key, Map<String, Object> value) {
    Object previousValue = getCapability(key);
    if (previousValue == null) {
      super.setCapability(key, value);
    }
    else if (previousValue instanceof Map) {
      super.setCapability(key, mergeMaps((Map<String, Object>) previousValue, value));
    }
    else {
      throw new IllegalArgumentException("Cannot merge capability " + key + " of different types: " +
        value.getClass().getName() + " vs " + previousValue.getClass().getName());
    }
  }

  private Map<String, Object> mergeMaps(Map<String, Object> base, Map<String, Object> extra) {
    Map<String, Object> result = new HashMap<>();
    for (Map.Entry<String, Object> entry : base.entrySet()) {
      String key = entry.getKey();
      Object baseValue = entry.getValue();
      Object extraValue = extra.get(key);
      result.put(key, merge(baseValue, extraValue));
    }
    for (Map.Entry<String, Object> entry : extra.entrySet()) {
      String key = entry.getKey();
      if (!result.containsKey(key)) {
        result.put(key, entry.getValue());
      }
    }
    return result;
  }

  @SuppressWarnings({"unchecked", "ConstantConditions"})
  private Object merge(Object baseValue, Object extraValue) {
    if (extraValue == null) {
      return baseValue;
    }
    else if (baseValue instanceof List && extraValue instanceof List) {
      return mergeLists((List<Object>) baseValue, (List<Object>) extraValue);
    }
    else if (baseValue.getClass().isArray() && extraValue.getClass().isArray()) {
      return mergeArrays((Object[]) baseValue, (Object[]) extraValue);
    }
    else if (baseValue.getClass().isArray() && extraValue instanceof List) {
      return mergeLists(asList((Object[]) baseValue), (List<Object>) extraValue);
    }
    else if (baseValue instanceof List && extraValue.getClass().isArray()) {
      return mergeLists((List<Object>) baseValue, asList((Object[]) extraValue));
    }
    else if (baseValue.getClass() != extraValue.getClass()) {
      throw new IllegalArgumentException("Cannot merge values of different types: " + baseValue + " vs " + extraValue);
    }
    else {
      return extraValue;
    }
  }

  private List<Object> mergeLists(List<Object> base, List<Object> extra) {
    ArrayList<Object> result = new ArrayList<>();
    result.addAll(base);
    result.addAll(extra);
    return result;
  }

  private Object[] mergeArrays(Object[] base, Object[] extra) {
    Object[] result = new Object[base.length + extra.length];
    System.arraycopy(base, 0, result, 0, base.length);
    System.arraycopy(extra, 0, result, base.length, extra.length);
    return result;
  }
}
