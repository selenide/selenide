package com.codeborne.selenide;

import com.browserup.bup.filters.RequestFilter;

import java.util.LinkedHashMap;

public class RequestFilters extends LinkedHashMap<String, RequestFilter> {
  public static RequestFilters from(final String name, final RequestFilter requestFilter) {
    final RequestFilters requestFilters = new RequestFilters();
    requestFilters.put(name, requestFilter);
    return requestFilters;
  }
}
