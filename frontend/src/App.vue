<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'

// Estado de acceso local de demostración.
// Más adelante se reemplazará por Firebase Authentication.
const authMode = ref('login')
const isLoggedIn = ref(false)
const currentUser = ref(null)

const loginUser = ref('')
const loginPass = ref('')

const regName = ref('')
const regEmail = ref('')
const regUser = ref('')
const regPass = ref('')
const regPass2 = ref('')

const loginMsg = ref({ text: '', type: '' })
const regMsg = ref({ text: '', type: '' })

const activeSection = ref('sec-general')
const currentTime = ref('')
const currentDate = ref('')

const sections = [
  { id: 'sec-general', title: 'Panel General', icon: '📊' },
  { id: 'sec-muelle', title: 'Muelle', icon: '🚢' },
  { id: 'sec-patio', title: 'Patio & Yard', icon: '🗄️' },
  { id: 'sec-equipos', title: 'Equipos', icon: '🏗️' },
  { id: 'sec-incidencias', title: 'Incidencias', icon: '🚨' },
  { id: 'sec-turnos', title: 'Turnos', icon: '👷' },
  { id: 'sec-bitacora', title: 'Bitácora', icon: '📖' }
]

const pageTitle = computed(() => {
  return sections.find(section => section.id === activeSection.value)?.title
    ?? 'Panel General'
})

let clockInterval = null

function getUsers() {
  try {
    const users = JSON.parse(localStorage.getItem('cargolocate_users') || '[]')
    return Array.isArray(users) ? users : []
  } catch {
    return []
  }
}

function saveUsers(users) {
  localStorage.setItem('cargolocate_users', JSON.stringify(users))
}

function setMessage(messageRef, text, type = 'error') {
  messageRef.value = { text, type }
}

function showAuth(mode) {
  authMode.value = mode
  loginMsg.value = { text: '', type: '' }
  regMsg.value = { text: '', type: '' }
}

function doRegister() {
  const name = regName.value.trim()
  const email = regEmail.value.trim().toLowerCase()
  const user = regUser.value.trim().toLowerCase()
  const pass = regPass.value

  if (!name || !email || !user || !pass || !regPass2.value) {
    setMessage(regMsg, 'Completa todos los campos.')
    return
  }

  if (!/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(email)) {
    setMessage(regMsg, 'Correo electrónico no válido.')
    return
  }

  if (pass.length < 6) {
    setMessage(regMsg, 'La contraseña debe tener al menos 6 caracteres.')
    return
  }

  if (pass !== regPass2.value) {
    setMessage(regMsg, 'Las contraseñas no coinciden.')
    return
  }

  const users = getUsers()

  if (users.some(item => item.user === user || item.email === email)) {
    setMessage(regMsg, 'El usuario o correo ya está registrado.')
    return
  }

  users.push({
    name,
    email,
    user,
    pass,
    role: 'Jefe de Turno'
  })

  saveUsers(users)
  setMessage(regMsg, 'Cuenta creada correctamente. Ya puedes iniciar sesión.', 'ok')

  regName.value = ''
  regEmail.value = ''
  regUser.value = ''
  regPass.value = ''
  regPass2.value = ''
}

function doLogin() {
  const identifier = loginUser.value.trim().toLowerCase()
  const password = loginPass.value
  const users = getUsers()

  if (!users.length) {
    setMessage(loginMsg, 'No hay usuarios registrados. Crea una cuenta primero.')
    return
  }

  const user = users.find(item =>
    (item.user === identifier || item.email === identifier) &&
    item.pass === password
  )

  if (!user) {
    setMessage(loginMsg, 'Credenciales incorrectas. Verifica usuario y contraseña.')
    return
  }

  const session = {
    user: user.user,
    name: user.name,
    email: user.email,
    role: user.role || 'Jefe de Turno',
    ts: Date.now()
  }

  localStorage.setItem('cargolocate_session', JSON.stringify(session))
  loginPass.value = ''
  enterApp(session)
}

function enterApp(user) {
  currentUser.value = user
  isLoggedIn.value = true
  updateClock()
}

function doLogout() {
  localStorage.removeItem('cargolocate_session')
  currentUser.value = null
  isLoggedIn.value = false
  activeSection.value = 'sec-general'
  loginUser.value = ''
  loginPass.value = ''
  showAuth('login')
}

function goSection(sectionId) {
  activeSection.value = sectionId
}

function initials(name = '') {
  return name
    .split(' ')
    .filter(Boolean)
    .map(word => word[0])
    .join('')
    .slice(0, 2)
    .toUpperCase()
}

function updateClock() {
  const now = new Date()

  currentTime.value = now.toLocaleTimeString('es-ES', {
    hour12: false
  })

  currentDate.value = now.toLocaleDateString('es-ES', {
    weekday: 'long',
    day: 'numeric',
    month: 'long',
    year: 'numeric'
  })
}

