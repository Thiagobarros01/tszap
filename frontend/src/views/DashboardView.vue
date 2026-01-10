<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import StatsCard from '@/components/common/StatsCard.vue'
import { atendimentoService } from '@/services/atendimentoService'
import type { Dashboard } from '@/types'

const router = useRouter()
const dashboard = ref<Dashboard | null>(null)
const loading = ref(true)

async function loadDashboard() {
  loading.value = true
  try {
    dashboard.value = await atendimentoService.getDashboard()
  } catch (error) {
    console.error('Erro ao carregar dashboard:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadDashboard()
})

function goToAtendimento() {
  router.push('/atendimento')
}
</script>

<template>
  <div class="space-y-6 animate-fade-in">
    <!-- Stats Grid -->
    <div v-if="loading" class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <div v-for="i in 3" :key="i" class="card animate-pulse">
        <div class="h-4 bg-slate-700 rounded w-1/3 mb-3"></div>
        <div class="h-8 bg-slate-700 rounded w-1/2"></div>
      </div>
    </div>

    <div v-else class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <StatsCard
        title="Total de Conversas"
        :value="dashboard?.totalConversas ?? 0"
        icon="chat"
        color="blue"
      />
      <StatsCard
        title="Em Atendimento"
        :value="dashboard?.emAtendimento ?? 0"
        icon="users"
        color="green"
      />
      <StatsCard
        title="Na Fila de Espera"
        :value="dashboard?.filaEspera ?? 0"
        icon="clock"
        color="yellow"
      />
    </div>

    <!-- Quick Actions -->
    <div class="card">
      <h2 class="text-lg font-semibold text-white mb-4">Ações Rápidas</h2>
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        <button 
          @click="goToAtendimento"
          class="flex items-center gap-4 p-4 bg-slate-800 hover:bg-slate-750 border border-slate-700 hover:border-whatsapp/50 rounded-xl transition-all group"
        >
          <div class="w-12 h-12 bg-whatsapp/20 rounded-xl flex items-center justify-center group-hover:bg-whatsapp/30 transition-colors">
            <svg class="w-6 h-6 text-whatsapp" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
            </svg>
          </div>
          <div class="text-left">
            <p class="font-medium text-white">Ir para Atendimento</p>
            <p class="text-sm text-slate-400">Acessar conversas ativas</p>
          </div>
        </button>

        <button 
          @click="router.push('/clientes')"
          class="flex items-center gap-4 p-4 bg-slate-800 hover:bg-slate-750 border border-slate-700 hover:border-blue-500/50 rounded-xl transition-all group"
        >
          <div class="w-12 h-12 bg-blue-500/20 rounded-xl flex items-center justify-center group-hover:bg-blue-500/30 transition-colors">
            <svg class="w-6 h-6 text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
            </svg>
          </div>
          <div class="text-left">
            <p class="font-medium text-white">Gerenciar Clientes</p>
            <p class="text-sm text-slate-400">Ver e editar clientes</p>
          </div>
        </button>

        <button 
          @click="loadDashboard"
          class="flex items-center gap-4 p-4 bg-slate-800 hover:bg-slate-750 border border-slate-700 hover:border-purple-500/50 rounded-xl transition-all group"
        >
          <div class="w-12 h-12 bg-purple-500/20 rounded-xl flex items-center justify-center group-hover:bg-purple-500/30 transition-colors">
            <svg class="w-6 h-6 text-purple-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
            </svg>
          </div>
          <div class="text-left">
            <p class="font-medium text-white">Atualizar Dashboard</p>
            <p class="text-sm text-slate-400">Recarregar estatísticas</p>
          </div>
        </button>
      </div>
    </div>

    <!-- Info Panel -->
    <div class="card bg-gradient-to-r from-whatsapp-darker/20 to-whatsapp-dark/10 border-whatsapp/20">
      <div class="flex items-start gap-4">
        <div class="w-12 h-12 bg-whatsapp/20 rounded-xl flex items-center justify-center shrink-0">
          <svg class="w-6 h-6 text-whatsapp" fill="currentColor" viewBox="0 0 24 24">
            <path d="M17.472 14.382c-.297-.149-1.758-.867-2.03-.967-.273-.099-.471-.148-.67.15-.197.297-.767.966-.94 1.164-.173.199-.347.223-.644.075-.297-.15-1.255-.463-2.39-1.475-.883-.788-1.48-1.761-1.653-2.059-.173-.297-.018-.458.13-.606.134-.133.298-.347.446-.52.149-.174.198-.298.298-.497.099-.198.05-.371-.025-.52-.075-.149-.669-1.612-.916-2.207-.242-.579-.487-.5-.669-.51-.173-.008-.371-.01-.57-.01-.198 0-.52.074-.792.372-.272.297-1.04 1.016-1.04 2.479 0 1.462 1.065 2.875 1.213 3.074.149.198 2.096 3.2 5.077 4.487.709.306 1.262.489 1.694.625.712.227 1.36.195 1.871.118.571-.085 1.758-.719 2.006-1.413.248-.694.248-1.289.173-1.413-.074-.124-.272-.198-.57-.347z"/>
          </svg>
        </div>
        <div>
          <h3 class="font-semibold text-white mb-1">Sistema de Atendimento WhatsApp</h3>
          <p class="text-sm text-slate-400">
            Gerencie todas as conversas do seu WhatsApp Business em um único lugar. 
            Atenda múltiplos clientes simultaneamente, transfira conversas entre atendentes 
            e acompanhe as métricas do seu atendimento.
          </p>
        </div>
      </div>
    </div>
  </div>
</template>
