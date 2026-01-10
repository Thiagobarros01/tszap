# ConversaZap - Frontend

Sistema de atendimento ao cliente via WhatsApp construído com Vue 3, TypeScript e Tailwind CSS.

## Tecnologias

- **Vue 3** - Composition API com `<script setup>`
- **TypeScript** - Tipagem estática
- **Tailwind CSS** - Estilização utilitária
- **Vue Router** - Navegação SPA
- **Pinia** - Gerenciamento de estado
- **Axios** - Cliente HTTP
- **Vite** - Bundler

## Estrutura do Projeto

```
src/
├── assets/           # Estilos globais
├── components/       # Componentes reutilizáveis
│   ├── chat/         # ConversaList, ChatWindow, MessageBubble
│   ├── common/       # StatsCard, Modal
│   └── layout/       # AppLayout, AppSidebar, AppHeader
├── router/           # Configuração de rotas
├── services/         # Serviços de API
├── stores/           # Stores Pinia (auth, conversa)
├── types/            # Interfaces TypeScript
└── views/            # Páginas da aplicação
```

## Páginas

| Rota | Descrição |
|------|-----------|
| `/login` | Autenticação com JWT |
| `/` | Dashboard com estatísticas |
| `/atendimento` | Painel de atendimento (lista de conversas + chat) |
| `/clientes` | CRUD de clientes |
| `/usuarios` | Gerenciamento de usuários (admin) |
| `/bot-config` | Configuração das etapas do bot (admin) |

## Instalação

```bash
# Instalar dependências
npm install

# Rodar em desenvolvimento
npm run dev

# Build para produção
npm run build
```

## Configuração

Crie um arquivo `.env` na raiz do projeto:

```env
VITE_API_URL=http://localhost:8080
```

## Backend

O backend Spring Boot deve estar rodando na porta 8080. Certifique-se de que os endpoints estão disponíveis:

- `POST /auth/login` - Autenticação
- `GET /painel/atendimento/conversas` - Listar conversas
- `GET /painel/atendimento/conversas/{id}/mensagens` - Mensagens
- `POST /painel/atendimento/conversas/{id}/responder` - Responder
- `POST /painel/atendimento/conversas/{id}/encerrar` - Encerrar
- `PATCH /painel/atendimento/conversas/{id}/transferir/{atendenteId}` - Transferir
- `GET /painel/atendimento/dashboard` - Dashboard
- `GET/POST/PUT /clientes` - CRUD clientes
- `GET/POST /usuarios` - Gerenciar usuários
- `GET/POST/DELETE /painel/bot/config` - Configurar bot

## Funcionalidades

- **Autenticação JWT** - Login seguro com token
- **Dashboard** - Métricas de atendimento em tempo real
- **Atendimento** - Chat com clientes, transferência entre atendentes
- **Clientes** - Cadastro e edição de clientes
- **Usuários** - Gerenciamento de atendentes (apenas admin)
- **Bot** - Configuração das etapas do bot de atendimento
