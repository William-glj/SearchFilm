import { apiClient } from '../core/apiClient';
import type { CinemaAlert } from '../types/entities';

export function renderMyAlerts(): HTMLElement {
  const section = document.createElement('section');
  section.className = 'alerts';
  section.innerHTML = '<h1>Mis alertas</h1><ul id="alert-list"><li>Cargando…</li></ul>';

  const list = section.querySelector<HTMLUListElement>('#alert-list')!;

  apiClient<CinemaAlert[]>('/api/alerts')
    .then((alerts) => {
      if (alerts.length === 0) {
        list.innerHTML = '<li>No tienes alertas</li>';
        return;
      }
      list.innerHTML = alerts
        .map((alert) => `<li>${alert.movieTitle} — ${alert.active ? 'activa' : 'inactiva'}</li>`)
        .join('');
    })
    .catch(() => {
      list.innerHTML = '<li>Error al cargar las alertas</li>';
    });

  return section;
}
