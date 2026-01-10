<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const login = ref('')
const senha = ref('')
const loading = ref(false)
const error = ref('')

async function handleSubmit() {
  if (!login.value || !senha.value) {
    error.value = 'Preencha todos os campos'
    return
  }

  loading.value = true
  error.value = ''

  try {
    await authStore.login({ login: login.value, senha: senha.value })
    router.push('/')
  } catch (e: any) {
    error.value = e.response?.data?.message || 'Credenciais inválidas'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-slate-950 px-4">
    <!-- Background Pattern -->
    <div class="absolute inset-0 overflow-hidden">
      <div class="absolute -top-1/2 -left-1/2 w-full h-full bg-gradient-to-br from-whatsapp/5 via-transparent to-transparent rotate-12"></div>
      <div class="absolute -bottom-1/2 -right-1/2 w-full h-full bg-gradient-to-tl from-whatsapp-dark/5 via-transparent to-transparent -rotate-12"></div>
    </div>

    <div class="relative w-full max-w-md animate-fade-in">
      <!-- Logo Card -->
      <div class="text-center mb-8">
        <div class="inline-flex items-center justify-center w-20 h-20 bg-whatsapp rounded-2xl mb-4 shadow-lg shadow-whatsapp/20">
          <svg class="w-12 h-12 text-white" fill="currentColor" viewBox="0 0 24 24">
            <path d="M17.472 14.382c-.297-.149-1.758-.867-2.03-.967-.273-.099-.471-.148-.67.15-.197.297-.767.966-.94 1.164-.173.199-.347.223-.644.075-.297-.15-1.255-.463-2.39-1.475-.883-.788-1.48-1.761-1.653-2.059-.173-.297-.018-.458.13-.606.134-.133.298-.347.446-.52.149-.174.198-.298.298-.497.099-.198.05-.371-.025-.52-.075-.149-.669-1.612-.916-2.207-.242-.579-.487-.5-.669-.51-.173-.008-.371-.01-.57-.01-.198 0-.52.074-.792.372-.272.297-1.04 1.016-1.04 2.479 0 1.462 1.065 2.875 1.213 3.074.149.198 2.096 3.2 5.077 4.487.709.306 1.262.489 1.694.625.712.227 1.36.195 1.871.118.571-.085 1.758-.719 2.006-1.413.248-.694.248-1.289.173-1.413-.074-.124-.272-.198-.57-.347z"/>
          </svg>
        </div>
        <h1 class="text-3xl font-bold text-white mb-2">ConversaZap</h1>
        <p class="text-slate-400">Entre para acessar o painel de atendimento</p>
      </div>

      <!-- Login Form -->
      <div class="card">
        <form @submit.prevent="handleSubmit" class="space-y-5">
          <!-- Error Alert -->
          <div 
            v-if="error" 
            class="bg-red-500/10 border border-red-500/20 text-red-400 px-4 py-3 rounded-lg text-sm flex items-center gap-2"
          >
            <svg class="w-5 h-5 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            {{ error }}
          </div>

          <div>
            <label for="login" class="label">Usuário</label>
            <input
              id="login"
              v-model="login"
              type="text"
              class="input"
              placeholder="Digite seu usuário"
              autocomplete="username"
            />
          </div>

          <div>
            <label for="senha" class="label">Senha</label>
            <input
              id="senha"
              v-model="senha"
              type="password"
              class="input"
              placeholder="Digite sua senha"
              autocomplete="current-password"
            />
          </div>

          <button
            type="submit"
            :disabled="loading"
            class="btn-primary w-full py-3"
          >
            <svg 
              v-if="loading" 
              class="animate-spin -ml-1 mr-2 h-5 w-5" 
              fill="none" 
              viewBox="0 0 24 24"
            >
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            {{ loading ? 'Entrando...' : 'Entrar' }}
          </button>
        </form>
      </div>

      <p class="text-center text-slate-600 text-sm mt-6">
        Sistema de Atendimento via WhatsApp
      </p>
    </div>
  </div>
</template>
