<template>
  <div class="color-usage-profile">
    <div class="profile-card">
      <h3 class="profile-title">
        <span class="title-icon">🎨</span>
        配色使用画像
      </h3>
      <div v-if="hasData">
        <div class="profile-section">
          <div class="section-header">
            <h4 class="section-title">常用色系</h4>
            <span class="section-sub">共 {{ colorUsage.totalColorSchemes }} 套配色方案</span>
          </div>
          <div class="family-stats">
            <div
              v-for="family in colorUsage.familyStats"
              :key="family.family"
              class="family-item"
              :title="family.familyName + ' ' + family.count + '次 (' + family.percentage.toFixed(1) + '%)'"
            >
              <div class="family-swatch" :style="{ background: family.representativeColor }"></div>
              <div class="family-info">
                <div class="family-name">{{ family.familyName }}</div>
                <div class="family-bar-wrapper">
                  <div
                    class="family-bar" :style="{ width: family.percentage + '%', background: family.representativeColor }"></div>
                </div>
              </div>
              <div class="family-count">{{ family.count }}<span class="family-percent">{{ family.percentage.toFixed(1) }}%</span></div>
            </div>
          </div>
        </div>

        <div class="profile-section" v-if="colorUsage.topColors && colorUsage.topColors.length > 0">
          <h4 class="section-title">常用颜色</h4>
          <div class="top-colors">
            <div
              v-for="color in colorUsage.topColors"
              :key="color.color"
              class="color-item"
            >
              <div class="color-swatch" :style="{ background: color.color }" :title="color.name"
                @mouseenter="handleColorHover(color.color)"
                @mouseleave="handleColorLeave"
              >
                <div class="color-tooltip" v-if="hoveredColor === color.color">
                  <div class="tooltip-name">{{ color.name }}</div>
                  <div class="tooltip-type">{{ color.usageType }} · {{ color.count }}次</div>
                  <div class="tooltip-hex">{{ color.color }}</div>
                </div>
              </div>
              <div class="color-info">
                <div class="color-name">{{ color.name }}</div>
                <div class="color-type">{{ color.usageType }}</div>
              </div>
              <div class="color-count">{{ color.count }}</div>
            </div>
          </div>
        </div>

        <div class="profile-section" v-if="colorUsage.topCombinations && colorUsage.topCombinations.length > 0">
          <h4 class="section-title">偏好配色组合</h4>
          <div class="top-combinations">
            <div
              v-for="(combo, index) in colorUsage.topCombinations"
              :key="index"
              class="combo-item"
              @mouseenter="handleColorHover(combo.colors.join('-'))"
              @mouseleave="handleColorLeave"
            >
              <div class="combo-swatches">
                <div
                  v-for="(color, colorIndex) in combo.colors"
                  :key="colorIndex"
                  class="combo-swatch"
                  :style="{ 
                    background: color,
                    marginLeft: colorIndex > 0 ? '-8px' : '0',
                    zIndex: combo.colors.length - colorIndex
                  }"
                ></div>
              </div>
              <div class="combo-info">
                <div class="combo-tag" v-if="combo.styleTag">{{ combo.styleTag }}</div>
                <div class="combo-count">使用 {{ combo.count }} 次</div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="empty-profile" v-else>
        <div class="empty-icon">🎨</div>
        <div class="empty-text">暂无配色数据</div>
        <div class="empty-desc">发布作品时添加配色方案后，这里会展示你的配色偏好</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  colorUsage: {
    type: Object,
    default: () => ({})
  }
})

const hoveredColor = ref(null)

const hasData = computed(() => {
  return props.colorUsage && 
         props.colorUsage.familyStats && 
         props.colorUsage.familyStats.length > 0
})

const handleColorHover = (color) => {
  hoveredColor.value = color
}

const handleColorLeave = () => {
  hoveredColor.value = null
}
</script>

<style scoped>
.color-usage-profile {
  width: 100%;
}

