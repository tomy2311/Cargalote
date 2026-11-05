<script setup>
import { computed } from 'vue'

const props = defineProps({
  estado: {
    type: String,
    required: true,
  },
})

const claseEstado = computed(() => {
  const estado = (props.estado || '')
    .toLowerCase()
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .trim()

  const equivalencias = {
    'operativo': 'operativo',
    'operativa': 'operativo',
    'mantenimiento': 'mantenimiento',
    'en mantenimiento': 'mantenimiento',
    'averiado': 'averiado',
    'averiada': 'averiado',
    'disponible': 'disponible',
  }

  return equivalencias[estado] || 'sin-clasificar'
})
</script>

<template>
  <span class="estado-badge" :class="claseEstado">
    {{ props.estado || 'Sin estado' }}
  </span>
</template>

<style scoped>
.estado-badge {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  border-radius: 999px;
  border: 1px solid transparent;
  font-size: 0.875rem;
  font-weight: 600;
  white-space: nowrap;
  background: rgba(154, 167, 196, 0.12);
  color: var(--muted);
  border-color: var(--line);
}

.operativo {
  background: rgba(63, 202, 122, 0.14);
  color: #93e8bb;
  border-color: rgba(63, 202, 122, 0.35);
}

.mantenimiento {
  background: rgba(255, 183, 3, 0.14);
  color: #ffd166;
  border-color: rgba(255, 183, 3, 0.35);
}

.averiado {
  background: rgba(255, 90, 78, 0.14);
  color: #ff9a8a;
  border-color: rgba(255, 90, 78, 0.35);
}

.disponible {
  background: rgba(47, 129, 247, 0.15);
  color: #8ab6ff;
  border-color: rgba(47, 129, 247, 0.35);
}

.sin-clasificar {
  background: rgba(154, 167, 196, 0.12);
  color: var(--muted);
}
</style>