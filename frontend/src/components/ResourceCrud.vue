<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { createResourceService } from '@/services/resourceService'

const props = defineProps({
  config: { type: Object, required: true },
})

const api = createResourceService(props.config.endpoint)
const rows = ref([])
const loading = ref(false)
const saving = ref(false)
const deletingId = ref(null)
const error = ref('')
const notice = ref({ text: '', type: '' })
const search = ref('')
const formVisible = ref(false)
const editingId = ref(null)
const optionLists = reactive({})
const form = reactive({})

const filteredRows = computed(() => {
  const term = search.value.trim().toLocaleLowerCase('es')
  if (!term) return rows.value
  return rows.value.filter(row =>
    props.config.columns.some(column => String(row[column.key] ?? '').toLocaleLowerCase('es').includes(term))
  )
})

function setNotice(text, type = 'ok') {
  notice.value = { text, type }
}

function emptyValue(field) {
  if (field.multiple || field.type === 'multi-api') return []
  if (field.defaultValue !== undefined) return field.defaultValue
  if (field.type === 'number') return ''
  return ''
}

function clearForm() {
  Object.keys(form).forEach(key => delete form[key])
  props.config.fields.forEach(field => {
    form[field.key] = emptyValue(field)
  })
  editingId.value = null
}

async function loadOptions() {
  const apiFields = props.config.fields.filter(field => field.endpoint)
  await Promise.all(apiFields.map(async field => {
    try {
      const service = createResourceService(field.endpoint)
      optionLists[field.key] = await service.list()
    } catch (err) {
      console.error(`No se pudieron cargar las opciones de ${field.label}:`, err)
      optionLists[field.key] = []
    }
  }))
}

function fieldId(field) {
  return `field${props.config.endpoint.replaceAll('/', '-')}-${field.key}`
}

function optionsFor(field) {
  return field.options || optionLists[field.key] || []
}

async function refresh() {
  loading.value = true
  error.value = ''
  try {
    const result = await api.list()
    rows.value = Array.isArray(result) ? result : []
  } catch (err) {
    error.value = err.message || 'No se pudieron cargar los registros.'
  } finally {
    loading.value = false
  }
}

async function openCreate() {
  clearForm()
  notice.value = { text: '', type: '' }
  await loadOptions()
  formVisible.value = true
}

async function openEdit(row) {
  await loadOptions()
  clearForm()
  props.config.fields.forEach(field => {
    let value = row[field.key]
    if (field.type === 'datetime-local' && value) value = String(value).slice(0, 16)
    if (field.type === 'multi-api' && Array.isArray(value)) value = value.map(String)
    if (field.type === 'select-api' && value !== null && value !== undefined) value = String(value)
    form[field.key] = value ?? emptyValue(field)
  })
  editingId.value = row.id
  notice.value = { text: '', type: '' }
  formVisible.value = true
}

function closeForm() {
  if (saving.value) return
  formVisible.value = false
  clearForm()
}

function buildPayload() {
  const payload = {}
  for (const field of props.config.fields) {
    let value = form[field.key]
    if (field.type === 'number') value = value === '' || value === null ? null : Number(value)
    if (field.type === 'select-api') value = value === '' || value === null ? null : Number(value)
    if (field.type === 'multi-api') value = Array.isArray(value) ? value.map(Number) : []
    if (['date', 'time', 'datetime-local'].includes(field.type) && value === '') value = null
    if (typeof value === 'string') value = value.trim()
    payload[field.key] = value
  }
  return payload
}

async function save() {
  const payload = buildPayload()
  for (const field of props.config.fields) {
    if (field.required && (payload[field.key] === '' || payload[field.key] === null || payload[field.key] === undefined)) {
      setNotice(`El campo "${field.label}" es obligatorio.`, 'error')
      return
    }
    if (field.type === 'number' && payload[field.key] !== null) {
      if (!Number.isFinite(payload[field.key]) || (field.min !== undefined && payload[field.key] < field.min) || (field.max !== undefined && payload[field.key] > field.max)) {
        setNotice(`Revisá el valor del campo "${field.label}".`, 'error')
        return
      }
    }
  }

  saving.value = true
  notice.value = { text: '', type: '' }
  try {
    if (editingId.value !== null) {
      await api.update(editingId.value, payload)
      setNotice('Registro actualizado correctamente.')
    } else {
      await api.create(payload)
      setNotice('Registro creado correctamente.')
    }
    formVisible.value = false
    clearForm()
    await refresh()
  } catch (err) {
    setNotice(err.message || 'No se pudo guardar el registro.', 'error')
  } finally {
    saving.value = false
  }
}

async function remove(row) {
  const label = row[props.config.identifier || props.config.columns[0].key] || `ID ${row.id}`
  if (!window.confirm(`¿Querés eliminar "${label}"? Esta acción no se puede deshacer.`)) return
  deletingId.value = row.id
  notice.value = { text: '', type: '' }
  try {
    await api.remove(row.id)
    setNotice('Registro eliminado correctamente.')
    await refresh()
  } catch (err) {
    setNotice(err.message || 'No se pudo eliminar el registro.', 'error')
  } finally {
    deletingId.value = null
  }
}

function cellValue(row, column) {
  const value = row[column.key]
  if (value === null || value === undefined || value === '') return '—'
  if (Array.isArray(value)) return value.join(', ')
  if (column.format === 'datetime' && value) return String(value).replace('T', ' ').slice(0, 16)
  return value
}

