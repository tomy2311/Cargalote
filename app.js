const LS_USERS = 'cargolocate_users';
const LS_SESSION = 'cargolocate_session';
const LS_PERSONAL = 'cargolocate_personal';
const LS_PLACES = 'cargolocate_places';

const DEFAULT_PLACES = [
  'Sin asignar', 'Muelle 2 · STS-1', 'Muelle 2 · STS-2', 'Muelle 2 · STS-3',
  'Patio · Bloque A', 'Patio · Bloque B', 'Patio · Bloque C (Reefer)', 'Patio · Bloque D (Vacíos)',
  'Gate 1', 'Gate 2', 'Gate 3', 'Control de Terminal'
];

function getPlaces() {
  const p = JSON.parse(localStorage.getItem(LS_PLACES) || 'null');
  return Array.isArray(p) && p.length ? p : DEFAULT_PLACES;
}
function savePlaces(p) { localStorage.setItem(LS_PLACES, JSON.stringify(p)); }

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
  if (Array.isArray(p) && p.length) return p;
  localStorage.setItem(LS_PERSONAL, JSON.stringify(DEFAULT_PERSONAL));
  return DEFAULT_PERSONAL.slice();
}
function savePersonal(p) { localStorage.setItem(LS_PERSONAL, JSON.stringify(p)); }

function renderPlacesSelect(el, selected) {
  el.innerHTML = getPlaces().map(p => '<option' + (p === selected ? ' selected' : '') + '>' + p + '</option>').join('');
}

function renderPersonal() {
  const grid = document.getElementById('crewGrid');
  if (!grid) return;
  const list = getPersonal();
  grid.innerHTML = list.map(p => {
    const on = p.shift === '08–14' || p.shift === 'Supervisión';
    return '<div class="crew-row">' +
      '<div class="avatar">' + initials(p.name) + '</div>' +
      '<div class="ci"><b>' + p.name + '</b><span>' + p.role + ' · Turno ' + p.shift + '</span></div>' +
      '<span class="shift' + (on ? ' on' : '') + '">' + p.shift + '</span>' +
      '<div class="place-box"><span class="pb-label">Lugar de trabajo:</span>' +
      '<select class="sel place-sel" onchange="setPersonPlace(' + p.id + ', this.value)">' +
      getPlaces().map(x => '<option' + (x === p.place ? ' selected' : '') + '>' + x + '</option>').join('') +
      '</select></div>' +
      '<button class="del-person" title="Quitar del turno" onclick="removePerson(' + p.id + ')">×</button>' +
      '</div>';
  }).join('');

  const assigned = list.filter(p => p.place && p.place !== 'Sin asignar').length;
  document.getElementById('perCount').textContent = list.length;
  document.getElementById('tallyCount').textContent = assigned;
}

function setPersonPlace(id, place) {
  const list = getPersonal();
  const p = list.find(x => x.id === id);
  if (p) { p.place = place; savePersonal(list); renderPersonal(); }
}

function addPerson() {
  const name = document.getElementById('pName').value.trim();
  const role = document.getElementById('pRole').value.trim();
  const shift = document.getElementById('pShift').value;
  const place = document.getElementById('pPlace').value;
  if (!name) return alert('Indica el nombre del personal.');
  const list = getPersonal();
  const id = list.length ? Math.max(...list.map(x => x.id)) + 1 : 1;
  list.push({ id: id, name: name, role: role || 'Apuntador', shift: shift || '08–14', place: place });
  savePersonal(list);
  renderPersonal();
  document.getElementById('pName').value = '';
  document.getElementById('pRole').value = '';
}

function removePerson(id) {
  const list = getPersonal().filter(x => x.id !== id);
  savePersonal(list);
  renderPersonal();
}

function getUsers() { return JSON.parse(localStorage.getItem(LS_USERS) || '[]'); }
function saveUsers(u) { localStorage.setItem(LS_USERS, JSON.stringify(u)); }
function getSession() { return JSON.parse(localStorage.getItem(LS_SESSION) || 'null'); }

function showAuth(card) {
  document.getElementById('loginCard').classList.toggle('hidden', card !== 'loginCard');
  document.getElementById('registerCard').classList.toggle('hidden', card !== 'registerCard');
  clearMsg();
}

function clearMsg() {
  ['loginMsg','regMsg'].forEach(id => { const el = document.getElementById(id); el.className = 'auth-msg'; el.textContent = ''; });
}

function setMsg(id, text, ok) {
  const el = document.getElementById(id);
  el.className = 'auth-msg ' + (ok ? 'ok' : 'error');
  el.textContent = text;
}

function doRegister() {
  const name = document.getElementById('regName').value.trim();
  const email = document.getElementById('regEmail').value.trim().toLowerCase();
  const user = document.getElementById('regUser').value.trim().toLowerCase();
  const pass = document.getElementById('regPass').value;
  const pass2 = document.getElementById('regPass2').value;

  if (!name || !email || !user || !pass) return setMsg('regMsg', 'Completa todos los campos.');
  if (!/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(email)) return setMsg('regMsg', 'Correo electrónico no válido.');
  if (pass.length < 6) return setMsg('regMsg', 'La contraseña debe tener al menos 6 caracteres.');
  if (pass !== pass2) return setMsg('regMsg', 'Las contraseñas no coinciden.');

  const users = getUsers();
  if (users.some(u => u.user === user || u.email === email)) return setMsg('regMsg', 'El usuario o correo ya está registrado.');

  users.push({ name: name, email: email, user: user, pass: pass, role: 'Jefe de Turno' });
  saveUsers(users);
  setMsg('regMsg', 'Cuenta creada correctamente. Ya puedes iniciar sesión.', true);
  setTimeout(() => showAuth('loginCard'), 1200);
}

