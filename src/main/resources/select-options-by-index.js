(function(select, indexes) {
  import 'selects.js'

  function checkMissingOptionsIndexes() {
    const missingOptionsIndexes = indexes.filter(index => !select.options[index]);
    return missingOptionsIndexes.length > 0 ? {optionsNotFound: missingOptionsIndexes} : null
  }

  function checkDisabledOptionsIndexes() {
    const disabledOptionsIndexes = indexes.filter(index => select.options[index].disabled);
    return disabledOptionsIndexes.length > 0 ? {disabledOptions: disabledOptionsIndexes} : null
  }

  return checkState(select) || checkMissingOptionsIndexes() || checkDisabledOptionsIndexes() || applyChanges(select, () => {
    for (let index of indexes) {
      select.options[index].selected = 'selected'
    }
  })
})(arguments[0], arguments[1])

