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
            <span class="num">{{ workCount }}</span>
            <span class="label">作品</span>
          </div>
          <div class="stat">
            <span class="num">{{ favoriteCount }}</span>
            <span class="label">收藏</span>
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
              <div class="work-actions">
                <el-button size="small" @click="editWork(work)">编辑</el-button>
                <el-button 
                  size="small" 
                  type="danger"
                  @click="toggleStatus(work)"
                >
                  {{ work.status === 1 ? '下架' : '上架' }}
                </el-button>
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
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import Header from '@/components/Header.vue'
import WorkCard from '@/components/WorkCard.vue'
import { getUserWorks, updateWorkStatus } from '@/api/work'
import { getUserFavorites } from '@/api/favorite'

const route = useRoute()
const router = useRouter()
const activeTab = ref('works')
const works = ref([])
const favorites = ref([])
const loading = ref(false)
const favLoading = ref(false)
const userId = computed(() => route.params.id || 1)

const workCount = computed(() => works.value.length)
const favoriteCount = computed(() => favorites.value.length)

const loadWorks = async () => {
  loading.value = true
  try {
    const res = await getUserWorks(userId.value, 1, 100)
    works.value = res.data?.records || []
  } finally {
    loading.value = false
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

const toggleStatus = async (work) => {
  const action = work.status === 1 ? '下架' : '上架'
  try {
    await ElMessageBox.confirm(`确定要${action}该作品吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await updateWorkStatus(work.id, work.status === 1 ? 0 : 1)
    work.status = work.status === 1 ? 0 : 1
    ElMessage.success(`${action}成功`)
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
  loadWorks()
  loadFavorites()
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
