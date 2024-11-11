const RE_WHITESPACES = new RegExp('[\\s\\n\\r\u00a0\u202F\u2060\u0020\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200a\u200b\u200c\u200d\u205f\u3000]+', 'g');

function normalizeText(text) {
  return text.replace(RE_WHITESPACES, ' ').trim().toLowerCase();
}

function extractText(node) {
  return node.nodeType === Node.TEXT_NODE ? normalizeText(node.textContent) : '';
}

function findNodes(root, limit, filter) {
  const treeWalker = document.createTreeWalker(root, NodeFilter.SHOW_TEXT);
  const nodeList = [];

  while (nodeList.length < limit && treeWalker.nextNode()) {
    const node = treeWalker.currentNode;

    if ((node.nodeType === Node.TEXT_NODE) && filter(node, extractText(node))) {
      nodeList.push(node.parentNode);
    }
  }

  return nodeList;
}
