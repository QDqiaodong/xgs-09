<template>
  <div class="scene-gallery">
    <Header />

    <section class="gallery-hero">
      <div class="container hero-inner">
        <div class="hero-text">
          <h1 class="hero-title">场景分类</h1>
          <p class="hero-subtitle">
            选择一个创作场景，开启你的手账记录之旅<br />
            从旅行美食到观影打卡，每个场景都有专属灵感
          </p>
        </div>
        <div class="hero-decor">
          <span class="decor-emoji decor-1">✈️</span>
          <span class="decor-emoji decor-2">🍜</span>
          <span class="decor-emoji decor-3">🎬</span>
          <span class="decor-emoji decor-4">✅</span>
          <span class="decor-emoji decor-5">🎉</span>
          <span class="decor-emoji decor-6">📚</span>
        </div>
      </div>
    </section>

    <main class="container gallery-main">
      <div class="section-header">
        <h2 class="section-title">
          <span class="title-icon">🎯</span>
          创作目的陈列
        </h2>
        <p class="section-desc">点击任意卡片，查看对应场景的记录任务模板</p>
      </div>

      <div class="scene-grid" v-loading="loading">
        <div
          v-for="scene in sceneCategories"
          :key="scene.id"
          class="scene-card"
          :style="{ background: scene.bannerColor || getDefaultGradient(scene.id) }"
          @click="enterScene(scene)"
        >
          <div class="scene-card-inner">
            <div class="scene-icon-wrapper">
              <span class="scene-icon">{{ scene.icon || '📝' }}</span>
            </div>
            <div class="scene-info">
              <h3 class="scene-name">{{ scene.name }}</h3>
              <p class="scene-desc">{{ scene.description || '记录属于你的精彩瞬间' }}</p>
            </div>
            <div class="scene-stats">
              <span class="stat-item">
                <span class="stat-icon">📋</span>
                <span class="stat-num">{{ scene.taskCount || 0 }}</span>
                <span class="stat-label">任务模板</span>
              </span>
              <span class="stat-item">
                <span class="stat-icon">✨</span>
                <span class="stat-num">{{ scene.workCount || 0 }}</span>
                <span class="stat-label">灵感作品</span>
              </span>
            </div>
            <div class="scene-cta">
              <span class="cta-text">探索灵感</span>
              <el-icon class="cta-arrow"><ArrowRight /></el-icon>
            </div>
          </div>
          <div class="scene-card-bg-pattern" :class="getPatternClass(scene.id)"></div>
        </div>
      </div>

      <div class="empty error-state" v-if="!loading && error">
        <el-empty :description="errorMessage" image-size="120">
          <template #image>
            <div class="error-icon-wrapper">
              <el-icon :size="80" color="#f56c6c"><WarningFilled /></el-icon>
            </div>
          </template>
          <template #description>
            <span class="error-text">{{ errorMessage }}</span>
          </template>
          <el-button type="primary" @click="loadSceneCategories">
            <el-icon class="mr-2"><RefreshRight /></el-icon>
            重新加载
          </el-button>
        </el-empty>
      </div>

      <div class="empty" v-else-if="!loading && sceneCategories.length === 0">
        <el-empty description="暂无场景分类" />
      </div>

      <div class="featured-section">
        <h2 class="section-title">
          <span class="title-icon">🔥</span>
          热门场景推荐
        </h2>
        <div class="featured-row">
          <div
            v-for="(scene, index) in featuredScenes"
            :key="scene.id"
            class="featured-card"
            @click="enterScene(scene)"
          >
            <span class="featured-rank" :style="{ background: getRankColor(index) }">{{ index + 1 }}</span>
            <div class="featured-content">
              <span class="featured-icon">{{ scene.icon }}</span>
              <div class="featured-text">
                <h4 class="featured-name">{{ scene.name }}</h4>
                <p class="featured-meta">
                  {{ scene.workCount || 0 }} 篇作品 · {{ scene.taskCount || 0 }} 个模板
                </p>
              </div>
            </div>
            <el-icon class="featured-arrow"><ArrowRight /></el-icon>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowRight, WarningFilled, RefreshRight } from '@element-plus/icons-vue'
