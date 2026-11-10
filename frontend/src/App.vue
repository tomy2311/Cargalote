<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import DashboardView from '@/views/DashboardView.vue'
import MuelleView from '@/views/MuelleView.vue'
import PatioView from '@/views/PatioView.vue'
import ResourceCrud from '@/components/ResourceCrud.vue'
import { resourceConfigs } from '@/config/resourceConfigs'

// Acceso local de demostración. La autenticación definitiva queda pendiente
// de configurar Firebase Authentication y su verificación en el backend.
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
  { id: 'sec-cargas', title: 'Cargas', icon: '📦' },
  { id: 'sec-equipos', title: 'Equipos', icon: '🏗️' },
  { id: 'sec-incidencias', title: 'Incidencias', icon: '🚨' },
  { id: 'sec-turnos', title: 'Turnos', icon: '👷' },
  { id: 'sec-bitacora', title: 'Bitácora', icon: '📖' },
]

const pageTitle = computed(() =>
  sections.find(section => section.id === activeSection.value)?.title ?? 'Panel General'
)

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

  users.push({ name, email, user, pass, role: 'Jefe de Turno' })
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
    (item.user === identifier || item.email === identifier) && item.pass === password
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
    ts: Date.now(),
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
  return name.split(' ').filter(Boolean).map(word => word[0]).join('').slice(0, 2).toUpperCase()
}

