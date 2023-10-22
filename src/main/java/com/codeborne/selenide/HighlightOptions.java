package com.codeborne.selenide;

public record HighlightOptions(String style) {
  private static final String DEFAULT_BACKGROUND = """
    background: linear-gradient(45deg, rgb(244,187,56), rgb(85, 180, 250), rgb(150, 30, 160));
    """;
  private static final String DEFAULT_BORDER = """
    border: 1px dotted rgb(244,187,56);
    """;
  public static final HighlightOptions DEFAULT = new HighlightOptions(DEFAULT_BACKGROUND);

  public static HighlightOptions background() {
    return new HighlightOptions(DEFAULT_BACKGROUND);
  }

  public static HighlightOptions background(String backgroundStyle) {
    return new HighlightOptions("background: " + backgroundStyle);
  }

  public static HighlightOptions border() {
    return new HighlightOptions(DEFAULT_BORDER);
  }

  public static HighlightOptions border(String borderStyle) {
    return new HighlightOptions("border: " + borderStyle);
  }

  public static HighlightOptions style(String style) {
    return new HighlightOptions(style);
  }
}
