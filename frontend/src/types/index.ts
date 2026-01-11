// Enums baseados no backend
export enum StatusConversa {
  BOT = 'BOT',
  HUMANO = 'HUMANO',
  ENCERRADA = 'ENCERRADA'
}

export enum Role {
  ADMIN = 'ADMIN',
  ATENDENTE = 'ATENDENTE'
}

export enum Departamento {
  COMERCIAL = 'COMERCIAL',
  SUPORTE = 'SUPORTE',
  FINANCEIRO = 'FINANCEIRO',
  LOGISTICA = 'LOGISTICA'
}

export enum OrigemMensagem {
  CLIENTE = 'CLIENTE',
  HUMANO = 'HUMANO',
  BOT = 'BOT'
}

// DTOs de Conversa
export interface ConversaResumo {
  conversaId: number
  telefoneCliente: string
  nomeCliente: string
  status: StatusConversa
  usuarioId: number | null
  nomeAtendente: string | null
  clienteId: number
  naoLidas: number
}

export interface Mensagem {
  origem: OrigemMensagem
  texto: string
  data: string
  idUsuario: number | null
  nomeAtendente: string | null
  lida: boolean
}

// DTOs de Dashboard
export interface Dashboard {
  totalConversas: number
  emAtendimento: number
  filaEspera: number
}

// DTOs de Cliente
export interface Cliente {
  id?: number
  nome: string
  telefone: string
  email?: string
}

export interface ClienteResponse {
  id: number
  nome: string
  telefone: string
  email?: string
}

// DTOs de Usuário
export interface Usuario {
  login: string
  senha: string
  email: string
  role: Role
  departamento: Departamento
}

export interface UsuarioForm {
  id?: number
  login: string
  senha?: string
  email: string
  role: Role
  departamento: Departamento
}


export interface AtualizarUsuarioDTO {
  login: string
  email: string
  role: Role
  departamento: Departamento
}

export interface UsuarioResumo {
  id: number
  login: string
  email: string
  role: Role
  departamento: Departamento
}

// DTOs de Autenticação
export interface LoginRequest {
  login: string
  senha: string
}

export interface AuthUser {
  id: number
  login: string
  role: Role
  departamento: Departamento
}

// DTOs de Bot
export interface OpcaoBot {
  id?: number
  gatilho: string
  proximaEtapaId?: number | null
  departamentoDestino?: Departamento | null
}

export interface EtapaBot {
  id?: number
  mensagem: string
  inicial?: boolean
  opcoes: OpcaoBot[]
}

// Responder mensagem
export interface ResponderMensagem {
  texto: string
}

// API Response wrapper
export interface ApiError {
  message: string
  status: number
}