import Header from '@/components/Header.vue'
import { getCategoryList } from '@/api/category'

const router = useRouter()
const sceneCategories = ref([])
const loading = ref(false)
const error = ref(false)
const errorMessage = ref('')

const featuredScenes = computed(() => {
  return [...sceneCategories.value]
    .sort((a, b) => (b.workCount || 0) - (a.workCount || 0))
    .slice(0, 3)
})

const loadSceneCategories = async () => {
  loading.value = true
  error.value = false
  errorMessage.value = ''
  try {
    const res = await getCategoryList('scene')
    sceneCategories.value = res.data || []
  } catch (e) {
    error.value = true
    errorMessage.value = e.message || '加载失败，请检查网络后重试'
    sceneCategories.value = []
  } finally {
    loading.value = false
  }
}

const enterScene = (scene) => {
  router.push({
    path: '/scene-board',
    query: {
      sceneCategoryId: scene.id,
      sceneName: scene.name
    }
  })
}

const getDefaultGradient = (id) => {
  const gradients = [
    'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)',
    'linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%)',
    'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    'linear-gradient(135deg, #fddb92 0%, #d1fdff 100%)',
    'linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%)',
    'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    'linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%)',
    'linear-gradient(135deg, #e0c3fc 0%, #8ec5fc 100%)'
  ]
  return gradients[((id - 9) % gradients.length + gradients.length) % gradients.length]
}

const getPatternClass = (id) => {
  const patterns = ['pattern-dots', 'pattern-lines', 'pattern-circles', 'pattern-waves']
  return patterns[((id - 9) % patterns.length + patterns.length) % patterns.length]
}

const getRankColor = (index) => {
  const colors = [
    'linear-gradient(135deg, #FFD700, #FFA500)',
    'linear-gradient(135deg, #C0C0C0, #A9A9A9)',
    'linear-gradient(135deg, #CD7F32, #B87333)'
  ]
  return colors[index] || '#999'
}

onMounted(() => {
  loadSceneCategories()
})
</script>

<style scoped>
.scene-gallery {
  min-height: 100vh;
}

.gallery-hero {
  background: linear-gradient(135deg, #fef6e4 0%, #f3d2c1 50%, #f582ae 100%);
  padding: 60px 0 80px;
  position: relative;
  overflow: hidden;
}

.hero-inner {
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  z-index: 2;
}

.hero-text {
  max-width: 560px;
}

.hero-title {
  font-size: 42px;
  font-weight: 700;
  color: #001858;
  margin-bottom: 16px;
  letter-spacing: 1px;
}

.hero-subtitle {
  font-size: 16px;
  color: #172c66;
  line-height: 1.8;
  opacity: 0.85;
}

.hero-decor {
  position: relative;
  width: 280px;
  height: 200px;
}

.decor-emoji {
  position: absolute;
  font-size: 48px;
  opacity: 0.7;
  animation: float 4s ease-in-out infinite;
}

.decor-1 { top: 0; left: 20px; animation-delay: 0s; }
.decor-2 { top: 40px; left: 100px; animation-delay: 0.5s; font-size: 52px; }
.decor-3 { top: 10px; left: 200px; animation-delay: 1s; }
.decor-4 { bottom: 20px; left: 0; animation-delay: 1.5s; font-size: 44px; }
.decor-5 { bottom: 0; left: 120px; animation-delay: 2s; font-size: 50px; }
.decor-6 { bottom: 40px; right: 0; animation-delay: 2.5s; }

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-15px) rotate(5deg); }
}

.gallery-main {
  padding: 40px 20px 80px;
  margin-top: -40px;
  position: relative;
  z-index: 5;
}

.section-header {
  margin-bottom: 32px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 26px;
  font-weight: 700;
  color: #333;
  margin-bottom: 8px;
}

.title-icon {
  font-size: 28px;
}

.section-desc {
  font-size: 14px;
  color: #888;
  margin-left: 38px;
}

