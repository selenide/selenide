(function (node) {
  return Array.prototype.filter.call(node.childNodes, function (element) {
    return element.nodeType === Node.TEXT_NODE;
  }).map(function (element) {
    return element.textContent;
  }).join("\n");
})(arguments[0]);
