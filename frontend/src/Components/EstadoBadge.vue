<script setup>
import { computed } from 'vue'

const props = defineProps({
  estado: {
    type: String,
    required: true,
  },
})

const claseEstado = computed(() =>
  props.estado
    .toLowerCase()
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .trim()
    .replace(/\s+/g, '-')
)
</script>

<template>
  <span class="estado-badge" :class="claseEstado">
    {{ estado }}
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
  line-height: 1.4;
  white-space: nowrap;
  background-color: #e5e7eb;
  color: #374151;
  border-color: #d1d5db;
}

.operativo {
  background-color: #dcfce7;
  color: #166534;
  border-color: #86efac;
}

.en-mantenimiento {
  background-color: #fef9c3;
  color: #854d0e;
  border-color: #fde047;
}

.averiado {
  background-color: #fee2e2;
  color: #991b1b;
  border-color: #fca5a5;
}

.disponible {
  background-color: #dbeafe;
  color: #1e40af;
  border-color: #93c5fd;
}
</style>