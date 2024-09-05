(function (select, texts) {
  import 'selects.js'

  function optionByText(requestedText) {
    return Array.from(select.options).find(option => option.text === requestedText)
  }

  function checkMissingOptionsTexts() {
    const missingOptionsTexts = texts.filter(text => !optionByText(text));
    return missingOptionsTexts.length > 0 ? {optionsNotFound: missingOptionsTexts} : null
  }

  function checkDisabledOptionsTexts() {
    const disabledOptionsTexts = texts.filter(text => optionByText(text).disabled);
    return disabledOptionsTexts.length > 0 ? {disabledOptions: disabledOptionsTexts} : null
  }

  return checkState(select) || checkMissingOptionsTexts() || checkDisabledOptionsTexts() || applyChanges(select, () => {
    for (let requestedText of texts) {
      optionByText(requestedText).selected = 'selected'
    }
  })
})(arguments[0], arguments[1])

