import { apiClient } from '../core/apiClient';
import type { Cinema } from '../types/entities';

export function renderExplore(): HTMLElement {
  const section = document.createElement('section');
  section.className = 'explore';
  section.innerHTML = '<h1>Explorar cines</h1><ul id="cinema-list"><li>Cargando…</li></ul>';

  const list = section.querySelector<HTMLUListElement>('#cinema-list')!;

  apiClient<Cinema[]>('/api/cinemas')
    .then((cinemas) => {
      if (cinemas.length === 0) {
        list.innerHTML = '<li>No hay cines disponibles</li>';
        return;
      }
      list.innerHTML = cinemas
        .map((cinema) => `<li>${cinema.name} — ${cinema.city ?? cinema.zone ?? ''}</li>`)
        .join('');
    })
    .catch(() => {
      list.innerHTML = '<li>Error al cargar los cines</li>';
    });

  return section;
}
