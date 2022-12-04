(function(select, values) {
  function arrayFrom(array) {
    //if (String.prototype.contains) { return Array.from(array) }
    var result = [];
    for (var i = 0; i < array.length; i++) {
      result.push(array[i]);
    }
    return result;
  }

  if (select.disabled) {
    return {disabledSelect: 'Cannot select option in a disabled select'};
  }
  select.focus();

  function optionByValue(requestedValue) {
    return arrayFrom(select.options).find(function(option) { return option.value === requestedValue })
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
  event.initEvent('click', true, true);
  select.dispatchEvent(event);
  event.initEvent('change', true, true);
  select.dispatchEvent(event);

  return {};
})(arguments[0], arguments[1])

