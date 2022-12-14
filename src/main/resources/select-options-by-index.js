(function(select, indexes) {
  if (select.disabled) {
    return {disabledSelect: 'Cannot select option in a disabled select'};
  }
  select.focus();

  const missingOptionsIndexes = indexes.filter(index => !select.options[index]);
  if (missingOptionsIndexes.length > 0) {
    return {optionsNotFound: missingOptionsIndexes};
  }

  const disabledOptionsIndexes = indexes.filter(index => select.options[index].disabled);
  if (disabledOptionsIndexes.length > 0) {
    return {disabledOptions: disabledOptionsIndexes};
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
  for (let index of indexes) {
    select.options[index].selected = 'selected';
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