onMounted(() => {
  const savedSession = localStorage.getItem('cargolocate_session')

  if (savedSession) {
    try {
      enterApp(JSON.parse(savedSession))
    } catch {
      localStorage.removeItem('cargolocate_session')
    }
  }

  updateClock()
  clockInterval = setInterval(updateClock, 1000)
})

onUnmounted(() => {
  clearInterval(clockInterval)
})
</script>

<template>
  <div>
    <!-- LOGIN Y REGISTRO -->
    <div v-if="!isLoggedIn" id="authWrapper">
      <div class="auth-side">
        <div class="brand">
          <div class="anchor">🏗️</div>
          <h1>
            CARGALOCATE
            <span>TERMINAL PORTUARIO</span>
          </h1>
        </div>

        <div>
          <h2>
            Control operativo de la
            <em>terminal de contenedores</em>
            para el jefe de turno.
          </h2>

          <p>
            Supervisa muelle, patio, grúas, gates y equipos desde una misma plataforma
            de logística portuaria.
          </p>

          <div class="auth-stats" style="margin-top: 28px;">
            <div><b>Muelle</b><span>Operaciones y atraques</span></div>
            <div><b>Patio</b><span>Contenedores y bloques</span></div>
            <div><b>Equipos</b><span>Estado y disponibilidad</span></div>
            <div><b>Personal</b><span>Organización de turnos</span></div>
          </div>
        </div>

        <div class="auth-stats">
          <div>
            <b>Terminal Cargolocate</b>
            <span>Gestión operativa portuaria</span>
          </div>
        </div>
      </div>

      <div class="auth-form">
        <!-- LOGIN -->
        <form
          v-if="authMode === 'login'"
          class="card-auth"
          @submit.prevent="doLogin"
        >
          <h3>Acceso del Jefe de Turno</h3>
          <p class="sub">Inicia sesión para gestionar la operación de la terminal.</p>

          <div class="field">
            <label>Usuario o correo</label>
            <input
              v-model="loginUser"
              type="text"
              placeholder="ej. lvega@cargolocate.com"
              autocomplete="username"
            >
          </div>

          <div class="field">
            <label>Contraseña</label>
            <input
              v-model="loginPass"
              type="password"
              placeholder="Contraseña"
              autocomplete="current-password"
            >
          </div>

          <button class="btn-primary" type="submit">Ingresar</button>

          <div v-if="loginMsg.text" class="auth-msg" :class="loginMsg.type">
            {{ loginMsg.text }}
          </div>

          <p class="auth-switch">
            ¿Aún no tienes cuenta?
            <a href="#" @click.prevent="showAuth('register')">Regístrate aquí</a>
          </p>
        </form>

        <!-- REGISTRO -->
        <form
          v-else
          class="card-auth"
          @submit.prevent="doRegister"
        >
          <h3>Registro de Personal</h3>
          <p class="sub">Crea tu cuenta de jefe de turno de la terminal Cargolocate.</p>

          <div class="field">
            <label>Nombre completo</label>
            <input v-model="regName" type="text" placeholder="ej. Laura Vega">
          </div>

          <div class="field">
            <label>Correo electrónico</label>
            <input v-model="regEmail" type="email" placeholder="ej. lvega@cargolocate.com">
          </div>

          <div class="field">
            <label>Usuario</label>
            <input v-model="regUser" type="text" placeholder="ej. lvega">
          </div>

          <div class="field">
            <label>Contraseña</label>
            <input v-model="regPass" type="password" placeholder="Mínimo 6 caracteres" autocomplete="new-password">
          </div>

          <div class="field">
            <label>Confirmar contraseña</label>
            <input v-model="regPass2" type="password" placeholder="Repite la contraseña" autocomplete="new-password">
          </div>

          <button class="btn-primary" type="submit">Crear cuenta</button>

          <div v-if="regMsg.text" class="auth-msg" :class="regMsg.type">
            {{ regMsg.text }}
          </div>

          <p class="auth-switch">
            ¿Ya tienes cuenta?
            <a href="#" @click.prevent="showAuth('login')">Inicia sesión</a>
          </p>
        </form>
      </div>
    </div>

    <!-- DASHBOARD -->
    <div v-else id="app">
      <aside class="sidebar">
        <div class="brand">
          <div class="anchor">🏗️</div>
          <h1>
            CARGALOCATE
            <span style="display: block; font-size: 10px; color: var(--muted);">
              JEFE DE TURNO · TERMINAL
            </span>
          </h1>
        </div>

        <nav class="nav">
          <div class="nav-sep">Operación</div>
          <button
            v-for="section in sections.slice(0, 3)"
            :key="section.id"
            :class="{ active: activeSection === section.id }"
            @click="goSection(section.id)"
          >
            <span class="ico">{{ section.icon }}</span>
            <span>{{ section.title }}</span>
          </button>

          <div class="nav-sep">Recursos</div>
          <button
            v-for="section in sections.slice(3, 5)"
            :key="section.id"
            :class="{ active: activeSection === section.id }"
            @click="goSection(section.id)"
          >
            <span class="ico">{{ section.icon }}</span>
            <span>{{ section.title }}</span>
          </button>

          <div class="nav-sep">Personal</div>
          <button
            v-for="section in sections.slice(5)"
            :key="section.id"
            :class="{ active: activeSection === section.id }"
            @click="goSection(section.id)"
          >
            <span class="ico">{{ section.icon }}</span>
            <span>{{ section.title }}</span>
          </button>
        </nav>

        <div class="side-foot">
          <div class="meta">
            Terminal Cargolocate · Muelle 2<br>
            Turno: 08:00 – 20:00
          </div>

          <button class="btn-logout" @click="doLogout">
            🚪 Cerrar sesión
          </button>
        </div>
      </aside>

      <main class="main">
        <div class="topbar">
          <div>
            <h2>{{ pageTitle }}</h2>
            <div class="crumb">Terminal Cargolocate · Gestión operativa de la terminal</div>
          </div>

          <div class="topbar-right">
            <div class="clock">
              <b>{{ currentTime }}</b>
              <span>{{ currentDate }}</span>
            </div>

            <div class="user-chip">
              <div class="avatar">{{ initials(currentUser?.name) }}</div>
              <div>
                <b>{{ currentUser?.name }}</b>
                <span>{{ currentUser?.role || 'Jefe de Turno' }}</span>
              </div>
            </div>
          </div>
        </div>

        <section class="section active">
          <!-- PANEL GENERAL -->
          <template v-if="activeSection === 'sec-general'">
            <div class="grid-kpis">
              <div class="kpi flat">
                <div class="k-label">Movimientos (24h)</div>
                <div class="k-value">—<small>mvm</small></div>
                <div class="k-trend">Pendiente de conexión</div>
              </div>

              <div class="kpi flat">
                <div class="k-label">Productividad</div>
                <div class="k-value">—<small>mov/h</small></div>
                <div class="k-trend">Pendiente de conexión</div>
              </div>

              <div class="kpi flat">
                <div class="k-label">Equipos operativos</div>
                <div class="k-value">—</div>
                <div class="k-trend">Pendiente de conexión</div>
              </div>

              <div class="kpi flat">
                <div class="k-label">Incidencias activas</div>
                <div class="k-value">—</div>
                <div class="k-trend">Pendiente de conexión</div>
              </div>
            </div>

            <div class="grid-2">
              <div class="panel-c">
                <div class="p-head">
                  <h4>Movimientos por hora · Terminal</h4>
                  <span class="tag">Pendiente de conexión</span>
                </div>
                <div class="chart-wrap" style="display: grid; place-items: center; color: var(--muted); text-align: center;">
                  El gráfico estará disponible cuando conectemos las estadísticas del backend.
                </div>
              </div>

              <div class="panel-c">
                <div class="p-head">
                  <h4>Operación de la terminal</h4>
                  <span class="tag">Datos operativos</span>
                </div>
                <div style="padding: 28px 12px; color: var(--muted); text-align: center;">
                  Los indicadores operativos aparecerán cuando recibamos datos reales.
                </div>
              </div>
            </div>

            <div class="grid-2">
              <div class="panel-c">
                <div class="p-head">
                  <h4>Incidencias activas</h4>
                  <span class="tag">Sin datos cargados</span>
                </div>
                <div style="padding: 24px 12px; color: var(--muted); text-align: center;">
                  Las incidencias aparecerán aquí cuando conectemos esta sección con el backend.
                </div>
              </div>

              <div class="panel-c">
                <div class="p-head">
                  <h4>Reefer · Temperaturas</h4>
                  <span class="tag">Pendiente de integración</span>
                </div>
                <div style="padding: 24px 12px; color: var(--muted); text-align: center;">
                  El monitoreo Reefer estará disponible cuando definamos e integremos sus datos.
                </div>
              </div>
            </div>
          </template>

          <!-- MUELLE -->
          <template v-else-if="activeSection === 'sec-muelle'">
            <div class="panel-c">
              <div class="p-head">
                <h4>Operaciones de muelle</h4>
                <span class="tag">Pendiente de integración</span>
              </div>
              <p style="color: var(--muted); line-height: 1.6;">
                La información de buques, atraques y operaciones de muelle se mostrará aquí
                cuando definamos su almacenamiento y conexión con el backend.
              </p>
            </div>

            <div class="panel-c">
              <div class="p-head">
                <h4>Buques y atraques</h4>
                <span class="tag">Sin registros</span>
              </div>
              <table>
                <thead>
                  <tr>
                    <th>Buque</th>
                    <th>Atraque</th>
                    <th>STS asignadas</th>
                    <th>Mov/h</th>
                    <th>Avance</th>
                    <th>Estado</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td colspan="6" style="text-align: center; padding: 28px; color: var(--muted);">
                      No hay información de buques disponible.
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </template>

          <!-- PATIO & YARD -->
          <template v-else-if="activeSection === 'sec-patio'">
            <div class="panel-c">
              <div class="p-head">
                <h4>Inventario del patio</h4>
                <span class="tag">Pendiente de integración</span>
              </div>
              <p style="color: var(--muted); line-height: 1.6;">
                La ocupación por bloques, las capacidades y el inventario se mostrarán aquí
                cuando incorporemos esos datos al backend.
              </p>
            </div>

            <div class="panel-c">
              <div class="p-head">
                <h4>Programación de recibos y despachos</h4>
                <span class="tag">Sin registros</span>
              </div>
              <table>
                <thead>
                  <tr>
                    <th>Orden</th>
                    <th>Operación</th>
                    <th>Contenedores</th>
                    <th>Ventana</th>
                    <th>Patio</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td colspan="5" style="text-align: center; padding: 28px; color: var(--muted);">
                      No hay operaciones programadas disponibles.
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </template>

          <!-- EQUIPOS -->
          <template v-else-if="activeSection === 'sec-equipos'">
            <div class="panel-c">
              <div class="p-head">
                <h4>Parque de equipos</h4>
                <span class="tag">Pendiente de conexión</span>
              </div>
              <table>
                <thead>
                  <tr>
                    <th>Nombre</th>
                    <th>Tipo</th>
                    <th>Estado</th>
                    <th>Horas de uso</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td colspan="4" style="text-align: center; padding: 28px; color: var(--muted);">
                      Los equipos aparecerán aquí cuando conectemos la API.
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </template>

          <!-- INCIDENCIAS -->
          <template v-else-if="activeSection === 'sec-incidencias'">
            <div class="panel-c">
              <div class="p-head">
                <h4>Historial de incidencias</h4>
                <span class="tag">Pendiente de conexión</span>
              </div>
              <table>
                <thead>
                  <tr>
                    <th>Hora</th>
                    <th>Severidad</th>
                    <th>Descripción</th>
                    <th>Zona</th>
                    <th>Estado</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td colspan="5" style="text-align: center; padding: 28px; color: var(--muted);">
                      Las incidencias aparecerán aquí cuando conectemos la API.
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </template>

          <!-- TURNOS -->
          <template v-else-if="activeSection === 'sec-turnos'">
            <div class="grid-kpis">
              <div class="kpi flat">
                <div class="k-label">Personal en turno</div>
                <div class="k-value">—</div>
                <div class="k-trend">Pendiente de conexión</div>
              </div>
              <div class="kpi flat">
                <div class="k-label">Personal asignado</div>
                <div class="k-value">—</div>
                <div class="k-trend">Pendiente de conexión</div>
              </div>
              <div class="kpi flat">
                <div class="k-label">Turno actual</div>
                <div class="k-value">—</div>
                <div class="k-trend">Pendiente de configuración</div>
              </div>
              <div class="kpi flat">
                <div class="k-label">Lugares de trabajo</div>
                <div class="k-value">—</div>
                <div class="k-trend">Pendiente de conexión</div>
              </div>
            </div>

            <div class="panel-c">
              <div class="p-head">
                <h4>Personal y asignación de turnos</h4>
                <span class="tag">Pendiente de conexión</span>
              </div>
              <table>
                <thead>
                  <tr>
                    <th>Nombre</th>
                    <th>Rol</th>
                    <th>Turno</th>
                    <th>Lugar de trabajo</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td colspan="4" style="text-align: center; padding: 28px; color: var(--muted);">
                      El personal aparecerá aquí cuando conectemos la API.
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </template>

          <!-- BITÁCORA -->
          <template v-else-if="activeSection === 'sec-bitacora'">
            <div class="panel-c">
              <div class="p-head">
                <h4>Bitácora de la terminal</h4>
                <span class="tag">Pendiente de conexión</span>
              </div>
              <table>
                <thead>
                  <tr>
                    <th>Fecha</th>
                    <th>Hora</th>
                    <th>Descripción</th>
                    <th>Usuario</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td colspan="4" style="text-align: center; padding: 28px; color: var(--muted);">
                      Las anotaciones aparecerán aquí cuando conectemos la API.
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </template>
        </section>
      </main>
    </div>
  </div>
</template>
