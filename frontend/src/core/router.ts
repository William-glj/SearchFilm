import { isAuthenticated, getRole } from './state';

export interface RouteGuard {
  auth?: boolean;
  roles?: string[];
}

export interface Route {
  path: string;
  render: () => HTMLElement;
  guard?: RouteGuard;
}

const routes: Route[] = [];

function outlet(): HTMLElement {
  const app = document.getElementById('app');
  if (!app) throw new Error('No se encontró el elemento #app');
  return app;
}

export function registerRoute(route: Route): void {
  routes.push(route);
}

function findRoute(path: string): Route | undefined {
  const normalized = path === '/' ? '/' : path.replace(/\/+$/, '');
  return routes.find((route) => route.path === normalized);
}

function mount(view: HTMLElement): void {
  outlet().replaceChildren(view);
}

function renderNotFound(): HTMLElement {
  const el = document.createElement('main');
  el.innerHTML = '<h1>404</h1><p>Página no encontrada</p>';
  return el;
}

/** Redirige y monta la vista destino (sin re-entrar al bucle de guardas). */
function redirect(path: string): void {
  const route = findRoute(path);
  window.history.pushState({}, '', path);
  mount(route ? route.render() : renderNotFound());
}

function passesGuard(route: Route): boolean {
  const guard = route.guard;
  if (!guard) return true;

  // Rutas privadas: sin token -> /login
  if (guard.auth && !isAuthenticated()) {
    redirect('/login');
    return false;
  }

  // Restricción por rol: ROLE_USER en /admin -> vista 403
  if (guard.roles && guard.roles.length > 0) {
    const role = getRole();
    if (!role || !guard.roles.includes(role)) {
      redirect('/403');
      return false;
    }
  }
  return true;
}

function resolve(path: string): void {
  const route = findRoute(path);
  if (!route) {
    window.history.pushState({}, '', path);
    mount(renderNotFound());
    return;
  }
  if (!passesGuard(route)) return;
  window.history.pushState({}, '', path);
  mount(route.render());
}

export function navigate(path: string): void {
  resolve(path);
}

window.addEventListener('popstate', () => {
  resolve(window.location.pathname);
});
