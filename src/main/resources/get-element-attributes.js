(function(element) {
  const attributesMap = {};

  const attrs = element.attributes;
  for (let i = 0; i < attrs.length; i++) {
    const a = attrs[i];
    if (a.name !== 'style') {
      attributesMap[a.name] = a.value;
    }
  }

  if (element.value) attributesMap['value'] = element.value;
  if (element.type) attributesMap['type'] = element.type;

  return attributesMap;
})(arguments[0])

