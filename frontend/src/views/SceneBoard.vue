<template>
  <div class="scene-board">
    <Header />

    <section class="board-banner" :style="{ background: currentScene?.bannerColor || defaultBanner }">
      <div class="container banner-inner">
        <router-link to="/scenes" class="back-link">
          <el-icon><ArrowLeft /></el-icon>
          返回场景分类
        </router-link>

        <div class="banner-content">
          <div class="banner-icon">
            <span class="icon-emoji">{{ currentScene?.icon || '📝' }}</span>
          </div>
          <div class="banner-text">
            <h1 class="scene-title">{{ currentSceneName }}</h1>
            <p class="scene-desc">{{ currentScene?.description || '记录属于你的精彩瞬间' }}</p>
            <div class="scene-meta">
              <span class="meta-item">
                <span class="meta-icon">📋</span>
                {{ sceneTasks.length }} 个记录项目
              </span>
              <span class="meta-item">
                <span class="meta-icon">✨</span>
                {{ currentScene?.workCount || 0 }} 篇灵感作品
              </span>
            </div>
          </div>
        </div>

        <div class="banner-actions">
          <button class="btn-action btn-primary-action" @click="goCategoryFilter">
            <el-icon><Collection /></el-icon>
            查看灵感作品
          </button>
          <button class="btn-action btn-secondary-action" @click="goPublish">
            <el-icon><EditPen /></el-icon>
            创作手账
          </button>
        </div>
      </div>
      <div class="banner-pattern"></div>
    </section>

    <main class="container board-main">
      <div class="quick-switcher">
        <span class="switcher-label">快速切换场景：</span>
        <div class="switcher-chips">
          <router-link
            v-for="scene in allScenes"
            :key="scene.id"
            :to="{ path: '/scene-board', query: { sceneCategoryId: scene.id, sceneName: scene.name } }"
            class="switcher-chip"
            :class="{ active: currentSceneId === scene.id }"
            :style="currentSceneId === scene.id ? { background: scene.bannerColor, color: '#fff', borderColor: 'transparent' } : {}"
          >
            <span class="chip-icon">{{ scene.icon }}</span>
            {{ scene.name }}
          </router-link>
        </div>
      </div>

      <div class="section-header">
        <h2 class="section-title">
          <span class="title-icon">💡</span>
          记录项目模板
        </h2>
        <p class="section-desc">选择一个记录项目，开启手账创作；可将作品归入对应创作目的</p>
      </div>

      <div class="tasks-grid" v-loading="loading">
        <div
          v-for="task in sceneTasks"
          :key="task.id"
          class="task-card card"
        >
          <div class="task-header">
            <div class="task-icon-wrapper" :style="{ background: getTaskIconBg(task.id) }">
              <span class="task-icon">{{ task.icon || '📌' }}</span>
            </div>
            <div class="task-header-info">
              <h3 class="task-title">{{ task.title }}</h3>
              <p class="task-subtitle">{{ task.description }}</p>
            </div>
          </div>

          <div class="task-section" v-if="task.promptTips">
            <div class="section-label">
              <span class="label-icon">💬</span>
              灵感提示
            </div>
            <div class="prompt-content">
              {{ task.promptTips }}
            </div>
          </div>

          <div class="task-section" v-if="task.layoutSuggestion">
            <div class="section-label">
              <span class="label-icon">🎨</span>
              排版建议
            </div>
            <div class="layout-content">
              {{ task.layoutSuggestion }}
            </div>
          </div>

          <div class="task-actions">
            <button class="task-btn btn-create" @click="createFromTask(task)">
              <el-icon><Edit /></el-icon>
              按此模板创作
            </button>
            <button class="task-btn btn-browse" @click="browseWorks(task)">
              <el-icon><View /></el-icon>
              查看相关作品
            </button>
          </div>
        </div>
      </div>

      <div class="empty error-state" v-if="!loading && tasksError">
        <el-empty :description="tasksErrorMessage" image-size="120">
          <template #image>
            <div class="error-icon-wrapper">
              <el-icon :size="80" color="#f56c6c"><WarningFilled /></el-icon>
            </div>
          </template>
          <template #description>
            <span class="error-text">{{ tasksErrorMessage }}</span>
          </template>
          <el-button type="primary" @click="loadSceneTasks">
            <el-icon class="mr-2"><RefreshRight /></el-icon>
            重新加载
          </el-button>
        </el-empty>
      </div>

      <div class="empty" v-else-if="!loading && sceneTasks.length === 0">
        <el-empty :description="`${currentSceneName}暂无记录项目模板`" />
      </div>

      <div class="related-section" v-if="relatedWorks.length > 0 || !worksLoading">
        <div class="section-header">
          <h2 class="section-title">
            <span class="title-icon">🌟</span>
            {{ creativePurposeText }}
          </h2>
          <router-link
            :to="{ path: '/category', query: { sceneCategoryId: currentSceneId } }"
            class="view-all-link"
          >
            查看全部
            <el-icon><ArrowRight /></el-icon>
          </router-link>
        </div>

        <div class="related-grid" v-loading="worksLoading">
          <WorkCard
            v-for="work in relatedWorks"
            :key="work.id"
            :work="work"
          />
        </div>

        <div class="empty error-state" v-if="!worksLoading && worksError">
          <el-empty :description="worksErrorMessage" image-size="100">
            <template #image>
              <div class="error-icon-wrapper small">
                <el-icon :size="60" color="#f56c6c"><WarningFilled /></el-icon>
              </div>
            </template>
            <template #description>
              <span class="error-text">{{ worksErrorMessage }}</span>
            </template>
            <el-button type="primary" size="small" @click="loadRelatedWorks">
              <el-icon class="mr-2"><RefreshRight /></el-icon>
              重新加载
            </el-button>
          </el-empty>
        </div>

        <div class="empty" v-else-if="!worksLoading && relatedWorks.length === 0">
          <el-empty :description="`${currentSceneName}暂无灵感作品，快来创作第一篇吧~`" />
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, ArrowRight, Edit, View, Collection, EditPen, WarningFilled, RefreshRight } from '@element-plus/icons-vue'
import Header from '@/components/Header.vue'
import WorkCard from '@/components/WorkCard.vue'
import { getSceneTaskList } from '@/api/scene'
import { getCategoryList } from '@/api/category'
import { getWorkList } from '@/api/work'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const worksLoading = ref(false)
const sceneTasks = ref([])
const allScenes = ref([])
const relatedWorks = ref([])
const tasksError = ref(false)
const tasksErrorMessage = ref('')
const worksError = ref(false)
const worksErrorMessage = ref('')

