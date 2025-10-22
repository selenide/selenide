(function () {
  import 'texts.js'

  const root = arguments[0] || document.body;
  const options = arguments[3];
  const attributeName = arguments[1];
  const attributeValue = normalizeText(arguments[2], options);
  const limit = arguments[4];

  function matchesValue(element) {
    const value = element.getAttribute(attributeName);
    const normalizedValue = normalizeText(value, options);
    return textMatches(options, normalizedValue, attributeValue)
  }

  const candidates = root.querySelectorAll(`[${attributeName}]`);
  return Array.from(candidates).filter(element => matchesValue(element))
})(...arguments)
