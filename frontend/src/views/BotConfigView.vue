<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { botService } from '@/services/botService'
import Modal from '@/components/common/Modal.vue'
import type { EtapaBot, OpcaoBot } from '@/types'
import { Departamento } from '@/types'

const etapas = ref<EtapaBot[]>([])
const loading = ref(true)
const showModal = ref(false)
const editingEtapa = ref<EtapaBot | null>(null)
const saving = ref(false)
const error = ref('')

// Estado inicial do form
const form = ref<EtapaBot>({
  mensagem: '',
  inicial: false,
  opcoes: []
})

const departamentos = [
  { value: null, label: 'Nenhum (continua no bot)' },
  { value: Departamento.COMERCIAL, label: 'Comercial' },
  { value: Departamento.SUPORTE, label: 'Suporte' },
  { value: Departamento.FINANCEIRO, label: 'Financeiro' },
  { value: Departamento.LOGISTICA, label: 'Logística' }
]

async function loadEtapas() {
  loading.value = true
  try {
    etapas.value = await botService.listarEtapas()
  } catch (e) {
    console.error('Erro ao carregar etapas:', e)
  } finally {
    loading.value = false
  }
}

function openNewModal() {
  editingEtapa.value = null
  // Limpa o form completamente para evitar lixo de memória
  form.value = {
    mensagem: '',
    inicial: false,
    opcoes: []
  }
  error.value = ''
  showModal.value = true
}

function openEditModal(etapa: EtapaBot) {
  editingEtapa.value = etapa
  // Clona os dados para não editar o objeto da lista diretamente antes de salvar
  form.value = {
    id: etapa.id,
    mensagem: etapa.mensagem,
    inicial: etapa.inicial || false,
    // Cria uma cópia profunda das opções para não duplicar visualmente se reabrir
    opcoes: etapa.opcoes ? JSON.parse(JSON.stringify(etapa.opcoes)) : []
  }
  error.value = ''
  showModal.value = true
}

function closeModal() {
  showModal.value = false
  editingEtapa.value = null
}

function addOpcao() {
  if (!form.value.opcoes) form.value.opcoes = []
  form.value.opcoes.push({
    gatilho: '',
    proximaEtapaId: null,
    departamentoDestino: null
  })
}

function removeOpcao(index: number) {
  if (form.value.opcoes) {
    form.value.opcoes.splice(index, 1)
  }
}

async function handleSubmit() {
  if (!form.value.mensagem) {
    error.value = 'A mensagem é obrigatória'
    return
  }

  saving.value = true
  error.value = ''

  try {
    // --- LÓGICA CORRIGIDA: DECIDE ENTRE POST E PUT ---
    if (form.value.id) {
      // Tem ID? É Edição -> PUT
      await botService.atualizarEtapa(form.value.id, form.value)
    } else {
      // Não tem ID? É Novo -> POST
      await botService.criarEtapa(form.value)
    }
    
    await loadEtapas()
    closeModal()
  } catch (e: any) {
    console.error(e)
    error.value = e.response?.data?.message || 'Erro ao salvar etapa'
  } finally {
    saving.value = false
  }
}

async function removeEtapa(etapa: EtapaBot) {
  if (!etapa.id) return
  if (!confirm(`Deseja remover a etapa "${etapa.mensagem.substring(0, 50)}..."?`)) return
  
  try {
    await botService.removerEtapa(etapa.id)
    await loadEtapas()
  } catch (e) {
    console.error('Erro ao remover etapa:', e)
  }
}

function getDepartamentoLabel(dep: Departamento | null | undefined): string {
  if (!dep) return 'Próxima etapa'
  const labels: Record<Departamento, string> = {
    [Departamento.COMERCIAL]: 'Comercial',
    [Departamento.SUPORTE]: 'Suporte',
    [Departamento.FINANCEIRO]: 'Financeiro',
    [Departamento.LOGISTICA]: 'Logística'
  }
  return labels[dep] || dep
}

function getDestinoValue(opcao: OpcaoBot): string {
  if (opcao.departamentoDestino) {
    return 'dep:' + opcao.departamentoDestino
  }
  if (opcao.proximaEtapaId) {
    return 'etapa:' + opcao.proximaEtapaId
  }
  return ''
}

