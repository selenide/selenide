(function(select, values) {
  if (select.disabled) {
    return {disabledSelect: 'Cannot select option in a disabled select'};
  }

  for (let requestedValue of values) {
    let optionFound = false
    for (let option of select.options) {
      if (option.value === requestedValue) {
        if (option.disabled) {
          return {disabledOption: requestedValue};
        }
        optionFound = true
        option.selected = 'selected';
      }
    }
    if (!optionFound) {
      return {optionNotFound: requestedValue};
    }
  }

  const event = document.createEvent('HTMLEvents');
  event.initEvent('change', true, true);
  select.dispatchEvent(event);

  return {};
})(arguments[0], arguments[1])

