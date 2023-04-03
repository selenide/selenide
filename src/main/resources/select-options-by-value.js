(function (select, values) {
  if (select.tagName.toLowerCase() !== 'select') {
    return {nonSelect: 'Cannot select option from a non-select element'};
  }
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

  function getSelectedOptionsString(select) {
    return Array.from(select.options).map(option => option.selected).join(",");
  }

  let previousSelectedOptions = getSelectedOptionsString(select);
  for (let requestedValue of values) {
    optionByValue(requestedValue).selected = 'selected';
  }

  const event = document.createEvent('HTMLEvents');
  event.initEvent('click', true, true);
  select.dispatchEvent(event);
  if (getSelectedOptionsString(select) !== previousSelectedOptions) {
    event.initEvent('change', true, true);
    select.dispatchEvent(event);
  }

  return {};
})(arguments[0], arguments[1])

