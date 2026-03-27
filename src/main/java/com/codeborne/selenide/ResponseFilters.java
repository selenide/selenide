package com.codeborne.selenide;

import com.browserup.bup.filters.ResponseFilter;

import java.util.LinkedHashMap;

public class ResponseFilters extends LinkedHashMap<String, ResponseFilter> {

  public static ResponseFilters from(final String name, final ResponseFilter responseFilter) {
    final ResponseFilters responseFilters = new ResponseFilters();
    responseFilters.put(name, responseFilter);
    return responseFilters;
  }
}
