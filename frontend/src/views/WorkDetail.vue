<template>
  <div class="work-detail">
    <Header />
    
    <main class="container detail-content" v-loading="loading">
      <div v-if="error" class="state-container">
        <el-result icon="error" :title="errorTitle" :sub-title="errorSubtitle">
          <template #extra>
            <el-button v-if="!isNotFoundError" type="primary" @click="loadDetail">重新加载</el-button>
            <el-button @click="goHome">返回首页</el-button>
          </template>
        </el-result>
      </div>

      <template v-else-if="work">
        <div class="detail-header">
          <h1 class="detail-title">{{ work.title }}</h1>
          <div class="detail-meta">
            <div class="author-info">
              <img :src="work.avatar || 'https://api.dicebear.com/7.x/avataaars/svg?seed=default'" alt="" />
              <div>
                <span class="author-name">{{ work.nickname || '手账达人' }}</span>
                <span class="publish-time">{{ formatDate(work.createTime) }}</span>
              </div>
            </div>
            <div class="action-btns">
              <button 
                class="action-btn favorite" 
                :class="{ active: isFavorite }"
                @click="handleFavorite"
              >
                <el-icon><Star :fill="isFavorite ? '#ff6b9d' : 'none'" /></el-icon>
                {{ isFavorite ? '已收藏' : '收藏' }}
              </button>
              <button class="action-btn">
                <el-icon><Share /></el-icon>
                分享
              </button>
            </div>
          </div>
        </div>

        <div class="detail-body">
          <div class="tags">
            <span 
              v-for="cat in work.categories" 
              :key="cat.id"
              class="style-tag"
              :style="cat.type === 'style' ? getStyleTagStyle(cat.name) : getSceneTagStyle()"
            >
              <span v-if="cat.type === 'style'" class="tag-icon">{{ getStyleConfig(cat.name).icon }}</span>
              {{ cat.name }}
            </span>
            <span class="tag tag-cover-type" v-if="coverTypeInfo">
              {{ coverTypeInfo.name }} · {{ coverTypeInfo.widthRatio }}:{{ coverTypeInfo.heightRatio }}
            </span>
          </div>

          <div 
            class="cover-section" 
            :class="['cover-' + coverTypeClass]"
          >
            <div class="cover-image-wrapper" :style="{ aspectRatio: coverAspectRatio }">
              <img :src="work.coverImage || defaultImage" alt="" @error="handleImageError" />
            </div>
            <div class="cover-type-desc" v-if="coverTypeInfo">
              <el-icon><Picture /></el-icon>
              <span>{{ coverTypeInfo.tip }}</span>
            </div>
          </div>

          <div class="stats-row">
            <div class="stat-item">
              <el-icon><View /></el-icon>
              <span>{{ work.viewCount || 0 }} 浏览</span>
            </div>
            <div class="stat-item">
              <el-icon><Star /></el-icon>
              <span>{{ work.favoriteCount || 0 }} 收藏</span>
            </div>
          </div>

          <div class="content-section" v-if="work.content">
            <h3 class="section-title">作品内容</h3>
            <div class="content-text">{{ work.content }}</div>
          </div>

          <div class="layout-preview-section">
            <h3 class="section-title">版式网格预览</h3>
            <LayoutGridPreviewer :layout-config="workLayoutConfig" />
          </div>

          <div class="material-breakdown-section">
            <div class="section-header">
              <el-icon class="section-icon"><MagicStick /></el-icon>
              <h3 class="section-title">素材拆解卡</h3>
            </div>
            <p class="section-desc">了解这篇手账由哪些视觉材料组成</p>
            <div class="material-cards">
              <div
                v-for="material in materialList"
                :key="material.key"
                class="material-card"
                :style="{ borderColor: material.borderColor, background: material.bgColor }"
              >
                <div class="material-icon">{{ material.icon }}</div>
                <div class="material-info">
                  <span class="material-name">{{ material.name }}</span>
                  <span class="material-count">{{ material.count }} 个</span>
                </div>
                <div class="material-description">{{ material.description }}</div>
                <div class="material-samples" v-if="material.areas && material.areas.length > 0">
                  <span
                    v-for="(area, index) in material.areas.slice(0, 3)"
                    :key="index"
                    class="material-tag"
                  >
                    {{ area.label }}
                  </span>
                </div>
              </div>
            </div>
          </div>

          <div class="inspiration-section" v-if="work.inspiration">
            <div class="section-header">
              <el-icon class="section-icon"><MagicStick /></el-icon>
              <h3 class="section-title">灵感来源</h3>
            </div>
            <div class="section-content">{{ work.inspiration }}</div>
          </div>

          <div class="layout-section" v-if="work.layoutIdea">
            <div class="section-header">
              <el-icon class="section-icon"><Grid /></el-icon>
              <h3 class="section-title">排版想法</h3>
            </div>
            <div class="section-content">{{ work.layoutIdea }}</div>
          </div>

          <div class="color-section" v-if="work.colorScheme">
            <div class="section-header">
              <el-icon class="section-icon"><Brush /></el-icon>
              <h3 class="section-title">色票墙</h3>
            </div>
            <ColorPaletteWall
              :color-scheme="work.colorScheme"
              mode="expanded"
            />
            <div v-if="!hasValidSwatches" class="section-content">{{ work.colorScheme }}</div>
          </div>

          <div class="related-palette-section" v-if="relatedColorPalettes.length > 0">
            <div class="section-header">
              <el-icon class="section-icon"><Brush /></el-icon>
              <h3 class="section-title">相似色彩灵感</h3>
            </div>
            <p class="section-desc">探索更多配色方案，找到属于你的色彩灵感</p>
            <div class="related-palette-list" v-loading="loadingPalettes">
              <div
                v-for="palette in relatedColorPalettes.slice(0, 4)"
                :key="palette.id"
                class="related-palette-card"
                @click="handleUsePalette(palette)"
              >
                <div class="related-preview">
                  <div
                    v-for="(swatch, i) in getRelatedPaletteColors(palette)"
                    :key="i"
                    class="related-swatch"
                    :style="{ background: swatch.color, flex: swatch.type === 'primary' ? 2 : 1 }"
                  ></div>
                </div>
                <div class="related-info">
                  <span class="related-name">{{ palette.name }}</span>
                  <span class="related-desc">{{ palette.styleDescription }}</span>
                  <span class="related-count">{{ palette.useCount || 0 }} 人使用</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </template>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { View, Star, Share, MagicStick, Grid, Brush, Picture } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import Header from '@/components/Header.vue'
