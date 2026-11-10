<script setup>
import ResourceCrud from '@/components/ResourceCrud.vue'
import { resourceConfigs } from '@/config/resourceConfigs'
</script>

<template>
  <div>
    <ResourceCrud :config="resourceConfigs.bloquesPatio" />
    <div class="grid-2">
      <ResourceCrud :config="resourceConfigs.contenedores" />
      <ResourceCrud :config="resourceConfigs.operacionesPatio" />
    </div>
  </div>
</template>
