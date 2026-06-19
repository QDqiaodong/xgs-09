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

          <div class="elements-section" v-if="work.elementGroups && work.elementGroups.length > 0">
            <div class="section-header">
              <el-icon class="section-icon"><Collection /></el-icon>
              <h3 class="section-title">页面元素清单</h3>
              <div class="elements-summary">
                共 {{ totalElementCount }} 个元素
              </div>
            </div>
            <div class="element-groups">
              <div
                v-for="group in work.elementGroups"
                :key="group.category"
                class="element-group-card"
                :style="{ '--group-color': getElementCategoryColor(group.category), '--group-border': getElementCategoryBorder(group.category) }"
              >
                <div class="group-header">
                  <div class="group-icon-wrap">
                    <span class="group-icon">{{ group.categoryIcon }}</span>
                  </div>
                  <div class="group-info">
                    <h4 class="group-name">{{ group.categoryDesc }}</h4>
                    <span class="group-count">{{ group.elements.length }} 项 · 共 {{ group.totalCount }} 个</span>
                  </div>
                </div>
                <div class="group-elements">
                  <div
                    v-for="el in group.elements"
                    :key="el.id"
                    class="group-element-item"
                  >
                    <div class="element-thumb" v-if="el.imageUrl">
                      <img :src="el.imageUrl" :alt="el.name" @error="handleElementImageError" />
                    </div>
                    <div class="element-thumb placeholder" v-else>
                      <span>{{ group.categoryIcon }}</span>
                    </div>
                    <div class="element-info">
                      <div class="element-name-row">
                        <span class="element-name">{{ el.name }}</span>
                        <span v-if="el.quantity > 1" class="element-quantity" :style="{ background: getElementCategoryColor(group.category) }">
                          ×{{ el.quantity }}
                        </span>
                      </div>
                      <div v-if="el.description" class="element-desc">{{ el.description }}</div>
                    </div>
                  </div>
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
import { View, Star, Share, MagicStick, Grid, Brush, Picture, Collection } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import Header from '@/components/Header.vue'
import LayoutGridPreviewer from '@/components/LayoutGridPreviewer.vue'
import ColorPaletteWall from '@/components/ColorPaletteWall.vue'
import { getWorkDetail } from '@/api/work'
import { toggleFavorite } from '@/api/favorite'
import { getDefaultLayout } from '@/constants/layoutTemplates'
import { getCoverTypeByCode } from '@/constants/coverTypes'
import { getStyleConfig } from '@/constants/styleTagConfig'
import { getElementCategoryByCode } from '@/constants/elementCategories'

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

const totalElementCount = computed(() => {
  if (!work.value || !work.value.elementGroups) return 0
  return work.value.elementGroups.reduce((sum, g) => sum + (g.totalCount || 0), 0)
})

const getElementCategoryColor = (code) => {
  const cat = getElementCategoryByCode(code)
  return cat ? cat.color : '#ff6b9d'
}

const getElementCategoryBorder = (code) => {
  const cat = getElementCategoryByCode(code)
  return cat ? cat.borderColor : '#ffb6c1'
}

const handleElementImageError = (e) => {
  e.target.style.display = 'none'
}

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

.elements-section {
  margin-bottom: 32px;
  padding: 24px;
  border-radius: 12px;
  transition: all 0.3s;
  background: linear-gradient(135deg, #fff5f8 0%, #eef2ff 100%);
  border-left: 4px solid #f093fb;
}

.elements-section .section-icon {
  color: #f093fb;
}

.elements-section .section-title {
  margin-bottom: 0;
  border-left: none;
  padding-left: 0;
  color: #7b2d8e;
}

.elements-summary {
  margin-left: auto;
  padding: 4px 12px;
  background: #fff;
  border-radius: 12px;
  font-size: 13px;
  color: #888;
  font-weight: 500;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.element-groups {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.element-group-card {
  --group-color: #ff6b9d;
  --group-border: #ffb6c1;
  background: #fff;
  border-radius: 14px;
  border: 1px solid #f0f0f0;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.04);
  transition: all 0.3s;
}

.element-group-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.group-header {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px 20px;
  background: linear-gradient(135deg, var(--group-color) 0%, var(--group-border) 100%);
}

.group-icon-wrap {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.25);
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(4px);
}

.group-icon {
  font-size: 24px;
}

.group-info {
  flex: 1;
}

.group-name {
  margin: 0 0 2px 0;
  font-size: 16px;
  font-weight: 700;
  color: #fff;
}

.group-count {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.85);
}

.group-elements {
  padding: 12px 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.group-element-item {
  display: flex;
  gap: 12px;
  padding: 10px;
  border-radius: 10px;
  background: #fafafa;
  transition: all 0.3s;
}

.group-element-item:hover {
  background: #f5f5f5;
}

.element-thumb {
  width: 52px;
  height: 52px;
  border-radius: 10px;
  overflow: hidden;
  flex-shrink: 0;
  border: 1px solid #eee;
  background: #fff;
}

.element-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.element-thumb.placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #fafafa 0%, #f0f0f0 100%);
  font-size: 24px;
}

.element-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 4px;
}

.element-name-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.element-name {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.element-quantity {
  padding: 2px 8px;
  border-radius: 10px;
  color: #fff;
  font-size: 12px;
  font-weight: 600;
  line-height: 1.4;
}

.element-desc {
  font-size: 13px;
  color: #888;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

@media (max-width: 600px) {
  .elements-summary {
    display: none;
  }

  .element-groups {
    gap: 16px;
  }

  .group-header {
    padding: 14px 16px;
    gap: 12px;
  }

  .group-icon-wrap {
    width: 40px;
    height: 40px;
  }

  .group-icon {
    font-size: 20px;
  }

  .group-elements {
    padding: 10px 14px;
  }

  .element-thumb {
    width: 44px;
    height: 44px;
  }

  .element-thumb.placeholder {
    font-size: 20px;
  }
}
</style>
