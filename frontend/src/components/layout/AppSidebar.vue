<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useConversaStore } from '@/stores/conversa'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const conversaStore = useConversaStore()

interface NavItem {
  name: string
  path: string
  icon: string
  badge?: number
  adminOnly?: boolean
}

const navItems = computed<NavItem[]>(() => [
  { name: 'Dashboard', path: '/', icon: 'chart' },
  { name: 'Atendimento', path: '/atendimento', icon: 'chat', badge: conversaStore.totalNaoLidas },
  { name: 'Clientes', path: '/clientes', icon: 'users' },
  { name: 'Usuários', path: '/usuarios', icon: 'team', adminOnly: true },
  { name: 'Config. Bot', path: '/bot-config', icon: 'bot', adminOnly: true }
])

const filteredNavItems = computed(() => 
  navItems.value.filter(item => !item.adminOnly || authStore.isAdmin)
)

function isActive(path: string): boolean {
  if (path === '/') return route.path === '/'
  return route.path.startsWith(path)
}

function navigate(path: string) {
  router.push(path)
}

function handleLogout() {
  authStore.logout()
  router.push('/login')
}
</script>

<template>
  <aside class="w-64 bg-slate-900 border-r border-slate-800 flex flex-col">
    <!-- Logo -->
    <div class="h-16 flex items-center px-6 border-b border-slate-800">
      <div class="flex items-center gap-3">
        <div class="w-10 h-10 bg-whatsapp rounded-xl flex items-center justify-center">
          <svg class="w-6 h-6 text-white" fill="currentColor" viewBox="0 0 24 24">
            <path d="M17.472 14.382c-.297-.149-1.758-.867-2.03-.967-.273-.099-.471-.148-.67.15-.197.297-.767.966-.94 1.164-.173.199-.347.223-.644.075-.297-.15-1.255-.463-2.39-1.475-.883-.788-1.48-1.761-1.653-2.059-.173-.297-.018-.458.13-.606.134-.133.298-.347.446-.52.149-.174.198-.298.298-.497.099-.198.05-.371-.025-.52-.075-.149-.669-1.612-.916-2.207-.242-.579-.487-.5-.669-.51-.173-.008-.371-.01-.57-.01-.198 0-.52.074-.792.372-.272.297-1.04 1.016-1.04 2.479 0 1.462 1.065 2.875 1.213 3.074.149.198 2.096 3.2 5.077 4.487.709.306 1.262.489 1.694.625.712.227 1.36.195 1.871.118.571-.085 1.758-.719 2.006-1.413.248-.694.248-1.289.173-1.413-.074-.124-.272-.198-.57-.347z"/>
          </svg>
        </div>
        <span class="text-xl font-bold text-white">ConversaZap</span>
      </div>
    </div>

    <!-- Navigation -->
    <nav class="flex-1 p-4 space-y-1">
      <button
        v-for="item in filteredNavItems"
        :key="item.path"
        @click="navigate(item.path)"
        :class="[
          'w-full flex items-center gap-3 px-4 py-3 rounded-xl text-left transition-all duration-200',
          isActive(item.path)
            ? 'bg-whatsapp/10 text-whatsapp'
            : 'text-slate-400 hover:bg-slate-800 hover:text-slate-200'
        ]"
      >
        <!-- Icons -->
        <svg v-if="item.icon === 'chart'" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
        </svg>
        <svg v-else-if="item.icon === 'chat'" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
        </svg>
        <svg v-else-if="item.icon === 'users'" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
        </svg>
        <svg v-else-if="item.icon === 'team'" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
        </svg>
        <svg v-else-if="item.icon === 'bot'" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.75 17L9 20l-1 1h8l-1-1-.75-3M3 13h18M5 17h14a2 2 0 002-2V5a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
        </svg>
        
        <span class="font-medium">{{ item.name }}</span>
        
        <!-- Badge -->
        <span 
          v-if="item.badge && item.badge > 0" 
          class="ml-auto bg-whatsapp text-white text-xs font-bold px-2 py-0.5 rounded-full"
        >
          {{ item.badge > 99 ? '99+' : item.badge }}
        </span>
      </button>
    </nav>

    <!-- User section -->
    <div class="p-4 border-t border-slate-800">
      <div class="flex items-center gap-3 px-4 py-3">
        <div class="w-10 h-10 bg-slate-700 rounded-full flex items-center justify-center">
          <span class="text-sm font-bold text-slate-300">
            {{ authStore.user?.login?.charAt(0).toUpperCase() || 'U' }}
          </span>
        </div>
        <div class="flex-1 min-w-0">
          <p class="text-sm font-medium text-slate-200 truncate">
            {{ authStore.user?.login || 'Usuário' }}
          </p>
          <p class="text-xs text-slate-500 truncate">
            {{ authStore.user?.departamento || '' }}
          </p>
        </div>
        <button 
          @click="handleLogout"
          class="p-2 text-slate-400 hover:text-red-400 hover:bg-slate-800 rounded-lg transition-colors"
          title="Sair"
        >
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
          </svg>
        </button>
      </div>
    </div>
  </aside>
</template>