const currentSceneId = computed(() => Number(route.query.sceneCategoryId) || null)
const currentSceneName = computed(() => {
  const scene = allScenes.value.find(s => s.id === currentSceneId.value)
  return scene?.name || route.query.sceneName || '场景记录'
})

const currentScene = computed(() => {
  return allScenes.value.find(s => s.id === currentSceneId.value)
})

const creativePurposeText = computed(() => {
  const sceneName = currentSceneName.value
  return `${sceneName}灵感作品`
})

const defaultBanner = 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'

const getTaskIconBg = (id) => {
  const colors = [
    'linear-gradient(135deg, #ffecd2, #fcb69f)',
    'linear-gradient(135deg, #a8edea, #fed6e3)',
    'linear-gradient(135deg, #ff9a9e, #fecfef)',
    'linear-gradient(135deg, #fddb92, #d1fdff)',
    'linear-gradient(135deg, #84fab0, #8fd3f4)',
    'linear-gradient(135deg, #e0c3fc, #8ec5fc)',
    'linear-gradient(135deg, #4facfe, #00f2fe)',
    'linear-gradient(135deg, #fbc2eb, #a6c1ee)'
  ]
  return colors[(id - 1) % colors.length]
}

const loadAllScenes = async () => {
  try {
    const res = await getCategoryList('scene')
    allScenes.value = res.data || []
  } catch (e) {
    console.error('加载场景列表失败', e)
  }
}

