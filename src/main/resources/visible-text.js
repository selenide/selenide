(function (element) {
  // Measures the rendered glyphs in place (no DOM mutations), so all CSS rules, nested styles and text direction are respected.
  const TOLERANCE = 0.5;
  const clipsCache = new Map();
  const transparencyCache = new Map();
  let canvasContext = null;
  const range = document.createRange();

  function isTransparent(el) {
    if (!el) return false;
    if (!transparencyCache.has(el)) {
      transparencyCache.set(el, getComputedStyle(el).opacity === '0' || isTransparent(el.parentElement));
    }
    return transparencyCache.get(el);
  }

  function isClipping(style) {
    return style.overflowX !== 'visible' || style.overflowY !== 'visible';
  }

  function ellipsisWidth(style) {
    // The ellipsis glyph is painted by browser, but not present in DOM. So we measure its width on a detached canvas.
    canvasContext = canvasContext || document.createElement('canvas').getContext('2d');
    canvasContext.font = `${style.fontStyle} ${style.fontWeight} ${style.fontSize} ${style.fontFamily}`;
    return canvasContext.measureText('\u2026').width;
  }

  function clipBox(el, style) {
    const rect = el.getBoundingClientRect();
    const left = rect.left + el.clientLeft;
    const top = rect.top + el.clientTop;
    const box = {left: left, right: left + el.clientWidth, top: top, bottom: top + el.clientHeight};
    if (style.textOverflow === 'ellipsis' && style.overflowX !== 'visible' && el.scrollWidth > el.clientWidth) {
      if (style.direction === 'rtl') {
        box.left += ellipsisWidth(style);
      } else {
        box.right -= ellipsisWidth(style);
      }
    }
    return box;
  }

  function clipsOf(el) {
    if (!el) return [];
    if (clipsCache.has(el)) return clipsCache.get(el);
    const style = getComputedStyle(el);
    const parentClips = clipsOf(el.parentElement);
    const clips = isClipping(style) ? parentClips.concat([clipBox(el, style)]) : parentClips;
    clipsCache.set(el, clips);
    return clips;
  }

  function fitsInto(rect, clip) {
    const verticalCenter = (rect.top + rect.bottom) / 2;
    return rect.left >= clip.left - TOLERANCE && rect.right <= clip.right + TOLERANCE &&
      verticalCenter >= clip.top && verticalCenter <= clip.bottom;
  }

  function isRendered(rect, clips) {
    return (rect.width > 0 || rect.height > 0) && clips.every(clip => fitsInto(rect, clip));
  }

  function blockContainer(el) {
    let current = el;
    while (current !== element && /^(inline|contents)/.test(getComputedStyle(current).display)) {
      current = current.parentElement;
    }
    return current;
  }

  let result = '';
  let pendingSpace = false;
  let previousBlock = null;

  function append(text) {
    if (pendingSpace && result) result += ' ';
    result += text;
    pendingSpace = false;
  }

  function appendCharacters(textNode, text, clips) {
    for (let i = 0; i < text.length;) {
      const character = String.fromCodePoint(text.codePointAt(i));
      if (/\s/.test(character)) {
        pendingSpace = true;
      } else {
        range.setStart(textNode, i);
        range.setEnd(textNode, i + character.length);
        if (isRendered(range.getBoundingClientRect(), clips)) {
          append(character);
        } else {
          pendingSpace = true;
        }
      }
      i += character.length;
    }
  }

  function textRects(textNode) {
    range.selectNodeContents(textNode);
    return Array.from(range.getClientRects());
  }

  function appendText(textNode) {
    const parent = textNode.parentElement;
    const text = textNode.data;
    if (!parent || !text.trim() || getComputedStyle(parent).visibility !== 'visible' || isTransparent(parent)) return;
    const rects = textRects(textNode);
    if (rects.length === 0) return;

    const block = blockContainer(parent);
    if (previousBlock !== null && block !== previousBlock) pendingSpace = true;
    previousBlock = block;

    const clips = clipsOf(parent);
    if (rects.every(rect => isRendered(rect, clips))) {
      if (/^\s/.test(text)) pendingSpace = true;
      append(text.trim().replace(/\s+/g, ' '));
      if (/\s$/.test(text)) pendingSpace = true;
    } else {
      appendCharacters(textNode, text, clips);
    }
  }

  const walker = document.createTreeWalker(element, NodeFilter.SHOW_TEXT | NodeFilter.SHOW_ELEMENT);
  while (walker.nextNode()) {
    const node = walker.currentNode;
    if (node.nodeType === Node.TEXT_NODE) {
      appendText(node);
    } else if (node.localName === 'br') {
      pendingSpace = true;
    }
  }
  return result;
})(arguments[0]);
