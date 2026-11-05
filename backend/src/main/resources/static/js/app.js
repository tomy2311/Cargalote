const LS_SESSION = 'cargalocate_session';
const LS_PERSONAL = 'cargalocate_personal';
const LS_PLACES = 'cargalocate_places';

const DEFAULT_PLACES = [
  'Sin asignar', 'Muelle 2 · STS-1', 'Muelle 2 · STS-2', 'Muelle 2 · STS-3',
  'Patio · Bloque A', 'Patio · Bloque B', 'Patio · Bloque C (Reefer)', 'Patio · Bloque D (Vacíos)',
  'Gate 1', 'Gate 2', 'Gate 3', 'Control de Terminal'
];

function getPlaces() {
  const p = JSON.parse(localStorage.getItem(LS_PLACES) || 'null');
  return Array.isArray(p) && p.length ? p : DEFAULT_PLACES;
}

function savePlaces(p) {
  localStorage.setItem(LS_PLACES, JSON.stringify(p));
}

const DEFAULT_PERSONAL = [
  { id: 1, name: 'Laura Vega', role: 'Jefa de Turno', shift: 'Supervisión', place: 'Control de Terminal' },
  { id: 2, name: 'Marco Díaz', role: 'Controlador de patio', shift: '08–14', place: 'Patio · Bloque B' },
  { id: 3, name: 'Elena Ruiz', role: 'Operadora STS', shift: '08–14', place: 'Muelle 2 · STS-2' },
  { id: 4, name: 'Hugo Paredes', role: 'Operador RTG', shift: '14–22', place: 'Patio · Bloque A' },
  { id: 5, name: 'Sara Ibáñez', role: 'Controladora de Gate', shift: '08–14', place: 'Gate 1' },
  { id: 6, name: 'Iván Costa', role: 'Jefe de cuadrilla muelle', shift: '14–22', place: 'Muelle 2 · STS-1' },
  { id: 7, name: 'Ana Soler', role: 'Operadora Reach Stacker', shift: '08–14', place: 'Patio · Bloque D (Vacíos)' },
  { id: 8, name: 'Raúl Montes', role: 'Apuntador de muelle', shift: '08–14', place: 'Muelle 2 · STS-3' },
  { id: 9, name: 'Carmen Luque', role: 'Apuntadora', shift: '14–22', place: 'Patio · Bloque C (Reefer)' },
  { id: 10, name: 'Tomás Prieto', role: 'Apuntador', shift: '08–14', place: 'Gate 2' }
];

function getPersonal() {
  const p = JSON.parse(localStorage.getItem(LS_PERSONAL) || 'null');

  if (Array.isArray(p) && p.length) {
    return p;
  }

  localStorage.setItem(LS_PERSONAL, JSON.stringify(DEFAULT_PERSONAL));
  return DEFAULT_PERSONAL.slice();
}

function savePersonal(p) {
  localStorage.setItem(LS_PERSONAL, JSON.stringify(p));
}

function renderPlacesSelect(el, selected) {
  if (!el) return;

  el.innerHTML = getPlaces()
    .map(p =>
      '<option' +
      (p === selected ? ' selected' : '') +
      '>' +
      p +
      '</option>'
    )
    .join('');
}

function renderPersonal() {
  const grid = document.getElementById('crewGrid');

  if (!grid) return;

  const list = getPersonal();

  grid.innerHTML = list.map(p => {
    const on = p.shift === '08–14' || p.shift === 'Supervisión';

    return '<div class="crew-row">' +
      '<div class="avatar">' + initials(p.name) + '</div>' +
      '<div class="ci"><b>' + p.name + '</b><span>' +
      p.role + ' · Turno ' + p.shift +
      '</span></div>' +
      '<span class="shift' + (on ? ' on' : '') + '">' +
      p.shift +
      '</span>' +
      '<div class="place-box">' +
      '<span class="pb-label">Lugar de trabajo:</span>' +
      '<select class="sel place-sel" onchange="setPersonPlace(' +
      p.id +
      ', this.value)">' +
      getPlaces()
        .map(x =>
          '<option' +
          (x === p.place ? ' selected' : '') +
          '>' +
          x +
          '</option>'
        )
        .join('') +
      '</select>' +
      '</div>' +
      '<button class="del-person" title="Quitar del turno" onclick="removePerson(' +
      p.id +
      ')">×</button>' +
      '</div>';
  }).join('');

  const assigned = list.filter(
    p => p.place && p.place !== 'Sin asignar'
  ).length;

  const perCount = document.getElementById('perCount');
  const tallyCount = document.getElementById('tallyCount');

  if (perCount) {
    perCount.textContent = list.length;
  }

  if (tallyCount) {
    tallyCount.textContent = assigned;
  }
}

