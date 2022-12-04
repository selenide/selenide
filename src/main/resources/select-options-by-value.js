(function(select, values) {
  if (select.disabled) {
    return {disabledSelect: 'Cannot select option in a disabled select'};
  }

  function optionByValue(requestedValue) {
    return Array.from(select.options).find(function(option) { return option.value === requestedValue })
  }

  let missingOptionsValues = values.filter(function(value) { return !optionByValue(value) });
  if (missingOptionsValues.length > 0) {
    return {optionsNotFound: missingOptionsValues};
  }

  let disabledOptionsValues = values.filter(function(value) { return optionByValue(value).disabled });
  if (disabledOptionsValues.length > 0) {
    return {disabledOptions: disabledOptionsValues};
  }

  values.forEach(function (requestedValue) {
    optionByValue(requestedValue).selected = 'selected';
  });

  let event = document.createEvent('HTMLEvents');
  event.initEvent('change', true, true);
  select.dispatchEvent(event);

  return {};
})(arguments[0], arguments[1])

