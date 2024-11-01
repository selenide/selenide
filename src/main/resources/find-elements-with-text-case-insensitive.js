(function () {
  import 'texts.js'

  const root = arguments[0] || document.body;
  const text = normalizeText(arguments[1]);
  const limit = arguments[2];

  return findNodes(root, limit, (node, nodeText) => nodeText.includes(text));
})(...arguments)
