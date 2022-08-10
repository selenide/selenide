(function (element) {
  const computedStyle = getComputedStyle(element);
  const size = computedStyle.length;
  const result = {};
  for (let i = 0; i < size; i++) {
    const cssPropertyName = computedStyle[i];
    result[cssPropertyName] = computedStyle.getPropertyValue(cssPropertyName);
  }
  return result;
})(arguments[0]);

