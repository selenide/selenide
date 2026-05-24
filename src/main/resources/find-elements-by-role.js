(function () {
  import 'texts.js'

  const root = arguments[0] || document.body;
  const expectedRole = arguments[1];
  const expectedName = arguments[2];
  const options = arguments[3];
  const limit = arguments[4];

  const IMPLICIT_ROLES = [
    { role: 'button',       match: el => el.tagName === 'BUTTON' },
    { role: 'link',         match: el => (el.tagName === 'A' || el.tagName === 'AREA') && el.hasAttribute('href') },
    { role: 'checkbox',     match: el => el.tagName === 'INPUT' && el.type === 'checkbox' },
    { role: 'radio',        match: el => el.tagName === 'INPUT' && el.type === 'radio' },
    { role: 'searchbox',    match: el => el.tagName === 'INPUT' && el.type === 'search' },
    { role: 'textbox',      match: el => (el.tagName === 'INPUT' &&
                                          ['text','email','tel','url','password',''].includes(el.type || '')) ||
                                          el.tagName === 'TEXTAREA' },
    { role: 'listbox',      match: el => el.tagName === 'SELECT' && (el.multiple || el.size > 1) },
    { role: 'combobox',     match: el => el.tagName === 'SELECT' },
    { role: 'option',       match: el => el.tagName === 'OPTION' },
    { role: 'heading',      match: el => /^H[1-6]$/.test(el.tagName) },
    { role: 'img',          match: el => el.tagName === 'IMG' && !!el.getAttribute('alt') },
    { role: 'list',         match: el => el.tagName === 'UL' || el.tagName === 'OL' },
    { role: 'listitem',     match: el => el.tagName === 'LI' },
    { role: 'table',        match: el => el.tagName === 'TABLE' },
    { role: 'row',          match: el => el.tagName === 'TR' },
    { role: 'columnheader', match: el => el.tagName === 'TH' && el.getAttribute('scope') === 'col' },
    { role: 'rowheader',    match: el => el.tagName === 'TH' &&
                                          (el.getAttribute('scope') === 'row' || !el.hasAttribute('scope')) },
    { role: 'cell',         match: el => el.tagName === 'TD' },
    { role: 'navigation',   match: el => el.tagName === 'NAV' },
    { role: 'main',         match: el => el.tagName === 'MAIN' },
    { role: 'banner',       match: el => el.tagName === 'HEADER' && !el.closest('article, section, aside, nav') },
    { role: 'contentinfo',  match: el => el.tagName === 'FOOTER' && !el.closest('article, section, aside, nav') },
    { role: 'dialog',       match: el => el.tagName === 'DIALOG' },
    { role: 'form',         match: el => el.tagName === 'FORM' &&
                                          (el.hasAttribute('aria-label') ||
                                           el.hasAttribute('aria-labelledby') ||
                                           el.hasAttribute('name')) }
  ];

  function effectiveRole(el) {
    const explicit = (el.getAttribute('role') || '').trim().split(/\s+/)[0];
    if (explicit) return explicit;
    for (const entry of IMPLICIT_ROLES) {
      if (entry.match(el)) return entry.role;
    }
    return null;
  }

  function accessibleName(el) {
    const ariaLabelledBy = el.getAttribute('aria-labelledby');
    if (ariaLabelledBy) {
      const text = ariaLabelledBy.split(/\s+/)
        .map(id => document.getElementById(id))
        .filter(node => !!node)
        .map(node => node.textContent || '')
        .join(' ').trim();
      if (text) return text;
    }
    const ariaLabel = el.getAttribute('aria-label');
    if (ariaLabel && ariaLabel.trim()) return ariaLabel;

    const tag = el.tagName;
    if (tag === 'INPUT' || tag === 'SELECT' || tag === 'TEXTAREA') {
      const wrapping = el.closest('label');
      if (wrapping && wrapping.textContent.trim()) return wrapping.textContent.trim();
      if (el.id) {
        const labelled = document.querySelector('label[for="' + CSS.escape(el.id) + '"]');
        if (labelled && labelled.textContent.trim()) return labelled.textContent.trim();
      }
    }
    if (tag === 'IMG') {
      const alt = el.getAttribute('alt');
      if (alt) return alt;
    }
    const text = (el.textContent || '').trim();
    if (text) return text;
    const title = el.getAttribute('title');
    if (title) return title;
    return '';
  }

  function nameMatches(el) {
    if (expectedName == null) return true;
    const actual = normalizeText(accessibleName(el), options);
    const expected = normalizeText(expectedName, options);
    return textMatches(options, actual, expected);
  }

  const result = [];
  const candidates = root.querySelectorAll('*');
  for (let i = 0; i < candidates.length && result.length < limit; i++) {
    const el = candidates[i];
    if (effectiveRole(el) === expectedRole && nameMatches(el)) {
      result.push(el);
    }
  }
  return result;
})(...arguments)
