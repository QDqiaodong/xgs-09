<template>
  <div class="style-profile">
    <Header />
    
    <main class="container profile-content" v-loading="loading">
      <div class="profile-header" v-if="profile">
        <div class="profile-info">
          <img class="avatar" :src="profile.avatar || 'https://api.dicebear.com/7.x/avataaars/svg?seed=journal'" alt="" />
          <div class="info-text">
            <div class="name-row">
              <h2 class="username">{{ profile.nickname }}</h2>
              <span class="signature-badge" v-if="profile.signatureStyle">
                ✨ {{ profile.signatureStyle }}
              </span>
            </div>
            <p class="bio">{{ profile.bio }}</p>
            <div class="dominant-color-row" v-if="profile.dominantColor">
              <span class="color-label">主色调：</span>
              <span class="color-dot" :style="{ background: profile.dominantColor }"></span>
              <span class="color-hex">{{ profile.dominantColor }}</span>
            </div>
          </div>
        </div>
        <div class="profile-stats">
          <div class="stat">
            <span class="num">{{ profile.totalWorks || 0 }}</span>
            <span class="label">总作品</span>
          </div>
          <div class="stat">
            <span class="num">{{ profile.totalPublicWorks || 0 }}</span>
            <span class="label">可展示</span>
          </div>
          <div class="stat">
            <span class="num">{{ profile.stylePreferences?.length || 0 }}</span>
            <span class="label">风格类型</span>
          </div>
        </div>
      </div>

      <div class="empty-state" v-else-if="!loading && error">
        <el-empty :description="error" />
      </div>

      <div class="profile-sections" v-if="profile">
        <div class="section-card styles-section">
          <h3 class="section-title">
            <span class="title-icon">🎨</span>
            常用风格
          </h3>
          <div class="styles-grid" v-if="profile.stylePreferences && profile.stylePreferences.length > 0">
            <div 
              v-for="style in profile.stylePreferences.slice(0, 8)" 
              :key="style.categoryId"
              class="style-item"
            >
              <div class="style-header">
                <span class="style-name">{{ style.categoryName }}</span>
                <span class="style-count">{{ style.count }} 篇</span>
              </div>
              <div class="style-bar-wrapper">
                <div 
                  class="style-bar" 
                  :style="{ width: getPercentage(style.count, profile.totalPublicWorks) }"
                ></div>
                <span class="style-percentage">{{ getPercentage(style.count, profile.totalPublicWorks) }}</span>
              </div>
            </div>
          </div>
          <div class="empty-section" v-else>
            <span>暂无风格数据</span>
          </div>
        </div>

        <div class="section-card scenes-section">
          <h3 class="section-title">
            <span class="title-icon">📝</span>
            场景偏好
          </h3>
          <div class="scenes-grid" v-if="profile.scenePreferences && profile.scenePreferences.length > 0">
            <div 
              v-for="scene in profile.scenePreferences.slice(0, 6)" 
              :key="scene.categoryId"
              class="scene-tag"
            >
              <span class="scene-name">{{ scene.categoryName }}</span>
              <span class="scene-count">{{ scene.count }}</span>
            </div>
          </div>
          <div class="empty-section" v-else>
            <span>暂无场景数据</span>
          </div>
        </div>

        <div class="section-card color-section" v-if="profile.colorTendency">
          <h3 class="section-title">
            <span class="title-icon">🌈</span>
            色彩倾向
          </h3>
          <ColorUsageProfile :color-usage="profile.colorTendency" />
        </div>

        <div class="section-card works-section">
          <h3 class="section-title">
            <span class="title-icon">📚</span>
            代表作品
            <span class="subtitle">收藏数 · 浏览量 · 精选排序</span>
          </h3>
          <div class="works-grid" v-if="profile.featuredWorks && profile.featuredWorks.length > 0">
            <div 
              v-for="work in profile.featuredWorks.slice(0, 8)" 
              :key="work.workId" 
              class="work-cover-item"
              @click="goToWork(work.workId)"
            >
              <div class="cover-wrapper">
                <img 
                  v-if="work.coverImage" 
                  :src="work.coverImage" 
                  :alt="work.title"
                  class="cover-image"
                />
                <div class="cover-placeholder" v-else>
                  <span class="placeholder-icon">📖</span>
                </div>
                <div class="cover-overlay">
                  <div class="cover-stats">
                    <span class="stat-item">
                      <el-icon><Star /></el-icon>
                      {{ work.favoriteCount }}
                    </span>
                    <span class="stat-item">
                      <el-icon><View /></el-icon>
                      {{ work.viewCount }}
                    </span>
                  </div>
                </div>
              </div>
              <div class="cover-info">
                <h4 class="cover-title" :title="work.title">{{ work.title }}</h4>
                <div class="cover-meta">
                  <span class="cover-style" v-if="work.styleName">{{ work.styleName }}</span>
                  <span class="cover-date">{{ work.publishDate }}</span>
                </div>
              </div>
            </div>
          </div>
          <div class="empty-section" v-else>
            <span>暂无代表作品</span>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Star, View } from '@element-plus/icons-vue'
import Header from '@/components/Header.vue'
import ColorUsageProfile from '@/components/ColorUsageProfile.vue'
import { getUserStyleProfile } from '@/api/work'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const error = ref('')
const profile = ref(null)

const userId = computed(() => route.params.id || 1)

