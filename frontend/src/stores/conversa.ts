import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { atendimentoService } from '@/services/atendimentoService'
import type { ConversaResumo, Mensagem } from '@/types'
// @ts-ignore
import SockJS from 'sockjs-client/dist/sockjs'
// @ts-ignore
import Stomp from 'stompjs'

export const useConversaStore = defineStore('conversa', () => {
  // Estado
  const conversas = ref<ConversaResumo[]>([])
  const conversaSelecionada = ref<ConversaResumo | null>(null)
  const mensagens = ref<Mensagem[]>([])
  const loading = ref(false)
  const loadingMensagens = ref(false)

  // Estado do WebSocket
  const stompClient = ref<any>(null)
  const currentChatSubscription = ref<any>(null)

  const totalNaoLidas = computed(() => 
    conversas.value.reduce((acc, c) => acc + c.naoLidas, 0)
  )

  // --- ACTIONS HTTP ---

  async function carregarConversas(exibirLoading = true) {
    if (exibirLoading) loading.value = true
    try {
      conversas.value = await atendimentoService.listarConversas()
    } finally {
      if (exibirLoading) loading.value = false
    }
  }

  async function selecionarConversa(conversa: ConversaResumo) {
    // 1. Troca a conversa visualmente
    conversaSelecionada.value = conversa
    loadingMensagens.value = true
    
    // 2. Carrega mensagens via HTTP (histórico)
    try {
      mensagens.value = await atendimentoService.buscarMensagens(conversa.conversaId)
      if (conversa.naoLidas > 0) {
        await atendimentoService.marcarComoLida(conversa.conversaId)
        conversa.naoLidas = 0
      }
    } finally {
      loadingMensagens.value = false
    }

    // 3. Lógica do WebSocket: Assinar o tópico desta conversa específica
    inscreverNoChat(conversa.conversaId)
  }

  // --- LÓGICA WEBSOCKET ---

  function conectarWebSocket() {
    if (stompClient.value && stompClient.value.connected) return

    // Ajuste a URL se seu backend não estiver no localhost:8080
    const socket = new SockJS('http://localhost:8080/ws')
    const client = Stomp.over(socket)
    
    // client.debug = () => {} // Descomente para silenciar logs no console

    client.connect({}, () => {
      console.log('✅ WebSocket Conectado!')
      stompClient.value = client

      // 1. OUVIR O PAINEL (Atualizações da lista lateral)
      client.subscribe('/topic/painel', () => {
        console.log('🔔 Atualização recebida no painel')
        carregarConversas(false) // Recarrega lista sem loading spinner
      })

      // Se reconectar e já tiver conversa aberta, reassina
      if (conversaSelecionada.value) {
        inscreverNoChat(conversaSelecionada.value.conversaId)
      }

    }, (error: any) => {
      console.error('❌ Erro no WebSocket. Reconectando em 5s...', error)
      setTimeout(conectarWebSocket, 5000)
    })
  }

  function inscreverNoChat(conversaId: number) {
    if (!stompClient.value || !stompClient.value.connected) return

    // Se já ouvia outro chat, cancela para não misturar
    if (currentChatSubscription.value) {
      currentChatSubscription.value.unsubscribe()
      currentChatSubscription.value = null
    }

    // Assina o tópico específico da conversa
    currentChatSubscription.value = stompClient.value.subscribe(`/topic/conversa/${conversaId}`, () => {
      console.log(`🔔 Nova mensagem na conversa ${conversaId}`)
      // Atualiza as mensagens imediatamente
      atendimentoService.buscarMensagens(conversaId).then(msgs => {
        mensagens.value = msgs
        // Aqui você pode tocar o som de notificação se quiser
      })
    })
  }

  // --- ACTIONS DE MENSAGEM ---

  async function enviarMensagem(texto: string) {
    if (!conversaSelecionada.value) return
    // Envia via API (o Socket vai receber a notificação de volta e atualizar a tela)
    await atendimentoService.responder(conversaSelecionada.value.conversaId, { texto })
  }

  async function encerrarConversa() {
    if (!conversaSelecionada.value) return
    
    await atendimentoService.encerrar(conversaSelecionada.value.conversaId)
    
    if (currentChatSubscription.value) {
        currentChatSubscription.value.unsubscribe()
    }
    
    await carregarConversas()
    limparSelecao()
  }

  async function transferirConversa(atendenteId: number) {
    if (!conversaSelecionada.value) return
    await atendimentoService.transferir(conversaSelecionada.value.conversaId, atendenteId)
    
    if (currentChatSubscription.value) {
        currentChatSubscription.value.unsubscribe()
    }
    await carregarConversas()
    limparSelecao()
  }

  function limparSelecao() {
    conversaSelecionada.value = null
    mensagens.value = []
    if (currentChatSubscription.value) {
      currentChatSubscription.value.unsubscribe()
      currentChatSubscription.value = null
    }
  }

  return {
    conversas,
    conversaSelecionada,
    mensagens,
    loading,
    loadingMensagens,
    totalNaoLidas,
    conectarWebSocket, // Exporta a função
    carregarConversas,
    selecionarConversa,
    enviarMensagem,
    encerrarConversa,
    transferirConversa,
    limparSelecao
  }
})