<template>
  <div class="virtual-list" ref="containerRef" @scroll="handleScroll">
    <div :style="{ height: totalHeight + 'px', position: 'relative' }">
      <div :style="{ transform: `translateY(${offsetY}px)` }">
        <div
          v-for="(item, index) in visibleItems"
          :key="getItemKey(item, startIndex + index)"
          :style="{ height: itemHeight + 'px' }"
        >
          <slot :item="item" :index="startIndex + index"></slot>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'

const props = defineProps({
  items: {
    type: Array,
    default: () => []
  },
  itemHeight: {
    type: Number,
    default: 320
  },
  buffer: {
    type: Number,
    default: 5
  }
})

const emit = defineEmits(['loadMore'])

const containerRef = ref(null)
const scrollTop = ref(0)
const containerHeight = ref(0)

const totalHeight = computed(() => props.items.length * props.itemHeight)

const startIndex = computed(() => {
  const index = Math.floor(scrollTop.value / props.itemHeight)
  return Math.max(0, index - props.buffer)
})

const endIndex = computed(() => {
  const visibleCount = Math.ceil(containerHeight.value / props.itemHeight)
  const index = startIndex.value + visibleCount + props.buffer * 2
  return Math.min(props.items.length, index)
})

const visibleItems = computed(() => {
  return props.items.slice(startIndex.value, endIndex.value)
})

const offsetY = computed(() => {
  return startIndex.value * props.itemHeight
})

const getItemKey = (item, index) => {
  return item.id || index
}

const handleScroll = (e) => {
  scrollTop.value = e.target.scrollTop
  
  if (e.target.scrollTop + e.target.clientHeight >= e.target.scrollHeight - 100) {
    emit('loadMore')
  }
}

onMounted(async () => {
  await nextTick()
  if (containerRef.value) {
    containerHeight.value = containerRef.value.clientHeight
  }
})
</script>

<style scoped>
.virtual-list {
  height: 100%;
  overflow-y: auto;
  overflow-x: hidden;
  -webkit-overflow-scrolling: touch;
}
</style>
