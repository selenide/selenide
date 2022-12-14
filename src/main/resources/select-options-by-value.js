(function (select, values) {
  if (select.disabled) {
    return {disabledSelect: 'Cannot select option in a disabled select'};
  }
  select.focus();

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

  function getSelectedOptions(select) {
    var result = []
    for (var i = 0; i < select.options.length; i++) {
      if (select.options[i].selected) {
        result.push(i);
      }
    }
    return result;
  }

  var previousSelectedOptions = getSelectedOptions(select);
  for (let requestedValue of values) {
    optionByValue(requestedValue).selected = 'selected';
  }

  const event = document.createEvent('HTMLEvents');
  event.initEvent('click', true, true);
  select.dispatchEvent(event);
  if (JSON.stringify(getSelectedOptions(select)) != JSON.stringify(previousSelectedOptions)) {
    event.initEvent('change', true, true);
    select.dispatchEvent(event);
  }

  return {};
})(arguments[0], arguments[1])

