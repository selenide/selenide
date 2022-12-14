(function (select, texts) {
  if (select.disabled) {
    return {disabledSelect: 'Cannot select option in a disabled select'};
  }
  select.focus();

  function optionByText(requestedText) {
    return Array.from(select.options).find(option => option.text === requestedText)
  }

  const missingOptionsTexts = texts.filter(text => !optionByText(text));
  if (missingOptionsTexts.length > 0) {
    return {optionsNotFound: missingOptionsTexts};
  }

  const disabledOptionsTexts = texts.filter(text => optionByText(text).disabled);
  if (disabledOptionsTexts.length > 0) {
    return {disabledOptions: disabledOptionsTexts};
  }

  function getSelectedOptions(select) {
    var result = []
    for (var i = 0; i < select.options.length; i++) {
      if (select.options[i].selected) {
        result.push(i);
      }
    }
    return result;
  }

  var previousSelectedOptions = getSelectedOptions(select);
  for (let requestedText of texts) {
    optionByText(requestedText).selected = 'selected';
  }

  const event = document.createEvent('HTMLEvents');
  event.initEvent('click', true, true);
  select.dispatchEvent(event);
  if (JSON.stringify(getSelectedOptions(select)) != JSON.stringify(previousSelectedOptions)) {
    event.initEvent('change', true, true);
    select.dispatchEvent(event);
  }

  return {};
})(arguments[0], arguments[1])

