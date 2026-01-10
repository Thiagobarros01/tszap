import api from './api'
import type { LoginRequest } from '@/types'

export const authService = {
  async login(credentials: LoginRequest): Promise<string> {
    const response = await api.post<string>('/auth/login', credentials)
    return response.data
  }
}
