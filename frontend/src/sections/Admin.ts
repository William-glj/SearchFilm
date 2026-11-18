import { apiClient } from '../core/apiClient';

export function renderAdmin(): HTMLElement {
  const section = document.createElement('section');
  section.className = 'admin';
  section.innerHTML = '<h1>Panel de administración</h1><p id="admin-stats">Cargando métricas…</p>';

  const stats = section.querySelector<HTMLParagraphElement>('#admin-stats')!;

  apiClient<Record<string, unknown>>('/api/admin/stats')
    .then((data) => {
      stats.textContent = JSON.stringify(data);
    })
    .catch(() => {
      stats.textContent = 'Error al cargar métricas';
    });

  return section;
}
