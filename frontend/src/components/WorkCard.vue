<template>
  <div class="work-card card" @click="goDetail">
    <div class="card-image">
      <img 
        :src="work.coverImage || defaultImage" 
        :alt="work.title"
        loading="lazy"
        @error="handleImageError"
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
    </div>
    <div class="card-content">
      <h3 class="card-title">{{ work.title }}</h3>
      <div class="card-tags">
        <span 
          v-for="cat in work.categories || []" 
          :key="cat.id"
          class="tag"
          :class="cat.type === 'style' ? 'tag-style' : 'tag-scene'"
        >
          {{ cat.name }}
        </span>
      </div>
      <div class="card-footer">
        <div class="author">
          <img :src="work.avatar || 'https://api.dicebear.com/7.x/avataaars/svg?seed=default'" alt="" />
          <span>{{ work.nickname || '手账达人' }}</span>
        </div>
        <span class="date">{{ formatDate(work.createTime) }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { View, Star } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

const props = defineProps({
  work: {
    type: Object,
    required: true
  }
})

const router = useRouter()
const defaultImage = 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400&h=300&fit=crop'

const goDetail = () => {
  router.push(`/work/${props.work.id}`)
}

const handleImageError = (e) => {
  e.target.src = defaultImage
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
}

.card-image {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
  background: #f5f5f5;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s;
}

.work-card:hover .card-image img {
  transform: scale(1.05);
}

.card-stats {
  position: absolute;
  top: 10px;
  right: 10px;
  display: flex;
  gap: 10px;
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

.card-content {
  padding: 16px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 10px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-tags {
  margin-bottom: 12px;
}

.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.author {
  display: flex;
  align-items: center;
  gap: 8px;
}

.author img {
  width: 28px;
  height: 28px;
  border-radius: 50%;
}

.author span {
  font-size: 13px;
  color: #666;
}

.date {
  font-size: 12px;
  color: #999;
}
</style>
