import api from './api'
import type { EtapaBot } from '@/types'

export const botService = {
  async listarEtapas(): Promise<EtapaBot[]> {
    const response = await api.get<EtapaBot[]>('/painel/bot/config')
    return response.data
  },

  async salvarEtapa(etapa: EtapaBot): Promise<EtapaBot> {
    const response = await api.post<EtapaBot>('/painel/bot/config', etapa)
    return response.data
  },

  async removerEtapa(id: number): Promise<void> {
    await api.delete(`/painel/bot/config/${id}`)
  }
}
