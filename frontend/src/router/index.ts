import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/',
      component: () => import('@/components/layout/AppLayout.vue'),
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          name: 'dashboard',
          component: () => import('@/views/DashboardView.vue')
        },
        {
          path: 'atendimento',
          name: 'atendimento',
          component: () => import('@/views/AtendimentoView.vue')
        },
        {
          path: 'clientes',
          name: 'clientes',
          component: () => import('@/views/ClientesView.vue')
        },
        {
          path: 'usuarios',
          name: 'usuarios',
          component: () => import('@/views/UsuariosView.vue')
        },
        {
          path: 'bot-config',
          name: 'bot-config',
          component: () => import('@/views/BotConfigView.vue')
        }
      ]
    }
  ]
})

router.beforeEach((to, _from, next) => {
  const authStore = useAuthStore()
  
  if (to.meta.requiresAuth !== false && !authStore.isAuthenticated) {
    next({ name: 'login' })
  } else if (to.name === 'login' && authStore.isAuthenticated) {
    next({ name: 'dashboard' })
  } else {
    next()
  }
})

export default router