.scene-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
  margin-bottom: 60px;
}

.scene-card {
  position: relative;
  border-radius: 20px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  min-height: 240px;
}

.scene-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 16px 40px rgba(0, 0, 0, 0.2);
}

.scene-card-inner {
  position: relative;
  z-index: 2;
  padding: 28px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.scene-card-bg-pattern {
  position: absolute;
  top: 0;
  right: 0;
  width: 160px;
  height: 160px;
  opacity: 0.15;
  z-index: 1;
  pointer-events: none;
}

.pattern-dots {
  background-image: radial-gradient(#fff 2px, transparent 2px);
  background-size: 16px 16px;
}
.pattern-lines {
  background-image: repeating-linear-gradient(45deg, #fff, #fff 2px, transparent 2px, transparent 14px);
}
.pattern-circles {
  background-image: radial-gradient(circle at 30% 30%, #fff 3px, transparent 4px),
                    radial-gradient(circle at 70% 70%, #fff 2px, transparent 3px);
  background-size: 40px 40px;
}
.pattern-waves {
  background: radial-gradient(ellipse at 50% 0%, transparent 60%, #fff 60%, #fff 62%, transparent 62%);
  background-size: 60px 30px;
}

.scene-icon-wrapper {
  margin-bottom: 16px;
}

.scene-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 64px;
  height: 64px;
  background: rgba(255, 255, 255, 0.35);
  backdrop-filter: blur(10px);
  border-radius: 18px;
  font-size: 36px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.scene-info {
  flex: 1;
  margin-bottom: 20px;
}

.scene-name {
  font-size: 22px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 8px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.15);
}

.scene-desc {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.9);
  line-height: 1.6;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.scene-stats {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  background: rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(8px);
  border-radius: 12px;
}

.stat-icon {
  font-size: 14px;
}

.stat-num {
  font-size: 16px;
  font-weight: 700;
  color: #fff;
}

.stat-label {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.9);
}

.scene-cta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 14px;
  transition: all 0.3s;
}

.scene-card:hover .scene-cta {
  background: #fff;
}

.cta-text {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.cta-arrow {
  font-size: 18px;
  color: #ff6b9d;
  transition: transform 0.3s;
}

.scene-card:hover .cta-arrow {
  transform: translateX(4px);
}

.featured-section {
  background: #fff;
  padding: 32px;
  border-radius: 20px;
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.06);
}

.featured-section .section-title {
  margin-bottom: 24px;
}

.featured-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 16px;
}

.featured-card {
  position: relative;
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 18px 20px;
  background: linear-gradient(135deg, #fafbfc 0%, #f5f7fa 100%);
  border-radius: 16px;
  border: 1px solid #eef0f3;
  cursor: pointer;
  transition: all 0.3s;
}

.featured-card:hover {
  transform: translateX(4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
  border-color: #ffd1e0;
  background: linear-gradient(135deg, #fff5f8 0%, #ffe8f0 100%);
}

.featured-rank {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 10px;
  color: #fff;
  font-weight: 700;
  font-size: 14px;
  flex-shrink: 0;
}

.featured-content {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  min-width: 0;
}

.featured-icon {
  font-size: 32px;
  flex-shrink: 0;
}

.featured-text {
  min-width: 0;
}

.featured-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.featured-meta {
  font-size: 12px;
  color: #888;
}

.featured-arrow {
  font-size: 18px;
  color: #bbb;
  transition: all 0.3s;
  flex-shrink: 0;
}

.featured-card:hover .featured-arrow {
  color: #ff6b9d;
  transform: translateX(4px);
}

.empty {
  padding: 60px 0;
}

.error-state {
  .error-icon-wrapper {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 120px;
    height: 120px;
    margin: 0 auto 20px;
    border-radius: 50%;
    background: linear-gradient(135deg, #fff5f5 0%, #ffe5e5 100%);
  }

  .error-text {
    color: #f56c6c;
    font-size: 14px;
  }

  :deep(.el-empty__description) {
    margin-bottom: 24px;
  }
}

.mr-2 {
  margin-right: 6px;
}
</style>
