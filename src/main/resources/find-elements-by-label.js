(function () {
  import 'texts.js'

  function findElementsByLabel(label) {
    const id = label.getAttribute('for');
    if (id)
      return Array.from(document.querySelectorAll(`[id="${id}"]`));
    else
      return Array.from(label.querySelectorAll(`input,select,textarea`));
  }

  // TODO case-(in)sensitive
  // TODO full string match
  const root = arguments[0] || document.body;
  const text = normalizeText(arguments[1]);
  const limit = arguments[2];

  const labels = findNodes(root, limit, (node, nodeText) => nodeText === text);
  return labels.map(label => findElementsByLabel(label)).flat()
/*
  return {
    labels: labels,
    forAttrs: labels.map(label => label.getAttribute('for')),
    forAttrsById: labels.map(label => Array.from(document.querySelectorAll(`[id="${label.getAttribute('for')}"]`))),
    findElementsByLabel: labels.map(label => findElementsByLabel(label)),
    result: labels.map(label => findElementsByLabel(label)).flat()
  };
*/
})(...arguments)
