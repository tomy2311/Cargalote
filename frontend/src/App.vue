<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import {
  listarEquipos,
  registrarEquipo,
  actualizarEquipo,
  eliminarEquipo
} from '@/services/equiposService'
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

const equipos = ref([])
const equiposLoading = ref(false)
const equiposError = ref('')
const equipoFormularioVisible = ref(false)
const equipoGuardando = ref(false)
const equipoEliminandoId = ref(null)
const equipoEditandoId = ref(null)
const equipoMensaje = ref({ text: '', type: '' })
const nuevoEquipo = ref({
  nombre: '',
  tipo: '',
  estado: '',
  horasUso: 0
})

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
async function cargarEquipos() {
  equiposLoading.value = true
  equiposError.value = ''

  try {
    const resultado = await listarEquipos()
    equipos.value = Array.isArray(resultado) ? resultado : []
  } catch (error) {
    console.error('Error al cargar equipos:', error)
    equiposError.value = error.message || 'No se pudieron cargar los equipos.'
  } finally {
    equiposLoading.value = false
  }
}

function limpiarFormularioEquipo() {
  nuevoEquipo.value = {
    nombre: '',
    tipo: '',
    estado: '',
    horasUso: 0
  }
  equipoEditandoId.value = null
}

function abrirFormularioNuevoEquipo() {
  limpiarFormularioEquipo()
  equipoMensaje.value = { text: '', type: '' }
  equipoFormularioVisible.value = true
}

function abrirFormularioEdicion(equipo) {
  equipoMensaje.value = { text: '', type: '' }
  equipoEditandoId.value = equipo.id
  nuevoEquipo.value = {
    nombre: equipo.nombre ?? '',
    tipo: equipo.tipo ?? '',
    estado: equipo.estado ?? '',
    horasUso: equipo.horasUso ?? 0
  }
  equipoFormularioVisible.value = true
}

function cancelarFormularioEquipo() {
  equipoFormularioVisible.value = false
  limpiarFormularioEquipo()
  equipoMensaje.value = { text: '', type: '' }
}

async function guardarEquipo() {
  const nombre = nuevoEquipo.value.nombre.trim()
  const tipo = nuevoEquipo.value.tipo.trim()
  const estado = nuevoEquipo.value.estado.trim()
  const horasUso = nuevoEquipo.value.horasUso

  if (!nombre || !tipo || !estado) {
    equipoMensaje.value = {
      text: 'Completa todos los campos obligatorios.',
      type: 'error'
    }
    return
  }

  if (
    horasUso === '' ||
    horasUso === null ||
    horasUso === undefined ||
    !Number.isInteger(Number(horasUso)) ||
    Number(horasUso) < 0
  ) {
    equipoMensaje.value = {
      text: 'Las horas de uso deben ser un número entero mayor o igual a cero.',
      type: 'error'
    }
    return
  }

  const datos = {
    nombre,
    tipo,
    estado,
    horasUso: Number(horasUso)
  }

  equipoGuardando.value = true
  equipoMensaje.value = { text: '', type: '' }

  try {
    if (equipoEditandoId.value !== null) {
      await actualizarEquipo(equipoEditandoId.value, datos)
      equipoMensaje.value = {
        text: 'Equipo actualizado correctamente.',
        type: 'ok'
      }
    } else {
      await registrarEquipo(datos)
      equipoMensaje.value = {
        text: 'Equipo registrado correctamente.',
        type: 'ok'
      }
    }

    equipoFormularioVisible.value = false
    limpiarFormularioEquipo()
    await cargarEquipos()
  } catch (error) {
    console.error('Error al guardar el equipo:', error)
    equipoMensaje.value = {
      text: error.message || 'No se pudo guardar el equipo.',
      type: 'error'
    }
  } finally {
    equipoGuardando.value = false
  }
}

async function confirmarEliminarEquipo(equipo) {
  const confirmado = window.confirm(
    `¿Estás seguro de que deseas eliminar el equipo ${equipo.nombre}?`
  )

  if (!confirmado) return

  equipoEliminandoId.value = equipo.id
  equipoMensaje.value = { text: '', type: '' }

  try {
    await eliminarEquipo(equipo.id)
    equipoMensaje.value = {
      text: 'Equipo eliminado correctamente.',
      type: 'ok'
    }
    await cargarEquipos()
  } catch (error) {
    console.error('Error al eliminar el equipo:', error)
    equipoMensaje.value = {
      text: error.message || 'No se pudo eliminar el equipo.',
      type: 'error'
    }
  } finally {
    equipoEliminandoId.value = null
  }
}

function claseEstadoEquipo(estado = '') {
  const valor = estado.toLowerCase()

  if (valor.includes('operat')) return 'ok'
  if (valor.includes('manten')) return 'warn'
  if (valor.includes('fuera') || valor.includes('inactiv')) return 'crit'
  return 'info'
}

