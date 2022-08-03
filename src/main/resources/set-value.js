(function(webelement, text) {

  function triggerEvent(target, eventName) {
    if (document.createEventObject) {
      var event = document.createEventObject();
      target.fireEvent('on' + eventName, event);
    }
    else {
      var event = document.createEvent('HTMLEvents');
      event.initEvent(eventName, true, true);
      target.dispatchEvent(event);
    }
  }

  function trigger(target, eventNames) {
    for (var i in eventNames) {
      try {
        triggerEvent(target, eventNames[i]);
      }
      catch (staleElementException) {
        console.log('failed to trigger event', eventNames[i])
      }
    }
  }

  if (webelement.getAttribute('readonly') !== null) return 'Cannot change value of readonly element';
  if (webelement.getAttribute('disabled') !== null) return 'Cannot change value of disabled element';

  trigger(document.activeElement, ['blur']);
  webelement.focus();
  var maxlength = webelement.getAttribute('maxlength') == null ? -1 : parseInt(webelement.getAttribute('maxlength'));
  webelement.value = maxlength === -1 ? text : text.length <= maxlength ? text : text.substring(0, maxlength);
  trigger(webelement, ['focus', 'keydown', 'keypress', 'input', 'keyup', 'change']);

  return "";
})(arguments[0], arguments[1]);

