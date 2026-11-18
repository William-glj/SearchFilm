import type { LoginResponse, Role } from '../types/entities';

const TOKEN_KEY = 'checkticket.token';
const SESSION_KEY = 'checkticket.session';

export interface Session {
  token: string;
  userId: number;
  email: string;
  fullName: string;
  role: Role;
}

export function saveSession(response: LoginResponse): void {
  localStorage.setItem(TOKEN_KEY, response.token);
  localStorage.setItem(
    SESSION_KEY,
    JSON.stringify({
      userId: response.userId,
      email: response.email,
      fullName: response.fullName,
      role: response.role,
    }),
  );
}

export function loadSession(): Session | null {
  const token = localStorage.getItem(TOKEN_KEY);
  const raw = localStorage.getItem(SESSION_KEY);
  if (!token || !raw) return null;
  try {
    const data = JSON.parse(raw);
    return { token, ...data } as Session;
  } catch {
    return null;
  }
}

export function clearSession(): void {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(SESSION_KEY);
}

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY);
}

export function isAuthenticated(): boolean {
  return getToken() !== null;
}

export function getRole(): Role | null {
  return loadSession()?.role ?? null;
}
