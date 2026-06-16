<template>
  <div class="user-center">
    <Header />
    
    <main class="container user-content">
      <div class="user-header">
        <div class="user-info">
          <img class="avatar" src="https://api.dicebear.com/7.x/avataaars/svg?seed=journal" alt="" />
          <div class="info-text">
            <h2 class="username">手账爱好者</h2>
            <p class="bio">热爱生活，记录每一个美好瞬间 ✨</p>
          </div>
        </div>
        <div class="user-stats">
          <div class="stat">
            <span class="num">{{ workStats.totalWorks || 0 }}</span>
            <span class="label">作品</span>
          </div>
          <div class="stat">
            <span class="num">{{ workStats.publicWorks || 0 }}</span>
            <span class="label">可展示</span>
          </div>
          <div class="stat">
            <span class="num">{{ favoriteCount }}</span>
            <span class="label">收藏</span>
          </div>
        </div>
      </div>

      <div class="stats-section">
        <div class="stats-card">
          <h3 class="stats-title">
            <span class="title-icon">🎨</span>
            风格分布
          </h3>
          <div class="stats-list" v-if="workStats.styleStats && workStats.styleStats.length > 0">
            <div 
              v-for="item in workStats.styleStats.slice(0, 5)" 
              :key="item.id" 
              class="stats-item"
            >
              <span class="stats-name">{{ item.name }}</span>
              <div class="stats-bar-wrapper">
                <div 
                  class="stats-bar style-bar" 
                  :style="{ width: getBarWidth(item.count, maxStyleCount) }"
                ></div>
              </div>
              <span class="stats-count">{{ item.count }}</span>
            </div>
          </div>
          <div class="empty-stats" v-else>
            <span>暂无风格数据</span>
          </div>
        </div>

        <div class="stats-card">
          <h3 class="stats-title">
            <span class="title-icon">📝</span>
            场景分布
          </h3>
          <div class="stats-list" v-if="workStats.sceneStats && workStats.sceneStats.length > 0">
            <div 
              v-for="item in workStats.sceneStats.slice(0, 5)" 
              :key="item.id" 
              class="stats-item"
            >
              <span class="stats-name">{{ item.name }}</span>
              <div class="stats-bar-wrapper">
                <div 
                  class="stats-bar scene-bar" 
                  :style="{ width: getBarWidth(item.count, maxSceneCount) }"
                ></div>
              </div>
              <span class="stats-count">{{ item.count }}</span>
            </div>
          </div>
          <div class="empty-stats" v-else>
            <span>暂无场景数据</span>
          </div>
        </div>

        <div class="stats-card">
          <h3 class="stats-title">
            <span class="title-icon">📊</span>
            作品状态
          </h3>
          <div class="stats-list" v-if="workStats.statusStats && workStats.statusStats.length > 0">
            <div 
              v-for="item in workStats.statusStats" 
              :key="item.status" 
              class="stats-item"
            >
              <span class="stats-name">{{ item.statusDesc }}</span>
              <div class="stats-bar-wrapper">
                <div 
                  class="stats-bar status-bar" 
                  :class="'status-' + item.status"
                  :style="{ width: getBarWidth(item.count, workStats.totalWorks) }"
                ></div>
              </div>
              <span class="stats-count">{{ item.count }}</span>
            </div>
          </div>
          <div class="empty-stats" v-else>
            <span>暂无状态数据</span>
          </div>
        </div>
      </div>

      <div class="tabs">
        <div 
          class="tab"
          :class="{ active: activeTab === 'works' }"
          @click="activeTab = 'works'"
        >
          我的作品
        </div>
        <div 
          class="tab"
          :class="{ active: activeTab === 'favorites' }"
          @click="activeTab = 'favorites'"
        >
          我的收藏
        </div>
      </div>

      <div class="tab-content">
        <div v-if="activeTab === 'works'" v-loading="loading">
          <div class="works-grid">
            <div v-for="work in works" :key="work.id" class="work-item">
              <WorkCard :work="work" />
              <div class="work-status-badge" :class="'badge-' + work.status">
                {{ getWorkStatusDesc(work.status) }}
              </div>
              <div class="work-actions">
                <el-button size="small" @click="editWork(work)">编辑</el-button>
                <el-dropdown @command="(status) => changeWorkStatus(work, status)" trigger="click">
                  <el-button size="small" type="primary">
                    状态切换
                    <el-icon class="el-icon--right"><ArrowDown /></el-icon>
                  </el-button>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item :command="WORK_STATUS.PUBLIC.code">
                        设为可展示
                      </el-dropdown-item>
                      <el-dropdown-item :command="WORK_STATUS.PRIVATE.code">
                        设为仅本人可见
                      </el-dropdown-item>
                      <el-dropdown-item :command="WORK_STATUS.ARCHIVED.code">
                        归档作品
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </div>
          </div>
          <div class="empty" v-if="!loading && works.length === 0">
            <el-empty description="还没有发布作品，快去发布吧~" />
          </div>
        </div>

        <div v-if="activeTab === 'favorites'" v-loading="favLoading">
          <div class="works-grid">
            <WorkCard 
              v-for="work in favorites" 
              :key="work.id" 
              :work="work"
            />
          </div>
          <div class="empty" v-if="!favLoading && favorites.length === 0">
            <el-empty description="还没有收藏作品~" />
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'
import Header from '@/components/Header.vue'
import WorkCard from '@/components/WorkCard.vue'
import { getUserWorks, updateWorkStatus, getUserWorkStats } from '@/api/work'
import { getUserFavorites } from '@/api/favorite'
import { WORK_STATUS, getWorkStatusDesc } from '@/constants/workStatus'

