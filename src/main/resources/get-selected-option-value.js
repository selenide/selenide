(function(select) {
  if (select.tagName.toLowerCase() !== 'select') {
    return ['', 'Expected <select>, but received: <' + select.tagName.toLowerCase() + '>']
  }
  if (!select.options) {
    return ['', 'Select has no options']
  }
  let option = select.options[select.selectedIndex];
  let value = !option ? null : option.value;
  return [value, null]
})(arguments[0]);

