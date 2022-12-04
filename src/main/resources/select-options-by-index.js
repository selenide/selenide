(function(select, indexes) {
  if (select.disabled) {
    return {disabledSelect: 'Cannot select option in a disabled select'};
  }

  let missingOptionsIndexes = indexes.filter(function(index) { return !select.options[index] });
  if (missingOptionsIndexes.length > 0) {
    return {optionsNotFound: missingOptionsIndexes};
  }

  let disabledOptionsIndexes = indexes.filter(function(index) { select.options[index].disabled });
  if (disabledOptionsIndexes.length > 0) {
    return {disabledOptions: disabledOptionsIndexes};
  }

  indexes.forEach(function(index) {
    select.options[index].selected = 'selected';
  });

  let event = document.createEvent('HTMLEvents');
  event.initEvent('change', true, true);
  select.dispatchEvent(event);

  return {};
})(arguments[0], arguments[1])

