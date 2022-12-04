(function(element) {
  let attributesMap = {};

  let attrs = element.attributes;
  for (let i = 0; i < attrs.length; i++) {
    let a = attrs[i];
    if (a.name !== 'style') {
      attributesMap[a.name] = a.value;
    }
  }

  if (element.value) attributesMap['value'] = element.value;
  if (element.type) attributesMap['type'] = element.type;

  return attributesMap;
})(arguments[0])