import LayoutGridPreviewer from '@/components/LayoutGridPreviewer.vue'
import ColorPaletteWall from '@/components/ColorPaletteWall.vue'
import { getWorkDetail } from '@/api/work'
import { toggleFavorite } from '@/api/favorite'
import { getColorPaletteList, useColorPalette } from '@/api/colorPalette'
import { getDefaultLayout } from '@/constants/layoutTemplates'
import { getCoverTypeByCode } from '@/constants/coverTypes'
import { getStyleConfig } from '@/constants/styleTagConfig'
import { MATERIAL_TYPES, extractMaterialStats } from '@/constants/materialTypes'

const route = useRoute()
const router = useRouter()
const work = ref(null)
const loading = ref(false)
const isFavorite = ref(false)
const error = ref(null)
const errorTitle = ref('')
const errorSubtitle = ref('')
const isNotFoundError = ref(false)
const defaultImage = 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800&h=500&fit=crop'
const relatedColorPalettes = ref([])
const loadingPalettes = ref(false)

const getStyleTagStyle = (name) => {
  const config = getStyleConfig(name)
  return {
    background: config.bg,
    color: config.color,
    borderColor: config.borderColor,
  }
}

const getSceneTagStyle = () => ({
  background: '#f0f9ff',
  color: '#4a9eff',
  borderColor: '#b3d9ff',
})

const coverTypeInfo = computed(() => {
  if (!work.value) return null
  return getCoverTypeByCode(work.value.coverType)
})

const coverTypeClass = computed(() => {
  if (!coverTypeInfo.value) return 'horizontal-collage'
  return coverTypeInfo.value.key.toLowerCase().replace(/_/g, '-')
})

const coverAspectRatio = computed(() => {
  if (!coverTypeInfo.value) return '3 / 2'
  return `${coverTypeInfo.value.widthRatio} / ${coverTypeInfo.value.heightRatio}`
})

const workLayoutConfig = computed(() => {
  if (!work.value) return getDefaultLayout()
  if (work.value.layoutConfig) {
    try {
      return typeof work.value.layoutConfig === 'string'
        ? JSON.parse(work.value.layoutConfig)
        : work.value.layoutConfig
    } catch (e) {
      console.error('解析 layoutConfig 失败', e)
    }
  }
  return getDefaultLayout(work.value.categories)
})

const HEX_COLOR_REGEX = /^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$/

const hasValidSwatches = computed(() => {
  if (!work.value || !work.value.colorScheme) return false
  try {
    const parsed = JSON.parse(work.value.colorScheme)
    if (Array.isArray(parsed) && parsed.length > 0 && parsed[0].color) {
      return parsed.some(s =>
        HEX_COLOR_REGEX.test(s.color) && (s.name || s.purpose)
      )
    }
    return false
  } catch (e) {
    return false
  }
})

