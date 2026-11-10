<script setup>
import ResourceCrud from '@/components/ResourceCrud.vue'
import { resourceConfigs } from '@/config/resourceConfigs'
</script>

<template>
  <div>
    <div class="grid-2">
      <ResourceCrud :config="resourceConfigs.buques" />
      <ResourceCrud :config="resourceConfigs.atraques" />
    </div>
    <ResourceCrud :config="resourceConfigs.operacionesMuelle" />
  </div>
</template>
