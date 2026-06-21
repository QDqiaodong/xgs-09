<template>
  <div class="layout-template-selector">
    <div class="selector-header">
      <div class="selector-title-wrap">
        <span class="selector-title">📐 排版模板档案</span>
        <span class="selector-desc">选择预设的排版布局模板，快速确定手账结构</span>
      </div>
      <div class="template-type-filters">
        <span
          v-for="type in templateTypes"
          :key="type.value"
          class="type-filter"
          :class="{ active: selectedType === type.value }"
          @click="selectType(type.value)"
        >
          {{ type.label }}
        </span>
      </div>
    </div>

    <div class="template-grid">
      <div
        v-for="template in filteredTemplates"
        :key="template.id"
        class="template-card"
        :class="{ active: selectedTemplateId === template.id, recommended: template.isRecommended }"
        @click="selectTemplate(template)"
      >
        <div v-if="template.isRecommended" class="recommend-badge">
          <el-icon><Star /></el-icon>
          推荐
        </div>
        <div class="template-preview">
          <div class="mini-preview" :style="getMiniPreviewStyle(template)">
            <div
              v-for="(area, idx) in getTemplateAreas(template)"
              :key="idx"
              class="mini-area"
              :class="`mini-area-${area.type}`"
              :style="getAreaStyle(area)"
            >
              <span class="mini-label">{{ area.label }}</span>
            </div>
          </div>
        </div>
        <div class="template-info">
          <div class="template-name-row">
            <span class="template-name">{{ template.templateName }}</span>
            <span class="template-type-tag">{{ template.templateTypeName }}</span>
          </div>
          <div class="template-desc">{{ template.description }}</div>
          <div class="template-tags">
            <span v-for="tag in template.styleTagList" :key="tag" class="tag style-tag">
              {{ tag }}
            </span>
            <span v-for="tag in template.sceneTagList" :key="tag" class="tag scene-tag">
              {{ tag }}
            </span>
          </div>
          <div class="template-meta">
            <span class="use-count">{{ template.useCount || 0 }} 人使用</span>
          </div>
        </div>
        <div class="template-check" v-if="selectedTemplateId === template.id">
          <el-icon><Check /></el-icon>
        </div>
      </div>
    </div>

    <div v-if="filteredTemplates.length === 0" class="empty-tip">
      <el-icon><Document /></el-icon>
      <span>暂无该类型的模板</span>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { Check, Star, Document } from '@element-plus/icons-vue'
import { getLayoutTemplateList } from '@/api/layoutTemplate'

