(function () {
  function findInShadows(target, shadowRoots, searchContext) {
    if (shadowRoots.length === 0) {
      return Array.from(searchContext.querySelectorAll(target));
    }

    const nextSelector = shadowRoots[0];
    const otherSelectors = shadowRoots.slice(1);
    const nextInnerShadowRoots = Array.from(searchContext.querySelectorAll(nextSelector));
    return nextInnerShadowRoots.map(function (childShadowRoot) {
      return findInShadows(target, otherSelectors, getShadowRoot(childShadowRoot));
    }).flat();
  }

  function getShadowRoot(element) {
    if (!element.shadowRoot) {
      throw Error('The element is not a shadow host or has \'closed\' shadow-dom mode: ' + element);
    }
    return element.shadowRoot;
  }

  return arguments.length === 2 ?
    findInShadows(arguments[0], arguments[1], document) :
    findInShadows(arguments[0], arguments[1], getShadowRoot(arguments[2]));
})
