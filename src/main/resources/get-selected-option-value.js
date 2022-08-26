(function(select) {
  if (select.tagName.toLowerCase() !== 'select') {
    return ['', `Expected <select>, but received: <${select.tagName.toLowerCase()}>`]
  }
  if (!select.options) {
    return ['', 'Select has no options']
  }
  return [select.options[select.selectedIndex]?.value, null]
})(arguments[0]);

