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

  select.dispatchEvent(new Event('click'));

  if (getSelectedOptionsString(select) !== previousSelectedOptions) {
    select.dispatchEvent(new Event('input'));
    select.dispatchEvent(new Event('change'));
  }

  return {};
})(arguments[0], arguments[1])

