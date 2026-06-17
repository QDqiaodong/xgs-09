<template>
  <div class="home">
    <Header />
    
    <section class="hero">
      <div class="container">
        <div class="hero-content">
          <h1 class="hero-title">记录生活的美好瞬间</h1>
          <p class="hero-subtitle">发现手账排版灵感，分享你的创作故事</p>
          <div class="hero-search">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索手账作品、排版风格..."
              size="large"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </div>
        </div>
      </div>
    </section>

    <main class="container main-content">
      <div class="section-tabs">
        <div 
          class="tab" 
          :class="{ active: activeTab === 'latest' }"
          @click="activeTab = 'latest'"
        >
          最新发布
        </div>
        <div 
          class="tab" 
          :class="{ active: activeTab === 'hot' }"
          @click="activeTab = 'hot'"
        >
          热门推荐
        </div>
      </div>

      <div class="category-bar">
        <span 
          v-for="cat in styleCategories" 
          :key="cat.id"
          class="style-chip"
          :class="{ active: selectedCategory === cat.id }"
          :style="getChipStyle(cat.name, selectedCategory === cat.id)"
          @click="selectCategory(cat.id)"
        >
          <span class="chip-icon">{{ getStyleConfig(cat.name).icon }}</span>
          {{ cat.name }}
        </span>
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
        <el-empty description="暂无作品，快来发布第一个吧~" />
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import Header from '@/components/Header.vue'
import WorkCard from '@/components/WorkCard.vue'
import { getLatestWorks, getHotWorks, getWorkList } from '@/api/work'
import { getCategoryList } from '@/api/category'
import { getStyleConfig } from '@/constants/styleTagConfig'

const router = useRouter()
const activeTab = ref('latest')
const searchKeyword = ref('')
const selectedCategory = ref(null)
const works = ref([])
const loading = ref(false)
const loadingMore = ref(false)
const pageNum = ref(1)
const pageSize = ref(12)
const hasMore = ref(true)
const styleCategories = ref([])

const getChipStyle = (name, isActive) => {
  const config = getStyleConfig(name)
  if (isActive) {
    return {
      background: config.activeGradient,
      color: config.activeColor,
      borderColor: 'transparent',
    }
  }
  return {
    background: config.bg,
    color: config.color,
    borderColor: config.borderColor,
  }
}

const loadCategories = async () => {
  const res = await getCategoryList('style')
  styleCategories.value = res.data || []
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
    let res
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    
    if (selectedCategory.value) {
      params.categoryId = selectedCategory.value
      res = await getWorkList(params)
    } else if (activeTab.value === 'latest') {
      res = await getLatestWorks(pageNum.value, pageSize.value)
    } else {
      res = await getHotWorks(pageNum.value, pageSize.value)
    }
    
    const newWorks = res.data?.records || []
    if (reset) {
      works.value = newWorks
    } else {
      works.value = [...works.value, ...newWorks]
    }
    
    const total = res.data?.total || 0
    hasMore.value = works.value.length < total
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

const selectCategory = (id) => {
  selectedCategory.value = selectedCategory.value === id ? null : id
  loadWorks(true)
}

const handleSearch = () => {
  if (searchKeyword.value) {
    router.push({ path: '/category', query: { keyword: searchKeyword.value } })
  }
}

watch(activeTab, () => {
  selectedCategory.value = null
  loadWorks(true)
})

onMounted(() => {
  loadCategories()
  loadWorks(true)
})
</script>

<style scoped>
.hero {
  background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
  padding: 60px 0;
  margin-bottom: 30px;
}

.hero-content {
  text-align: center;
  max-width: 600px;
  margin: 0 auto;
}

.hero-title {
  font-size: 36px;
  font-weight: 700;
  color: #333;
  margin-bottom: 12px;
}

.hero-subtitle {
  font-size: 16px;
  color: #666;
  margin-bottom: 30px;
}

.hero-search {
  max-width: 500px;
  margin: 0 auto;
}

.main-content {
  padding-bottom: 60px;
}

.section-tabs {
  display: flex;
  gap: 32px;
  margin-bottom: 20px;
}

.tab {
  font-size: 18px;
  font-weight: 600;
  color: #999;
  cursor: pointer;
  padding-bottom: 8px;
  border-bottom: 2px solid transparent;
  transition: all 0.3s;
}

.tab.active {
  color: #ff6b9d;
  border-bottom-color: #ff6b9d;
}

.category-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 24px;
}

.category-bar .style-chip {
  font-size: 14px;
}

.category-bar .style-chip:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
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