function setPersonPlace(id, place) {
  const list = getPersonal();
  const p = list.find(x => x.id === id);

  if (p) {
    p.place = place;
    savePersonal(list);
    renderPersonal();
  }
}

function addPerson() {
  const nameInput = document.getElementById('pName');
  const roleInput = document.getElementById('pRole');
  const shiftInput = document.getElementById('pShift');
  const placeInput = document.getElementById('pPlace');

  if (!nameInput || !roleInput || !shiftInput || !placeInput) return;

  const name = nameInput.value.trim();
  const role = roleInput.value.trim();
  const shift = shiftInput.value;
  const place = placeInput.value;

  if (!name) {
    return alert('Indica el nombre del personal.');
  }

  const list = getPersonal();
  const id = list.length
    ? Math.max(...list.map(x => x.id)) + 1
    : 1;

  list.push({
    id: id,
    name: name,
    role: role || 'Apuntador',
    shift: shift || '08–14',
    place: place
  });

  savePersonal(list);
  renderPersonal();

  nameInput.value = '';
  roleInput.value = '';
}

function removePerson(id) {
  const list = getPersonal().filter(x => x.id !== id);
  savePersonal(list);
  renderPersonal();
}

/* ================= AUTENTICACIÓN ================= */

function getSession() {
  try {
    return JSON.parse(localStorage.getItem(LS_SESSION) || 'null');
  } catch {
    return null;
  }
}

function saveSession(session) {
  localStorage.setItem(LS_SESSION, JSON.stringify(session));
}

function clearSession() {
  localStorage.removeItem(LS_SESSION);
}

function showAuth(card) {
  const loginCard = document.getElementById('loginCard');
  const registerCard = document.getElementById('registerCard');

  if (!loginCard || !registerCard) return;

  loginCard.classList.toggle('hidden', card !== 'loginCard');
  registerCard.classList.toggle('hidden', card !== 'registerCard');

  clearMsg();
}

function clearMsg() {
  ['loginMsg', 'regMsg'].forEach(id => {
    const el = document.getElementById(id);

    if (!el) return;

    el.className = 'auth-msg';
    el.textContent = '';
  });
}

function setMsg(id, text, ok = false) {
  const el = document.getElementById(id);

  if (!el) return;

  el.className = 'auth-msg ' + (ok ? 'ok' : 'error');
  el.textContent = text;
}

async function apiRequest(endpoint, options = {}) {
  const response = await fetch('/api' + endpoint, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers || {})
    }
  });

  const contentType = response.headers.get('content-type') || '';

  const data = contentType.includes('application/json')
    ? await response.json()
    : await response.text();

  if (!response.ok) {
    const message =
      typeof data === 'object' && data !== null && data.message
        ? data.message
        : 'Error en la comunicación con el servidor.';

    throw new Error(message);
  }

  return data;
}

async function doRegister() {
  const name = document.getElementById('regName').value.trim();
  const email = document.getElementById('regEmail').value.trim().toLowerCase();
  const user = document.getElementById('regUser').value.trim().toLowerCase();
  const pass = document.getElementById('regPass').value;
  const pass2 = document.getElementById('regPass2').value;

  if (!name || !email || !user || !pass) {
    return setMsg('regMsg', 'Completa todos los campos.');
  }

  if (!/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(email)) {
    return setMsg('regMsg', 'Correo electrónico no válido.');
  }

  if (pass.length < 6) {
    return setMsg('regMsg', 'La contraseña debe tener al menos 6 caracteres.');
  }

  if (pass !== pass2) {
    return setMsg('regMsg', 'Las contraseñas no coinciden.');
  }

  try {
    const data = await apiRequest('/auth/register', {
      method: 'POST',
      body: JSON.stringify({
        name: name,
        email: email,
        user: user,
        password: pass
      })
    });

    setMsg(
      'regMsg',
      data.message || 'Cuenta creada correctamente. Ya puedes iniciar sesión.',
      true
    );

    setTimeout(() => {
      showAuth('loginCard');
    }, 1200);

  } catch (error) {
    setMsg(
      'regMsg',
      error.message || 'No se pudo registrar el usuario.'
    );
  }
}