.profile-card {
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.profile-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.title-icon {
  font-size: 20px;
}

.profile-section {
  margin-bottom: 28px;
}

.profile-section:last-child {
  margin-bottom: 0;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px 0;
}

.section-header .section-title {
  margin-bottom: 0;
}

.section-sub {
  font-size: 12px;
  color: #999;
}

.family-stats {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.family-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 12px;
  border-radius: 8px;
  transition: background 0.2s;
  cursor: pointer;
}

.family-item:hover {
  background: #fafafa;
}

.family-swatch {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.family-info {
  flex: 1;
  min-width: 0;
}

.family-name {
  font-size: 13px;
  color: #333;
  margin-bottom: 4px;
  font-weight: 500;
}

.family-bar-wrapper {
  height: 6px;
  background: #f0f0f0;
  border-radius: 3px;
  overflow: hidden;
}

.family-bar {
  height: 100%;
  border-radius: 3px;
  transition: width 0.5s ease;
}

.family-count {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  flex-shrink: 0;
  text-align: right;
  min-width: 80px;
}

.family-percent {
  font-size: 11px;
  color: #999;
  font-weight: 400;
  margin-left: 4px;
}

.top-colors {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 16px;
}

.color-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px 8px;
  border-radius: 10px;
  transition: all 0.2s;
  cursor: pointer;
  position: relative;
}

.color-item:hover {
  background: #fafafa;
  transform: translateY(-2px);
}

.color-swatch {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  margin-bottom: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  position: relative;
  transition: transform 0.2s;
}

.color-item:hover .color-swatch {
  transform: scale(1.1);
}

.color-tooltip {
  position: absolute;
  bottom: calc(100% + 8px);
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, 0.85);
  color: #fff;
  padding: 8px 12px;
  border-radius: 8px;
  font-size: 12px;
  white-space: nowrap;
  z-index: 100;
  pointer-events: none;
  animation: fadeIn 0.2s ease;
}

.color-tooltip::after {
  content: '';
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%);
  border: 6px solid transparent;
  border-top-color: rgba(0, 0, 0, 0.85);
}

.tooltip-name {
  font-weight: 600;
  margin-bottom: 2px;
}

.tooltip-type {
  color: #ccc;
  font-size: 11px;
  margin-bottom: 2px;
}

.tooltip-hex {
  color: #aaa;
  font-size: 10px;
  font-family: monospace;
}

.color-info {
  text-align: center;
}

.color-name {
  font-size: 12px;
  color: #333;
  font-weight: 500;
  margin-bottom: 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 90px;
}

.color-type {
  font-size: 11px;
  color: #999;
}

.color-count {
  position: absolute;
  top: 4px;
  right: 4px;
  background: #ff6b9d;
  color: #fff;
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 10px;
  font-weight: 600;
  min-width: 18px;
  text-align: center;
}

.top-combinations {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.combo-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 16px;
  border-radius: 10px;
  background: linear-gradient(135deg, #fafafa 0%, #f5f5f5 100%);
  transition: all 0.2s;
}

.combo-item:hover {
  transform: translateX(4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.combo-swatches {
  display: flex;
  align-items: center;
}

.combo-swatch {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  border: 3px solid #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  position: relative;
}

.combo-info {
  flex: 1;
}

.combo-tag {
  font-size: 13px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.combo-count {
  font-size: 12px;
  color: #999;
}

.empty-profile {
  text-align: center;
  padding: 60px 20px;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
  opacity: 0.5;
}

.empty-text {
  font-size: 16px;
  color: #666;
  margin-bottom: 8px;
  font-weight: 500;
}

.empty-desc {
  font-size: 13px;
  color: #999;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateX(-50%) translateY(4px);
  }
  to {
    opacity: 1;
    transform: translateX(-50%) translateY(0);
  }
}

@media (max-width: 768px) {
  .top-colors {
    grid-template-columns: repeat(auto-fill, minmax(80px, 1fr));
    gap: 12px;
  }

  .color-swatch {
    width: 48px;
    height: 48px;
  }

  .family-count {
    min-width: 60px;
    font-size: 12px;
  }
}
</style>
