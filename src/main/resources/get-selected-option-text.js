(function(select) {
  function arrayFrom(array) {
    //if (String.prototype.contains) { return Array.from(array) }
    var result = [];
    for (var i = 0; i < array.length; i++) {
      result.push(array[i]);
    }
    return result;
  }

  if (select.tagName.toLowerCase() !== 'select') {
    return ['', 'Expected <select>, but received: <' + select.tagName.toLowerCase() + '>']
  }
  if (!select.options) {
    return ['', 'Select has no options']
  }
  return [arrayFrom(select.selectedOptions).map(function(option) { return option.text }).join(''), null]
})(arguments[0]);

