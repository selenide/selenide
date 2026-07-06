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
  document.body.appendChild(clone);

  const availableWidth = element.getBoundingClientRect().width;
  const fullWidth = clone.getBoundingClientRect().width;

  if (fullWidth <= availableWidth + 0.5) {
    document.body.removeChild(clone);
    return fullText;
  }

  const sourceTextNodes = [];
  const cloneTextNodes = [];
  const walker = document.createTreeWalker(element, NodeFilter.SHOW_TEXT, null);
  const cloneWalker = document.createTreeWalker(clone, NodeFilter.SHOW_TEXT, null);
  while (walker.nextNode()) {
    sourceTextNodes.push(walker.currentNode);
    cloneWalker.nextNode();
    cloneTextNodes.push(cloneWalker.currentNode);
  }

  const originalText = sourceTextNodes.map(node => node.textContent).join('');

  function setCloneTextLength(length) {
    let remaining = length;
    for (let index = 0; index < cloneTextNodes.length; index++) {
      const nodeText = sourceTextNodes[index].textContent;
      if (remaining >= nodeText.length) {
        cloneTextNodes[index].textContent = nodeText;
        remaining -= nodeText.length;
      } else {
        cloneTextNodes[index].textContent = nodeText.substring(0, remaining);
        for (let clearIndex = index + 1; clearIndex < cloneTextNodes.length; clearIndex++) {
          cloneTextNodes[clearIndex].textContent = '';
        }
        return;
      }
    }
  }

  let low = 0;
  let high = originalText.length;
  let visibleLength = 0;

  while (low <= high) {
    const mid = Math.floor((low + high) / 2);
    setCloneTextLength(mid);
    const width = clone.getBoundingClientRect().width;
    if (width <= availableWidth + 0.5) {
      visibleLength = mid;
      low = mid + 1;
    } else {
      high = mid - 1;
    }
  }

  document.body.removeChild(clone);
  return originalText.substring(0, visibleLength).trim();
})(arguments[0]);
