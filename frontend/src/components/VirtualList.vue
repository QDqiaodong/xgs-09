<template>
  <div class="virtual-list" ref="containerRef" @scroll="handleScroll">
    <div :style="{ height: totalHeight + 'px', position: 'relative' }">
      <div :style="{ transform: `translateY(${offsetY}px)` }">
        <div
          class="virtual-list-row"
          v-for="(row, rowIndex) in visibleRows"
          :key="rowIndex"
          :style="{ height: rowHeight + 'px' }"
        >
          <div
            class="virtual-list-col"
            v-for="(item, colIndex) in row"
            :key="getItemKey(item, startRowIndex * columns + colIndex)"
          >
            <slot :item="item" :index="startRowIndex * columns + colIndex"></slot>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick, watch } from 'vue'

const props = defineProps({
  items: {
    type: Array,
    default: () => []
  },
  itemHeight: {
    type: Number,
    default: 380
  },
  columns: {
    type: Number,
    default: 1
  },
  gap: {
    type: Number,
    default: 24
  },
  buffer: {
    type: Number,
    default: 3
  }
})

const emit = defineEmits(['loadMore'])

const containerRef = ref(null)
const scrollTop = ref(0)
const containerHeight = ref(0)

const rowHeight = computed(() => props.itemHeight + props.gap)

const totalRows = computed(() => Math.ceil(props.items.length / props.columns))

const totalHeight = computed(() => {
  if (totalRows.value === 0) return 0
  return totalRows.value * rowHeight.value - props.gap
})

const startRowIndex = computed(() => {
  const index = Math.floor(scrollTop.value / rowHeight.value)
  return Math.max(0, index - props.buffer)
})

const visibleRowCount = computed(() => {
  return Math.ceil(containerHeight.value / rowHeight.value) + props.buffer * 2
})

const endRowIndex = computed(() => {
  return Math.min(totalRows.value, startRowIndex.value + visibleRowCount.value)
})

const visibleRows = computed(() => {
  const rows = []
  for (let r = startRowIndex.value; r < endRowIndex.value; r++) {
    const start = r * props.columns
    const end = Math.min(start + props.columns, props.items.length)
    const row = props.items.slice(start, end)
    while (row.length < props.columns) {
      row.push(null)
    }
    rows.push(row)
  }
  return rows
})

const offsetY = computed(() => {
  return startRowIndex.value * rowHeight.value
})

const getItemKey = (item, index) => {
  return item?.id || index
}

const handleScroll = (e) => {
  scrollTop.value = e.target.scrollTop
  
  if (e.target.scrollTop + e.target.clientHeight >= e.target.scrollHeight - 100) {
    emit('loadMore')
  }
}

const updateContainerHeight = () => {
  if (containerRef.value) {
    containerHeight.value = containerRef.value.clientHeight
  }
}

onMounted(async () => {
  await nextTick()
  updateContainerHeight()
  window.addEventListener('resize', updateContainerHeight)
})

watch(() => props.items, () => {
  nextTick(updateContainerHeight)
}, { deep: true })
</script>

<style scoped>
.virtual-list {
  height: 100%;
  overflow-y: auto;
  overflow-x: hidden;
  -webkit-overflow-scrolling: touch;
}

.virtual-list-row {
  display: flex;
  gap: v-bind('gap + "px"');
}

.virtual-list-col {
  flex: 1;
  min-width: 0;
}
</style>
