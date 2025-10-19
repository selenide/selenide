(function () {
  import 'texts.js'

  const root = arguments[0] || document.body;
  const text = arguments[1];
  const options = arguments[2];
  const limit = arguments[3];

  return findNodes(root, text, options, limit);
})(...arguments)
