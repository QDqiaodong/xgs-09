<template>
  <div class="layout-grid-previewer">
    <div class="previewer-header">
      <h4 class="previewer-title">版式结构预览</h4>
      <div class="legend">
        <span class="legend-item">
          <span class="legend-color legend-text"></span>
          <span class="legend-label">文字区</span>
        </span>
        <span class="legend-item">
          <span class="legend-color legend-sticker"></span>
          <span class="legend-label">贴纸区</span>
        </span>
        <span class="legend-item">
          <span class="legend-color legend-image"></span>
          <span class="legend-label">图片区</span>
        </span>
        <span class="legend-item">
          <span class="legend-color legend-white"></span>
          <span class="legend-label">留白</span>
        </span>
      </div>
    </div>

    <div class="previewer-body">
      <div class="page-frame" :style="pageFrameStyle">
        <div class="page-content" :style="pageContentStyle">
          <div
            v-for="(area, index) in layoutAreas"
            :key="index"
            class="layout-area"
            :class="`area-${area.type}`"
            :style="getAreaStyle(area)"
          >
            <span class="area-label">{{ area.label }}</span>
            <div v-if="area.type === 'text'" class="area-text-lines">
              <span v-for="i in 3" :key="i" class="text-line"></span>
            </div>
            <div v-else-if="area.type === 'sticker'" class="area-stickers">
              <span v-for="i in (area.stickerCount || 3)" :key="i" class="sticker-dot"></span>
            </div>
            <div v-else-if="area.type === 'image'" class="area-image-placeholder">
              <el-icon><Picture /></el-icon>
            </div>
          </div>
        </div>
      </div>

      <div class="layout-info">
        <div class="info-item" v-if="layoutConfig.columns">
          <span class="info-label">分栏</span>
          <span class="info-value">{{ layoutConfig.columns }} 栏</span>
        </div>
        <div class="info-item" v-if="layoutConfig.padding">
          <span class="info-label">留白</span>
          <span class="info-value">{{ layoutConfig.padding }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">文字区</span>
          <span class="info-value">{{ textAreaCount }} 块</span>
        </div>
        <div class="info-item">
          <span class="info-label">贴纸区</span>
          <span class="info-value">{{ stickerAreaCount }} 块</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { Picture } from '@element-plus/icons-vue'

const props = defineProps({
  layoutConfig: {
    type: Object,
    default: () => ({
      columns: 2,
      columnGap: '12px',
      padding: '20px',
      areas: []
    })
  }
})

const layoutAreas = computed(() => {
  return props.layoutConfig?.areas || []
})

const textAreaCount = computed(() => {
  return layoutAreas.value.filter(a => a.type === 'text').length
})

const stickerAreaCount = computed(() => {
  return layoutAreas.value.filter(a => a.type === 'sticker').length
})

const pageFrameStyle = computed(() => {
  return {
    padding: props.layoutConfig?.padding || '20px'
  }
})

const pageContentStyle = computed(() => {
  const columns = props.layoutConfig?.columns || 2
  const columnGap = props.layoutConfig?.columnGap || '12px'
  return {
    display: 'grid',
    gridTemplateColumns: `repeat(${columns}, 1fr)`,
    gap: columnGap,
    height: '100%'
  }
})

const getAreaStyle = (area) => {
  const style = {}
  if (area.columnSpan) {
    style.gridColumn = `span ${area.columnSpan}`
  }
  if (area.rowSpan) {
    style.gridRow = `span ${area.rowSpan}`
  }
  if (area.alignSelf) {
    style.alignSelf = area.alignSelf
  }
  if (area.justifySelf) {
    style.justifySelf = area.justifySelf
  }
  return style
}
</script>

<style scoped>
.layout-grid-previewer {
  background: #faf8f5;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
}

.previewer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 12px;
}

.previewer-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.legend {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #666;
}

.legend-color {
  width: 14px;
  height: 14px;
  border-radius: 3px;
  display: inline-block;
}

.legend-text {
  background: #e8f4fd;
  border: 1px solid #91d5ff;
}

.legend-sticker {
  background: #fff0f6;
  border: 1px solid #ffadd2;
}

.legend-image {
  background: #f6ffed;
  border: 1px solid #b7eb8f;
}

.legend-white {
  background: #fff;
  border: 1px dashed #d9d9d9;
}

.previewer-body {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}

.page-frame {
  flex: 1;
  max-width: 360px;
  background: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  aspect-ratio: 3 / 4;
}

.page-content {
  height: 100%;
}

.layout-area {
  border-radius: 6px;
  padding: 10px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-height: 50px;
  position: relative;
  transition: all 0.3s;
}

.layout-area:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.area-text {
  background: #e8f4fd;
  border: 1px solid #91d5ff;
}

.area-sticker {
  background: #fff0f6;
  border: 1px solid #ffadd2;
}

.area-image {
  background: #f6ffed;
  border: 1px solid #b7eb8f;
}

.area-label {
  font-size: 11px;
  font-weight: 600;
  color: #666;
  line-height: 1;
}

.area-text-lines {
  display: flex;
  flex-direction: column;
  gap: 5px;
  flex: 1;
  justify-content: center;
}

.text-line {
  display: block;
  height: 3px;
  background: rgba(24, 144, 255, 0.3);
  border-radius: 2px;
}

.text-line:nth-child(1) {
  width: 100%;
}

.text-line:nth-child(2) {
  width: 85%;
}

.text-line:nth-child(3) {
  width: 70%;
}

.area-stickers {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  flex: 1;
  align-items: center;
  justify-content: center;
}

.sticker-dot {
  width: 14px;
  height: 14px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ff9c9c, #ff6b9d);
  box-shadow: 0 1px 3px rgba(255, 107, 157, 0.3);
}

.sticker-dot:nth-child(2) {
  background: linear-gradient(135deg, #ffd700, #ffb347);
}

.sticker-dot:nth-child(3) {
  background: linear-gradient(135deg, #98d8c8, #52c41a);
}

.sticker-dot:nth-child(4) {
  background: linear-gradient(135deg, #a0c4ff, #5b8def);
}

.sticker-dot:nth-child(5) {
  background: linear-gradient(135deg, #bdb2ff, #9254de);
}

.area-image-placeholder {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #95de64;
  font-size: 24px;
}

.layout-info {
  flex: 0 0 auto;
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #eee;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
}

.info-label {
  font-size: 13px;
  color: #999;
}

.info-value {
  font-size: 13px;
  font-weight: 600;
  color: #333;
}

@media (max-width: 600px) {
  .previewer-body {
    flex-direction: column;
    align-items: center;
  }

  .page-frame {
    max-width: 280px;
    width: 100%;
  }

  .layout-info {
    width: 100%;
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: space-between;
  }
}
</style>