async function doLogin() {
  const id = document.getElementById('loginUser').value.trim().toLowerCase();
  const pass = document.getElementById('loginPass').value;

  if (!id || !pass) {
    return setMsg('loginMsg', 'Completa usuario y contraseña.');
  }

  try {
    const data = await apiRequest('/auth/login', {
      method: 'POST',
      body: JSON.stringify({
        user: id,
        password: pass
      })
    });

    const user = data.user;

    saveSession({
      user: user.user,
      name: user.name,
      email: user.email,
      role: user.role,
      ts: Date.now()
    });

    enterApp(user);

  } catch (error) {
    setMsg(
      'loginMsg',
      error.message || 'No se pudo iniciar sesión.'
    );
  }
}

function doLogout() {
  clearSession();
  location.reload();
}

/* ================= UTILIDADES ================= */

function initials(name) {
  if (!name) return '--';

  return name
    .split(' ')
    .map(w => w[0])
    .join('')
    .slice(0, 2)
    .toUpperCase();
}

function enterApp(u) {
  document.getElementById('authWrapper').classList.add('hidden');
  document.getElementById('app').classList.remove('hidden');

  document.getElementById('userName').textContent = u.name;
  document.getElementById('userAv').textContent = initials(u.name);
  document.getElementById('userRole').textContent = u.role || 'Jefe de Turno';

  startClock();
  startTelemetry();
  drawChart();
}

window.addEventListener('keydown', e => {
  if (e.key !== 'Enter') return;

  const app = document.getElementById('app');
  const reg = document.getElementById('registerCard');

  if (!app || !reg) return;

  if (app.classList.contains('hidden')) {
    if (reg.classList.contains('hidden')) {
      doLogin();
    } else {
      doRegister();
    }
  }
});

(function () {
  const sess = getSession();

  if (sess) {
    enterApp({
      user: sess.user,
      name: sess.name,
      email: sess.email,
      role: sess.role
    });
  }
})();

function startClock() {
  const t = document.getElementById('clockTime');
  const d = document.getElementById('clockDate');

  if (!t || !d) return;

  const tick = () => {
    const now = new Date();

    t.textContent = now.toLocaleTimeString('es-AR', {
      hour12: false
    });

    d.textContent = now.toLocaleDateString('es-AR', {
      weekday: 'long',
      day: 'numeric',
      month: 'long',
      year: 'numeric'
    });
  };

  tick();
  setInterval(tick, 1000);
}

const titles = {
  'sec-general': 'Panel General',
  'sec-muelle': 'Muelle',
  'sec-patio': 'Patio & Yard',
  'sec-equipos': 'Equipos',
  'sec-incidencias': 'Incidencias',
  'sec-turnos': 'Turnos',
  'sec-bitacora': 'Bitácora'
};

function goSection(btn) {
  if (!btn) return;

  document.querySelectorAll('.nav button')
    .forEach(b => b.classList.remove('active'));

  btn.classList.add('active');

  document.querySelectorAll('.section')
    .forEach(s => s.classList.remove('active'));

  const section = document.getElementById(btn.dataset.sec);

  if (section) {
    section.classList.add('active');
  }

  const pageTitle = document.getElementById('pageTitle');

  if (pageTitle) {
    pageTitle.textContent = titles[btn.dataset.sec];
  }
}

function startTelemetry() {
  const prod = document.getElementById('kpiProd');
  const gSts = document.getElementById('gSts');
  const gRtg = document.getElementById('gRtg');
  const gGates = document.getElementById('gGates');

  if (!prod || !gSts || !gRtg || !gGates) return;

  let prodV = 34.2;

  setInterval(() => {
    prodV += (Math.random() - 0.5) * 0.8;
    prodV = Math.max(28, Math.min(42, prodV));

    prod.textContent = prodV.toFixed(1);

    gSts.textContent = 7 + (Math.random() < 0.08 ? -1 : 0);
    gRtg.textContent = 9 + (Math.random() < 0.08 ? -1 : 0);
    gGates.textContent = 3 + (Math.random() < 0.1 ? -1 : 0);

    pushChart(prodV);
  }, 1000);
}

