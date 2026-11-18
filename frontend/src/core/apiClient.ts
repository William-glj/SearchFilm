import { getToken, clearSession } from './state';

// TODO: usar variable de entorno de Vite (import.meta.env.VITE_API_URL)
const BASE_URL = 'http://localhost:8080';

interface ApiOptions extends RequestInit {
  /** Si es false, no añade la cabecera Authorization. */
  auth?: boolean;
}

/**
 * Wrapper centralizado de fetch: inyecta `Authorization: Bearer <token>`
 * y fuerza el cierre de sesión sin recarga ante cualquier 401.
 */
export async function apiClient<T>(path: string, options: ApiOptions = {}): Promise<T> {
  const token = getToken();
  const headers: Record<string, string> = { 'Content-Type': 'application/json' };
  if (token && options.auth !== false) {
    headers.Authorization = `Bearer ${token}`;
  }

  const response = await fetch(`${BASE_URL}${path}`, { ...options, headers });

  if (response.status === 401) {
    clearSession();
    if (window.location.pathname !== '/login') {
      window.history.pushState({}, '', '/login');
      window.dispatchEvent(new PopStateEvent('popstate'));
    }
    throw new Error('Sesión expirada');
  }

  if (!response.ok) {
    throw new Error(`HTTP ${response.status}`);
  }
  if (response.status === 204) {
    return undefined as T;
  }
  return (await response.json()) as T;
}
