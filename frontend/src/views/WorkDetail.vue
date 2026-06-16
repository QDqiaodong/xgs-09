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
          </div>

          <div class="cover-section">
            <img :src="work.coverImage || defaultImage" alt="" @error="handleImageError" />
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

          <div class="inspiration-section" v-if="work.layoutIdea || work.colorScheme || work.inspiration">
            <h3 class="section-title">创作思路</h3>
            
            <div class="idea-item" v-if="work.layoutIdea">
              <div class="idea-label">排版思路</div>
              <div class="idea-content">{{ work.layoutIdea }}</div>
            </div>
            
            <div class="idea-item" v-if="work.colorScheme">
              <div class="idea-label">配色方案</div>
              <div class="idea-content">{{ work.colorScheme }}</div>
            </div>
            
            <div class="idea-item" v-if="work.inspiration">
              <div class="idea-label">创作灵感</div>
              <div class="idea-content">{{ work.inspiration }}</div>
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
import { View, Star, Share } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import Header from '@/components/Header.vue'
import LayoutGridPreviewer from '@/components/LayoutGridPreviewer.vue'
import { getWorkDetail } from '@/api/work'
import { toggleFavorite } from '@/api/favorite'
import { getDefaultLayout } from '@/constants/layoutTemplates'

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
}

.cover-section {
  margin-bottom: 24px;
  border-radius: 12px;
  overflow: hidden;
}

.cover-section img {
  width: 100%;
  max-height: 500px;
  object-fit: cover;
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
.inspiration-section {
  margin-bottom: 32px;
}

.content-text {
  line-height: 1.8;
  color: #555;
  font-size: 15px;
}

.idea-item {
  margin-bottom: 20px;
  padding: 16px;
  background: #faf8f5;
  border-radius: 8px;
}

.idea-label {
  font-size: 14px;
  font-weight: 600;
  color: #ff6b9d;
  margin-bottom: 8px;
}

.idea-content {
  line-height: 1.7;
  color: #555;
  font-size: 14px;
}
</style>
