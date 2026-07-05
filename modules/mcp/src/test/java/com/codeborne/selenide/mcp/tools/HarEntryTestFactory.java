package com.codeborne.selenide.mcp.tools;

import de.sstoehr.harreader.model.HarEntry;
import de.sstoehr.harreader.model.HarRequest;
import de.sstoehr.harreader.model.HarResponse;
import de.sstoehr.harreader.model.HttpMethod;

final class HarEntryTestFactory {
  private HarEntryTestFactory() {
  }

  static HarEntry entry(String url, int status) {
    HarRequest request = new HarRequest();
    request.setUrl(url);
    request.setMethod(HttpMethod.GET);
    HarResponse response = new HarResponse();
    response.setStatus(status);
    HarEntry entry = new HarEntry();
    entry.setRequest(request);
    entry.setResponse(response);
    return entry;
  }
}
