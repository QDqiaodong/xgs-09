import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/work/:id',
    name: 'WorkDetail',
    component: () => import('@/views/WorkDetail.vue'),
    meta: { title: '作品详情' }
  },
  {
    path: '/publish',
    name: 'Publish',
    component: () => import('@/views/Publish.vue'),
    meta: { title: '发布作品' }
  },
  {
    path: '/user/:id',
    name: 'UserCenter',
    component: () => import('@/views/UserCenter.vue'),
    meta: { title: '个人中心' }
  },
  {
    path: '/user/:id/style',
    name: 'UserStyleProfile',
    component: () => import('@/views/UserStyleProfile.vue'),
    meta: { title: '排版风格' }
  },
  {
    path: '/category',
    name: 'Category',
    component: () => import('@/views/Category.vue'),
    meta: { title: '分类浏览' }
  },
  {
    path: '/favorites',
    name: 'Favorites',
    component: () => import('@/views/Favorites.vue'),
    meta: { title: '我的收藏' }
  },
  {
    path: '/scenes',
    name: 'SceneGallery',
    component: () => import('@/views/SceneGallery.vue'),
    meta: { title: '场景分类' }
  },
  {
    path: '/scene-board',
    name: 'SceneBoard',
    component: () => import('@/views/SceneBoard.vue'),
    meta: { title: '场景任务板' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 手账灵感库` : '手账创作与排版灵感库'
  next()
})

export default router
