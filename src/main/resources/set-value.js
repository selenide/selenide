(function(webelement, text) {
  if (webelement.getAttribute('readonly') != undefined) return 'Cannot change value of readonly element';
  if (webelement.getAttribute('disabled') != undefined) return 'Cannot change value of disabled element';
  webelement.focus();
  var maxlength = webelement.getAttribute('maxlength') == null ? -1 : parseInt(webelement.getAttribute('maxlength'));
  webelement.value = maxlength == -1 ? text : text.length <= maxlength ? text : text.substring(0, maxlength);
  return null;
})(arguments[0], arguments[1]);

