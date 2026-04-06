package com.codeborne.selenide.mcp;

import com.codeborne.selenide.ex.ElementIsNotClickableError;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ElementShould;
import com.codeborne.selenide.ex.UIAssertionError;

class ToolErrorHandler {
  String formatError(Throwable error, String selector) {
    StringBuilder sb = new StringBuilder();
    sb.append("Error: ").append(error.getMessage()).append("\n");
    sb.append("Selector: ").append(selector).append("\n");

    if (error instanceof ElementNotFound) {
      sb.append("Type: element_not_found\n");
      sb.append("Suggestion: Check that the selector is correct. ")
        .append("Use browser_snapshot to see available elements.\n");
    }
    else if (error instanceof ElementIsNotClickableError) {
      sb.append("Type: element_not_clickable\n");
      sb.append("Suggestion: Element may be overlapped by another element. ")
        .append("Try browser_execute_js to scroll or dismiss overlays.\n");
    }
    else if (error instanceof ElementShould) {
      sb.append("Type: condition_not_met\n");
      sb.append("Suggestion: Element was found but condition failed. ")
        .append("Use browser_find to inspect current element state.\n");
    }
    else if (error instanceof UIAssertionError) {
      sb.append("Type: assertion_error\n");
    }

    if (error.getCause() != null) {
      sb.append("Caused by: ").append(error.getCause().getMessage()).append("\n");
    }
    return sb.toString();
  }
}
