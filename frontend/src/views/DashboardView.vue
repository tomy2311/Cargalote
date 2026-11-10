<script setup>
import { onMounted, ref } from 'vue'
import { getDashboardResumen } from '@/services/resourceService'

const resumen = ref(null)
const loading = ref(false)
const error = ref('')

async function cargarResumen() {
  loading.value = true
  error.value = ''
  try {
    resumen.value = await getDashboardResumen()
  } catch (err) {
    error.value = err.message || 'No se pudo cargar el resumen operativo.'
  } finally {
    loading.value = false
  }
}

onMounted(cargarResumen)
</script>

<template>
  <div>
    <div class="dashboard-toolbar">
      <p class="resource-description">Indicadores calculados a partir de los registros actuales del sistema.</p>
      <button class="btn-action btn-secondary" type="button" :disabled="loading" @click="cargarResumen">↻ Actualizar indicadores</button>
    </div>

    <div v-if="loading" class="panel-c equipos-empty-state">Cargando resumen operativo...</div>
    <div v-else-if="error" class="panel-c equipos-message error" role="alert">
      <span>{{ error }}</span>
      <button class="btn-action btn-secondary" type="button" @click="cargarResumen">Reintentar</button>
    </div>

    <template v-else-if="resumen">
      <div class="grid-kpis">
        <div class="kpi flat">
          <div class="k-label">Cargas registradas</div>
          <div class="k-value">{{ resumen.totalCargas }}</div>
          <div class="k-trend">Total en el sistema</div>
        </div>
        <div class="kpi flat">
          <div class="k-label">Equipos operativos</div>
          <div class="k-value">{{ resumen.equiposOperativos }}<small>/ {{ resumen.totalEquipos }}</small></div>
          <div class="k-trend">Según el estado de cada equipo</div>
        </div>
        <div class="kpi flat">
          <div class="k-label">Incidencias activas</div>
          <div class="k-value">{{ resumen.incidenciasActivas }}</div>
          <div class="k-trend">Pendientes de resolución</div>
        </div>
        <div class="kpi flat">
          <div class="k-label">Ocupación del patio</div>
          <div class="k-value">{{ resumen.ocupacionPatioPorcentaje }}<small>%</small></div>
          <div class="k-trend">Ocupados sobre capacidad total</div>
        </div>
      </div>

      <div class="grid-2">
        <div class="panel-c">
          <div class="p-head">
            <h4>Operación de muelle</h4>
            <span class="tag">Datos actuales</span>
          </div>
          <div class="dashboard-metric-list">
            <div><span>Buques activos</span><strong>{{ resumen.buquesActivos }}</strong></div>
            <div><span>Operaciones de muelle activas</span><strong>{{ resumen.operacionesMuelleActivas }}</strong></div>
            <div><span>Movimientos por hora asignados</span><strong>{{ resumen.movimientosPorHora }}</strong></div>
          </div>
        </div>

        <div class="panel-c">
          <div class="p-head">
            <h4>Patio y recursos</h4>
            <span class="tag">Datos actuales</span>
          </div>
          <div class="dashboard-metric-list">
            <div><span>Contenedores registrados</span><strong>{{ resumen.totalContenedores }}</strong></div>
            <div><span>Operaciones de patio pendientes</span><strong>{{ resumen.operacionesPatioPendientes }}</strong></div>
            <div><span>Personal registrado</span><strong>{{ resumen.totalPersonal }}</strong></div>
          </div>
        </div>
      </div>

      <div class="panel-c dashboard-note">
        <div class="p-head"><h4>Información del panel</h4><span class="tag">Datos reales</span></div>
        <p>Los indicadores se calculan con los registros guardados en PostgreSQL. Los valores de productividad y ocupación dependen de que las operaciones y capacidades estén cargadas correctamente.</p>
      </div>
    </template>
  </div>
</template>
