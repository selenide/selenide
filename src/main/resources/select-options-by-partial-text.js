(function (select, texts) {
  if (select.tagName.toLowerCase() !== 'select') {
    return {nonSelect: 'Cannot select option from a non-select element'};
  }
  if (select.disabled) {
    return {disabledSelect: 'Cannot select option in a disabled select'};
  }
  select.focus();

  function optionByText(requestedText) {
    return Array.from(select.options).find(option => option.text.includes(requestedText))
  }

  const missingOptionsTexts = texts.filter(text => !optionByText(text));
  if (missingOptionsTexts.length > 0) {
    return {optionsNotFound: missingOptionsTexts};
  }

  const disabledOptionsTexts = texts.filter(text => optionByText(text).disabled);
  if (disabledOptionsTexts.length > 0) {
    return {disabledOptions: disabledOptionsTexts};
  }

  function getSelectedOptionsString(select) {
    return Array.from(select.options).map(option => option.selected).join(",");
  }

  let previousSelectedOptions = getSelectedOptionsString(select);
  for (let requestedPartialText of texts) {
    optionByText(requestedPartialText).selected = 'selected';
  }

  const event = document.createEvent('HTMLEvents');
  event.initEvent('click', true, true);
  select.dispatchEvent(event);
  if (getSelectedOptionsString(select) !== previousSelectedOptions) {
    event.initEvent('change', true, true);
    select.dispatchEvent(event);
  }

  return {};
})(arguments[0], arguments[1])

