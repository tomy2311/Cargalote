<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'

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
  return JSON.parse(localStorage.getItem('cargolocate_users') || '[]')
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

  localStorage.setItem('cargolocate_users', JSON.stringify(users))

  setMessage(
    regMsg,
    'Cuenta creada correctamente. Ya puedes iniciar sesión.',
    'ok'
  )

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
    setMessage(loginMsg, 'Credenciales incorrectas. Verifica tus datos.')
    return
  }

  const session = {
    user: user.user,
    name: user.name,
    email: user.email,
    role: user.role,
    ts: Date.now()
  }

  localStorage.setItem('cargolocate_session', JSON.stringify(session))

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
            Supervisa muelle, patio, grúas, gates y equipos.
            Plataforma de logística portuaria Cargolocate · Terminal Muelle 2.
          </p>

          <div class="auth-stats" style="margin-top: 28px;">
            <div><b>1,148</b><span>Mov. 24h</span></div>
            <div><b>42</b><span>Mov/hora</span></div>
            <div><b>9</b><span>Grúas STS</span></div>
            <div><b>98.2%</b><span>Disponibilidad</span></div>
          </div>
        </div>

        <div class="auth-stats">
          <div>
            <b>Terminal Cargolocate</b>
            <span>Puerto de Algeciras · Muelle 2</span>
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

          <div
            v-if="loginMsg.text"
            class="auth-msg"
            :class="loginMsg.type"
          >
            {{ loginMsg.text }}
          </div>

          <p class="auth-switch">
            ¿Aún no tienes cuenta?
            <a href="#" @click.prevent="showAuth('register')">
              Regístrate aquí
            </a>
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
            <input v-model="regPass" type="password" placeholder="Mínimo 6 caracteres">
          </div>

          <div class="field">
            <label>Confirmar contraseña</label>
            <input v-model="regPass2" type="password" placeholder="Repite la contraseña">
          </div>

          <button class="btn-primary" type="submit">Crear cuenta</button>

          <div
            v-if="regMsg.text"
            class="auth-msg"
            :class="regMsg.type"
          >
            {{ regMsg.text }}
          </div>

          <p class="auth-switch">
            ¿Ya tienes cuenta?
            <a href="#" @click.prevent="showAuth('login')">
              Inicia sesión
            </a>
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
            Puerto de Algeciras<br>
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
            <div class="crumb">
              Terminal Cargolocate · Ruta Algeciras → Rotterdam · Próximo atraque ETA 4h
            </div>
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

        <!-- CONTENIDO PROVISIONAL -->
        <section class="section active">
          <div v-if="activeSection === 'sec-general'" class="grid-kpis">
            <div class="kpi up">
              <div class="k-label">Movimientos (24h)</div>
              <div class="k-value">1,148<small>mvm</small></div>
              <div class="k-trend">▲ 6.2% vs ayer</div>
            </div>

            <div class="kpi flat">
              <div class="k-label">Productividad</div>
              <div class="k-value">34.2<small>mov/h</small></div>
              <div class="k-trend">● Promedio muelle + patio</div>
            </div>

            <div class="kpi warn">
              <div class="k-label">Grúas STS operativas</div>
              <div class="k-value">7<small>/ 9</small></div>
              <div class="k-trend">▲ 2 en mantenimiento</div>
            </div>

            <div class="kpi warn">
              <div class="k-label">Ocupación del patio</div>
              <div class="k-value">78<small>%</small></div>
              <div class="k-trend">▲ 3% en últimas 6h</div>
            </div>
          </div>

          <div class="panel-c">
            <div class="p-head">
              <h4>{{ pageTitle }}</h4>
              <span class="tag">CARGALOCATE</span>
            </div>

            <p>
              La estructura de Vue ya está funcionando. En los próximos pasos
              vamos a trasladar el contenido de cada sección y conectar los datos
              con el backend de Spring Boot.
            </p>
          </div>
        </section>
      </main>
    </div>
  </div>
</template>