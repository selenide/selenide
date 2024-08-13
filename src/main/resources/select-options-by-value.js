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


  select.dispatchEvent(new Event('click'));

  if (getSelectedOptionsString(select) !== previousSelectedOptions) {
    select.dispatchEvent(new Event('input'));
    select.dispatchEvent(new Event('change'));
  }

  return {};
})(arguments[0], arguments[1])