function doLogin() {
  const id = document.getElementById('loginUser').value.trim().toLowerCase();
  const pass = document.getElementById('loginPass').value;
  const users = getUsers();

  if (users.length === 0) {
    return setMsg('loginMsg', 'No hay usuarios registrados. Crea una cuenta primero.');
  }

  const u = users.find(x => (x.user === id || x.email === id) && x.pass === pass);
  if (!u) return setMsg('loginMsg', 'Credenciales incorrectas. Verifica usuario y contraseña.');

  localStorage.setItem(LS_SESSION, JSON.stringify({ user: u.user, name: u.name, email: u.email, ts: Date.now() }));
  enterApp(u);
}

function doLogout() {
  localStorage.removeItem(LS_SESSION);
  location.reload();
}

function initials(name) {
  return name.split(' ').map(w => w[0]).join('').slice(0, 2).toUpperCase();
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
  if (document.getElementById('app').classList.contains('hidden')) {
    const reg = document.getElementById('registerCard');
    if (reg.classList.contains('hidden')) { doLogin(); } else { doRegister(); }
  }
});

(function () {
  const sess = getSession();
  if (sess) enterApp({ name: sess.name });
})();

function startClock() {
  const t = document.getElementById('clockTime');
  const d = document.getElementById('clockDate');
  const tick = () => {
    const now = new Date();
    t.textContent = now.toLocaleTimeString('es-ES', { hour12: false });
    d.textContent = now.toLocaleDateString('es-ES', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' });
  };
  tick();
  setInterval(tick, 1000);
}

const titles = {
  'sec-general': 'Panel General', 'sec-muelle': 'Muelle', 'sec-patio': 'Patio & Yard',
  'sec-equipos': 'Equipos', 'sec-incidencias': 'Incidencias', 'sec-turnos': 'Turnos', 'sec-bitacora': 'Bitácora'
};

function goSection(btn) {
  document.querySelectorAll('.nav button').forEach(b => b.classList.remove('active'));
  btn.classList.add('active');
  document.querySelectorAll('.section').forEach(s => s.classList.remove('active'));
  document.getElementById(btn.dataset.sec).classList.add('active');
  document.getElementById('pageTitle').textContent = titles[btn.dataset.sec];
}

function startTelemetry() {
  const prod = document.getElementById('kpiProd');
  const gSts = document.getElementById('gSts'), gRtg = document.getElementById('gRtg'), gGates = document.getElementById('gGates');
  let prodV = 34.2;

  setInterval(() => {
    prodV += (Math.random() - 0.5) * 0.8; prodV = Math.max(28, Math.min(42, prodV));
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
  const w = cv.clientWidth, h = cv.clientHeight;
  cv.width = w; cv.height = h;

  const pad = 30;
  ctx.clearRect(0, 0, w, h);
  ctx.strokeStyle = '#2a3250'; ctx.lineWidth = 1;
  for (let i = 0; i <= 4; i++) {
    const y = pad + (h - pad * 2) * i / 4;
    ctx.beginPath(); ctx.moveTo(pad, y); ctx.lineTo(w - 8, y); ctx.stroke();
    ctx.fillStyle = '#9aa7c4'; ctx.font = '11px Segoe UI'; ctx.textAlign = 'right';
    ctx.fillText(Math.round(50 - i * 10) + ' m/h', pad - 8, y + 4);
  }

  ctx.beginPath();
  chartData.forEach((v, i) => {
    const x = pad + (w - pad - 8) * i / (chartData.length - 1);
    const y = pad + (h - pad * 2) * (1 - (v - 0) / 50);
    i === 0 ? ctx.moveTo(x, y) : ctx.lineTo(x, y);
  });
  ctx.strokeStyle = '#ff8c1a'; ctx.lineWidth = 2.5; ctx.lineJoin = 'round';
  ctx.stroke();

  const grad = ctx.createLinearGradient(0, 0, 0, h - pad);
  grad.addColorStop(0, 'rgba(47,129,247,.35)');
  grad.addColorStop(1, 'rgba(47,129,247,0)');
  ctx.lineTo(pad + (w - pad - 8), h - pad); ctx.lineTo(pad, h - pad); ctx.closePath();
  ctx.fillStyle = grad; ctx.fill();
}

function pushChart(v) {
  chartData.push(v); chartData.shift();
  drawChart();
}

window.addEventListener('resize', () => {
  if (!document.getElementById('app').classList.contains('hidden')) drawChart();
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
  box.innerHTML = seedLogs.map(([t, lv, msg]) => '<div><span class="lt">' + t + '</span><span class="lv">[' + lv + ']</span><span>' + msg + '</span></div>').join('');
}

function addLog() {
  const inp = document.getElementById('logInput');
  const txt = inp.value.trim();
  if (!txt) return;
  const now = new Date();
  const t = now.toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit', hour12: false });
  seedLogs.unshift([t, 'INFO', txt]);
  renderLogs();
  inp.value = '';
}

renderLogs();

(function () {
  renderPlacesSelect(document.getElementById('pPlace'), 'Sin asignar');
  renderPersonal();
})();