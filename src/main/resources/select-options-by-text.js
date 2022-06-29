(function(select, texts) {
  if (select.disabled) {
    return {disabledSelect: 'Cannot select option in a disabled select'};
  }

  for (let requestedText of texts) {
    let optionFound = false
    for (let option of select.options) {
      if (option.text === requestedText) {
        if (option.disabled) {
          return {disabledOption: requestedText};
        }
        optionFound = true
        option.selected = 'selected';
      }
    }
    if (!optionFound) {
      return {optionNotFound: requestedText};
    }
  }

  const event = document.createEvent('HTMLEvents');
  event.initEvent('change', true, true);
  select.dispatchEvent(event);

  return {};
})(arguments[0], arguments[1])

