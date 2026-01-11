<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { clienteService } from '@/services/clienteService'
import Modal from '@/components/common/Modal.vue'
import type { ClienteResponse, Cliente } from '@/types'

const clientes = ref<ClienteResponse[]>([])
const loading = ref(true)
const showModal = ref(false)
const editingCliente = ref<ClienteResponse | null>(null)
const saving = ref(false)
const error = ref('')

const form = ref<Cliente>({
  nome: '',
  telefone: '',
  email: ''
})

async function loadClientes() {
  loading.value = true
  try {
    clientes.value = await clienteService.listar()
  } catch (e) {
    console.error('Erro ao carregar clientes:', e)
  } finally {
    loading.value = false
  }
}

function openNewModal() {
  editingCliente.value = null
  form.value = { nome: '', telefone: '', email: '' }
  error.value = ''
  showModal.value = true
}

function openEditModal(cliente: ClienteResponse) {
  editingCliente.value = cliente
  form.value = {
    nome: cliente.nome,
    telefone: cliente.telefone,
    email: cliente.email || ''
  }
  error.value = ''
  showModal.value = true
}

function closeModal() {
  showModal.value = false
  editingCliente.value = null
}

async function handleSubmit() {
  if (!form.value.nome || !form.value.telefone) {
    error.value = 'Nome e telefone são obrigatórios'
    return
  }

  saving.value = true
  error.value = ''

  try {
    if (editingCliente.value) {
      await clienteService.atualizar(editingCliente.value.id, form.value)
    } else {
      await clienteService.salvar(form.value)
    }
    await loadClientes()
    closeModal()
  } catch (e: any) {
    error.value = e.response?.data?.message || 'Erro ao salvar cliente'
  } finally {
    saving.value = false
  }
}

function formatPhone(phone: string): string {
  const digits = phone.replace(/\D/g, '')
  if (digits.length === 13) {
    return `+${digits.slice(0, 2)} (${digits.slice(2, 4)}) ${digits.slice(4, 9)}-${digits.slice(9)}`
  }
  return phone
}

onMounted(() => {
  loadClientes()
})
</script>

<template>
  <div class="space-y-6 animate-fade-in">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <div>
        <p class="text-slate-400">Gerencie os clientes cadastrados no sistema</p>
      </div>
      <button @click="openNewModal" class="btn-primary">
        <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        Novo Cliente
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
    <div v-else-if="clientes.length === 0" class="card text-center py-12">
      <svg class="w-16 h-16 mx-auto text-slate-600 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
      </svg>
      <h3 class="text-lg font-medium text-slate-400 mb-2">Nenhum cliente cadastrado</h3>
      <p class="text-slate-500 mb-4">Comece adicionando seu primeiro cliente</p>
      <button @click="openNewModal" class="btn-primary">
        Adicionar Cliente
      </button>
    </div>

    <!-- Table -->
    <div v-else class="card overflow-hidden p-0">
      <table class="w-full">
        <thead class="bg-slate-800">
          <tr>
            <th class="text-left px-6 py-4 text-sm font-medium text-slate-400">Nome</th>
            <th class="text-left px-6 py-4 text-sm font-medium text-slate-400">Telefone</th>
            <th class="text-left px-6 py-4 text-sm font-medium text-slate-400">Email</th>
            <th class="text-right px-6 py-4 text-sm font-medium text-slate-400">Ações</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-slate-800">
          <tr 
            v-for="cliente in clientes" 
            :key="cliente.id"
            class="hover:bg-slate-800/50 transition-colors"
          >
            <td class="px-6 py-4">
              <div class="flex items-center gap-3">
                <div class="w-10 h-10 bg-slate-700 rounded-full flex items-center justify-center">
                  <span class="text-sm font-semibold text-slate-300">
                    {{ cliente.nome?.charAt(0)?.toUpperCase() || '?' }}
                  </span>
                </div>
                <span class="font-medium text-white">{{ cliente.nome || 'Sem nome' }}</span>
              </div>
            </td>
            <td class="px-6 py-4 text-slate-300">{{ formatPhone(cliente.telefone) }}</td>
            <td class="px-6 py-4 text-slate-400">{{ cliente.email || '-' }}</td>
            <td class="px-6 py-4 text-right">
              <button 
                @click="openEditModal(cliente)"
                class="btn-ghost p-2"
                title="Editar"
              >
                <svg 
      class="w-5 h-5"
      fill="none"
      stroke="currentColor"
      viewBox="0 0 24 24"
    >
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                </svg>
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal -->
    <Modal 
      :show="showModal" 
      :title="editingCliente ? 'Editar Cliente' : 'Novo Cliente'"
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
          <label class="label">Nome *</label>
          <input 
            v-model="form.nome" 
            type="text" 
            class="input" 
            placeholder="Nome do cliente"
          />
        </div>

        <div>
          <label class="label">Telefone *</label>
          <input 
            v-model="form.telefone" 
            type="text" 
            class="input" 
            placeholder="Ex: 5511999999999"
          />
        </div>

        <div>
          <label class="label">Email</label>
          <input 
            v-model="form.email" 
            type="email" 
            class="input" 
            placeholder="email@exemplo.com"
          />
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
