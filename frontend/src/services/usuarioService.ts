import api from './api'
import type { Usuario, UsuarioResumo } from '@/types'

export const usuarioService = {
  async listar(): Promise<UsuarioResumo[]> {
    const response = await api.get<UsuarioResumo[]>('/usuarios')
    return response.data
  },

  async salvar(usuario: Usuario): Promise<void> {
    await api.post('/usuarios', usuario)
  }
}