const chartData = Array(30).fill(34);

function drawChart() {
  const cv = document.getElementById('chartMain');

  if (!cv) return;

  const ctx = cv.getContext('2d');

  if (!ctx) return;

  const w = cv.clientWidth;
  const h = cv.clientHeight;

  if (w <= 0 || h <= 0) return;

  cv.width = w;
  cv.height = h;

  const pad = 30;

  ctx.clearRect(0, 0, w, h);

  ctx.strokeStyle = '#2a3250';
  ctx.lineWidth = 1;

  for (let i = 0; i <= 4; i++) {
    const y = pad + (h - pad * 2) * i / 4;

    ctx.beginPath();
    ctx.moveTo(pad, y);
    ctx.lineTo(w - 8, y);
    ctx.stroke();

    ctx.fillStyle = '#9aa7c4';
    ctx.font = '11px Segoe UI';
    ctx.textAlign = 'right';

    ctx.fillText(
      Math.round(50 - i * 10) + ' m/h',
      pad - 8,
      y + 4
    );
  }

  ctx.beginPath();

  chartData.forEach((v, i) => {
    const x = pad + (w - pad - 8) * i / (chartData.length - 1);
    const y = pad + (h - pad * 2) * (1 - (v - 0) / 50);

    if (i === 0) {
      ctx.moveTo(x, y);
    } else {
      ctx.lineTo(x, y);
    }
  });

  ctx.strokeStyle = '#ff8c1a';
  ctx.lineWidth = 2.5;
  ctx.lineJoin = 'round';
  ctx.stroke();

  const grad = ctx.createLinearGradient(0, 0, 0, h - pad);

  grad.addColorStop(0, 'rgba(47,129,247,.35)');
  grad.addColorStop(1, 'rgba(47,129,247,0)');

  ctx.lineTo(
    pad + (w - pad - 8),
    h - pad
  );

  ctx.lineTo(
    pad,
    h - pad
  );

  ctx.closePath();

  ctx.fillStyle = grad;
  ctx.fill();
}

function pushChart(v) {
  chartData.push(v);
  chartData.shift();
  drawChart();
}

window.addEventListener('resize', () => {
  const app = document.getElementById('app');

  if (app && !app.classList.contains('hidden')) {
    drawChart();
  }
});

const seedLogs = [
  ['13:05', 'CRÍTICA', 'Avería eléctrica en grúa STS-3 · muelle 2 detenido temporalmente.'],
  ['11:47', 'INFO', 'Reubicación de 24 contenedores reefer al bloque C completada.'],
  ['10:31', 'INFO', 'Gate 2 congestionado · se habilitan 2 carriles adicionales de inspección.'],
  ['09:20', 'AVISO', 'Reefer TRLU-88214 con temperatura 6.8 °C · contenedor reubicado.'],
  ['06:58', 'INFO', 'Inspección PSC del M/V Cargolocate Express completada sin hallazgos.'],
  ['03:12', 'INFO', 'Ronda nocturna de patio OK · 41 camiones despachados.']
];

function renderLogs() {
  const box = document.getElementById('logView');

  if (!box) return;

  box.innerHTML = seedLogs
    .map(([t, lv, msg]) =>
      '<div>' +
      '<span class="lt">' + t + '</span>' +
      '<span class="lv">[' + lv + ']</span>' +
      '<span>' + msg + '</span>' +
      '</div>'
    )
    .join('');
}

function addLog() {
  const inp = document.getElementById('logInput');

  if (!inp) return;

  const txt = inp.value.trim();

  if (!txt) return;

  const now = new Date();

  const t = now.toLocaleTimeString('es-AR', {
    hour: '2-digit',
    minute: '2-digit',
    hour12: false
  });

  seedLogs.unshift([t, 'INFO', txt]);

  renderLogs();

  inp.value = '';
}

renderLogs();

(function () {
  renderPlacesSelect(
    document.getElementById('pPlace'),
    'Sin asignar'
  );

  renderPersonal();
})();

