(function (select, texts) {
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

  const previousSelectedIndex = select.selectedIndex;
  var fireChangeEvent = false;
  for (let requestedPartialText of texts) {
    optionByText(requestedPartialText).selected = 'selected';
    fireChangeEvent = fireChangeEvent || previousSelectedIndex !== select.selectedIndex;
  }

  const event = document.createEvent('HTMLEvents');
  event.initEvent('click', true, true);
  select.dispatchEvent(event);
  if (fireChangeEvent) {
    event.initEvent('change', true, true);
    select.dispatchEvent(event);
  }

  return {};
})(arguments[0], arguments[1])

