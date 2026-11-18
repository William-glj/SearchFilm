export function renderForbidden(): HTMLElement {
  const section = document.createElement('section');
  section.className = 'forbidden';
  section.innerHTML = '<h1>403</h1><p>No tienes permisos para acceder a esta página</p>';
  return section;
}
