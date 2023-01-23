(function (element) {
  const range = document.createRange();
  const text = element.firstChild;
  range.setStart(text, 0);
  range.setEnd(text, text.textContent.length)
  let visible = range.getBoundingClientRect().width;
  for (let i = text.textContent.length - 1; i >= 0; i--) {
    if (visible > range.getBoundingClientRect().width) {
      i = i + 2
      range.setEnd(text,i);
      break
    }
    range.setEnd(text, i);
  }
  return range.toString();
})(arguments[0]);
