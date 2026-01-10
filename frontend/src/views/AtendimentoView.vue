<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useConversaStore } from '@/stores/conversa'
import ConversaList from '@/components/chat/ConversaList.vue'
import ChatWindow from '@/components/chat/ChatWindow.vue'

const conversaStore = useConversaStore()
const filtro = ref('')

onMounted(() => {
  // Carrega a lista inicial via HTTP
  conversaStore.carregarConversas()
  
  // LIGA O WEBSOCKET (Substitui o setInterval de 30s)
  conversaStore.conectarWebSocket()
})

onUnmounted(() => {
  // Limpa seleção ao sair da tela
  conversaStore.limparSelecao()
})

function handleRefresh() {
  conversaStore.carregarConversas()
}
</script>

<template>
  <div class="h-[calc(100vh-7rem)] flex gap-4 animate-fade-in">
    <div class="w-80 min-w-[280px] flex flex-col bg-slate-900 border border-slate-800 rounded-xl overflow-hidden shrink-0">
      <div class="p-4 border-b border-slate-800">
        <div class="flex items-center justify-between mb-3">
          <h2 class="font-semibold text-white">Conversas</h2>
          <button 
            @click="handleRefresh" 
            class="btn-ghost p-2"
            :class="{ 'animate-spin': conversaStore.loading }"
            title="Atualizar"
          >
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
            </svg>
          </button>
        </div>
        <div class="relative">
          <svg 
            class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-500" 
            fill="none" 
            stroke="currentColor" 
            viewBox="0 0 24 24"
          >
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <input 
            v-model="filtro"
            type="text" 
            placeholder="Buscar conversa..."
            class="input pl-10 py-2"
          />
        </div>
      </div>

      <ConversaList :filtro="filtro" />
    </div>

    <div class="flex-1">
      <ChatWindow />
    </div>
  </div>
</template>