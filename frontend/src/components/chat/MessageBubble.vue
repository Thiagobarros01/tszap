<script setup lang="ts">
import { computed } from 'vue'
import { OrigemMensagem, type Mensagem } from '@/types'

const props = defineProps<{
  mensagem: Mensagem
}>()

const isCliente = computed(() => props.mensagem.origem === OrigemMensagem.CLIENTE)
const isBot = computed(() => props.mensagem.origem === OrigemMensagem.BOT)

function formatDate(dateStr: string): string {
  const date = new Date(dateStr)
  return date.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' })
}

function formatFullDate(dateStr: string): string {
  const date = new Date(dateStr)
  return date.toLocaleDateString('pt-BR', { 
    day: '2-digit', 
    month: '2-digit',
    hour: '2-digit', 
    minute: '2-digit' 
  })
}
</script>

<template>
  <div 
    :class="[
      'flex mb-3 animate-slide-up',
      isCliente ? 'justify-start' : 'justify-end'
    ]"
  >
    <div 
      :class="[
        'max-w-[75%] rounded-2xl px-4 py-2.5 relative',
        isCliente 
          ? 'bg-slate-800 rounded-tl-sm' 
          : isBot 
            ? 'bg-blue-600/80 rounded-tr-sm'
            : 'bg-whatsapp-dark rounded-tr-sm'
      ]"
    >
      <!-- Sender info for non-client messages -->
      <p 
        v-if="!isCliente && mensagem.nomeAtendente" 
        class="text-xs font-medium mb-1"
        :class="isBot ? 'text-blue-200' : 'text-whatsapp-light'"
      >
        {{ isBot ? 'Bot' : mensagem.nomeAtendente }}
      </p>

      <!-- Message text -->
      <p class="text-white whitespace-pre-wrap break-words">{{ mensagem.texto }}</p>

      <!-- Time and Read Status -->
      <div 
        :class="[
          'flex items-center justify-end gap-1 mt-1',
          isCliente ? 'text-slate-500' : 'text-white/60'
        ]"
      >
        <span class="text-xs" :title="formatFullDate(mensagem.data)">
          {{ formatDate(mensagem.data) }}
        </span>
        <!-- Double check for read messages, single check for sent -->
        <template v-if="!isCliente">
          <!-- Double check (read) -->
          <svg 
            v-if="mensagem.lida" 
            class="w-4 h-4 text-blue-400" 
            viewBox="0 0 24 24" 
            fill="none" 
            stroke="currentColor"
            title="Lida"
          >
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7M5 19l4 4L19 13" />
          </svg>
          <!-- Single check (sent but not read) -->
          <svg 
            v-else 
            class="w-4 h-4 text-slate-400" 
            viewBox="0 0 24 24" 
            fill="none" 
            stroke="currentColor"
            title="Enviada"
          >
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
          </svg>
        </template>
      </div>
    </div>
  </div>
</template>