function goSection(sectionId) {
  activeSection.value = sectionId

  if (sectionId === 'sec-equipos') {
    cargarEquipos()
  }
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
              <div class="p-head equipos-heading">
                <div>
                  <h4>Parque de equipos</h4>
                  <span class="tag">
                    {{ equipos.length }} {{ equipos.length === 1 ? 'equipo' : 'equipos' }}
                  </span>
                </div>
                <div class="equipos-toolbar-actions">
                  <button
                    class="btn-action btn-secondary"
                    type="button"
                    :disabled="equiposLoading"
                    @click="cargarEquipos"
                  >
                    ↻ Actualizar
                  </button>
                  <button
                    class="btn-action btn-primary-action"
                    type="button"
                    @click="abrirFormularioNuevoEquipo"
                  >
                    + Nuevo equipo
                  </button>
                </div>
              </div>

              <div
                v-if="equipoMensaje.text"
                class="equipos-message"
                :class="equipoMensaje.type"
                role="status"
              >
                {{ equipoMensaje.text }}
              </div>

              <form
                v-if="equipoFormularioVisible"
                class="equipo-form"
                @submit.prevent="guardarEquipo"
              >
                <div class="equipo-form-heading">
                  <h4>{{ equipoEditandoId !== null ? 'Editar equipo' : 'Registrar equipo' }}</h4>
                  <span class="tag">{{ equipoEditandoId !== null ? `ID ${equipoEditandoId}` : 'Nuevo registro' }}</span>
                </div>

                <div class="equipo-form-grid">
                  <div class="field">
                    <label for="equipo-nombre">Nombre *</label>
                    <input
                      id="equipo-nombre"
                      v-model.trim="nuevoEquipo.nombre"
                      class="inp"
                      type="text"
                      maxlength="120"
                      placeholder="Ej. STS-01"
                      required
                    >
                  </div>

                  <div class="field">
                    <label for="equipo-tipo">Tipo *</label>
                    <input
                      id="equipo-tipo"
                      v-model.trim="nuevoEquipo.tipo"
                      class="inp"
                      type="text"
                      maxlength="120"
                      placeholder="Ej. Grúa de muelle"
                      required
                    >
                  </div>

                  <div class="field">
                    <label for="equipo-estado">Estado *</label>
                    <input
                      id="equipo-estado"
                      v-model.trim="nuevoEquipo.estado"
                      class="inp"
                      type="text"
                      maxlength="80"
                      placeholder="Ej. Operativo"
                      required
                    >
                  </div>

                  <div class="field">
                    <label for="equipo-horas">Horas de uso *</label>
                    <input
                      id="equipo-horas"
                      v-model.number="nuevoEquipo.horasUso"
                      class="inp"
                      type="number"
                      min="0"
                      step="1"
                      required
                    >
                  </div>
                </div>

                <div class="equipo-form-actions">
                  <button
                    class="btn-action btn-secondary"
                    type="button"
                    :disabled="equipoGuardando"
                    @click="cancelarFormularioEquipo"
                  >
                    Cancelar
                  </button>
                  <button
                    class="btn-action btn-primary-action"
                    type="submit"
                    :disabled="equipoGuardando"
                  >
                    {{ equipoGuardando ? 'Guardando...' : (equipoEditandoId !== null ? 'Guardar cambios' : 'Registrar equipo') }}
                  </button>
                </div>
              </form>

              <div v-if="equiposLoading" class="equipos-empty-state">
                Cargando equipos...
              </div>

              <div v-else-if="equiposError" class="equipos-message error" role="alert">
                {{ equiposError }}
                <button class="btn-action btn-secondary" type="button" @click="cargarEquipos">
                  Reintentar
                </button>
              </div>

              <div v-else-if="equipos.length === 0" class="equipos-empty-state">
                <strong>No hay equipos registrados.</strong>
                <span>Registrá el primer equipo para comenzar a administrar el parque.</span>
              </div>

              <div v-else class="equipos-table-wrap">
                <table>
                  <thead>
                    <tr>
                      <th>Nombre</th>
                      <th>Tipo</th>
                      <th>Estado</th>
                      <th>Horas de uso</th>
                      <th>Acciones</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="equipo in equipos" :key="equipo.id">
                      <td>{{ equipo.nombre }}</td>
                      <td>{{ equipo.tipo }}</td>
                      <td>
                        <span class="badge" :class="claseEstadoEquipo(equipo.estado)">
                          {{ equipo.estado }}
                        </span>
                      </td>
                      <td>{{ equipo.horasUso ?? '—' }}</td>
                      <td>
                        <div class="equipos-row-actions">
                          <button
                            class="btn-action btn-secondary btn-compact"
                            type="button"
                            @click="abrirFormularioEdicion(equipo)"
                          >
                            Editar
                          </button>
                          <button
                            class="btn-action btn-danger btn-compact"
                            type="button"
                            :disabled="equipoEliminandoId === equipo.id"
                            @click="confirmarEliminarEquipo(equipo)"
                          >
                            {{ equipoEliminandoId === equipo.id ? 'Eliminando...' : 'Eliminar' }}
                          </button>
                        </div>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
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
