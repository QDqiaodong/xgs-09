<template>
  <header class="header">
    <div class="container header-content">
      <router-link to="/" class="logo">
        <span class="logo-icon">📔</span>
        <span class="logo-text">手账灵感库</span>
      </router-link>
      
      <nav class="nav">
        <router-link to="/" class="nav-link">首页</router-link>
        <router-link to="/scenes" class="nav-link">场景灵感</router-link>
        <router-link to="/category" class="nav-link">分类浏览</router-link>
        <router-link to="/favorites" class="nav-link">我的收藏</router-link>
      </nav>
      
      <div class="header-right">
        <router-link to="/publish" class="btn-publish">
          <el-icon><Plus /></el-icon>
          发布作品
        </router-link>
        <router-link to="/user/1" class="user-avatar">
          <img src="https://api.dicebear.com/7.x/avataaars/svg?seed=journal" alt="avatar" />
        </router-link>
      </div>
    </div>
  </header>
</template>

<script setup>
import { Plus } from '@element-plus/icons-vue'
</script>

<style scoped>
.header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.logo-icon {
  font-size: 28px;
}

.nav {
  display: flex;
  gap: 32px;
}

.nav-link {
  color: #666;
  font-size: 15px;
  transition: color 0.3s;
}

.nav-link:hover,
.nav-link.router-link-active {
  color: #ff6b9d;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.btn-publish {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 20px;
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
  color: #fff;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  transition: transform 0.3s;
}

.btn-publish:hover {
  transform: translateY(-2px);
}

.user-avatar img {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: 2px solid #f0f0f0;
}
</style>