const getPercentage = (count, total) => {
  if (!total || total === 0) return '0%'
  return Math.round((count / total) * 100) + '%'
}

const loadProfile = async () => {
  loading.value = true
  error.value = ''
  try {
    const res = await getUserStyleProfile(userId.value)
    if (res.code === 200) {
      profile.value = res.data
    } else {
      error.value = res.message || '加载失败'
    }
  } catch (e) {
    error.value = '加载失败，请稍后重试'
    console.error(e)
  } finally {
    loading.value = false
  }
}

const goToWork = (workId) => {
  router.push(`/work/${workId}`)
}

onMounted(() => {
  loadProfile()
})
</script>

<style scoped>
.profile-content {
  padding: 30px 20px 60px;
}

.profile-header {
  background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 50%, #ffecd2 100%);
  padding: 36px;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(252, 182, 159, 0.3);
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 28px;
  position: relative;
  overflow: hidden;
}

.profile-header::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -10%;
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.3) 0%, transparent 70%);
  border-radius: 50%;
}

.profile-info {
  display: flex;
  align-items: center;
  gap: 24px;
  position: relative;
  z-index: 1;
}

.avatar {
  width: 96px;
  height: 96px;
  border-radius: 50%;
  border: 5px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.name-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.username {
  font-size: 26px;
  font-weight: 700;
  color: #333;
  margin: 0;
}

.signature-badge {
  background: rgba(255, 255, 255, 0.9);
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 13px;
  color: #ff6b9d;
  font-weight: 500;
  box-shadow: 0 2px 8px rgba(255, 107, 157, 0.2);
}

.bio {
  font-size: 15px;
  color: #666;
  margin: 0 0 10px 0;
  max-width: 400px;
  line-height: 1.6;
}

.dominant-color-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.color-label {
  font-size: 13px;
  color: #666;
}

.color-dot {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  border: 2px solid #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.2);
}

.color-hex {
  font-size: 13px;
  color: #666;
  font-family: 'Courier New', monospace;
  font-weight: 500;
}

.profile-stats {
  display: flex;
  gap: 48px;
  position: relative;
  z-index: 1;
}

.stat {
  text-align: center;
}

.stat .num {
  display: block;
  font-size: 32px;
  font-weight: 700;
  color: #fff;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.15);
}

.stat .label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
  margin-top: 4px;
}

.profile-sections {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.section-card {
  background: #fff;
  padding: 28px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 24px 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.title-icon {
  font-size: 22px;
}

.subtitle {
  font-size: 12px;
  color: #999;
  font-weight: normal;
  margin-left: auto;
}

.styles-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.style-item {
  background: #fafafa;
  padding: 16px 20px;
  border-radius: 10px;
  transition: all 0.3s ease;
}

.style-item:hover {
  background: #fff5f7;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 107, 157, 0.1);
}

.style-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.style-name {
  font-size: 15px;
  font-weight: 600;
  color: #333;
}

.style-count {
  font-size: 13px;
  color: #999;
}

.style-bar-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
}

.style-bar {
  height: 8px;
  background: linear-gradient(90deg, #ff9a9e, #fecfef);
  border-radius: 4px;
  transition: width 0.5s ease;
}

.style-percentage {
  font-size: 13px;
  font-weight: 600;
  color: #ff6b9d;
  min-width: 40px;
  text-align: right;
}

.scenes-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.scene-tag {
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
  padding: 10px 20px;
  border-radius: 24px;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
  cursor: default;
}

.scene-tag:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(168, 237, 234, 0.4);
}

.scene-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.scene-count {
  background: rgba(255, 255, 255, 0.8);
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  color: #666;
  font-weight: 600;
}

.works-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.work-cover-item {
  cursor: pointer;
  transition: all 0.3s ease;
}

.work-cover-item:hover {
  transform: translateY(-4px);
}

.cover-wrapper {
  position: relative;
  aspect-ratio: 4 / 3;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 10px;
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.work-cover-item:hover .cover-image {
  transform: scale(1.05);
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.placeholder-icon {
  font-size: 48px;
  opacity: 0.5;
}

.cover-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to bottom, transparent 50%, rgba(0, 0, 0, 0.6) 100%);
  display: flex;
  align-items: flex-end;
  padding: 12px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.work-cover-item:hover .cover-overlay {
  opacity: 1;
}

.cover-stats {
  display: flex;
  gap: 16px;
  color: #fff;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
}

.stat-item .el-icon {
  font-size: 14px;
}

.cover-info {
  padding: 0 4px;
}

.cover-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin: 0 0 6px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.cover-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.cover-style {
  font-size: 12px;
  color: #ff6b9d;
  background: #fff5f7;
  padding: 2px 8px;
  border-radius: 10px;
}

.cover-date {
  font-size: 12px;
  color: #999;
}

.empty-state,
.empty-section {
  text-align: center;
  padding: 40px 20px;
  color: #999;
  font-size: 14px;
}

@media (max-width: 768px) {
  .profile-header {
    flex-direction: column;
    gap: 24px;
    text-align: center;
  }
  
  .profile-info {
    flex-direction: column;
    text-align: center;
  }
  
  .name-row {
    justify-content: center;
    flex-wrap: wrap;
  }
  
  .dominant-color-row {
    justify-content: center;
  }
  
  .profile-stats {
    gap: 32px;
  }
  
  .styles-grid {
    grid-template-columns: 1fr;
  }
  
  .works-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