const props = defineProps({
  modelValue: {
    type: [Number, String],
    default: null
  },
  categoryIds: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['update:modelValue', 'change', 'select'])

const templateTypes = [
  { value: '', label: '全部' },
  { value: 'twoColumn', label: '双栏' },
  { value: 'collage', label: '拼贴' },
  { value: 'minimalist', label: '留白' },
  { value: 'timeline', label: '时间轴' },
  { value: 'centerFocus', label: '居中' },
  { value: 'magazine', label: '杂志风' },
  { value: 'natural', label: '森系' }
]

const templates = ref([])
const selectedType = ref('')
const selectedTemplateId = ref(props.modelValue)

const filteredTemplates = computed(() => {
  if (!selectedType.value) {
    return templates.value
  }
  return templates.value.filter(t => t.templateType === selectedType.value)
})

const loadTemplates = async () => {
  try {
    const res = await getLayoutTemplateList(selectedType.value || undefined, props.categoryIds)
    templates.value = res.data || []
  } catch (e) {
    console.error('加载模板列表失败', e)
  }
}

const selectType = (type) => {
  selectedType.value = type
  loadTemplates()
}

const selectTemplate = (template) => {
  selectedTemplateId.value = template.id
  emit('update:modelValue', template.id)
  emit('change', template.id)
  emit('select', template)
}

const getTemplateAreas = (template) => {
  try {
    const config = typeof template.layoutConfig === 'string'
      ? JSON.parse(template.layoutConfig)
      : template.layoutConfig
    return config?.areas || []
  } catch (e) {
    return []
  }
}

const getMiniPreviewStyle = (template) => {
  try {
    const config = typeof template.layoutConfig === 'string'
      ? JSON.parse(template.layoutConfig)
      : template.layoutConfig
    const columns = config?.columns || 2
    const gap = config?.columnGap || '6px'
    return {
      display: 'grid',
      gridTemplateColumns: `repeat(${columns}, 1fr)`,
      gap: gap,
      padding: config?.padding || '10px',
      height: '100%'
    }
  } catch (e) {
    return {
      display: 'grid',
      gridTemplateColumns: 'repeat(2, 1fr)',
      gap: '6px',
      padding: '10px',
      height: '100%'
    }
  }
}

const getAreaStyle = (area) => {
  const style = {}
  if (area.columnSpan) {
    style.gridColumn = `span ${area.columnSpan}`
  }
  if (area.rowSpan) {
    style.gridRow = `span ${area.rowSpan}`
  }
  return style
}

watch(() => props.modelValue, (val) => {
  selectedTemplateId.value = val
})

watch(() => props.categoryIds, () => {
  loadTemplates()
}, { deep: true, immediate: true })
</script>

<style scoped>
.layout-template-selector {
  background: linear-gradient(135deg, #fdfbff 0%, #fff5f8 100%);
  border-radius: 12px;
  border: 1px solid #f0e6fa;
  padding: 20px;
}

.selector-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
  gap: 16px;
  flex-wrap: wrap;
}

.selector-title-wrap {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.selector-title {
  font-size: 16px;
  font-weight: 700;
  color: #333;
}

.selector-desc {
  font-size: 13px;
  color: #999;
}

.template-type-filters {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.type-filter {
  padding: 6px 14px;
  border-radius: 16px;
  border: 1px solid #e8e8e8;
  background: #fff;
  font-size: 13px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
}

.type-filter:hover {
  border-color: #ff9a9e;
  color: #ff6b9d;
}

.type-filter.active {
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
  border-color: transparent;
  color: #fff;
}

.template-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 14px;
}

.template-card {
  position: relative;
  background: #fff;
  border: 2px solid #eee;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.template-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  border-color: #ffc8dd;
}

.template-card.active {
  border-color: #ff6b9d;
  background: linear-gradient(135deg, #fff5f8 0%, #ffe8f0 100%);
  box-shadow: 0 4px 16px rgba(255, 107, 157, 0.15);
}

.template-card.recommended {
  border-color: #ffd93d;
}

.recommend-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 2;
  display: flex;
  align-items: center;
  gap: 3px;
  padding: 3px 10px;
  background: linear-gradient(135deg, #ffd93d 0%, #ffb347 100%);
  color: #fff;
  font-size: 11px;
  font-weight: 600;
  border-radius: 12px;
}

.recommend-badge .el-icon {
  font-size: 12px;
}

.template-preview {
  height: 140px;
  background: #faf8f5;
  border-bottom: 1px solid #f0f0f0;
  padding: 8px;
}

.mini-preview {
  width: 100%;
  height: 100%;
  background: #fff;
  border-radius: 6px;
  border: 1px solid #e8e8e8;
}

.mini-area {
  border-radius: 4px;
  padding: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 20px;
  font-size: 9px;
}

.mini-label {
  font-size: 9px;
  font-weight: 600;
  color: #666;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.mini-area-text {
  background: #e8f4fd;
  border: 1px solid #91d5ff;
}

.mini-area-handwriting {
  background: #e6f7ff;
  border: 1px solid #91d5ff;
}

.mini-area-sticker {
  background: #fff0f6;
  border: 1px solid #ffadd2;
}

.mini-area-tape {
  background: #fffbe6;
  border: 1px solid #f5d76e;
}

.mini-area-stamp {
  background: #fce4ec;
  border: 1px solid #f48fb1;
}

.mini-area-image {
  background: #f6ffed;
  border: 1px solid #b7eb8f;
}

.template-info {
  padding: 14px;
}

.template-name-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.template-name {
  font-size: 15px;
  font-weight: 700;
  color: #333;
}

.template-type-tag {
  padding: 2px 8px;
  background: #f0f0f0;
  border-radius: 10px;
  font-size: 11px;
  color: #666;
}

.template-desc {
  font-size: 12px;
  color: #888;
  line-height: 1.5;
  margin-bottom: 10px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.template-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 8px;
}

.tag {
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 11px;
}

.tag.style-tag {
  background: #fff0f6;
  color: #ff6b9d;
}

.tag.scene-tag {
  background: #e6f7ff;
  color: #1890ff;
}

.template-meta {
  display: flex;
  align-items: center;
}

.use-count {
  font-size: 12px;
  color: #aaa;
}

.template-check {
  position: absolute;
  top: 10px;
  left: 10px;
  width: 26px;
  height: 26px;
  background: #ff6b9d;
  color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  box-shadow: 0 2px 8px rgba(255, 107, 157, 0.3);
  z-index: 2;
}

.empty-tip {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 40px;
  color: #999;
  font-size: 14px;
}

.empty-tip .el-icon {
  font-size: 32px;
  color: #ccc;
}

@media (max-width: 600px) {
  .template-grid {
    grid-template-columns: 1fr;
  }

  .selector-header {
    flex-direction: column;
  }
}
</style>
