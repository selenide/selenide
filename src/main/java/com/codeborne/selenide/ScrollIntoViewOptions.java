package com.codeborne.selenide;

import static com.codeborne.selenide.ScrollIntoViewOptions.Behavior.instant;
import static com.codeborne.selenide.ScrollIntoViewOptions.Block.start;
import static com.codeborne.selenide.ScrollIntoViewOptions.Inline.nearest;

/**
 * See <a href="https://developer.mozilla.org/en-US/docs/Web/API/Element/scrollIntoView">scrollIntoView documentation</a>
 *
 * @param behavior whether scrolling is instant or animates smoothly
 * @param block the vertical alignment of the element within the scrollable ancestor container
 * @param inline the horizontal alignment of the element within the scrollable ancestor container
 */
public record ScrollIntoViewOptions(
  Behavior behavior,
  Block block,
  Inline inline
) {

  public static ScrollIntoViewOptions instant() {
    return new ScrollIntoViewOptions(instant, start, nearest);
  }

  public ScrollIntoViewOptions block(Block block) {
    return new ScrollIntoViewOptions(behavior, block, inline);
  }

  public ScrollIntoViewOptions inline(Inline inline) {
    return new ScrollIntoViewOptions(behavior, block, inline);
  }

  public String toJson() {
    return "{behavior: '%s', block: '%s', inline: '%s'}".formatted(behavior, block, inline);
  }

  public enum Behavior {
    smooth,
    instant,
    auto
  }

  public enum Block {
    start,
    center,
    end,
    nearest
  }

  public enum Inline {
    start,
    center,
    end,
    nearest
  }
}
