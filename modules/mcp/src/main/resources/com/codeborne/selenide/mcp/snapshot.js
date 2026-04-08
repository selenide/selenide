function snapshot(rootSelector, mode, visibleOnly, maxDepth) {
  const INTERACTIVE = new Set(['input','button','a','select','textarea','details','summary']);
  const TEXT = new Set(['h1','h2','h3','h4','h5','h6','p','span','label','td','th','li','dt','dd']);
  let counter = 0;

  function isVisible(el) {
    if (!visibleOnly) return true;
    const rect = el.getBoundingClientRect();
    if (rect.width === 0 && rect.height === 0) return false;
    const style = window.getComputedStyle(el);
    return style.display !== 'none' && style.visibility !== 'hidden';
  }

  function shouldInclude(el, tag) {
    if (mode === 'full') return true;
    if (mode === 'action') return INTERACTIVE.has(tag);
    // assert mode
    if (INTERACTIVE.has(tag)) return true;
    if (TEXT.has(tag) && el.textContent.trim().length > 0) return true;
    if (el.getAttribute('role')) return true;
    return false;
  }

  function selectors(el) {
    const result = [];
    if (el.id) result.push('#' + el.id);
    const tid = el.dataset.testId || el.dataset.testid;
    if (tid) {
      const attr = el.dataset.testId ? 'data-test-id' : 'data-testid';
      result.push('[' + attr + '="' + tid + '"]');
    }
    if (el.name) result.push(el.tagName.toLowerCase() + '[name="' + el.name + '"]');
    const text = el.textContent.trim();
    if (text.length > 0 && text.length < 50) result.push('text=' + text);
    if (result.length === 0) {
      const tag = el.tagName.toLowerCase();
      const cls = Array.from(el.classList).slice(0, 2).join('.');
      result.push(cls ? tag + '.' + cls : tag);
    }
    return result;
  }

  const root = rootSelector ? document.querySelector(rootSelector) : document.body;
  if (!root) return JSON.stringify({error: 'Root not found: ' + rootSelector});

  function buildElementInfo(el, tag) {
    counter++;
    const info = {ref: 'e' + counter, tag: tag, selectors: selectors(el)};
    if (el.id) info.id = el.id;
    if (el.type) info.type = el.type;
    if (el.name) info.name = el.name;
    if (el.placeholder) info.placeholder = el.placeholder;
    const text = el.textContent.trim();
    if (text.length > 0 && text.length < 200) info.text = text;
    if (el.value !== undefined && el.value !== '') info.value = el.value;
    if (el.classList.length > 0) info.classes = Array.from(el.classList);
    info.visible = true;
    info.enabled = !el.disabled;
    return info;
  }

  const elements = [];
  function walk(node, depth) {
    if (maxDepth != null && depth > maxDepth) return;
    for (const el of node.children) {
      const tag = el.tagName.toLowerCase();
      if (!isVisible(el)) continue;
      if (shouldInclude(el, tag)) {
        elements.push(buildElementInfo(el, tag));
      }
      walk(el, depth + 1);
    }
  }
  walk(root, 0);

  return JSON.stringify({
    url: window.location.href,
    title: document.title,
    viewport: {width: window.innerWidth, height: window.innerHeight},
    elementCount: elements.length,
    elements: elements
  });
}
