<template>
  <div class="publish">
    <Header />
    
    <main class="container publish-content">
      <h1 class="page-title">发布手账作品</h1>
      
      <el-form :model="form" ref="formRef" label-width="100px" class="publish-form">
        <el-form-item label="作品标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入作品标题" maxlength="50" show-word-limit />
        </el-form-item>
        
        <el-form-item label="封面图片" prop="coverImage">
          <div class="cover-upload-section">
            <div 
              class="image-upload"
              :style="{ aspectRatio: selectedCoverAspectRatio }"
            >
              <div v-if="form.coverImage" class="image-preview">
                <img :src="form.coverImage" alt="" />
                <button class="remove-btn" @click="form.coverImage = ''">
                  <el-icon><Close /></el-icon>
                </button>
              </div>
              <div v-else class="upload-placeholder" @click="uploadImage">
                <el-icon><Plus /></el-icon>
                <span>上传封面图片</span>
              </div>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="封面比例">
          <div class="cover-type-selector">
            <div 
              v-for="type in coverTypeList" 
              :key="type.code"
              class="cover-type-option"
              :class="{ active: form.coverType === type.code }"
              @click="selectCoverType(type.code)"
            >
              <div class="cover-type-preview" :style="{ aspectRatio: `${type.widthRatio} / ${type.heightRatio}` }">
                <div class="preview-inner">
                  <span class="ratio-text">{{ type.widthRatio }}:{{ type.heightRatio }}</span>
                </div>
              </div>
              <div class="cover-type-info">
                <span class="cover-type-name">{{ type.name }}</span>
                <span class="cover-type-tip">{{ type.tip }}</span>
              </div>
              <div class="check-icon" v-if="form.coverType === type.code">
                <el-icon><Check /></el-icon>
              </div>
            </div>
          </div>
        </el-form-item>
        
        <el-form-item label="作品内容">
          <el-input 
            v-model="form.content" 
            type="textarea" 
            :rows="6"
            placeholder="描述你的手账作品内容..."
          />
        </el-form-item>
        
        <el-form-item label="分类标签">
          <div class="category-section">
            <div class="category-group">
              <span class="group-label">排版风格</span>
              <div class="category-chips">
                <span 
                  v-for="cat in styleCategories" 
                  :key="cat.id"
                  class="chip"
                  :class="{ active: form.categoryIds.includes(cat.id) }"
                  @click="toggleCategory(cat.id)"
                >
                  {{ cat.name }}
                </span>
              </div>
            </div>
            <div class="category-group">
              <span class="group-label">应用场景</span>
              <div class="category-chips">
                <span 
                  v-for="cat in sceneCategories" 
                  :key="cat.id"
                  class="chip"
                  :class="{ active: form.categoryIds.includes(cat.id) }"
                  @click="toggleCategory(cat.id)"
                >
                  {{ cat.name }}
                </span>
              </div>
            </div>
          </div>
        </el-form-item>
        
        <el-form-item label="排版思路">
          <el-input 
            v-model="form.layoutIdea" 
            type="textarea" 
            :rows="3"
            placeholder="分享你的排版设计思路..."
          />
        </el-form-item>
        
        <el-form-item label="配色方案">
          <el-input 
            v-model="form.colorScheme" 
            type="textarea" 
            :rows="3"
            placeholder="描述你的配色方案，比如主色调、辅助色等..."
          />
        </el-form-item>
        
        <el-form-item label="创作灵感">
          <el-input 
            v-model="form.inspiration" 
            type="textarea" 
            :rows="3"
            placeholder="分享你的创作灵感来源..."
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" size="large" @click="handleSubmit" :loading="submitting">
            发布作品
          </el-button>
          <el-button size="large" @click="goBack">取消</el-button>
        </el-form-item>
      </el-form>
    </main>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, Close, Check } from '@element-plus/icons-vue'
import Header from '@/components/Header.vue'
import { publishWork } from '@/api/work'
import { getCategoryList } from '@/api/category'
import { COVER_TYPE_LIST, DEFAULT_COVER_TYPE, getCoverTypeByCode } from '@/constants/coverTypes'

