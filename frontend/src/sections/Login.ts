import { apiClient } from '../core/apiClient';
import { saveSession } from '../core/state';
import { navigate } from '../core/router';
import type { LoginRequest, LoginResponse } from '../types/entities';

export function renderLogin(): HTMLElement {
  const section = document.createElement('section');
  section.className = 'login';
  section.innerHTML = `
    <h1>Iniciar sesión</h1>
    <form id="login-form">
      <label>Email <input name="email" type="email" required /></label>
      <label>Contraseña <input name="password" type="password" required /></label>
      <button type="submit">Entrar</button>
      <p id="login-error"></p>
    </form>`;

  section
    .querySelector<HTMLFormElement>('#login-form')!
    .addEventListener('submit', async (event) => {
      event.preventDefault();
      const form = event.currentTarget as HTMLFormElement;
      const data = new FormData(form);
      const payload: LoginRequest = {
        email: String(data.get('email') ?? ''),
        password: String(data.get('password') ?? ''),
      };

      try {
        const response = await apiClient<LoginResponse>('/api/auth/login', {
          method: 'POST',
          body: JSON.stringify(payload),
        });
        saveSession(response);
        navigate(response.role === 'ROLE_ADMIN' ? '/admin' : '/explorar');
      } catch {
        const error = section.querySelector<HTMLParagraphElement>('#login-error');
        if (error) error.textContent = 'Credenciales inválidas';
      }
    });

  return section;
}
