const elements = document.querySelectorAll('[data-node-id]');
elements.forEach(el => {
  const id = el.getAttribute('data-node-id');
  const maskedId = id.replace(/:/g, '_');
  el.addEventListener('dblclick', () => {
    console.log(`Navigating to ${maskedId}.html`);
    window.location.href = `${maskedId}.html`;
  });
});
