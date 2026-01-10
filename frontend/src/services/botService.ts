import api from './api'
import type { EtapaBot } from '@/types'

export const botService = {
  async listarEtapas(): Promise<EtapaBot[]> {
    const response = await api.get<EtapaBot[]>('/painel/bot/config')
    return response.data
  },

  // NOVO: Método específico para CRIAR (POST)
  async criarEtapa(etapa: EtapaBot): Promise<EtapaBot> {
    const response = await api.post<EtapaBot>('/painel/bot/config', etapa)
    return response.data
  },

  // NOVO: Método específico para ATUALIZAR (PUT)
  async atualizarEtapa(id: number, etapa: EtapaBot): Promise<EtapaBot> {
    const response = await api.put<EtapaBot>(`/painel/bot/config/${id}`, etapa)
    return response.data
  },

  async removerEtapa(id: number): Promise<void> {
    await api.delete(`/painel/bot/config/${id}`)
  }
}