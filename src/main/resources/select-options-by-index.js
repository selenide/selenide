(function(select, indexes) {
  if (select.disabled) {
    return {disabledSelect: 'Cannot select option in a disabled select'};
  }

  const missingOptionsIndexes = indexes.filter(index => !select.options[index]);
  if (missingOptionsIndexes.length > 0) {
    return {optionsNotFound: missingOptionsIndexes};
  }

  const disabledOptionsIndexes = indexes.filter(index => select.options[index].disabled);
  if (disabledOptionsIndexes.length > 0) {
    return {disabledOptions: disabledOptionsIndexes};
  }

  for (let index of indexes) {
    select.options[index].selected = 'selected';
  }

  const event = document.createEvent('HTMLEvents');
  event.initEvent('change', true, true);
  select.dispatchEvent(event);

  return {};
})(arguments[0], arguments[1])

