import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authService } from '@/services/authService'
import type { LoginRequest, AuthUser, Role, Departamento } from '@/types'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('token'))
  const user = ref<AuthUser | null>(null)

  const isAuthenticated = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')

  function parseJwt(tokenStr: string): AuthUser | null {
    try {
      const base64Url = tokenStr.split('.')[1]
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
      const jsonPayload = decodeURIComponent(
        atob(base64)
          .split('')
          .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      )
      const payload = JSON.parse(jsonPayload)
      return {
        id: payload.usuarioId || payload.sub,
        login: payload.sub,
        role: (payload.scope || payload.role) as Role,
        departamento: payload.departamento as Departamento
      }
    } catch {
      return null
    }
  }

  function initializeFromToken() {
    if (token.value) {
      user.value = parseJwt(token.value)
    }
  }

  async function login(credentials: LoginRequest): Promise<void> {
    const tokenResponse = await authService.login(credentials)
    token.value = tokenResponse
    localStorage.setItem('token', tokenResponse)
    user.value = parseJwt(tokenResponse)
  }

  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
  }

  // Inicializa o usuário se já houver token
  initializeFromToken()

  return {
    token,
    user,
    isAuthenticated,
    isAdmin,
    login,
    logout
  }
})
