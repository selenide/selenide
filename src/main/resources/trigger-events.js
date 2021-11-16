(function(webElement, eventNames) {
  for (let i = 0; i < eventNames.length; i++) {
    if (document.createEventObject) {
      let evt = document.createEventObject();
      webElement.fireEvent('on' + eventNames[i], evt);
    }
    else {
      let evt = document.createEvent('HTMLEvents');
      evt.initEvent(eventNames[i], true, true);
      webElement.dispatchEvent(evt);
    }
  }
})(arguments[0], arguments[1])
