import api from './api'
import type { Cliente, ClienteResponse } from '@/types'

export const clienteService = {
  async listar(): Promise<ClienteResponse[]> {
    const response = await api.get<ClienteResponse[]>('/clientes')
    return response.data
  },

  async obterPorId(id: number): Promise<ClienteResponse> {
    const response = await api.get<ClienteResponse>(`/clientes/${id}`)
    return response.data
  },

  async salvar(cliente: Cliente): Promise<ClienteResponse> {
    const response = await api.post<ClienteResponse>('/clientes', cliente)
    return response.data
  },

  async atualizar(id: number, cliente: Cliente): Promise<ClienteResponse> {
    const response = await api.put<ClienteResponse>(`/clientes/${id}`, cliente)
    return response.data
  }
}
