<template>
  <div class="favorites">
    <Header />
    
    <main class="container favorites-content">
      <h1 class="page-title">我的收藏</h1>
      
      <div class="works-grid" v-loading="loading">
        <WorkCard 
          v-for="work in favorites" 
          :key="work.id" 
          :work="work"
        />
      </div>

      <div class="empty" v-if="!loading && favorites.length === 0">
        <el-empty description="还没有收藏作品，快去发现喜欢的作品吧~" />
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Header from '@/components/Header.vue'
import WorkCard from '@/components/WorkCard.vue'
import { getUserFavorites } from '@/api/favorite'

const favorites = ref([])
const loading = ref(false)

const loadFavorites = async () => {
  loading.value = true
  try {
    const res = await getUserFavorites(1, 1, 100)
    favorites.value = res.data?.records || []
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadFavorites()
})
</script>

<style scoped>
.favorites-content {
  padding: 30px 20px 60px;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin-bottom: 24px;
}

.works-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.empty {
  padding: 60px 0;
}
</style>
