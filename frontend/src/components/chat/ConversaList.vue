<script setup lang="ts">
import { computed } from 'vue'
import { useConversaStore } from '@/stores/conversa'
import { StatusConversa, type ConversaResumo } from '@/types'

const conversaStore = useConversaStore()

const props = defineProps<{
  filtro: string
}>()

const conversasFiltradas = computed(() => {
  if (!props.filtro) return conversaStore.conversas
  const termo = props.filtro.toLowerCase()
  return conversaStore.conversas.filter(
    (c) =>
      c.nomeCliente?.toLowerCase().includes(termo) ||
      c.telefoneCliente.includes(termo)
  )
})

function getStatusColor(status: StatusConversa): string {
  switch (status) {
    case StatusConversa.HUMANO:
      return 'bg-green-500'
    case StatusConversa.BOT:
      return 'bg-blue-500'
    case StatusConversa.ENCERRADA:
      return 'bg-slate-500'
    default:
      return 'bg-slate-500'
  }
}

function getStatusLabel(status: StatusConversa): string {
  switch (status) {
    case StatusConversa.HUMANO:
      return 'Atendimento'
    case StatusConversa.BOT:
      return 'Bot'
    case StatusConversa.ENCERRADA:
      return 'Encerrada'
    default:
      return status
  }
}

function selectConversa(conversa: ConversaResumo) {
  conversaStore.selecionarConversa(conversa)
}

function isSelected(conversa: ConversaResumo): boolean {
  return conversaStore.conversaSelecionada?.conversaId === conversa.conversaId
}

function formatPhone(phone: string): string {
  // Remove non-digits and format
  const digits = phone.replace(/\D/g, '')
  if (digits.length === 13) {
    return `+${digits.slice(0, 2)} (${digits.slice(2, 4)}) ${digits.slice(4, 9)}-${digits.slice(9)}`
  }
  return phone
}
</script>

<template>
  <div class="flex flex-col h-full">
    <!-- Loading -->
    <div v-if="conversaStore.loading" class="flex-1 flex items-center justify-center">
      <div class="animate-spin w-8 h-8 border-2 border-whatsapp border-t-transparent rounded-full"></div>
    </div>

    <!-- Empty State -->
    <div 
      v-else-if="conversasFiltradas.length === 0" 
      class="flex-1 flex flex-col items-center justify-center text-slate-500 p-6"
    >
      <svg class="w-16 h-16 mb-4 opacity-50" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
      </svg>
      <p class="text-center">
        {{ filtro ? 'Nenhuma conversa encontrada' : 'Nenhuma conversa ativa' }}
      </p>
    </div>

    <!-- Conversation List -->
    <div v-else class="flex-1 overflow-y-auto scrollbar-thin space-y-1 p-2 pb-8">
      <button
        v-for="conversa in conversasFiltradas"
        :key="conversa.conversaId"
        @click="selectConversa(conversa)"
        :class="[
          'w-full flex items-center gap-2.5 p-2.5 rounded-lg text-left transition-all duration-200',
          isSelected(conversa)
            ? 'bg-whatsapp/10 border border-whatsapp/30'
            : 'hover:bg-slate-800 border border-transparent'
        ]"
      >
        <!-- Avatar -->
        <div class="relative shrink-0">
          <div class="w-10 h-10 bg-slate-700 rounded-full flex items-center justify-center">
            <span class="text-sm font-semibold text-slate-300">
              {{ (conversa.nomeCliente || conversa.telefoneCliente)?.charAt(0)?.toUpperCase() || '?' }}
            </span>
          </div>
          <span 
            :class="['absolute -bottom-0.5 -right-0.5 w-3 h-3 rounded-full border-2 border-slate-900', getStatusColor(conversa.status as StatusConversa)]"
          ></span>
        </div>

        <!-- Info -->
        <div class="flex-1 min-w-0 overflow-hidden">
          <div class="flex items-center justify-between gap-2 mb-1">
            <p class="font-medium text-white truncate text-sm">
              {{ conversa.nomeCliente || 'Cliente' }}
            </p>
            <span 
              v-if="conversa.naoLidas > 0"
              class="bg-whatsapp text-white text-xs font-bold px-1.5 py-0.5 rounded-full shrink-0"
            >
              {{ conversa.naoLidas }}
            </span>
          </div>
          <p class="text-xs text-slate-400 truncate mb-1">
            {{ formatPhone(conversa.telefoneCliente) }}
          </p>
          <div class="flex items-center gap-1.5 flex-wrap">
            <span 
              :class="[
                'text-xs px-1.5 py-0.5 rounded-full',
                conversa.status === StatusConversa.HUMANO ? 'bg-green-500/20 text-green-400' :
                conversa.status === StatusConversa.BOT ? 'bg-blue-500/20 text-blue-400' :
                'bg-slate-500/20 text-slate-400'
              ]"
            >
              {{ getStatusLabel(conversa.status as StatusConversa) }}
            </span>
            <span v-if="conversa.nomeAtendente" class="text-xs text-slate-500 truncate">
              • {{ conversa.nomeAtendente }}
            </span>
          </div>
        </div>
      </button>
    </div>
  </div>
</template>