const loadSceneTasks = async () => {
  if (!currentSceneId.value) {
    sceneTasks.value = []
    return
  }
  loading.value = true
  tasksError.value = false
  tasksErrorMessage.value = ''
  try {
    const res = await getSceneTaskList(currentSceneId.value)
    sceneTasks.value = res.data || []
  } catch (e) {
    tasksError.value = true
    tasksErrorMessage.value = e.message || '加载记录项目失败，请检查网络后重试'
    sceneTasks.value = []
  } finally {
    loading.value = false
  }
}

const loadRelatedWorks = async () => {
  if (!currentSceneId.value) {
    relatedWorks.value = []
    return
  }
  worksLoading.value = true
  worksError.value = false
  worksErrorMessage.value = ''
  try {
    const res = await getWorkList({
      pageNum: 1,
      pageSize: 6,
      sceneCategoryId: currentSceneId.value,
      status: 1
    })
    relatedWorks.value = res.data?.records || []
  } catch (e) {
    worksError.value = true
    worksErrorMessage.value = e.message || '加载灵感作品失败，请检查网络后重试'
    relatedWorks.value = []
  } finally {
    worksLoading.value = false
  }
}

const createFromTask = (task) => {
  router.push({
    path: '/publish',
    query: {
      sceneCategoryId: currentSceneId.value,
      taskTitle: task.title,
      taskPrompt: task.promptTips,
      taskLayout: task.layoutSuggestion,
      prefill: '1'
    }
  })
}

const browseWorks = (task) => {
  router.push({
    path: '/category',
    query: {
      sceneCategoryId: currentSceneId.value,
      keyword: task.title
    }
  })
}

const goCategoryFilter = () => {
  router.push({
    path: '/category',
    query: { sceneCategoryId: currentSceneId.value }
  })
}

const goPublish = () => {
  router.push({
    path: '/publish',
    query: {
      sceneCategoryId: currentSceneId.value,
      prefill: '1'
    }
  })
}

watch(() => route.query.sceneCategoryId, () => {
  loadSceneTasks()
  loadRelatedWorks()
})

onMounted(() => {
  loadAllScenes()
  loadSceneTasks()
  loadRelatedWorks()
})
</script>

<style scoped>
.scene-board {
  min-height: 100vh;
}

.board-banner {
  position: relative;
  padding: 30px 0 60px;
  overflow: hidden;
  color: #fff;
}

.banner-inner {
  position: relative;
  z-index: 2;
}

.back-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(8px);
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  color: #fff;
  margin-bottom: 24px;
  transition: all 0.3s;
}

.back-link:hover {
  background: rgba(255, 255, 255, 0.4);
  transform: translateX(-2px);
}

.banner-content {
  display: flex;
  align-items: center;
  gap: 28px;
  margin-bottom: 28px;
}

.banner-icon {
  flex-shrink: 0;
}

