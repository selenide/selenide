(function (select, texts) {
  if (select.disabled) {
    return {disabledSelect: 'Cannot select option in a disabled select'};
  }

  function optionByText(requestedText) {
    return Array.from(select.options).find(function(option) { return option.text.includes(requestedText) })
  }

  let missingOptionsTexts = texts.filter(function(text) { return !optionByText(text) });
  if (missingOptionsTexts.length > 0) {
    return {optionsNotFound: missingOptionsTexts};
  }

  let disabledOptionsTexts = texts.filter(function(text) { optionByText(text).disabled });
  if (disabledOptionsTexts.length > 0) {
    return {disabledOptions: disabledOptionsTexts};
  }

  texts.forEach(function(requestedPartialText) {
    optionByText(requestedPartialText).selected = 'selected';
  });

  let event = document.createEvent('HTMLEvents');
  event.initEvent('change', true, true);
  select.dispatchEvent(event);

  return {};
})(arguments[0], arguments[1])

