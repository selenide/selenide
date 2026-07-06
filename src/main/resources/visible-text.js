(function (element) {
  const fullText = element.innerText;
  if (!fullText) {
    return '';
  }

  const clone = element.cloneNode(true);
  clone.style.position = 'absolute';
  clone.style.visibility = 'hidden';
  clone.style.left = '-9999px';
  clone.style.top = '0';
  clone.style.overflow = 'visible';
  clone.style.textOverflow = 'clip';
  clone.style.maxWidth = 'none';
  clone.style.width = 'auto';
  clone.style.height = 'auto';
  clone.style.whiteSpace = 'nowrap';
  document.body.appendChild(clone);

  const availableWidth = element.getBoundingClientRect().width;

  function measureTextWidth(text) {
    clone.textContent = text;
    return clone.getBoundingClientRect().width;
  }

  if (measureTextWidth(fullText) <= availableWidth + 0.5) {
    clone.remove();
    return fullText;
  }

  let low = 0;
  let high = fullText.length;
  let visibleLength = 0;

  while (low <= high) {
    const mid = Math.floor((low + high) / 2);
    if (measureTextWidth(fullText.substring(0, mid)) <= availableWidth + 0.5) {
      visibleLength = mid;
      low = mid + 1;
    } else {
      high = mid - 1;
    }
  }

  clone.remove();
  return fullText.substring(0, visibleLength);
})(arguments[0]);
