import { registerRoute, navigate } from './core/router';
import { renderLogin } from './sections/Login';
import { renderExplore } from './sections/Explore';
import { renderMyAlerts } from './sections/MyAlerts';
import { renderAdmin } from './sections/Admin';
import { renderForbidden } from './sections/Forbidden';

registerRoute({ path: '/login', render: renderLogin });
registerRoute({ path: '/explorar', render: renderExplore });
registerRoute({ path: '/mis-alertas', render: renderMyAlerts, guard: { auth: true } });
registerRoute({ path: '/admin', render: renderAdmin, guard: { auth: true, roles: ['ROLE_ADMIN'] } });
registerRoute({ path: '/403', render: renderForbidden });

navigate(window.location.pathname === '/' ? '/explorar' : window.location.pathname);
