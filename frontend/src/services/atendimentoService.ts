import api from './api'
import type { ConversaResumo, Mensagem, Dashboard, ResponderMensagem } from '@/types'
import type { Page } from '@/types/index'

export const atendimentoService = {
  async listarConversas(): Promise<ConversaResumo[]> {
    const response = await api.get<ConversaResumo[]>('/painel/atendimento/conversas')
    return response.data
  },

  async buscarMensagens(conversaId: number, page:number=0, size:number=20):  Promise<Page<Mensagem>> {
    const response = await api.get<Page<Mensagem>>(`/painel/atendimento/conversas/${conversaId}/mensagens?page=${page}&size=${size}`)
    return response.data
  },

  async responder(conversaId: number, dto: ResponderMensagem): Promise<void> {
    await api.post(`/painel/atendimento/conversas/${conversaId}/responder`, dto)
  },

  async encerrar(conversaId: number): Promise<void> {
    await api.post(`/painel/atendimento/conversas/${conversaId}/encerrar`)
  },

  async transferir(conversaId: number, atendenteId: number): Promise<void> {
    await api.patch(`/painel/atendimento/conversas/${conversaId}/transferir/${atendenteId}`)
  },

  async marcarComoLida(conversaId: number): Promise<void> {
    await api.patch(`/painel/atendimento/conversas/${conversaId}/ler`)
  },

  async getDashboard(): Promise<Dashboard> {
    const response = await api.get<Dashboard>('/painel/atendimento/dashboard')
    return response.data
  }
}
