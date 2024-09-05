(function (select, values) {
  import 'selects.js'

  function checkMissingOptionsValues() {
    const missingOptionsValues = values.filter(value => !optionByValue(select, value))
    return missingOptionsValues.length > 0 ? {optionsNotFound: missingOptionsValues} : null
  }

  function checkDisabledOptionsValues() {
    const disabledOptionsValues = values.filter(value => optionByValue(select, value).disabled);
    return disabledOptionsValues.length > 0 ? {disabledOptions: disabledOptionsValues} : null
  }

  return checkState(select) || checkMissingOptionsValues() || checkDisabledOptionsValues() || applyChanges(select, () => {
    for (let requestedValue of values) {
      optionByValue(select, requestedValue).selected = 'selected'
    }
  })
})(arguments[0], arguments[1])

