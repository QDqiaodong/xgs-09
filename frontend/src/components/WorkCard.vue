<template>
  <div class="work-card card" @click="goDetail">
    <div 
      class="card-image-wrapper" 
      :style="{ aspectRatio: coverAspectRatio }"
    >
      <div class="card-image-skeleton" v-if="imageLoading">
        <div class="skeleton-shimmer"></div>
      </div>
      <img 
        class="card-image-img"
        :class="{ 'image-loaded': !imageLoading }"
        :src="work.coverImage || defaultImage" 
        :alt="work.title"
        loading="lazy"
        @error="handleImageError"
        @load="handleImageLoad"
      />
      <div class="card-stats">
        <span class="stat">
          <el-icon><View /></el-icon>
          {{ work.viewCount || 0 }}
        </span>
        <span class="stat">
          <el-icon><Star /></el-icon>
          {{ work.favoriteCount || 0 }}
        </span>
      </div>
      <div class="cover-type-badge" v-if="coverTypeInfo">
        {{ coverTypeInfo.name }}
      </div>
    </div>
    <div class="card-content">
      <h3 class="card-title" :title="work.title">{{ work.title }}</h3>
      <div class="card-tags">
        <span 
          v-for="cat in (work.categories || []).slice(0, 3)" 
          :key="cat.id"
          class="style-tag"
          :style="cat.type === 'style' ? getStyleTagStyle(cat.name) : getSceneTagStyle()"
        >
          <span v-if="cat.type === 'style'" class="tag-icon">{{ getStyleConfig(cat.name).icon }}</span>
          {{ cat.name }}
        </span>
        <span 
          v-if="(work.categories || []).length > 3" 
          class="tag tag-more"
        >
          +{{ (work.categories || []).length - 3 }}
        </span>
      </div>
      <ColorPaletteWall
        v-if="work.colorScheme"
        :color-scheme="work.colorScheme"
        mode="compact"
        class="card-palette"
      />
      <div class="card-footer">
        <div class="author">
          <img :src="work.avatar || 'https://api.dicebear.com/7.x/avataaars/svg?seed=default'" alt="" />
          <span class="author-name" :title="work.nickname || '手账达人'">{{ work.nickname || '手账达人' }}</span>
        </div>
        <span class="date">{{ formatDate(work.createTime) }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { View, Star } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { getCoverTypeByCode } from '@/constants/coverTypes'
import { getStyleConfig } from '@/constants/styleTagConfig'
import ColorPaletteWall from '@/components/ColorPaletteWall.vue'

const props = defineProps({
  work: {
    type: Object,
    required: true
  }
})

const router = useRouter()
const defaultImage = 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400&h=300&fit=crop'
const imageLoading = ref(true)

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
  return getCoverTypeByCode(props.work.coverType)
})

const coverAspectRatio = computed(() => {
  const info = coverTypeInfo.value
  return `${info.widthRatio} / ${info.heightRatio}`
})

const goDetail = () => {
  router.push(`/work/${props.work.id}`)
}

const handleImageError = (e) => {
  e.target.src = defaultImage
  imageLoading.value = false
}

const handleImageLoad = () => {
  imageLoading.value = false
}

const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  const now = new Date()
  const diff = now - d
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  return `${d.getMonth() + 1}月${d.getDate()}日`
}
</script>

<style scoped>
.work-card {
  cursor: pointer;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.card-image-wrapper {
  position: relative;
  width: 100%;
  overflow: hidden;
  background: #f5f5f5;
  flex-shrink: 0;
}

.card-image-skeleton {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  z-index: 1;
  overflow: hidden;
}

.skeleton-shimmer {
  width: 100%;
  height: 100%;
  animation: shimmer 1.5s infinite linear;
}

@keyframes shimmer {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}

.card-image-img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
  opacity: 0;
  transition: opacity 0.3s ease, transform 0.5s ease;
  z-index: 2;
}

.card-image-img.image-loaded {
  opacity: 1;
}

.work-card:hover .card-image-img {
  transform: scale(1.05);
}

.card-stats {
  position: absolute;
  top: 10px;
  right: 10px;
  display: flex;
  gap: 10px;
  z-index: 3;
}

.stat {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  background: rgba(0, 0, 0, 0.6);
  color: #fff;
  border-radius: 12px;
  font-size: 12px;
}

.cover-type-badge {
  position: absolute;
  bottom: 10px;
  left: 10px;
  padding: 4px 12px;
  background: rgba(255, 107, 157, 0.9);
  color: #fff;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  z-index: 3;
}

.card-content {
  padding: 16px;
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 10px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.4;
  flex-shrink: 0;
}

.card-tags {
  margin-bottom: 12px;
  min-height: 24px;
  flex-shrink: 0;
  overflow: hidden;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.card-tags .tag,
.card-tags .style-tag {
  margin-right: 0;
  margin-bottom: 0;
  line-height: 1.4;
}

.tag-more {
  background: #f5f5f5;
  color: #999;
}

.card-palette {
  margin-bottom: 8px;
  flex-shrink: 0;
}

.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
  margin-top: auto;
  flex-shrink: 0;
}

.author {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
  flex: 1;
}

.author img {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  flex-shrink: 0;
}

.author-name {
  font-size: 13px;
  color: #666;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 120px;
}

.date {
  font-size: 12px;
  color: #999;
  flex-shrink: 0;
  margin-left: 8px;
}
</style>
