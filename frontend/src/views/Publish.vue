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
          <div class="color-scheme-editor">
            <div class="color-type-section">
              <div class="color-type-header">
                <span class="type-label">主色 <span class="type-count">({{ primaryCount }}/{{ MAX_PRIMARY }})</span></span>
                <span class="type-desc">主色调，决定整体氛围</span>
              </div>
              <div class="color-swatches">
                <div 
                  v-for="(swatch, index) in primarySwatches" 
                  :key="'p-' + index"
                  class="color-swatch-item primary"
                >
                  <div class="swatch-color" :style="{ background: swatch.color }">
                    <el-color-picker 
                      v-model="swatch.color" 
                      size="small"
                      :show-alpha="false"
                    />
                  </div>
                  <div class="swatch-info">
                    <el-input 
                      v-model="swatch.name" 
                      placeholder="颜色名称" 
                      size="small"
                    />
                    <el-input 
                      v-model="swatch.purpose" 
                      placeholder="用途说明" 
                      size="small"
                    />
                  </div>
                  <button 
                    class="remove-swatch" 
                    @click="removeColorSwatch(swatch, 'primary')"
                    :disabled="primaryCount <= 1"
                  >
                    <el-icon><Close /></el-icon>
                  </button>
                </div>
              </div>
              <button 
                class="add-swatch-btn small" 
                @click="addColorSwatch('primary')"
                :disabled="primaryCount >= MAX_PRIMARY"
              >
                <el-icon><Plus /></el-icon>
                添加主色
              </button>
            </div>

            <div class="color-type-section">
              <div class="color-type-header">
                <span class="type-label">辅助色 <span class="type-count">({{ secondaryCount }}/{{ MAX_SECONDARY }})</span></span>
                <span class="type-desc">搭配主色，丰富层次</span>
              </div>
              <div class="color-swatches">
                <div 
                  v-for="(swatch, index) in secondarySwatches" 
                  :key="'s-' + index"
                  class="color-swatch-item secondary"
                >
                  <div class="swatch-color" :style="{ background: swatch.color }">
                    <el-color-picker 
                      v-model="swatch.color" 
                      size="small"
                      :show-alpha="false"
                    />
                  </div>
                  <div class="swatch-info">
                    <el-input 
                      v-model="swatch.name" 
                      placeholder="颜色名称" 
                      size="small"
                    />
                    <el-input 
                      v-model="swatch.purpose" 
                      placeholder="用途说明" 
                      size="small"
                    />
                  </div>
                  <button 
                    class="remove-swatch" 
                    @click="removeColorSwatch(swatch, 'secondary')"
                    :disabled="secondaryCount <= 1"
                  >
                    <el-icon><Close /></el-icon>
                  </button>
                </div>
              </div>
              <button 
                class="add-swatch-btn small" 
                @click="addColorSwatch('secondary')"
                :disabled="secondaryCount >= MAX_SECONDARY"
              >
                <el-icon><Plus /></el-icon>
                添加辅助色
              </button>
            </div>

            <div class="color-type-section">
              <div class="color-type-header">
                <span class="type-label">点缀色 <span class="type-count">({{ accentCount }}/{{ MAX_ACCENT }})</span></span>
                <span class="type-desc">小面积使用，突出重点</span>
              </div>
              <div class="color-swatches">
                <div 
                  v-for="(swatch, index) in accentSwatches" 
                  :key="'a-' + index"
                  class="color-swatch-item accent"
                >
                  <div class="swatch-color" :style="{ background: swatch.color }">
                    <el-color-picker 
                      v-model="swatch.color" 
                      size="small"
                      :show-alpha="false"
                    />
                  </div>
                  <div class="swatch-info">
                    <el-input 
                      v-model="swatch.name" 
                      placeholder="颜色名称" 
                      size="small"
                    />
                    <el-input 
                      v-model="swatch.purpose" 
                      placeholder="用途说明" 
                      size="small"
                    />
                  </div>
                  <button 
                    class="remove-swatch" 
                    @click="removeColorSwatch(swatch, 'accent')"
                    :disabled="accentCount <= 0"
                  >
                    <el-icon><Close /></el-icon>
                  </button>
                </div>
              </div>
              <button 
                class="add-swatch-btn small" 
                @click="addColorSwatch('accent')"
                :disabled="accentCount >= MAX_ACCENT"
              >
                <el-icon><Plus /></el-icon>
                添加点缀色
              </button>
            </div>

            <div class="color-scheme-tip" v-if="colorSchemeError">
              <el-icon class="tip-icon"><Warning /></el-icon>
              <span>{{ colorSchemeError }}</span>
            </div>
          </div>
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
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, Close, Check, Warning } from '@element-plus/icons-vue'
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

const MAX_PRIMARY = 2
const MAX_SECONDARY = 4
const MAX_ACCENT = 3
const MIN_PRIMARY = 1
const MIN_SECONDARY = 1
const MAX_TOTAL = 8

const HEX_COLOR_REGEX = /^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$/

const primarySwatches = ref([
  { color: '#FFB6C1', name: '主色', purpose: '用于标题和重点内容', type: 'primary' }
])

const secondarySwatches = ref([
  { color: '#98D8C8', name: '辅助色', purpose: '用于装饰和点缀', type: 'secondary' }
])

const accentSwatches = ref([
  { color: '#FFD700', name: '点缀色', purpose: '小面积强调', type: 'accent' }
])

const colorSchemeError = ref('')

const primaryCount = computed(() => primarySwatches.value.length)
const secondaryCount = computed(() => secondarySwatches.value.length)
const accentCount = computed(() => accentSwatches.value.length)
const totalCount = computed(() => primaryCount.value + secondaryCount.value + accentCount.value)

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

