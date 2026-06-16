<template>
  <div class="virtual-list" ref="containerRef" @scroll="handleScroll">
    <div :style="{ height: totalHeight + 'px', position: 'relative' }">
      <div :style="{ transform: `translateY(${offsetY}px)` }">
        <div
          class="virtual-list-row"
          v-for="(row, rowIndex) in visibleRows"
          :key="rowIndex"
          :style="{ height: getRowHeight(startRowIndex + rowIndex) + 'px' }"
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
import { getCardHeightByCoverType } from '@/constants/coverTypes'

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
  },
  getItemHeight: {
    type: Function,
    default: null
  },
  columnWidth: {
    type: Number,
    default: 280
  }
})

const emit = defineEmits(['loadMore'])

const containerRef = ref(null)
const scrollTop = ref(0)
const containerHeight = ref(0)

const calculateItemHeight = (item, index) => {
  if (props.getItemHeight) {
    return props.getItemHeight(item, index)
  }
  if (item && item.coverType !== undefined) {
    return getCardHeightByCoverType(item.coverType, props.columnWidth)
  }
  return props.itemHeight
}

const getRowHeight = (rowIndex) => {
  let maxHeight = props.itemHeight
  for (let c = 0; c < props.columns; c++) {
    const itemIndex = rowIndex * props.columns + c
    if (itemIndex < props.items.length) {
      const height = calculateItemHeight(props.items[itemIndex], itemIndex)
      if (height > maxHeight) {
        maxHeight = height
      }
    }
  }
  return maxHeight + props.gap
}

const rowHeights = computed(() => {
  const heights = []
  const totalRows = Math.ceil(props.items.length / props.columns)
  for (let r = 0; r < totalRows; r++) {
    heights.push(getRowHeight(r))
  }
  return heights
})

const rowOffsets = computed(() => {
  const offsets = []
  let offset = 0
  for (let h of rowHeights.value) {
    offsets.push(offset)
    offset += h
  }
  return offsets
})

const totalHeight = computed(() => {
  if (rowHeights.value.length === 0) return 0
  return rowOffsets.value[rowOffsets.value.length - 1] + rowHeights.value[rowHeights.value.length - 1] - props.gap
})

const totalRows = computed(() => Math.ceil(props.items.length / props.columns))

const findStartRow = (scrollPos) => {
  let low = 0
  let high = rowOffsets.value.length - 1
  while (low < high) {
    const mid = Math.floor((low + high) / 2)
    if (rowOffsets.value[mid] + rowHeights.value[mid] < scrollPos) {
      low = mid + 1
    } else {
      high = mid
    }
  }
  return Math.max(0, low - props.buffer)
}

const startRowIndex = computed(() => {
  return findStartRow(scrollTop.value)
})

const visibleRowCount = computed(() => {
  return Math.ceil(containerHeight.value / props.itemHeight) + props.buffer * 2
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
  return rowOffsets.value[startRowIndex.value] || 0
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