const router = useRouter()
const formRef = ref(null)
const submitting = ref(false)
const styleCategories = ref([])
const sceneCategories = ref([])
const coverTypeList = COVER_TYPE_LIST

const form = reactive({
  userId: 1,
  title: '',
  coverImage: '',
  coverType: DEFAULT_COVER_TYPE.code,
  content: '',
  layoutIdea: '',
  colorScheme: '',
  inspiration: '',
  categoryIds: []
})

const selectedCoverAspectRatio = computed(() => {
  const type = getCoverTypeByCode(form.coverType)
  return `${type.widthRatio} / ${type.heightRatio}`
})

const selectCoverType = (code) => {
  form.coverType = code
}

const loadCategories = async () => {
  const [styleRes, sceneRes] = await Promise.all([
    getCategoryList('style'),
    getCategoryList('scene')
  ])
  styleCategories.value = styleRes.data || []
  sceneCategories.value = sceneRes.data || []
}

const toggleCategory = (id) => {
  const index = form.categoryIds.indexOf(id)
  if (index > -1) {
    form.categoryIds.splice(index, 1)
  } else {
    form.categoryIds.push(id)
  }
}

const uploadImage = () => {
  const samples = [
    'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800&h=500&fit=crop',
    'https://images.unsplash.com/photo-1513542789411-b6a5d4f31634?w=800&h=500&fit=crop',
    'https://images.unsplash.com/photo-1519681393784-d120267933ba?w=800&h=500&fit=crop'
  ]
  form.coverImage = samples[Math.floor(Math.random() * samples.length)]
}

const handleSubmit = async () => {
  if (!form.title) {
    ElMessage.warning('请输入作品标题')
    return
  }
  if (!form.coverImage) {
    ElMessage.warning('请上传封面图片')
    return
  }
  
  submitting.value = true
  try {
    await publishWork(form)
    ElMessage.success('发布成功！')
    router.push('/')
  } catch (e) {
    console.error(e)
  } finally {
    submitting.value = false
  }
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  loadCategories()
})
</script>

<style scoped>
.publish-content {
  max-width: 800px;
  padding: 30px 20px 60px;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin-bottom: 30px;
  text-align: center;
}

.publish-form {
  background: #fff;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.cover-upload-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.image-upload {
  width: 320px;
  max-width: 100%;
}

.upload-placeholder {
  width: 100%;
  height: 100%;
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  color: #999;
}

.upload-placeholder:hover {
  border-color: #ff9a9e;
  color: #ff6b9d;
}

.upload-placeholder .el-icon {
  font-size: 32px;
  margin-bottom: 8px;
}

.image-preview {
  position: relative;
  width: 100%;
  height: 100%;
}

.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
}

.remove-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.6);
  color: #fff;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cover-type-selector {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.cover-type-option {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 12px;
  border: 2px solid #e8e8e8;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  background: #fafafa;
  flex: 1;
  min-width: 180px;
  max-width: 220px;
}

.cover-type-option:hover {
  border-color: #ff9a9e;
  background: #fff5f8;
}

.cover-type-option.active {
  border-color: #ff6b9d;
  background: linear-gradient(135deg, #fff5f8 0%, #ffe8f0 100%);
  box-shadow: 0 2px 8px rgba(255, 107, 157, 0.15);
}

.cover-type-preview {
  width: 100%;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #eee;
}

.preview-inner {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
}

.ratio-text {
  font-size: 16px;
  font-weight: 600;
  color: #fff;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.cover-type-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.cover-type-name {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.cover-type-tip {
  font-size: 12px;
  color: #999;
  line-height: 1.4;
}

.check-icon {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #ff6b9d;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
}

.category-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.category-group {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.group-label {
  min-width: 80px;
  font-size: 14px;
  color: #666;
  padding-top: 6px;
}

.category-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  flex: 1;
}

.chip {
  padding: 6px 16px;
  border: 1px solid #e8e8e8;
  border-radius: 16px;
  font-size: 13px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
}

.chip:hover {
  border-color: #ff9a9e;
}

.chip.active {
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
  border-color: transparent;
  color: #fff;
}
</style>
