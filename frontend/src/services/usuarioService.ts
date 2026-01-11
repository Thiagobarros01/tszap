import api from './api'
import type { Usuario, UsuarioResumo,AtualizarUsuarioDTO } from '@/types'

export const usuarioService = {
  async listar(): Promise<UsuarioResumo[]> {
    const response = await api.get<UsuarioResumo[]>('/usuarios')
    return response.data
  },

  async salvar(usuario: Usuario): Promise<void> {
    await api.post('/usuarios', usuario)
  },

  async atualizar(id: number, usuario: AtualizarUsuarioDTO): Promise<UsuarioResumo> {
    const response = await api.put<UsuarioResumo>(`/usuarios/${id}`, usuario)
    return response.data
}
}
