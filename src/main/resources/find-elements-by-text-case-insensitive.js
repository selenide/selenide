(function () {
  var root = arguments[0] || document.body;
  var text = arguments[1].toLowerCase().replace(/\s+/gi, ' ');
  var limit = arguments[2];

  var treeWalker = document.createTreeWalker(root, NodeFilter.SHOW_TEXT);
  var nodeList = [];

  while (nodeList.length < limit && treeWalker.nextNode()) {
    var node = treeWalker.currentNode;

    if (node.nodeType === Node.TEXT_NODE && node.textContent.toLowerCase().replace(/\s+/gi, ' ').trim() === text) {
      nodeList.push(node.parentNode);
    }
  }

  return nodeList;
})(...arguments)
