(function (element, offsetX, offsetY, options) {
  const rect = element.getBoundingClientRect();

  function createEvent() {
    if (typeof (Event) === 'function') {
      return new MouseEvent('click', Object.assign({
        'view': window,
        'bubbles': true,
        'cancelable': true,
        'clientX': rect.left + rect.width / 2 + offsetX,
        'clientY': rect.top + rect.height / 2 + offsetY,
      }, options));
    }
    else {
      const event = document.createEvent('MouseEvent');
      event.initEvent('click', true, true);
      event.type = 'click'
      event.view = window
      event.altKey = options.altKey
      event.ctrlKey = options.ctrlKey
      event.shiftKey = options.shiftKey
      event.metaKey = options.metaKey
      event.clientX = rect.left + rect.width / 2 + offsetX
      event.clientY = rect.top + rect.height / 2 + offsetY
      return event;
    }
  }
  element.dispatchEvent(createEvent());
})(arguments[0], arguments[1], arguments[2], arguments[3]);
