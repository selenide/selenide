function checkState(select) {
  if (select.tagName.toLowerCase() !== 'select') return {'nonSelect': 'Cannot select option from a non-select element'}
  if (select.disabled) return {'disabledSelect': 'Cannot select option in a disabled select'}
  return null
}

function getSelectedOptionsString(select) {
  return Array.from(select.options).map(option => option.selected).join(",")
}

function optionByValue(select, requestedValue) {
  return Array.from(select.options).find(option => option.value === requestedValue)
}

function applyChanges(select, changes) {
  let previousSelectedOptions = getSelectedOptionsString(select)

  select.focus()
  changes(select)

  select.dispatchEvent(new Event('click', {bubbles: true, cancelable: true}));
  if (getSelectedOptionsString(select) !== previousSelectedOptions) {
    select.dispatchEvent(new Event('input', {bubbles: true, cancelable: true}))
    select.dispatchEvent(new Event('change', {bubbles: true, cancelable: true}))
  }
  return {};
}
