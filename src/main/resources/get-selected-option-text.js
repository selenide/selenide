(function(select) {
  if (select.tagName.toLowerCase() !== 'select') {
    return ['', 'Expected <select>, but received: <' + select.tagName.toLowerCase() + '>']
  }
  if (!select.options) {
    return ['', 'Select has no options']
  }
  return [Array.from(select.selectedOptions).map(function(option) { return option.text }).join(''), null]
})(arguments[0]);