const route = useRoute()
const router = useRouter()
const activeTab = ref('works')
const works = ref([])
const favorites = ref([])
const loading = ref(false)
const favLoading = ref(false)
const statsLoading = ref(false)
const workStats = ref({})
const userId = computed(() => route.params.id || 1)

const resetData = () => {
  works.value = []
  favorites.value = []
  workStats.value = {}
}

const loadAllData = () => {
  resetData()
  loadWorks()
  loadWorkStats()
  loadFavorites()
}

watch(userId, () => {
  loadAllData()
}, { immediate: false })

const favoriteCount = computed(() => favorites.value.length)

const maxStyleCount = computed(() => {
  if (!workStats.value.styleStats || workStats.value.styleStats.length === 0) return 0
  return Math.max(...workStats.value.styleStats.map(s => s.count))
})

const maxSceneCount = computed(() => {
  if (!workStats.value.sceneStats || workStats.value.sceneStats.length === 0) return 0
  return Math.max(...workStats.value.sceneStats.map(s => s.count))
})

const getBarWidth = (count, max) => {
  if (!max || max === 0) return '0%'
  const percentage = (count / max) * 100
  return Math.max(percentage, 5) + '%'
}

const loadWorks = async () => {
  loading.value = true
  try {
    const res = await getUserWorks(userId.value, 1, 100)
    works.value = res.data?.records || []
  } finally {
    loading.value = false
  }
}

const loadWorkStats = async () => {
  statsLoading.value = true
  try {
    const res = await getUserWorkStats(userId.value)
    workStats.value = res.data || {}
  } finally {
    statsLoading.value = false
  }
}

const loadFavorites = async () => {
  favLoading.value = true
  try {
    const res = await getUserFavorites(userId.value, 1, 100)
    favorites.value = res.data?.records || []
  } finally {
    favLoading.value = false
  }
}

const changeWorkStatus = async (work, status) => {
  const statusDesc = getWorkStatusDesc(status)
  try {
    await ElMessageBox.confirm(`确定要将作品状态改为「${statusDesc}」吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await updateWorkStatus(work.id, status)
    work.status = status
    ElMessage.success(`状态已改为「${statusDesc}」`)
    loadWorkStats()
  } catch (e) {
    if (e !== 'cancel') {
      console.error(e)
    }
  }
}

const editWork = (work) => {
  ElMessage.info('编辑功能开发中...')
}

onMounted(() => {
  loadAllData()
})
</script>

<style scoped>
.user-content {
  padding: 30px 20px 60px;
}

.user-header {
  background: #fff;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  border: 4px solid #f0f0f0;
}

.username {
  font-size: 22px;
  font-weight: 600;
  color: #333;
  margin-bottom: 6px;
}

.bio {
  font-size: 14px;
  color: #999;
}

.user-stats {
  display: flex;
  gap: 40px;
}

.stat {
  text-align: center;
}

.stat .num {
  display: block;
  font-size: 28px;
  font-weight: 700;
  color: #ff6b9d;
}

.stat .label {
  font-size: 14px;
  color: #999;
}

.stats-section {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stats-card {
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.stats-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.title-icon {
  font-size: 20px;
}

.stats-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.stats-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.stats-name {
  width: 80px;
  font-size: 13px;
  color: #666;
  flex-shrink: 0;
}

.stats-bar-wrapper {
  flex: 1;
  height: 8px;
  background: #f5f5f5;
  border-radius: 4px;
  overflow: hidden;
}

.stats-bar {
  height: 100%;
  border-radius: 4px;
  transition: width 0.3s ease;
}

.style-bar {
  background: linear-gradient(90deg, #ff9a9e, #fecfef);
}

.scene-bar {
  background: linear-gradient(90deg, #a8edea, #fed6e3);
}

.status-bar {
  background: linear-gradient(90deg, #667eea, #764ba2);
}

.status-bar.status-1 {
  background: linear-gradient(90deg, #667eea, #764ba2);
}

.status-bar.status-2 {
  background: linear-gradient(90deg, #f093fb, #f5576c);
}

.status-bar.status-3 {
  background: linear-gradient(90deg, #bdc3c7, #2c3e50);
}

.stats-count {
  width: 36px;
  text-align: right;
  font-size: 14px;
  font-weight: 600;
  color: #333;
  flex-shrink: 0;
}

.empty-stats {
  text-align: center;
  padding: 30px 0;
  color: #999;
  font-size: 14px;
}

.tabs {
  display: flex;
  gap: 32px;
  margin-bottom: 24px;
  border-bottom: 2px solid #f0f0f0;
}

.tab {
  font-size: 16px;
  font-weight: 500;
  color: #999;
  padding-bottom: 12px;
  cursor: pointer;
  border-bottom: 2px solid transparent;
  margin-bottom: -2px;
  transition: all 0.3s;
}

.tab.active {
  color: #ff6b9d;
  border-bottom-color: #ff6b9d;
}

.works-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.work-item {
  position: relative;
}

.work-status-badge {
  position: absolute;
  top: 10px;
  left: 10px;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  z-index: 10;
  color: #fff;
  background: rgba(0, 0, 0, 0.6);
}

.badge-1 {
  background: linear-gradient(135deg, #667eea, #764ba2);
}

.badge-2 {
  background: linear-gradient(135deg, #f093fb, #f5576c);
}

.badge-3 {
  background: linear-gradient(135deg, #bdc3c7, #2c3e50);
}

.work-actions {
  position: absolute;
  top: 10px;
  right: 10px;
  display: flex;
  gap: 8px;
  z-index: 10;
}

.empty {
  padding: 60px 0;
}
</style>
