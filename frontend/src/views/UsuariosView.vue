<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { usuarioService } from '@/services/usuarioService'
import { useAuthStore } from '@/stores/auth'
import Modal from '@/components/common/Modal.vue'
import type { UsuarioResumo, Usuario } from '@/types'
import { Role, Departamento } from '@/types'

const authStore = useAuthStore()
const usuarios = ref<UsuarioResumo[]>([])
const loading = ref(true)
const showModal = ref(false)
const saving = ref(false)
const error = ref('')

const form = ref<Usuario>({
  login: '',
  senha: '',
  email: '',
  role: Role.ATENDENTE,
  departamento: Departamento.COMERCIAL
})

const roles = [
  { value: Role.ADMIN, label: 'Administrador' },
  { value: Role.ATENDENTE, label: 'Atendente' }
]

const departamentos = [
  { value: Departamento.COMERCIAL, label: 'Comercial' },
  { value: Departamento.SUPORTE, label: 'Suporte' },
  { value: Departamento.FINANCEIRO, label: 'Financeiro' },
  { value: Departamento.LOGISTICA, label: 'Logística' }
]

async function loadUsuarios() {
  loading.value = true
  try {
    usuarios.value = await usuarioService.listar()
  } catch (e) {
    console.error('Erro ao carregar usuários:', e)
  } finally {
    loading.value = false
  }
}

function openNewModal() {
  form.value = {
    login: '',
    senha: '',
    email: '',
    role: Role.ATENDENTE,
    departamento: Departamento.COMERCIAL
  }
  error.value = ''
  showModal.value = true
}

function closeModal() {
  showModal.value = false
}

async function handleSubmit() {
  if (!form.value.login || !form.value.senha || !form.value.email) {
    error.value = 'Todos os campos são obrigatórios'
    return
  }

  saving.value = true
  error.value = ''

  try {
    await usuarioService.salvar(form.value)
    await loadUsuarios()
    closeModal()
  } catch (e: any) {
    error.value = e.response?.data?.message || 'Erro ao salvar usuário'
  } finally {
    saving.value = false
  }
}

function getRoleLabel(role: Role): string {
  return role === Role.ADMIN ? 'Administrador' : 'Atendente'
}

function getRoleBadgeClass(role: Role): string {
  return role === Role.ADMIN 
    ? 'bg-purple-500/20 text-purple-400' 
    : 'bg-blue-500/20 text-blue-400'
}

function getDepartamentoLabel(dep: Departamento): string {
  const labels: Record<Departamento, string> = {
    [Departamento.COMERCIAL]: 'Comercial',
    [Departamento.SUPORTE]: 'Suporte',
    [Departamento.FINANCEIRO]: 'Financeiro',
    [Departamento.LOGISTICA]: 'Logística'
  }
  return labels[dep] || dep
}

onMounted(() => {
  loadUsuarios()
})
</script>

<template>
  <div class="space-y-6 animate-fade-in">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <div>
        <p class="text-slate-400">Gerencie os usuários e atendentes do sistema</p>
      </div>
      <button 
        v-if="authStore.isAdmin" 
        @click="openNewModal" 
        class="btn-primary"
      >
        <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        Novo Usuário
      </button>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="card">
      <div class="animate-pulse space-y-4">
        <div class="h-4 bg-slate-700 rounded w-1/4"></div>
        <div v-for="i in 5" :key="i" class="h-12 bg-slate-800 rounded"></div>
      </div>
    </div>

    <!-- Empty State -->
    <div v-else-if="usuarios.length === 0" class="card text-center py-12">
      <svg class="w-16 h-16 mx-auto text-slate-600 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
      </svg>
      <h3 class="text-lg font-medium text-slate-400 mb-2">Nenhum usuário cadastrado</h3>
      <p class="text-slate-500">A lista de usuários está vazia</p>
    </div>

    <!-- Table -->
    <div v-else class="card overflow-hidden p-0">
      <table class="w-full">
        <thead class="bg-slate-800">
          <tr>
            <th class="text-left px-6 py-4 text-sm font-medium text-slate-400">Usuário</th>
            <th class="text-left px-6 py-4 text-sm font-medium text-slate-400">Email</th>
            <th class="text-left px-6 py-4 text-sm font-medium text-slate-400">Perfil</th>
            <th class="text-left px-6 py-4 text-sm font-medium text-slate-400">Departamento</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-slate-800">
          <tr 
            v-for="usuario in usuarios" 
            :key="usuario.id"
            class="hover:bg-slate-800/50 transition-colors"
          >
            <td class="px-6 py-4">
              <div class="flex items-center gap-3">
                <div class="w-10 h-10 bg-slate-700 rounded-full flex items-center justify-center">
                  <span class="text-sm font-semibold text-slate-300">
                    {{ usuario.login.charAt(0).toUpperCase() }}
                  </span>
                </div>
                <span class="font-medium text-white">{{ usuario.login }}</span>
              </div>
            </td>
            <td class="px-6 py-4 text-slate-300">{{ usuario.email }}</td>
            <td class="px-6 py-4">
              <span :class="['badge', getRoleBadgeClass(usuario.role)]">
                {{ getRoleLabel(usuario.role) }}
              </span>
            </td>
            <td class="px-6 py-4 text-slate-300">
              {{ getDepartamentoLabel(usuario.departamento) }}
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal -->
    <Modal 
      :show="showModal" 
      title="Novo Usuário"
      @close="closeModal"
    >
      <form @submit.prevent="handleSubmit" class="space-y-4">
        <!-- Error -->
        <div 
          v-if="error" 
          class="bg-red-500/10 border border-red-500/20 text-red-400 px-4 py-3 rounded-lg text-sm"
        >
          {{ error }}
        </div>

        <div>
          <label class="label">Login *</label>
          <input 
            v-model="form.login" 
            type="text" 
            class="input" 
            placeholder="Nome de usuário"
          />
        </div>

        <div>
          <label class="label">Senha *</label>
          <input 
            v-model="form.senha" 
            type="password" 
            class="input" 
            placeholder="Senha"
          />
        </div>

        <div>
          <label class="label">Email *</label>
          <input 
            v-model="form.email" 
            type="email" 
            class="input" 
            placeholder="email@exemplo.com"
          />
        </div>

        <div>
          <label class="label">Perfil *</label>
          <select v-model="form.role" class="input">
            <option v-for="role in roles" :key="role.value" :value="role.value">
              {{ role.label }}
            </option>
          </select>
        </div>

        <div>
          <label class="label">Departamento *</label>
          <select v-model="form.departamento" class="input">
            <option v-for="dep in departamentos" :key="dep.value" :value="dep.value">
              {{ dep.label }}
            </option>
          </select>
        </div>

        <div class="flex justify-end gap-3 pt-4">
          <button type="button" @click="closeModal" class="btn-secondary">
            Cancelar
          </button>
          <button type="submit" :disabled="saving" class="btn-primary">
            <svg 
              v-if="saving" 
              class="animate-spin -ml-1 mr-2 h-4 w-4" 
              fill="none" 
              viewBox="0 0 24 24"
            >
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"></path>
            </svg>
            {{ saving ? 'Salvando...' : 'Salvar' }}
          </button>
        </div>
      </form>
    </Modal>
  </div>
</template>
