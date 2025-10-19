const RE_WHITESPACES = new RegExp('[\\s\\n\\r\u00a0\u202F\u2060\u0020\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200a\u200b\u200c\u200d\u205f\u3000]+', 'g');

function normalizeText(text, options) {
  const trimmed = text.replace(RE_WHITESPACES, ' ').trim();
  switch (options['caseSensitivity']) {
    case 'CASE_SENSITIVE': return trimmed;
    case 'CASE_INSENSITIVE': return trimmed.toLowerCase();
    default: throw new Error('Unknown case sensitivity: ' + options['caseSensitivity']);
  }
}

function extractText(node, options) {
  return node.nodeType === Node.TEXT_NODE ? normalizeText(node.textContent, options) : '';
}

function textMatches(options, nodeText, expectedText) {
  switch (options['textCheck']) {
    case 'FULL_TEXT':
      return nodeText === expectedText;
    case 'PARTIAL_TEXT':
      return nodeText.includes(expectedText);
    default:
      throw new Error('Unknown text check mode: ' + options['textCheck']);
  }
}

function findNodes(root, text, options, limit) {
  const expectedText = normalizeText(text, options);
  const treeWalker = document.createTreeWalker(root, NodeFilter.SHOW_TEXT);
  const nodeList = [];

  while (nodeList.length < limit && treeWalker.nextNode()) {
    const node = treeWalker.currentNode;

    if (node.nodeType === Node.TEXT_NODE) {
      const nodeText = extractText(node, options);
      if (textMatches(options, nodeText, expectedText)) {
        nodeList.push(node.parentNode);
      }
    }
  }

  return nodeList;
}
