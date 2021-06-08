(function () {
  const root = arguments[0] || document.body;
  const text = arguments[1].toLowerCase().replace(/\s+/gi, ' ');
  const limit = arguments[2];

  const treeWalker = document.createTreeWalker(root, NodeFilter.SHOW_TEXT);
  const nodeList = [];

  while (nodeList.length < limit && treeWalker.nextNode()) {
    const node = treeWalker.currentNode;

    if (node.nodeType === Node.TEXT_NODE && node.textContent.toLowerCase().replace(/\s+/gi, ' ') === text) {
      nodeList.push(node.parentNode);
    }
  }

  return nodeList;
})(...arguments)
