(function (element, offsetX, offsetY) {
  const rect = element.getBoundingClientRect();

  function mouseEvent() {
    if (typeof (Event) === 'function') {
      return new MouseEvent('click', {
        'view': window,
        'bubbles': true,
        'cancelable': true,
        'clientX': rect.left + rect.width / 2 + offsetX,
        'clientY': rect.top + rect.height / 2 + offsetY
      });
    }
    else {
      const event = document.createEvent('MouseEvent');
      event.initEvent('click', true, true);
      event.type = 'click'
      event.view = window;
      event.clientX = rect.left + rect.width / 2 + offsetX
      event.clientY = rect.top + rect.height / 2 + offsetY
      return event;
    }
  }
  element.dispatchEvent(mouseEvent());
})(arguments[0], arguments[1], arguments[2]);
