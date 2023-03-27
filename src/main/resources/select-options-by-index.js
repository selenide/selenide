(function(select, indexes) {
  if (select.tagName.toLowerCase() !== 'select') {
    return {nonSelect: 'Cannot select option from a non-select element'};
  }
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

  function getSelectedOptionsString(select) {
    return Array.from(select.options).map(option => option.selected).join(",");
  }

  let previousSelectedOptions = getSelectedOptionsString(select);
  for (let index of indexes) {
    select.options[index].selected = 'selected';
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