const materialList = computed(() => {
  const stats = extractMaterialStats(workLayoutConfig.value)
  const list = []

  for (const [key, config] of Object.entries(MATERIAL_TYPES)) {
    const stat = stats[key]
    if (stat && stat.count > 0) {
      list.push({
        ...config,
        count: stat.count,
        areas: stat.areas
      })
    }
  }

  return list
})

const loadRelatedPalettes = async () => {
  if (!work.value || !work.value.categories) {
    relatedColorPalettes.value = []
    return
  }
  loadingPalettes.value = true
  try {
    const categoryIds = work.value.categories.map(c => c.id)
    const res = await getColorPaletteList(categoryIds)
    relatedColorPalettes.value = (res.data || []).filter(p => {
      try {
        const colors = JSON.parse(p.colorScheme)
        return Array.isArray(colors) && colors.length > 0
      } catch (e) {
        return false
      }
    }).slice(0, 6)
  } catch (e) {
    console.error('加载相关色彩灵感卡组失败', e)
    relatedColorPalettes.value = []
  } finally {
    loadingPalettes.value = false
  }
}

const getRelatedPaletteColors = (palette) => {
  try {
    const colors = JSON.parse(palette.colorScheme)
    return colors.slice(0, 5)
  } catch (e) {
    return []
  }
}

const handleUsePalette = async (palette) => {
  try {
    await useColorPalette(palette.id)
    ElMessage.success(`已为「${palette.name}」贡献热度，去发布页试试这套配色吧！`)
  } catch (e) {
    console.error('记录使用次数失败', e)
  }
}

const loadDetail = async () => {
  loading.value = true
  work.value = null
  error.value = null
  isNotFoundError.value = false
  try {
    const res = await getWorkDetail(route.params.id, 1)
    work.value = res.data
    isFavorite.value = res.data?.isFavorite || false
    await loadRelatedPalettes()
  } catch (e) {
    error.value = e
    if (e.code === 404 || (e.message && e.message === '作品不存在')) {
      isNotFoundError.value = true
      errorTitle.value = '作品不存在'
      errorSubtitle.value = '该作品可能已被删除或链接无效'
    } else if (e.message && e.message.includes('Network')) {
      errorTitle.value = '网络异常'
      errorSubtitle.value = '请检查您的网络连接后重试'
    } else if (e.message && e.message.includes('timeout')) {
      errorTitle.value = '请求超时'
      errorSubtitle.value = '服务器响应超时，请稍后再试'
    } else {
      errorTitle.value = '加载失败'
      errorSubtitle.value = e.message || '数据加载异常，请稍后再试'
    }
  } finally {
    loading.value = false
  }
}

const goHome = () => {
  router.push('/')
}

const handleFavorite = async () => {
  try {
    const res = await toggleFavorite(1, work.value.id)
    isFavorite.value = res.data
    work.value.favoriteCount += res.data ? 1 : -1
    ElMessage.success(res.data ? '收藏成功' : '已取消收藏')
  } catch (e) {
    console.error(e)
  }
}

const handleImageError = (e) => {
  e.target.src = defaultImage
}

