(function () {
  import 'texts.js'

  const root = arguments[0] || document.body;
  const text = arguments[1];
  const options = arguments[2];
  const limit = arguments[3];

  function findElementsByLabel(label) {
    const id = label.getAttribute('for');
    if (id)
      return Array.from(root.querySelectorAll(`[id="${id}"]`));
    else
      return Array.from(label.querySelectorAll(`input,select,textarea`));
  }

  const labels = findNodes(root, text, options, limit);
  return labels.map(label => findElementsByLabel(label)).flat()
})(...arguments)
