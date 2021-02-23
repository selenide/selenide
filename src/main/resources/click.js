(function (element, offsetX, offsetY) {
  const rect = element.getBoundingClientRect();
  element.dispatchEvent(new MouseEvent('click', {
    'view': window,
    'bubbles': true,
    'cancelable': true,
    'clientX': rect.left + rect.width / 2 + offsetX,
    'clientY': rect.top + rect.height / 2 + offsetY
  }));
})(arguments[0], arguments[1], arguments[2]);
