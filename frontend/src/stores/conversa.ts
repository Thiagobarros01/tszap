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
      // ATENÇÃO: Usei 'id' aqui pq o Java manda 'id'. Confirme no seu types/index.ts
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

  // --- LÓGICA WEBSOCKET & ORDENAÇÃO (O SEGREDO) ---

  function moverParaTopo(idConversa: number) {
    // Procura a conversa na lista atual (memória)
    const index = conversas.value.findIndex(c => c.conversaId === idConversa)

    if (index > -1) {
      // 1. Remove da posição atual e guarda
      const [conversa] = conversas.value.splice(index, 1)
      
      // 2. Se não for a conversa que estou lendo agora, aumenta o contador de não lidas
      if (!conversaSelecionada.value || conversaSelecionada.value.conversaId !== idConversa) {
          conversa.naoLidas++
      }

      // 3. Coloca no topo da lista (início do array)
      conversas.value.unshift(conversa)
    } else {
      // Se não achou (é conversa nova que não estava na lista), busca do servidor
      console.log('Conversa nova detectada, recarregando lista...')
      carregarConversas(false)
    }
  }

  function conectarWebSocket() {
    if (stompClient.value && stompClient.value.connected) return

    const socket = new SockJS('http://localhost:8080/ws')
    const client = Stomp.over(socket)
    
    // client.debug = () => {} // Descomente para silenciar logs no console

    client.connect({}, () => {
      console.log('✅ WebSocket Conectado!')
      stompClient.value = client

      // 1. OUVIR O PAINEL (Atualizações da lista lateral)
      client.subscribe('/topic/painel', (msg: any) => {
        // O backend agora manda o ID da conversa no corpo da mensagem
        const idConversa = parseInt(msg.body)

        if (!isNaN(idConversa)) {
            // Mágica: Sobe pro topo sem ir no servidor
            moverParaTopo(idConversa)
        } else {
            // Fallback: se não vier ID, recarrega tudo
            carregarConversas(false) 
        }
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

  function inscreverNoChat(idConversa: number) {
    if (!stompClient.value || !stompClient.value.connected) return

    // Se já ouvia outro chat, cancela para não misturar
    if (currentChatSubscription.value) {
      currentChatSubscription.value.unsubscribe()
      currentChatSubscription.value = null
    }

    // Assina o tópico específico da conversa
    currentChatSubscription.value = stompClient.value.subscribe(`/topic/conversa/${idConversa}`, () => {
      console.log(`🔔 Nova mensagem na conversa ${idConversa}`)
      // Atualiza as mensagens imediatamente
      atendimentoService.buscarMensagens(idConversa).then(msgs => {
        mensagens.value = msgs
        // Opcional: Tocar som aqui
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
    
    limparSelecao()
    // Recarrega para remover da lista de abertas
    await carregarConversas()
  }

  async function transferirConversa(atendenteId: number) {
    if (!conversaSelecionada.value) return
    await atendimentoService.transferir(conversaSelecionada.value.conversaId, atendenteId)
    
    limparSelecao()
    await carregarConversas()
  }

  function limparSelecao() {
    if (currentChatSubscription.value) {
      currentChatSubscription.value.unsubscribe()
      currentChatSubscription.value = null
    }
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
    conectarWebSocket, // Exporta a função
    carregarConversas,
    selecionarConversa,
    enviarMensagem,
    encerrarConversa,
    transferirConversa,
    limparSelecao,
    moverParaTopo
  }
})