.icon-emoji {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 96px;
  height: 96px;
  background: rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(12px);
  border-radius: 28px;
  font-size: 52px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.banner-text {
  flex: 1;
  min-width: 0;
}

.scene-title {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 10px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.scene-desc {
  font-size: 15px;
  line-height: 1.7;
  opacity: 0.95;
  margin-bottom: 16px;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.scene-meta {
  display: flex;
  gap: 24px;
}

.meta-item {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(6px);
  border-radius: 14px;
  font-size: 13px;
  font-weight: 500;
}

.meta-icon {
  font-size: 14px;
}

.banner-actions {
  display: flex;
  gap: 16px;
}

.btn-action {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: 24px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  border: none;
  transition: all 0.3s;
}

.btn-primary-action {
  background: #fff;
  color: #333;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.btn-primary-action:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.18);
}

.btn-secondary-action {
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(8px);
  color: #fff;
  border: 2px solid rgba(255, 255, 255, 0.5);
}

.btn-secondary-action:hover {
  background: rgba(255, 255, 255, 0.4);
  transform: translateY(-2px);
}

.banner-pattern {
  position: absolute;
  top: 0;
  right: 0;
  width: 50%;
  height: 100%;
  background-image: radial-gradient(circle at 80% 30%, rgba(255, 255, 255, 0.2) 0%, transparent 40%),
                    radial-gradient(circle at 60% 80%, rgba(255, 255, 255, 0.15) 0%, transparent 35%);
  pointer-events: none;
  z-index: 1;
}

.board-main {
  padding: 36px 20px 80px;
  margin-top: -30px;
  position: relative;
  z-index: 5;
}

.quick-switcher {
  background: #fff;
  padding: 20px 24px;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  margin-bottom: 32px;
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.switcher-label {
  font-size: 14px;
  font-weight: 600;
  color: #555;
  flex-shrink: 0;
}

.switcher-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  flex: 1;
}

.switcher-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 7px 16px;
  border: 1px solid #e8e8e8;
  border-radius: 20px;
  font-size: 13px;
  background: #fafafa;
  color: #555;
  transition: all 0.3s;
  font-weight: 500;
}

.switcher-chip:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.switcher-chip.active {
  color: #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.chip-icon {
  font-size: 15px;
}

.section-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 24px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 24px;
  font-weight: 700;
  color: #333;
}

.title-icon {
  font-size: 26px;
}

.section-desc {
  font-size: 13px;
  color: #888;
  margin-top: 6px;
}

.view-all-link {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  font-weight: 500;
  color: #ff6b9d;
  transition: all 0.3s;
}

.view-all-link:hover {
  gap: 8px;
}

.tasks-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 20px;
  margin-bottom: 56px;
}

.task-card {
  padding: 24px;
  display: flex;
  flex-direction: column;
}

.task-header {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}

.task-icon-wrapper {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  border-radius: 16px;
  flex-shrink: 0;
}

.task-icon {
  font-size: 28px;
}

.task-header-info {
  flex: 1;
  min-width: 0;
}

.task-title {
  font-size: 18px;
  font-weight: 700;
  color: #333;
  margin-bottom: 6px;
  line-height: 1.4;
}

.task-subtitle {
  font-size: 13px;
  color: #777;
  line-height: 1.6;
}

.task-section {
  margin-bottom: 18px;
}

.section-label {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  font-weight: 600;
  color: #ff6b9d;
  padding: 4px 10px;
  background: #fff5f8;
  border-radius: 10px;
  margin-bottom: 10px;
}

.label-icon {
  font-size: 13px;
}

.prompt-content {
  font-size: 13px;
  line-height: 1.8;
  color: #555;
  padding: 14px 16px;
  background: #faf8f5;
  border-left: 3px solid #ffd166;
  border-radius: 0 10px 10px 0;
}

.layout-content {
  font-size: 13px;
  line-height: 1.8;
  color: #555;
  padding: 14px 16px;
  background: #f0f9ff;
  border-left: 3px solid #4facfe;
  border-radius: 0 10px 10px 0;
}

.task-actions {
  display: flex;
  gap: 12px;
  margin-top: auto;
  padding-top: 8px;
}

.task-btn {
  flex: 1;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 10px 14px;
  border-radius: 14px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  border: none;
  transition: all 0.3s;
}

.btn-create {
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
  color: #fff;
}

.btn-create:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 14px rgba(255, 154, 158, 0.45);
}

.btn-browse {
  background: #f5f7fa;
  color: #555;
  border: 1px solid #e8e8e8;
}

.btn-browse:hover {
  background: #eef2f7;
  color: #333;
  transform: translateY(-2px);
}

.related-section {
  margin-top: 8px;
}

.related-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.empty {
  padding: 40px 0;
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

  .error-icon-wrapper.small {
    width: 100px;
    height: 100px;
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
