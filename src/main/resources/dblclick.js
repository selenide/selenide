(function (element, offsetX, offsetY, options) {
  const rect = element.getBoundingClientRect();

  function mouseEvent() {
    if (typeof (Event) === 'function') {
      return new MouseEvent('dblclick', Object.assign({
        'view': window,
        'bubbles': true,
        'cancelable': true,
        'clientX': rect.left + rect.width / 2 + offsetX,
        'clientY': rect.top + rect.height / 2 + offsetY
      }, options));
    }
    else {
      const event = document.createEvent('MouseEvent');
      event.initEvent('dblclick', true, true);
      event.type = 'dblclick'
      event.view = window;
      event.altKey = options.altKey
      event.ctrlKey = options.ctrlKey
      event.shiftKey = options.shiftKey
      event.metaKey = options.metaKey
      event.clientX = rect.left + rect.width / 2 + offsetX
      event.clientY = rect.top + rect.height / 2 + offsetY
      return event;
    }
  }
  element.dispatchEvent(mouseEvent());
})(arguments[0], arguments[1], arguments[2], arguments[3]);
