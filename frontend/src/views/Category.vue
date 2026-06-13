<template>
  <div class="category">
    <Header />
    
    <main class="container category-content">
      <h1 class="page-title">分类浏览</h1>
      
      <div class="category-filters">
        <div class="filter-group">
          <span class="filter-label">排版风格</span>
          <div class="filter-options">
            <span 
              class="option"
              :class="{ active: !selectedStyle }"
              @click="selectedStyle = null"
            >
              全部
            </span>
            <span 
              v-for="cat in styleCategories" 
              :key="cat.id"
              class="option"
              :class="{ active: selectedStyle === cat.id }"
              @click="selectedStyle = cat.id"
            >
              {{ cat.name }}
            </span>
          </div>
        </div>
        
        <div class="filter-group">
          <span class="filter-label">应用场景</span>
          <div class="filter-options">
            <span 
              class="option"
              :class="{ active: !selectedScene }"
              @click="selectedScene = null"
            >
              全部
            </span>
            <span 
              v-for="cat in sceneCategories" 
              :key="cat.id"
              class="option"
              :class="{ active: selectedScene === cat.id }"
              @click="selectedScene = cat.id"
            >
              {{ cat.name }}
            </span>
          </div>
        </div>
        
        <div class="filter-group">
          <span class="filter-label">搜索</span>
          <div class="filter-options">
            <el-input
              v-model="keyword"
              placeholder="搜索作品..."
              style="width: 240px"
              clearable
              @clear="loadWorks(true)"
              @keyup.enter="loadWorks(true)"
            >
              <template #append>
                <el-button @click="loadWorks(true)">
                  <el-icon><Search /></el-icon>
                </el-button>
              </template>
            </el-input>
          </div>
        </div>
      </div>

      <div class="works-grid" v-loading="loading">
        <WorkCard 
          v-for="work in works" 
          :key="work.id" 
          :work="work"
        />
      </div>

      <div class="load-more" v-if="hasMore && !loading">
        <el-button type="primary" @click="loadMore" :loading="loadingMore">
          加载更多
        </el-button>
      </div>

      <div class="empty" v-if="!loading && works.length === 0">
        <el-empty description="没有找到相关作品" />
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import Header from '@/components/Header.vue'
import WorkCard from '@/components/WorkCard.vue'
import { getWorkList } from '@/api/work'
import { getCategoryList } from '@/api/category'

const route = useRoute()
const styleCategories = ref([])
const sceneCategories = ref([])
const selectedStyle = ref(null)
const selectedScene = ref(null)
const keyword = ref('')
const works = ref([])
const loading = ref(false)
const loadingMore = ref(false)
const pageNum = ref(1)
const pageSize = ref(12)
const hasMore = ref(true)

const loadCategories = async () => {
  const [styleRes, sceneRes] = await Promise.all([
    getCategoryList('style'),
    getCategoryList('scene')
  ])
  styleCategories.value = styleRes.data || []
  sceneCategories.value = sceneRes.data || []
}

const loadWorks = async (reset = false) => {
  if (reset) {
    pageNum.value = 1
    hasMore.value = true
    works.value = []
  }
  
  if (reset) loading.value = true
  else loadingMore.value = true
  
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: keyword.value,
      status: 1
    }
    
    const res = await getWorkList(params)
    let newWorks = res.data?.records || []
    
    if (selectedStyle.value || selectedScene.value) {
      newWorks = newWorks.filter(work => {
        const cats = work.categories || []
        const styleMatch = !selectedStyle.value || cats.some(c => c.id === selectedStyle.value)
        const sceneMatch = !selectedScene.value || cats.some(c => c.id === selectedScene.value)
        return styleMatch && sceneMatch
      })
    }
    
    if (reset) {
      works.value = newWorks
    } else {
      works.value = [...works.value, ...newWorks]
    }
    
    hasMore.value = res.data?.records?.length === pageSize.value
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

const loadMore = () => {
  if (hasMore.value && !loadingMore.value) {
    pageNum.value++
    loadWorks()
  }
}

watch([selectedStyle, selectedScene], () => {
  loadWorks(true)
})

onMounted(() => {
  if (route.query.keyword) {
    keyword.value = route.query.keyword
  }
  loadCategories()
  loadWorks(true)
})
</script>

<style scoped>
.category-content {
  padding: 30px 20px 60px;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin-bottom: 24px;
}

.category-filters {
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  margin-bottom: 24px;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.filter-group:last-child {
  margin-bottom: 0;
}

.filter-label {
  min-width: 80px;
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.filter-options {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.option {
  padding: 6px 16px;
  border: 1px solid #e8e8e8;
  border-radius: 16px;
  font-size: 13px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
}

.option:hover {
  border-color: #ff9a9e;
  color: #ff6b9d;
}

.option.active {
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
  border-color: transparent;
  color: #fff;
}

.works-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.load-more {
  text-align: center;
  margin-top: 40px;
}

.empty {
  padding: 60px 0;
}
</style>
