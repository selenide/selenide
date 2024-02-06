(function (element) {
  function isEqual(rect1, rect2) {
    return rect1.x === rect2.x &&
      rect1.y === rect2.y &&
      rect1.width === rect2.width &&
      rect1.height === rect2.height;
  }

  // requestAnimationFrame() calls are paused in most browsers
  // when running in background tabs or hidden <iframe>s,
  // in order to improve performance and battery life.
  if (document.visibilityState === 'hidden') {
    return null;
  }

  // wait for two consecutive frames to make sure there are no animations
  return new Promise((resolve) => {
    window.requestAnimationFrame(() => {
      const rect1 = element.getBoundingClientRect();
      window.requestAnimationFrame(() => {
        const rect2 = element.getBoundingClientRect();
        resolve(!isEqual(rect1, rect2))
      });
    });
  });
})(arguments[0]);