function badgeClass(value = '') {
  const normalized = String(value).toLocaleLowerCase('es')
  if (normalized.includes('operat') || normalized.includes('disponible') || normalized.includes('resuelt') || normalized.includes('complet') || normalized.includes('finaliz')) return 'ok'
  if (normalized.includes('manten') || normalized.includes('seguimiento') || normalized.includes('programad') || normalized.includes('pausad')) return 'warn'
  if (normalized.includes('averiad') || normalized.includes('fuera') || normalized.includes('crít') || normalized.includes('crit') || normalized.includes('abierta')) return 'crit'
  return 'info'
}

onMounted(() => {
  clearForm()
  refresh()
  loadOptions()
})
</script>

<template>
  <div class="panel-c resource-panel">
    <div class="p-head resource-heading">
      <div>
        <h4>{{ config.title }}</h4>
        <span class="tag">{{ rows.length }} {{ rows.length === 1 ? 'registro' : 'registros' }}</span>
        <p v-if="config.description" class="resource-description">{{ config.description }}</p>
      </div>
      <div class="resource-toolbar">
        <button class="btn-action btn-secondary" type="button" :disabled="loading" @click="refresh">↻ Actualizar</button>
        <button class="btn-action btn-primary-action" type="button" @click="openCreate">+ Nuevo</button>
      </div>
    </div>

    <div v-if="notice.text" class="equipos-message" :class="notice.type" role="status">
      {{ notice.text }}
    </div>

    <form v-if="formVisible" class="equipo-form resource-form" @submit.prevent="save">
      <div class="equipo-form-heading">
        <h4>{{ editingId !== null ? 'Editar registro' : 'Nuevo registro' }}</h4>
        <span class="tag">{{ editingId !== null ? `ID ${editingId}` : 'Nuevo' }}</span>
      </div>

      <div class="equipo-form-grid resource-field-grid">
        <div v-for="field in config.fields" :key="field.key" class="field">
          <label :for="fieldId(field)">{{ field.label }}{{ field.required ? ' *' : '' }}</label>

          <textarea
            v-if="field.type === 'textarea'"
            :id="fieldId(field)"
            v-model="form[field.key]"
            class="inp resource-textarea"
            :required="field.required"
            :placeholder="field.placeholder || ''"
            rows="3"
          />

          <select
            v-else-if="field.type === 'select' || field.type === 'select-api'"
            :id="fieldId(field)"
            v-model="form[field.key]"
            class="sel resource-input"
            :required="field.required"
          >
            <option value="">Seleccionar...</option>
            <option v-for="option in optionsFor(field)" :key="option[field.optionValue || 'value']" :value="String(option[field.optionValue || 'value'])">
              {{ option[field.optionLabel || 'label'] }}
            </option>
          </select>

          <select
            v-else-if="field.type === 'multi-api'"
            :id="fieldId(field)"
            v-model="form[field.key]"
            class="sel resource-input resource-multiple"
            multiple
            size="4"
          >
            <option v-for="option in optionsFor(field)" :key="option[field.optionValue || 'id']" :value="String(option[field.optionValue || 'id'])">
              {{ option[field.optionLabel || 'nombre'] }}
            </option>
          </select>

          <input
            v-else
            :id="fieldId(field)"
            v-model="form[field.key]"
            class="inp resource-input"
            :type="field.type || 'text'"
            :required="field.required"
            :min="field.min"
            :max="field.max"
            :step="field.step"
            :maxlength="field.maxlength"
            :placeholder="field.placeholder || ''"
          >
          <small v-if="field.type === 'multi-api'" class="resource-help">Mantené Ctrl para seleccionar varios equipos.</small>
        </div>
      </div>

      <div class="equipo-form-actions">
        <button class="btn-action btn-secondary" type="button" :disabled="saving" @click="closeForm">Cancelar</button>
        <button class="btn-action btn-primary-action" type="submit" :disabled="saving">
          {{ saving ? 'Guardando...' : (editingId !== null ? 'Guardar cambios' : 'Crear registro') }}
        </button>
      </div>
    </form>

    <div class="resource-list-toolbar">
      <input v-model="search" class="inp resource-search" type="search" placeholder="Buscar en los registros...">
    </div>

    <div v-if="loading" class="equipos-empty-state">Cargando {{ config.title.toLowerCase() }}...</div>
    <div v-else-if="error" class="equipos-message error" role="alert">
      <span>{{ error }}</span>
      <button class="btn-action btn-secondary" type="button" @click="refresh">Reintentar</button>
    </div>
    <div v-else-if="rows.length === 0" class="equipos-empty-state">
      <strong>No hay registros todavía.</strong>
      <span>Usá «Nuevo» para agregar el primer registro.</span>
    </div>
    <div v-else-if="filteredRows.length === 0" class="equipos-empty-state">No hay resultados para esa búsqueda.</div>
    <div v-else class="equipos-table-wrap resource-table-wrap">
      <table>
        <thead>
          <tr>
            <th v-for="column in config.columns" :key="column.key">{{ column.label }}</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in filteredRows" :key="row.id">
            <td v-for="column in config.columns" :key="column.key">
              <span v-if="column.badge" class="badge" :class="badgeClass(row[column.key])">{{ cellValue(row, column) }}</span>
              <span v-else>{{ cellValue(row, column) }}</span>
            </td>
            <td>
              <div class="equipos-row-actions">
                <button class="btn-action btn-secondary btn-compact" type="button" @click="openEdit(row)">Editar</button>
                <button class="btn-action btn-danger btn-compact" type="button" :disabled="deletingId === row.id" @click="remove(row)">
                  {{ deletingId === row.id ? 'Eliminando...' : 'Eliminar' }}
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
