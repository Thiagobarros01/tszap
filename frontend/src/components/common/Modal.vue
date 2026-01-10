<script setup lang="ts">
defineProps<{
  title: string
  show: boolean
}>()

const emit = defineEmits<{
  close: []
}>()
</script>

<template>
  <Teleport to="body">
    <Transition
      enter-active-class="transition-opacity duration-200"
      leave-active-class="transition-opacity duration-200"
      enter-from-class="opacity-0"
      leave-to-class="opacity-0"
    >
      <div 
        v-if="show" 
        class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4"
        @click.self="emit('close')"
      >
        <div class="bg-slate-900 border border-slate-800 rounded-xl w-full max-w-lg animate-slide-up">
          <div class="flex items-center justify-between p-4 border-b border-slate-800">
            <h3 class="text-lg font-semibold text-white">{{ title }}</h3>
            <button @click="emit('close')" class="btn-ghost p-1">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
          
          <div class="p-4">
            <slot />
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>
