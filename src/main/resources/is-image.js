(function(element) {
  return element.tagName.toLowerCase() === 'img' &&
    element.complete &&
    typeof element.naturalWidth != 'undefined' &&
    element.naturalWidth > 0
})(arguments[0])
