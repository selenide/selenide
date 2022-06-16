(() => {
  const fullWidth = Math.max(document.body.scrollWidth, document.documentElement.scrollWidth, document.body.offsetWidth, document.documentElement.offsetWidth, document.body.clientWidth, document.documentElement.clientWidth);
  const fullHeight = Math.max(document.body.scrollHeight, document.documentElement.scrollHeight, document.body.offsetHeight, document.documentElement.offsetHeight, document.body.clientHeight, document.documentElement.clientHeight);
  const viewWidth = window.innerWidth;
  const viewHeight = window.innerHeight;
  const exceedViewport = fullWidth > viewWidth || fullHeight > viewHeight;
  return {fullWidth, fullHeight, exceedViewport};
})()
