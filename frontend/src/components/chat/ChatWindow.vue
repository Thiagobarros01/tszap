<script setup lang="ts">
import { ref, watch, nextTick, onMounted } from 'vue'
import { useConversaStore } from '@/stores/conversa'
import { usuarioService } from '@/services/usuarioService'
import MessageBubble from './MessageBubble.vue'
import type { UsuarioResumo } from '@/types'

const conversaStore = useConversaStore()
const messagesContainer = ref<HTMLElement | null>(null)
const novoTexto = ref('')
const sending = ref(false)
const showTransferModal = ref(false)
const usuarios = ref<UsuarioResumo[]>([])
const loadingUsuarios = ref(false)

function scrollToBottom() {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

watch(() => conversaStore.mensagens, scrollToBottom, { deep: true })

async function enviarMensagem() {
  if (!novoTexto.value.trim() || sending.value) return

  sending.value = true
  try {
    await conversaStore.enviarMensagem(novoTexto.value.trim())
    novoTexto.value = ''
  } finally {
    sending.value = false
  }
}

async function encerrarConversa() {
  if (!confirm('Deseja encerrar esta conversa?')) return
  await conversaStore.encerrarConversa()
}

async function abrirTransferModal() {
  showTransferModal.value = true
  loadingUsuarios.value = true
  try {
    usuarios.value = await usuarioService.listar()
  } finally {
    loadingUsuarios.value = false
  }
}

async function transferirPara(usuarioId: number) {
  await conversaStore.transferirConversa(usuarioId)
  showTransferModal.value = false
}

function handleKeydown(event: KeyboardEvent) {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault()
    enviarMensagem()
  }
}

onMounted(() => {
  scrollToBottom()
})
</script>

<template>
  <div class="flex flex-col h-full bg-slate-900 rounded-xl overflow-hidden">
    <!-- No conversation selected -->
    <div 
      v-if="!conversaStore.conversaSelecionada" 
      class="flex-1 flex flex-col items-center justify-center text-slate-500 p-8"
    >
      <div class="w-24 h-24 bg-slate-800 rounded-full flex items-center justify-center mb-4">
        <svg class="w-12 h-12 opacity-50" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
        </svg>
      </div>
      <h3 class="text-lg font-medium text-slate-400 mb-2">Selecione uma conversa</h3>
      <p class="text-sm text-center max-w-xs">
        Escolha uma conversa na lista ao lado para visualizar as mensagens e interagir com o cliente
      </p>
    </div>

    <!-- Chat content -->
    <template v-else>
      <!-- Header -->
      <div class="flex items-center justify-between px-4 py-3 bg-slate-800 border-b border-slate-700">
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 bg-slate-700 rounded-full flex items-center justify-center">
            <span class="text-sm font-semibold text-slate-300">
              {{ (conversaStore.conversaSelecionada.nomeCliente || 'C').charAt(0).toUpperCase() }}
            </span>
          </div>
          <div>
            <p class="font-medium text-white">
              {{ conversaStore.conversaSelecionada.nomeCliente || 'Cliente' }}
            </p>
            <p class="text-sm text-slate-400">
              {{ conversaStore.conversaSelecionada.telefoneCliente }}
            </p>
          </div>
        </div>

        <div class="flex items-center gap-2">
          <button 
            @click="abrirTransferModal"
            class="btn-ghost p-2"
            title="Transferir conversa"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4" />
            </svg>
          </button>
          <button 
            @click="encerrarConversa"
            class="btn-danger p-2"
            title="Encerrar conversa"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
      </div>

      <!-- Messages -->
      <div 
        ref="messagesContainer"
        class="flex-1 overflow-y-auto scrollbar-thin p-4 bg-slate-950/50"
        style="background-image: url('data:image/svg+xml,%3Csvg width=\'60\' height=\'60\' viewBox=\'0 0 60 60\' xmlns=\'http://www.w3.org/2000/svg\'%3E%3Cg fill=\'none\' fill-rule=\'evenodd\'%3E%3Cg fill=\'%23334155\' fill-opacity=\'0.05\'%3E%3Cpath d=\'M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z\'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E');"
      >
        <!-- Loading -->
        <div v-if="conversaStore.loadingMensagens" class="flex justify-center py-8">
          <div class="animate-spin w-8 h-8 border-2 border-whatsapp border-t-transparent rounded-full"></div>
        </div>

        <!-- Messages -->
        <template v-else>
          <div v-if="conversaStore.mensagens.length === 0" class="text-center text-slate-500 py-8">
            Nenhuma mensagem nesta conversa
          </div>
          <MessageBubble 
            v-for="(msg, index) in conversaStore.mensagens" 
            :key="index" 
            :mensagem="msg" 
          />
        </template>
      </div>

      <!-- Input -->
      <div class="p-4 bg-slate-800 border-t border-slate-700">
        <form @submit.prevent="enviarMensagem" class="flex gap-3">
          <textarea
            v-model="novoTexto"
            @keydown="handleKeydown"
            placeholder="Digite sua mensagem..."
            rows="1"
            class="input flex-1 resize-none max-h-32"
          ></textarea>
          <button 
            type="submit"
            :disabled="!novoTexto.trim() || sending"
            class="btn-primary px-4"
          >
            <svg 
              v-if="sending" 
              class="animate-spin w-5 h-5" 
              fill="none" 
              viewBox="0 0 24 24"
            >
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"></path>
            </svg>
            <svg v-else class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
            </svg>
          </button>
        </form>
      </div>
    </template>

    <!-- Transfer Modal -->
    <Teleport to="body">
      <div 
        v-if="showTransferModal" 
        class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4"
        @click.self="showTransferModal = false"
      >
        <div class="bg-slate-900 border border-slate-800 rounded-xl w-full max-w-md animate-slide-up">
          <div class="flex items-center justify-between p-4 border-b border-slate-800">
            <h3 class="text-lg font-semibold text-white">Transferir Conversa</h3>
            <button @click="showTransferModal = false" class="btn-ghost p-1">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
          
          <div class="p-4 max-h-96 overflow-y-auto">
            <div v-if="loadingUsuarios" class="flex justify-center py-8">
              <div class="animate-spin w-6 h-6 border-2 border-whatsapp border-t-transparent rounded-full"></div>
            </div>
            
            <div v-else-if="usuarios.length === 0" class="text-center text-slate-500 py-4">
              Nenhum atendente disponível
            </div>
            
            <div v-else class="space-y-2">
              <button
                v-for="usuario in usuarios"
                :key="usuario.id"
                @click="transferirPara(usuario.id)"
                class="w-full flex items-center gap-3 p-3 rounded-lg hover:bg-slate-800 transition-colors text-left"
              >
                <div class="w-10 h-10 bg-slate-700 rounded-full flex items-center justify-center">
                  <span class="text-sm font-semibold text-slate-300">
                    {{ usuario.login.charAt(0).toUpperCase() }}
                  </span>
                </div>
                <div>
                  <p class="font-medium text-white">{{ usuario.login }}</p>
                  <p class="text-sm text-slate-400">{{ usuario.departamento }}</p>
                </div>
              </button>
            </div>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>
