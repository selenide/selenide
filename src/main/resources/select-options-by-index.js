(function(select, indexes) {
  if (select.disabled) {
    return {disabledSelect: 'Cannot select option in a disabled select'};
  }
  for (let index of indexes) {
    const option = select.options[index];
    if (!option) {
      return {optionNotFound: index};
    }
    if (option.disabled) {
      return {disabledOption: index};
    }
    option.selected = 'selected';
  }

  const event = document.createEvent('HTMLEvents');
  event.initEvent('change', true, true);
  select.dispatchEvent(event);

  return {};
})(arguments[0], arguments[1])

