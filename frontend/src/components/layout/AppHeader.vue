<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const pageTitle = computed(() => {
  const titles: Record<string, string> = {
    '/': 'Dashboard',
    '/atendimento': 'Atendimento',
    '/clientes': 'Clientes',
    '/usuarios': 'Usuários',
    '/bot-config': 'Configuração do Bot'
  }
  return titles[route.path] || 'ConversaZap'
})
</script>

<template>
  <header class="h-16 bg-slate-900 border-b border-slate-800 flex items-center justify-between px-6">
    <h1 class="text-xl font-semibold text-white">{{ pageTitle }}</h1>
    
    <div class="flex items-center gap-4">
      <div class="text-right">
        <p class="text-xs text-slate-500">
          {{ new Date().toLocaleDateString('pt-BR', { weekday: 'long', day: 'numeric', month: 'long' }) }}
        </p>
      </div>
    </div>
  </header>
</template>
