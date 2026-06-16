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
              class="tag"
              :class="cat.type === 'style' ? 'tag-style' : 'tag-scene'"
            >
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
              <h3 class="section-title">色彩方案</h3>
            </div>
            <template v-if="validColorSwatches.length > 0">
              <div class="color-groups">
                <div class="color-group" v-if="primarySwatches.length > 0">
                  <div class="group-label primary">
                    <span class="label-text">主色</span>
                    <span class="label-count">{{ primarySwatches.length }}</span>
                  </div>
                  <div class="group-swatches">
                    <div 
                      v-for="(swatch, index) in primarySwatches" 
                      :key="'p-' + index"
                      class="swatch-card"
                    >
                      <div class="swatch-preview" :style="{ background: swatch.color }"></div>
                      <div class="swatch-details">
                        <span class="swatch-name">{{ swatch.name }}</span>
                        <span class="swatch-purpose">{{ swatch.purpose }}</span>
                      </div>
                    </div>
                  </div>
                </div>

                <div class="color-group" v-if="secondarySwatches.length > 0">
                  <div class="group-label secondary">
                    <span class="label-text">辅助色</span>
                    <span class="label-count">{{ secondarySwatches.length }}</span>
                  </div>
                  <div class="group-swatches">
                    <div 
                      v-for="(swatch, index) in secondarySwatches" 
                      :key="'s-' + index"
                      class="swatch-card"
                    >
                      <div class="swatch-preview" :style="{ background: swatch.color }"></div>
                      <div class="swatch-details">
                        <span class="swatch-name">{{ swatch.name }}</span>
                        <span class="swatch-purpose">{{ swatch.purpose }}</span>
                      </div>
                    </div>
                  </div>
                </div>

                <div class="color-group" v-if="accentSwatches.length > 0">
                  <div class="group-label accent">
                    <span class="label-text">点缀色</span>
                    <span class="label-count">{{ accentSwatches.length }}</span>
                  </div>
                  <div class="group-swatches">
                    <div 
                      v-for="(swatch, index) in accentSwatches" 
                      :key="'a-' + index"
                      class="swatch-card"
                    >
                      <div class="swatch-preview" :style="{ background: swatch.color }"></div>
                      <div class="swatch-details">
                        <span class="swatch-name">{{ swatch.name }}</span>
                        <span class="swatch-purpose">{{ swatch.purpose }}</span>
                      </div>
                    </div>
                  </div>
                </div>

                <div class="color-group" v-if="untypedSwatches.length > 0">
                  <div class="group-label other">
                    <span class="label-text">其他</span>
                    <span class="label-count">{{ untypedSwatches.length }}</span>
                  </div>
                  <div class="group-swatches">
                    <div 
                      v-for="(swatch, index) in untypedSwatches" 
                      :key="'u-' + index"
                      class="swatch-card"
                    >
                      <div class="swatch-preview" :style="{ background: swatch.color }"></div>
                      <div class="swatch-details">
                        <span class="swatch-name">{{ swatch.name }}</span>
                        <span class="swatch-purpose">{{ swatch.purpose }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </template>
            <div v-else class="section-content">{{ work.colorScheme }}</div>
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
import { getWorkDetail } from '@/api/work'
import { toggleFavorite } from '@/api/favorite'
import { getDefaultLayout } from '@/constants/layoutTemplates'
import { getCoverTypeByCode } from '@/constants/coverTypes'

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

const colorSwatches = computed(() => {
  if (!work.value || !work.value.colorScheme) return []
  try {
    const parsed = JSON.parse(work.value.colorScheme)
    if (Array.isArray(parsed) && parsed.length > 0 && parsed[0].color) {
      return parsed
    }
    return []
  } catch (e) {
    return []
  }
})

const HEX_COLOR_REGEX = /^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$/

const isValidHexColor = (color) => {
  return typeof color === 'string' && HEX_COLOR_REGEX.test(color)
}

const validColorSwatches = computed(() => {
  return colorSwatches.value.filter(swatch => 
    isValidHexColor(swatch.color) && (swatch.name || swatch.purpose)
  )
})

const primarySwatches = computed(() => {
  return validColorSwatches.value.filter(s => s.type === 'primary')
})

const secondarySwatches = computed(() => {
  return validColorSwatches.value.filter(s => s.type === 'secondary')
})

const accentSwatches = computed(() => {
  return validColorSwatches.value.filter(s => s.type === 'accent')
})

const untypedSwatches = computed(() => {
  return validColorSwatches.value.filter(s => !s.type || !['primary', 'secondary', 'accent'].includes(s.type))
})

const loadDetail = async () => {
  loading.value = true
  work.value = null
  error.value = null
  isNotFoundError.value = false
  try {
    const res = await getWorkDetail(route.params.id, 1)
    work.value = res.data
    isFavorite.value = res.data?.isFavorite || false
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

.color-groups {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.color-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.group-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #333;
  padding-left: 8px;
  border-left: 3px solid #e91e63;
}

.group-label.primary {
  border-left-color: #ff6b9d;
  color: #ad1457;
}

.group-label.secondary {
  border-left-color: #4a9eff;
  color: #1565c0;
}

.group-label.accent {
  border-left-color: #ffc107;
  color: #ff8f00;
}

.group-label.other {
  border-left-color: #9e9e9e;
  color: #616161;
}

.label-text {
  font-size: 14px;
}

.label-count {
  font-size: 12px;
  font-weight: 400;
  color: #999;
  background: #fff;
  padding: 2px 8px;
  border-radius: 10px;
}

.group-swatches {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 4px;
  scrollbar-width: thin;
  scrollbar-color: rgba(233, 30, 99, 0.3) transparent;
}

.group-swatches::-webkit-scrollbar {
  height: 4px;
}

.group-swatches::-webkit-scrollbar-track {
  background: transparent;
}

.group-swatches::-webkit-scrollbar-thumb {
  background: rgba(233, 30, 99, 0.2);
  border-radius: 2px;
}

.swatch-card {
  flex-shrink: 0;
  width: 160px;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s;
}

.swatch-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.swatch-preview {
  width: 100%;
  height: 80px;
}

.swatch-details {
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.swatch-name {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.swatch-purpose {
  font-size: 12px;
  color: #999;
  line-height: 1.4;
}

@media (max-width: 600px) {
  .color-swatches-horizontal {
    gap: 12px;
    margin: 0 -24px;
    padding: 0 24px 8px;
  }
  
  .swatch-card {
    width: 140px;
  }
  
  .swatch-preview {
    height: 70px;
  }
  
  .swatch-details {
    padding: 10px;
  }
}
</style>