function setDestino(opcao: OpcaoBot, value: string) {
  if (value.startsWith('dep:')) {
    opcao.departamentoDestino = value.replace('dep:', '') as Departamento
    opcao.proximaEtapaId = null
  } else if (value.startsWith('etapa:')) {
    opcao.proximaEtapaId = parseInt(value.replace('etapa:', ''))
    opcao.departamentoDestino = null
  } else {
    opcao.departamentoDestino = null
    opcao.proximaEtapaId = null
  }
}

onMounted(() => {
  loadEtapas()
})
</script>

<template>
  <div class="space-y-6 animate-fade-in">
    <div class="flex items-center justify-between">
      <div>
        <p class="text-slate-400">Configure as etapas e opções do bot de atendimento</p>
      </div>
      <button @click="openNewModal" class="btn-primary">
        <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        Nova Etapa
      </button>
    </div>

    <div class="card bg-blue-500/5 border-blue-500/20">
      <div class="flex gap-3">
        <svg class="w-6 h-6 text-blue-400 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
        <div>
          <p class="text-sm text-slate-300">
            Configure as etapas que o bot irá seguir durante o atendimento inicial.
            Cada etapa pode ter opções que direcionam para outras etapas ou para um departamento específico.
          </p>
        </div>
      </div>
    </div>

    <div v-if="loading" class="space-y-4">
      <div v-for="i in 3" :key="i" class="card animate-pulse">
        <div class="h-4 bg-slate-700 rounded w-1/4 mb-3"></div>
        <div class="h-6 bg-slate-800 rounded w-3/4 mb-4"></div>
        <div class="space-y-2">
          <div class="h-4 bg-slate-800 rounded w-1/2"></div>
          <div class="h-4 bg-slate-800 rounded w-1/3"></div>
        </div>
      </div>
    </div>

    <div v-else-if="etapas.length === 0" class="card text-center py-12">
      <svg class="w-16 h-16 mx-auto text-slate-600 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9.75 17L9 20l-1 1h8l-1-1-.75-3M3 13h18M5 17h14a2 2 0 002-2V5a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
      </svg>
      <h3 class="text-lg font-medium text-slate-400 mb-2">Nenhuma etapa configurada</h3>
      <p class="text-slate-500 mb-4">Comece adicionando a primeira etapa do bot</p>
      <button @click="openNewModal" class="btn-primary">
        Adicionar Etapa
      </button>
    </div>

    <div v-else class="space-y-4">
      <div 
        v-for="(etapa, index) in etapas" 
        :key="etapa.id"
        class="card hover:border-slate-700 transition-colors"
      >
        <div class="flex items-start justify-between mb-4">
          <div class="flex items-center gap-3">
            <span class="w-8 h-8 bg-whatsapp/20 text-whatsapp rounded-lg flex items-center justify-center font-bold text-sm">
              {{ index + 1 }}
            </span>
            <div>
              <div class="flex items-center gap-2">
                <p class="font-medium text-white">Etapa {{ index + 1 }}</p>
                <span v-if="etapa.inicial" class="text-xs bg-yellow-500/20 text-yellow-400 px-2 py-0.5 rounded-full">Inicial</span>
              </div>
              <p class="text-sm text-slate-400">{{ etapa.opcoes?.length || 0 }} opção(ões)</p>
            </div>
          </div>
          <div class="flex items-center gap-2">
            <button 
              @click="openEditModal(etapa)"
              class="btn-ghost p-2"
              title="Editar"
            >
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
              </svg>
            </button>
            <button 
              @click="removeEtapa(etapa)"
              class="btn-ghost p-2 text-red-400 hover:text-red-300"
              title="Remover"
            >
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
              </svg>
            </button>
          </div>
        </div>

        <div class="bg-slate-800 rounded-lg p-4 mb-4">
          <p class="text-slate-200 whitespace-pre-wrap">{{ etapa.mensagem }}</p>
        </div>

        <div v-if="etapa.opcoes?.length > 0" class="space-y-2">
          <p class="text-xs text-slate-500 uppercase tracking-wide font-medium">Opções</p>
          <div class="grid gap-2">
            <div 
              v-for="(opcao, idx) in (etapa.opcoes || [])" 
              :key="idx"
              class="flex items-center justify-between bg-slate-800/50 rounded-lg px-3 py-2"
            >
              <span class="text-sm text-slate-300 font-mono bg-slate-700 px-2 py-0.5 rounded">{{ opcao.gatilho }}</span>
              <span class="text-slate-500">→</span>
              <span 
                :class="[
                  'text-xs px-2 py-0.5 rounded-full',
                  opcao.departamentoDestino 
                    ? 'bg-green-500/20 text-green-400' 
                    : opcao.proximaEtapaId
                      ? 'bg-blue-500/20 text-blue-400'
                      : 'bg-slate-600/50 text-slate-400'
                ]"
              >
                {{ opcao.departamentoDestino ? 'Transf. ' + getDepartamentoLabel(opcao.departamentoDestino) : opcao.proximaEtapaId ? 'Próxima etapa' : 'Não definido' }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <Modal 
      :show="showModal" 
      :title="editingEtapa ? 'Editar Etapa' : 'Nova Etapa'"
      @close="closeModal"
    >
      <form @submit.prevent="handleSubmit" class="space-y-4">
        <div 
          v-if="error" 
          class="bg-red-500/10 border border-red-500/20 text-red-400 px-4 py-3 rounded-lg text-sm"
        >
          {{ error }}
        </div>

        <div class="flex items-center gap-3">
          <input 
            v-model="form.inicial" 
            type="checkbox" 
            id="inicial"
            class="w-5 h-5 rounded border-slate-600 bg-slate-800 text-whatsapp focus:ring-whatsapp"
          />
          <label for="inicial" class="label mb-0 cursor-pointer">Esta é a mensagem de Boas-vindas?</label>
        </div>

        <div>
          <label class="label">Mensagem *</label>
          <textarea 
            v-model="form.mensagem" 
            rows="4"
            class="input resize-none" 
            placeholder="Digite a mensagem que o bot irá enviar..."
          ></textarea>
        </div>

        <div>
          <div class="flex items-center justify-between mb-2">
            <label class="label mb-0">Botões / Opções</label>
            <button 
              type="button" 
              @click="addOpcao"
              class="text-whatsapp hover:text-whatsapp-light text-sm font-medium"
            >
              + Adicionar Opção
            </button>
          </div>

          <div v-if="!form.opcoes || form.opcoes.length === 0" class="text-sm text-slate-500 text-center py-4 bg-slate-800 rounded-lg">
            Nenhuma opção adicionada
          </div>

          <div v-else class="space-y-3">
            <div 
              v-for="(opcao, idx) in form.opcoes" 
              :key="idx"
              class="bg-slate-800 rounded-lg p-3 flex items-center gap-3"
            >
              <input 
                v-model="opcao.gatilho" 
                type="text" 
                class="input w-20 text-center" 
                placeholder="#"
              />
              <select 
                :value="getDestinoValue(opcao)"
                @change="setDestino(opcao, ($event.target as HTMLSelectElement).value)"
                class="input flex-1"
              >
                <option value="">-- Selecione o Destino --</option>
                <optgroup label="Ações Finais">
                  <option value="dep:COMERCIAL">Transf. Comercial</option>
                  <option value="dep:SUPORTE">Transf. Suporte</option>
                  <option value="dep:FINANCEIRO">Transf. Financeiro</option>
                  <option value="dep:LOGISTICA">Transf. Logística</option>
                </optgroup>
                <optgroup label="Ir para outra Etapa">
                  <option 
                    v-for="etapaOpt in etapas.filter(e => String(e.id) !== String(form.id))" 
                    :key="etapaOpt.id" 
                    :value="'etapa:' + etapaOpt.id"
                  >
                    Etapa: {{ etapaOpt.mensagem?.substring(0, 30) }}...
                  </option>
                </optgroup>
              </select>
              <button 
                type="button" 
                @click="removeOpcao(idx)"
                class="btn-ghost p-2 text-red-400 shrink-0"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                </svg>
              </button>
            </div>
          </div>
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