const addColorSwatch = (type) => {
  const swatch = {
    color: '#FFFFFF',
    name: '',
    purpose: '',
    type
  }
  
  if (type === 'primary' && primaryCount.value < MAX_PRIMARY) {
    primarySwatches.value.push(swatch)
  } else if (type === 'secondary' && secondaryCount.value < MAX_SECONDARY) {
    secondarySwatches.value.push(swatch)
  } else if (type === 'accent' && accentCount.value < MAX_ACCENT) {
    accentSwatches.value.push(swatch)
  }
}

const removeColorSwatch = (swatch, type) => {
  if (type === 'primary' && primaryCount.value > MIN_PRIMARY) {
    const index = primarySwatches.value.indexOf(swatch)
    if (index > -1) primarySwatches.value.splice(index, 1)
  } else if (type === 'secondary' && secondaryCount.value > MIN_SECONDARY) {
    const index = secondarySwatches.value.indexOf(swatch)
    if (index > -1) secondarySwatches.value.splice(index, 1)
  } else if (type === 'accent' && accentCount.value > 0) {
    const index = accentSwatches.value.indexOf(swatch)
    if (index > -1) accentSwatches.value.splice(index, 1)
  }
}

const isValidHexColor = (color) => {
  return HEX_COLOR_REGEX.test(color)
}

const validateColorScheme = () => {
  const allSwatches = [...primarySwatches.value, ...secondarySwatches.value, ...accentSwatches.value]
  
  if (primaryCount.value < MIN_PRIMARY) {
    colorSchemeError.value = `至少需要 ${MIN_PRIMARY} 个主色`
    return false
  }
  if (secondaryCount.value < MIN_SECONDARY) {
    colorSchemeError.value = `至少需要 ${MIN_SECONDARY} 个辅助色`
    return false
  }
  if (totalCount.value > MAX_TOTAL) {
    colorSchemeError.value = `颜色总数不能超过 ${MAX_TOTAL} 个`
    return false
  }
  
  for (const swatch of allSwatches) {
    if (!isValidHexColor(swatch.color)) {
      colorSchemeError.value = `颜色 "${swatch.name || swatch.color}" 格式无效，请使用十六进制颜色`
      return false
    }
  }
  
  const colors = allSwatches.map(s => s.color.toLowerCase())
  const uniqueColors = [...new Set(colors)]
  if (colors.length !== uniqueColors.length) {
    colorSchemeError.value = '存在重复的颜色值'
    return false
  }
  
  colorSchemeError.value = ''
  return true
}

const getColorSchemeJson = () => {
  const allSwatches = [
    ...primarySwatches.value.map(s => ({ ...s, type: 'primary' })),
    ...secondarySwatches.value.map(s => ({ ...s, type: 'secondary' })),
    ...accentSwatches.value.map(s => ({ ...s, type: 'accent' }))
  ]
  const validSwatches = allSwatches.filter(s => s.name || s.purpose)
  return JSON.stringify(validSwatches)
}

watch(
  () => [...primarySwatches.value, ...secondarySwatches.value, ...accentSwatches.value],
  () => {
    validateColorScheme()
  },
  { deep: true }
)

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
  
  if (!validateColorScheme()) {
    ElMessage.warning(colorSchemeError.value || '色彩方案校验失败')
    return
  }
  
  form.colorScheme = getColorSchemeJson()
  
  submitting.value = true
  try {
    await publishWork(form)
    ElMessage.success('发布成功！')
    router.push('/')
  } catch (e) {
    console.error(e)
    ElMessage.error(e.message || '发布失败，请重试')
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

.color-scheme-editor {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.color-type-section {
  padding: 16px;
  background: #fafafa;
  border-radius: 10px;
  border: 1px solid #eee;
}

.color-type-section.primary {
  border-left: 4px solid #ff6b9d;
}

.color-type-section.secondary {
  border-left: 4px solid #4a9eff;
}

.color-type-section.accent {
  border-left: 4px solid #ffc107;
}

.color-type-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.type-label {
  font-size: 15px;
  font-weight: 600;
  color: #333;
}

.type-count {
  font-size: 13px;
  color: #999;
  font-weight: 400;
}

.type-desc {
  font-size: 12px;
  color: #999;
}

.color-swatches {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 12px;
}

.color-swatch-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #eee;
  transition: all 0.3s;
}

.color-swatch-item:hover {
  border-color: #ddd;
}

.swatch-color {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  border: 2px solid #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.swatch-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.remove-swatch {
  width: 28px;
  height: 28px;
  border: none;
  background: #ff6b6b;
  color: #fff;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 0.3s;
}

.remove-swatch:hover:not(:disabled) {
  background: #ff5252;
}

.remove-swatch:disabled {
  background: #ffcccc;
  cursor: not-allowed;
  opacity: 0.6;
}

.add-swatch-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 10px;
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  background: #fff;
  color: #999;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s;
}

.add-swatch-btn:hover:not(:disabled) {
  border-color: #ff9a9e;
  color: #ff6b9d;
  background: #fff5f8;
}

.add-swatch-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.add-swatch-btn.small {
  padding: 8px;
  font-size: 12px;
}

.color-scheme-tip {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #fff3cd;
  border-radius: 8px;
  color: #856404;
  font-size: 13px;
}

.tip-icon {
  font-size: 18px;
  flex-shrink: 0;
}

@media (max-width: 600px) {
  .color-swatch-item {
    flex-wrap: wrap;
  }
  
  .swatch-info {
    width: calc(100% - 60px);
  }
  
  .color-type-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
}
</style>
