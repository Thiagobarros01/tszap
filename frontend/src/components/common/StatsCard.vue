<script setup lang="ts">
defineProps<{
  title: string
  value: number | string
  icon: 'chat' | 'users' | 'clock' | 'check'
  color: 'green' | 'blue' | 'yellow' | 'purple'
}>()
</script>

<template>
  <div class="card relative overflow-hidden group hover:border-slate-700 transition-colors">
    <div 
      class="absolute inset-0 opacity-5 group-hover:opacity-10 transition-opacity"
      :class="{
        'bg-gradient-to-br from-green-500 to-green-600': color === 'green',
        'bg-gradient-to-br from-blue-500 to-blue-600': color === 'blue',
        'bg-gradient-to-br from-yellow-500 to-yellow-600': color === 'yellow',
        'bg-gradient-to-br from-purple-500 to-purple-600': color === 'purple'
      }"
    ></div>
    
    <div class="relative flex items-start justify-between">
      <div>
        <p class="text-sm font-medium text-slate-400 mb-1">{{ title }}</p>
        <p class="text-3xl font-bold text-white">{{ value }}</p>
      </div>
      
      <div 
        class="w-12 h-12 rounded-xl flex items-center justify-center"
        :class="{
          'bg-green-500/20 text-green-400': color === 'green',
          'bg-blue-500/20 text-blue-400': color === 'blue',
          'bg-yellow-500/20 text-yellow-400': color === 'yellow',
          'bg-purple-500/20 text-purple-400': color === 'purple'
        }"
      >
        <svg v-if="icon === 'chat'" class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
        </svg>
        <svg v-else-if="icon === 'users'" class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
        </svg>
        <svg v-else-if="icon === 'clock'" class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
        <svg v-else-if="icon === 'check'" class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
      </div>
    </div>
  </div>
</template>
