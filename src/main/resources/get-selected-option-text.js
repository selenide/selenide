(function(select) {
  if (select.tagName.toLowerCase() !== 'select') {
    return ['', 'Expected <select>, but received: <' + select.tagName.toLowerCase() + '>']
  }
  if (!select.options) {
    return ['', 'Select has no options']
  }
  let option = select.options[select.selectedIndex];
  let text = !option ? null : option?.text;
  return [text, null]
})(arguments[0]);

