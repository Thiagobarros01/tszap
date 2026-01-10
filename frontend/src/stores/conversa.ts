import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { atendimentoService } from '@/services/atendimentoService'
import type { ConversaResumo, Mensagem } from '@/types'

export const useConversaStore = defineStore('conversa', () => {
  const conversas = ref<ConversaResumo[]>([])
  const conversaSelecionada = ref<ConversaResumo | null>(null)
  const mensagens = ref<Mensagem[]>([])
  const loading = ref(false)
  const loadingMensagens = ref(false)

  const totalNaoLidas = computed(() => 
    conversas.value.reduce((acc, c) => acc + c.naoLidas, 0)
  )

  async function carregarConversas() {
    loading.value = true
    try {
      conversas.value = await atendimentoService.listarConversas()
    } finally {
      loading.value = false
    }
  }

  async function selecionarConversa(conversa: ConversaResumo) {
    conversaSelecionada.value = conversa
    loadingMensagens.value = true
    try {
      mensagens.value = await atendimentoService.buscarMensagens(conversa.conversaId)
      if (conversa.naoLidas > 0) {
        await atendimentoService.marcarComoLida(conversa.conversaId)
        conversa.naoLidas = 0
      }
    } finally {
      loadingMensagens.value = false
    }
  }

  async function enviarMensagem(texto: string) {
    if (!conversaSelecionada.value) return
    
    await atendimentoService.responder(conversaSelecionada.value.conversaId, { texto })
    mensagens.value = await atendimentoService.buscarMensagens(conversaSelecionada.value.conversaId)
  }

  async function encerrarConversa() {
    if (!conversaSelecionada.value) return
    
    await atendimentoService.encerrar(conversaSelecionada.value.conversaId)
    await carregarConversas()
    conversaSelecionada.value = null
    mensagens.value = []
  }

  async function transferirConversa(atendenteId: number) {
    if (!conversaSelecionada.value) return
    
    await atendimentoService.transferir(conversaSelecionada.value.conversaId, atendenteId)
    await carregarConversas()
  }

  function limparSelecao() {
    conversaSelecionada.value = null
    mensagens.value = []
  }

  return {
    conversas,
    conversaSelecionada,
    mensagens,
    loading,
    loadingMensagens,
    totalNaoLidas,
    carregarConversas,
    selecionarConversa,
    enviarMensagem,
    encerrarConversa,
    transferirConversa,
    limparSelecao
  }
})
