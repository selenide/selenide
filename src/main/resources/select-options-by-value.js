(function(select, values) {
  if (select.disabled) {
    return {disabledSelect: 'Cannot select option in a disabled select'};
  }

  function optionByValue(requestedValue) {
    return Array.from(select.options).find(option => option.value === requestedValue)
  }

  const missingOptionsValues = values.filter(value => !optionByValue(value));
  if (missingOptionsValues.length > 0) {
    return {optionsNotFound: missingOptionsValues};
  }

  const disabledOptionsValues = values.filter(value => optionByValue(value).disabled);
  if (disabledOptionsValues.length > 0) {
    return {disabledOptions: disabledOptionsValues};
  }

  for (let requestedValue of values) {
    optionByValue(requestedValue).selected = 'selected';
  }

  const event = document.createEvent('HTMLEvents');
  event.initEvent('change', true, true);
  select.dispatchEvent(event);

  return {};
})(arguments[0], arguments[1])