function updateClock() {
  const now = new Date()
  currentTime.value = now.toLocaleTimeString('es-AR', { hour12: false })
  currentDate.value = now.toLocaleDateString('es-AR', {
    weekday: 'long', day: 'numeric', month: 'long', year: 'numeric',
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

onUnmounted(() => clearInterval(clockInterval))
</script>

<template>
  <div>
    <!-- LOGIN Y REGISTRO -->
    <div v-if="!isLoggedIn" id="authWrapper">
      <div class="auth-side">
        <div class="brand">
          <div class="anchor">🏗️</div>
          <h1>CARGALOCATE <span>TERMINAL PORTUARIO</span></h1>
        </div>

        <div>
          <h2>Control operativo de la <em>terminal de contenedores</em> para el jefe de turno.</h2>
          <p>Supervisa muelle, patio, grúas, gates y equipos desde una misma plataforma de logística portuaria.</p>
          <div class="auth-stats" style="margin-top: 28px;">
            <div><b>Muelle</b><span>Operaciones y atraques</span></div>
            <div><b>Patio</b><span>Contenedores y bloques</span></div>
            <div><b>Equipos</b><span>Estado y disponibilidad</span></div>
            <div><b>Personal</b><span>Organización de turnos</span></div>
          </div>
        </div>

        <div class="auth-stats">
          <div><b>Terminal Cargolocate</b><span>Gestión operativa portuaria</span></div>
        </div>
      </div>

      <div class="auth-form">
        <form v-if="authMode === 'login'" class="card-auth" @submit.prevent="doLogin">
          <h3>Acceso del Jefe de Turno</h3>
          <p class="sub">Inicia sesión para gestionar la operación de la terminal.</p>
          <div class="field">
            <label>Usuario o correo</label>
            <input v-model="loginUser" type="text" placeholder="ej. lvega@cargolocate.com" autocomplete="username" required>
          </div>
          <div class="field">
            <label>Contraseña</label>
            <input v-model="loginPass" type="password" placeholder="Contraseña" autocomplete="current-password" required>
          </div>
          <button class="btn-primary" type="submit">Ingresar</button>
          <div v-if="loginMsg.text" class="auth-msg" :class="loginMsg.type">{{ loginMsg.text }}</div>
          <p class="auth-switch">¿Aún no tienes cuenta? <a href="#" @click.prevent="showAuth('register')">Regístrate aquí</a></p>
        </form>

        <form v-else class="card-auth" @submit.prevent="doRegister">
          <h3>Registro de Personal</h3>
          <p class="sub">Crea tu cuenta de jefe de turno de la terminal Cargolocate.</p>
          <div class="field"><label>Nombre completo</label><input v-model="regName" type="text" placeholder="ej. Laura Vega" required></div>
          <div class="field"><label>Correo electrónico</label><input v-model="regEmail" type="email" placeholder="ej. lvega@cargolocate.com" required></div>
          <div class="field"><label>Usuario</label><input v-model="regUser" type="text" placeholder="ej. lvega" required></div>
          <div class="field"><label>Contraseña</label><input v-model="regPass" type="password" placeholder="Mínimo 6 caracteres" autocomplete="new-password" required></div>
          <div class="field"><label>Confirmar contraseña</label><input v-model="regPass2" type="password" placeholder="Repite la contraseña" autocomplete="new-password" required></div>
          <button class="btn-primary" type="submit">Crear cuenta</button>
          <div v-if="regMsg.text" class="auth-msg" :class="regMsg.type">{{ regMsg.text }}</div>
          <p class="auth-switch">¿Ya tienes cuenta? <a href="#" @click.prevent="showAuth('login')">Inicia sesión</a></p>
        </form>
      </div>
    </div>

    <!-- DASHBOARD -->
    <div v-else id="app">
      <aside class="sidebar">
        <div class="brand">
          <div class="anchor">🏗️</div>
          <h1>CARGALOCATE <span class="brand-subtitle">JEFE DE TURNO · TERMINAL</span></h1>
        </div>

        <nav class="nav">
          <div class="nav-sep">Operación</div>
          <button v-for="section in sections.slice(0, 3)" :key="section.id" :class="{ active: activeSection === section.id }" @click="goSection(section.id)">
            <span class="ico">{{ section.icon }}</span><span>{{ section.title }}</span>
          </button>
          <div class="nav-sep">Recursos</div>
          <button v-for="section in sections.slice(3, 6)" :key="section.id" :class="{ active: activeSection === section.id }" @click="goSection(section.id)">
            <span class="ico">{{ section.icon }}</span><span>{{ section.title }}</span>
          </button>
          <div class="nav-sep">Personal</div>
          <button v-for="section in sections.slice(6)" :key="section.id" :class="{ active: activeSection === section.id }" @click="goSection(section.id)">
            <span class="ico">{{ section.icon }}</span><span>{{ section.title }}</span>
          </button>
        </nav>

        <div class="side-foot">
          <div class="meta">Terminal Cargolocate · Muelle 2<br>Turno: 08:00 – 20:00</div>
          <button class="btn-logout" @click="doLogout">🚪 Cerrar sesión</button>
        </div>
      </aside>

      <main class="main">
        <div class="topbar">
          <div>
            <h2>{{ pageTitle }}</h2>
            <div class="crumb">Terminal Cargolocate · Gestión operativa de la terminal</div>
          </div>
          <div class="topbar-right">
            <div class="clock"><b>{{ currentTime }}</b><span>{{ currentDate }}</span></div>
            <div class="user-chip">
              <div class="avatar">{{ initials(currentUser?.name) }}</div>
              <div><b>{{ currentUser?.name }}</b><span>{{ currentUser?.role || 'Jefe de Turno' }}</span></div>
            </div>
          </div>
        </div>

        <section class="section active">
          <DashboardView v-if="activeSection === 'sec-general'" />
          <MuelleView v-else-if="activeSection === 'sec-muelle'" />
          <PatioView v-else-if="activeSection === 'sec-patio'" />
          <ResourceCrud v-else-if="activeSection === 'sec-cargas'" :config="resourceConfigs.cargas" />
          <ResourceCrud v-else-if="activeSection === 'sec-equipos'" :config="resourceConfigs.equipos" />
          <ResourceCrud v-else-if="activeSection === 'sec-incidencias'" :config="resourceConfigs.incidencias" />
          <ResourceCrud v-else-if="activeSection === 'sec-turnos'" :config="resourceConfigs.personal" />
          <ResourceCrud v-else-if="activeSection === 'sec-bitacora'" :config="resourceConfigs.bitacora" />
        </section>
      </main>
    </div>
  </div>
</template>