const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日`
}

onMounted(() => {
  loadDetail()
})

watch(
  () => route.params.id,
  (newId, oldId) => {
    if (newId && newId !== oldId) {
      loadDetail()
    }
  }
)
</script>

<style scoped>
.detail-content {
  padding: 30px 20px 60px;
  max-width: 800px;
}

.state-container {
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.detail-header {
  margin-bottom: 30px;
}

.detail-title {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin-bottom: 20px;
  line-height: 1.4;
}

.detail-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-info img {
  width: 48px;
  height: 48px;
  border-radius: 50%;
}

.author-name {
  display: block;
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin-bottom: 2px;
}

.publish-time {
  font-size: 13px;
  color: #999;
}

.action-btns {
  display: flex;
  gap: 12px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 20px;
  border: 1px solid #e8e8e8;
  border-radius: 20px;
  background: #fff;
  color: #666;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.action-btn:hover {
  border-color: #ff9a9e;
  color: #ff6b9d;
}

.action-btn.favorite.active {
  background: #fff0f5;
  border-color: #ff9a9e;
  color: #ff6b9d;
}

.tags {
  margin-bottom: 24px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.tag-cover-type {
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
  color: #fff;
  border: none;
}

.cover-section {
  margin-bottom: 24px;
  border-radius: 12px;
  overflow: hidden;
  background: #fafafa;
}

.cover-image-wrapper {
  width: 100%;
  position: relative;
  overflow: hidden;
}

.cover-image-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.cover-vertical-full .cover-image-wrapper {
  max-width: 500px;
  margin: 0 auto;
}

.cover-closeup .cover-image-wrapper {
  max-width: 500px;
  margin: 0 auto;
  border-radius: 12px;
}

.cover-type-desc {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 12px 16px;
  background: #fff5f8;
  color: #ff6b9d;
  font-size: 13px;
  border-top: 1px solid #ffe0e9;
}

.cover-type-desc .el-icon {
  font-size: 16px;
}

.stats-row {
  display: flex;
  gap: 32px;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 24px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #666;
  font-size: 14px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
  padding-left: 12px;
  border-left: 4px solid #ff6b9d;
}

.content-section,
.inspiration-section,
.layout-section,
.color-section {
  margin-bottom: 32px;
  padding: 24px;
  border-radius: 12px;
  transition: all 0.3s;
}

.inspiration-section {
  background: linear-gradient(135deg, #fff9e6 0%, #fff5d6 100%);
  border-left: 4px solid #ffc107;
}

.layout-section {
  background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%);
  border-left: 4px solid #4caf50;
}

.color-section {
  background: linear-gradient(135deg, #fce4ec 0%, #f8bbd0 100%);
  border-left: 4px solid #e91e63;
}

.content-text {
  line-height: 1.8;
  color: #555;
  font-size: 15px;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.section-icon {
  font-size: 24px;
}

.inspiration-section .section-icon {
  color: #ff9800;
}

.layout-section .section-icon {
  color: #4caf50;
}

.color-section .section-icon {
  color: #e91e63;
}

.inspiration-section .section-title,
.layout-section .section-title,
.color-section .section-title {
  margin-bottom: 0;
  border-left: none;
  padding-left: 0;
}

.inspiration-section .section-title {
  color: #e65100;
}

.layout-section .section-title {
  color: #2e7d32;
}

.color-section .section-title {
  color: #ad1457;
}

.section-content {
  line-height: 1.8;
  color: #555;
  font-size: 15px;
  white-space: pre-wrap;
}

.material-breakdown-section {
  margin-bottom: 32px;
  padding: 24px;
  border-radius: 12px;
  background: linear-gradient(135deg, #fff9e6 0%, #fff0f5 100%);
  border-left: 4px solid #ff9a9e;
}

.material-breakdown-section .section-icon {
  color: #ff6b9d;
}

.material-breakdown-section .section-title {
  margin-bottom: 0;
  border-left: none;
  padding-left: 0;
  color: #c2185b;
}

.section-desc {
  font-size: 13px;
  color: #888;
  margin: 0 0 20px 36px;
}

.material-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 16px;
}

.material-card {
  border: 2px solid;
  border-radius: 12px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  transition: all 0.3s;
}

.material-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.material-icon {
  font-size: 32px;
}

.material-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.material-name {
  font-size: 16px;
  font-weight: 700;
  color: #333;
}

.material-count {
  font-size: 13px;
  font-weight: 600;
  color: #666;
  background: rgba(255, 255, 255, 0.6);
  padding: 2px 10px;
  border-radius: 10px;
}

.material-description {
  font-size: 12px;
  color: #777;
  line-height: 1.5;
}

.material-samples {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: auto;
  padding-top: 8px;
  border-top: 1px dashed rgba(0, 0, 0, 0.1);
}

.material-tag {
  font-size: 11px;
  padding: 3px 8px;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 8px;
  color: #555;
}

.related-palette-section {
  margin-bottom: 32px;
  padding: 24px;
  border-radius: 12px;
  background: linear-gradient(135deg, #fdfbff 0%, #fff5f8 100%);
  border-left: 4px solid #9370db;
}

.related-palette-section .section-icon {
  color: #9370db;
}

.related-palette-section .section-title {
  margin-bottom: 0;
  border-left: none;
  padding-left: 0;
  color: #6a0dad;
}

.related-palette-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 14px;
}

.related-palette-card {
  background: #fff;
  border: 2px solid #eee;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.related-palette-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(147, 112, 219, 0.15);
  border-color: #dda0dd;
}

.related-preview {
  display: flex;
  height: 48px;
  overflow: hidden;
}

.related-swatch {
  transition: flex 0.3s;
}

.related-palette-card:hover .related-swatch {
  flex: 1 !important;
}

.related-info {
  padding: 12px 14px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.related-name {
  font-size: 14px;
  font-weight: 700;
  color: #333;
}

.related-desc {
  font-size: 12px;
  color: #888;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.related-count {
  font-size: 11px;
  color: #aaa;
  margin-top: 2px;
}

@media (max-width: 600px) {
  .related-palette-list {
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
  }

  .related-palette-section {
    padding: 18px;
  }

  .related-info {
    padding: 10px 12px;
  }
}
</style>
