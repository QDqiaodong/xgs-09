<template>
  <div class="color-palette-wall" :class="[mode]">
    <template v-if="parsedSwatches.length > 0">
      <div v-if="mode === 'compact'" class="compact-palette">
        <div class="compact-row" v-if="primarySwatches.length > 0">
          <span class="compact-label label-primary">主色</span>
          <div class="compact-swatches">
            <div
              v-for="(swatch, i) in primarySwatches"
              :key="'p-' + i"
              class="compact-swatch"
              :style="{ background: swatch.color }"
              :title="swatch.name + (swatch.purpose ? ' · ' + swatch.purpose : '')"
            >
              <span class="hex-text" :style="{ color: getContrastColor(swatch.color) }">{{ swatch.color }}</span>
            </div>
          </div>
        </div>
        <div class="compact-row" v-if="secondarySwatches.length > 0">
          <span class="compact-label label-secondary">辅助色</span>
          <div class="compact-swatches">
            <div
              v-for="(swatch, i) in secondarySwatches"
              :key="'s-' + i"
              class="compact-swatch"
              :style="{ background: swatch.color }"
              :title="swatch.name + (swatch.purpose ? ' · ' + swatch.purpose : '')"
            >
              <span class="hex-text" :style="{ color: getContrastColor(swatch.color) }">{{ swatch.color }}</span>
            </div>
          </div>
        </div>
        <div class="compact-row" v-if="accentSwatches.length > 0">
          <span class="compact-label label-accent">点缀色</span>
          <div class="compact-swatches">
            <div
              v-for="(swatch, i) in accentSwatches"
              :key="'a-' + i"
              class="compact-swatch"
              :style="{ background: swatch.color }"
              :title="swatch.name + (swatch.purpose ? ' · ' + swatch.purpose : '')"
            >
              <span class="hex-text" :style="{ color: getContrastColor(swatch.color) }">{{ swatch.color }}</span>
            </div>
          </div>
        </div>
        <div class="compact-row" v-if="untypedSwatches.length > 0">
          <span class="compact-label label-other">其他</span>
          <div class="compact-swatches">
            <div
              v-for="(swatch, i) in untypedSwatches"
              :key="'u-' + i"
              class="compact-swatch"
              :style="{ background: swatch.color }"
              :title="swatch.name + (swatch.purpose ? ' · ' + swatch.purpose : '')"
            >
              <span class="hex-text" :style="{ color: getContrastColor(swatch.color) }">{{ swatch.color }}</span>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="expanded-palette">
        <div class="palette-overview">
          <div class="overview-strip">
            <div
              v-for="(swatch, i) in allSwatches"
              :key="'ov-' + i"
              class="overview-swatch"
              :style="{ background: swatch.color, flex: swatch.type === 'primary' ? 2 : 1 }"
            >
              <span class="overview-hex" :style="{ color: getContrastColor(swatch.color) }">{{ swatch.color }}</span>
            </div>
          </div>
        </div>

        <div class="palette-groups">
          <div class="palette-group group-primary" v-if="primarySwatches.length > 0">
            <div class="group-header">
              <div class="group-indicator"></div>
              <div class="group-title-area">
                <span class="group-title">主色</span>
                <span class="group-desc">奠定整体色调的核心色彩</span>
              </div>
              <span class="group-count">{{ primarySwatches.length }}</span>
            </div>
            <div class="group-wall">
              <div
                v-for="(swatch, i) in primarySwatches"
                :key="'pw-' + i"
                class="wall-card wall-card-primary"
              >
                <div class="wall-color" :style="{ background: swatch.color }">
                  <span class="wall-hex" :style="{ color: getContrastColor(swatch.color) }">{{ swatch.color.toUpperCase() }}</span>
                </div>
                <div class="wall-info">
                  <span class="wall-name">{{ swatch.name || '未命名' }}</span>
                  <span class="wall-purpose" v-if="swatch.purpose">{{ swatch.purpose }}</span>
                </div>
              </div>
            </div>
          </div>

          <div class="palette-group group-secondary" v-if="secondarySwatches.length > 0">
            <div class="group-header">
              <div class="group-indicator"></div>
              <div class="group-title-area">
                <span class="group-title">辅助色</span>
                <span class="group-desc">搭配主色的协调色彩</span>
              </div>
              <span class="group-count">{{ secondarySwatches.length }}</span>
            </div>
            <div class="group-wall">
              <div
                v-for="(swatch, i) in secondarySwatches"
                :key="'sw-' + i"
                class="wall-card wall-card-secondary"
              >
                <div class="wall-color" :style="{ background: swatch.color }">
                  <span class="wall-hex" :style="{ color: getContrastColor(swatch.color) }">{{ swatch.color.toUpperCase() }}</span>
                </div>
                <div class="wall-info">
                  <span class="wall-name">{{ swatch.name || '未命名' }}</span>
                  <span class="wall-purpose" v-if="swatch.purpose">{{ swatch.purpose }}</span>
                </div>
              </div>
            </div>
          </div>

          <div class="palette-group group-accent" v-if="accentSwatches.length > 0">
            <div class="group-header">
              <div class="group-indicator"></div>
              <div class="group-title-area">
                <span class="group-title">点缀色</span>
                <span class="group-desc">画龙点睛的亮点色彩</span>
              </div>
              <span class="group-count">{{ accentSwatches.length }}</span>
            </div>
            <div class="group-wall">
              <div
                v-for="(swatch, i) in accentSwatches"
                :key="'aw-' + i"
                class="wall-card wall-card-accent"
              >
                <div class="wall-color" :style="{ background: swatch.color }">
                  <span class="wall-hex" :style="{ color: getContrastColor(swatch.color) }">{{ swatch.color.toUpperCase() }}</span>
                </div>
                <div class="wall-info">
                  <span class="wall-name">{{ swatch.name || '未命名' }}</span>
                  <span class="wall-purpose" v-if="swatch.purpose">{{ swatch.purpose }}</span>
                </div>
              </div>
            </div>
          </div>

          <div class="palette-group group-other" v-if="untypedSwatches.length > 0">
            <div class="group-header">
              <div class="group-indicator"></div>
              <div class="group-title-area">
                <span class="group-title">其他</span>
                <span class="group-desc">补充使用的色彩</span>
              </div>
              <span class="group-count">{{ untypedSwatches.length }}</span>
            </div>
            <div class="group-wall">
              <div
                v-for="(swatch, i) in untypedSwatches"
                :key="'uw-' + i"
                class="wall-card wall-card-other"
              >
                <div class="wall-color" :style="{ background: swatch.color }">
                  <span class="wall-hex" :style="{ color: getContrastColor(swatch.color) }">{{ swatch.color.toUpperCase() }}</span>
                </div>
                <div class="wall-info">
                  <span class="wall-name">{{ swatch.name || '未命名' }}</span>
                  <span class="wall-purpose" v-if="swatch.purpose">{{ swatch.purpose }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const HEX_COLOR_REGEX = /^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$/

const props = defineProps({
  colorScheme: {
    type: [String, Array],
    default: null
  },
  mode: {
    type: String,
    default: 'compact',
    validator: (v) => ['compact', 'expanded'].includes(v)
  }
})

const isValidHexColor = (color) => {
  return typeof color === 'string' && HEX_COLOR_REGEX.test(color)
}

const parsedSwatches = computed(() => {
  if (!props.colorScheme) return []
  try {
    const parsed = typeof props.colorScheme === 'string'
      ? JSON.parse(props.colorScheme)
      : props.colorScheme
    if (Array.isArray(parsed) && parsed.length > 0 && parsed[0].color) {
      return parsed.filter(s =>
        isValidHexColor(s.color) && (s.name || s.purpose)
      )
    }
    return []
  } catch (e) {
    return []
  }
})

const primarySwatches = computed(() =>
  parsedSwatches.value.filter(s => s.type === 'primary')
)

const secondarySwatches = computed(() =>
  parsedSwatches.value.filter(s => s.type === 'secondary')
)

const accentSwatches = computed(() =>
  parsedSwatches.value.filter(s => s.type === 'accent')
)

const untypedSwatches = computed(() =>
  parsedSwatches.value.filter(s => !s.type || !['primary', 'secondary', 'accent'].includes(s.type))
)

const allSwatches = computed(() => [
  ...primarySwatches.value,
  ...secondarySwatches.value,
  ...accentSwatches.value,
  ...untypedSwatches.value,
])

const getContrastColor = (hexColor) => {
  if (!hexColor || !HEX_COLOR_REGEX.test(hexColor)) return '#333'
  const hex = hexColor.replace('#', '')
  const full = hex.length === 3
    ? hex[0] + hex[0] + hex[1] + hex[1] + hex[2] + hex[2]
    : hex
  const r = parseInt(full.substring(0, 2), 16)
  const g = parseInt(full.substring(2, 4), 16)
  const b = parseInt(full.substring(4, 6), 16)
  const luminance = (0.299 * r + 0.587 * g + 0.114 * b) / 255
  return luminance > 0.55 ? 'rgba(0,0,0,0.7)' : 'rgba(255,255,255,0.9)'
}
</script>

<style scoped>
.color-palette-wall.compact .compact-palette {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.compact-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.compact-label {
  font-size: 10px;
  font-weight: 600;
  flex-shrink: 0;
  width: 32px;
  text-align: center;
  padding: 2px 0;
  border-radius: 4px;
  line-height: 1.3;
}

.label-primary {
  background: rgba(255, 107, 157, 0.12);
  color: #d1477a;
}

.label-secondary {
  background: rgba(74, 158, 255, 0.12);
  color: #2b7fd4;
}

.label-accent {
  background: rgba(255, 193, 7, 0.15);
  color: #d49600;
}

.label-other {
  background: rgba(158, 158, 158, 0.12);
  color: #757575;
}

.compact-swatches {
  display: flex;
  gap: 4px;
  flex: 1;
  min-width: 0;
}

.compact-swatch {
  flex: 1;
  min-width: 0;
  height: 24px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: default;
  transition: transform 0.2s, box-shadow 0.2s;
  position: relative;
  overflow: hidden;
}

.compact-swatch:hover {
  transform: scaleY(1.15);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  z-index: 1;
}

.hex-text {
  font-size: 8px;
  font-weight: 600;
  font-family: 'SF Mono', 'Monaco', 'Menlo', monospace;
  letter-spacing: -0.3px;
  opacity: 0;
  transition: opacity 0.2s;
  white-space: nowrap;
}

.compact-swatch:hover .hex-text {
  opacity: 1;
}

.expanded-palette {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.palette-overview {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.overview-strip {
  display: flex;
  height: 56px;
  border-radius: 12px;
  overflow: hidden;
}

.overview-swatch {
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: default;
  transition: flex 0.3s;
  position: relative;
  min-width: 0;
}

.overview-swatch:hover {
  flex: 3 !important;
}

.overview-hex {
  font-size: 11px;
  font-weight: 700;
  font-family: 'SF Mono', 'Monaco', 'Menlo', monospace;
  letter-spacing: 0.3px;
  opacity: 0;
  transition: opacity 0.2s;
  white-space: nowrap;
}

.overview-swatch:hover .overview-hex {
  opacity: 1;
}

.palette-groups {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.palette-group {
  border-radius: 12px;
  padding: 16px;
  transition: all 0.3s;
}

.group-primary {
  background: linear-gradient(135deg, #fff5f8 0%, #ffe0ea 100%);
  border: 1px solid rgba(255, 107, 157, 0.15);
}

.group-secondary {
  background: linear-gradient(135deg, #f0f7ff 0%, #dceeff 100%);
  border: 1px solid rgba(74, 158, 255, 0.15);
}

.group-accent {
  background: linear-gradient(135deg, #fffde7 0%, #fff8cd 100%);
  border: 1px solid rgba(255, 193, 7, 0.2);
}

.group-other {
  background: linear-gradient(135deg, #f5f5f5 0%, #eeeeee 100%);
  border: 1px solid rgba(158, 158, 158, 0.15);
}

.group-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 14px;
}

.group-indicator {
  width: 4px;
  height: 28px;
  border-radius: 2px;
  flex-shrink: 0;
}

.group-primary .group-indicator {
  background: linear-gradient(180deg, #ff6b9d, #d1477a);
}

.group-secondary .group-indicator {
  background: linear-gradient(180deg, #4a9eff, #2b7fd4);
}

.group-accent .group-indicator {
  background: linear-gradient(180deg, #ffc107, #d49600);
}

.group-other .group-indicator {
  background: linear-gradient(180deg, #9e9e9e, #757575);
}

.group-title-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.group-title {
  font-size: 15px;
  font-weight: 700;
}

.group-primary .group-title {
  color: #ad1457;
}

.group-secondary .group-title {
  color: #1565c0;
}

.group-accent .group-title {
  color: #e65100;
}

.group-other .group-title {
  color: #616161;
}

.group-desc {
  font-size: 12px;
  color: #999;
}

.group-count {
  font-size: 12px;
  font-weight: 600;
  padding: 2px 10px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.7);
  color: #666;
}

.group-wall {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 4px;
  scrollbar-width: thin;
}

.group-wall::-webkit-scrollbar {
  height: 4px;
}

.group-wall::-webkit-scrollbar-track {
  background: transparent;
}

.group-wall::-webkit-scrollbar-thumb {
  border-radius: 2px;
}

.group-primary .group-wall::-webkit-scrollbar-thumb {
  background: rgba(255, 107, 157, 0.25);
}

.group-secondary .group-wall::-webkit-scrollbar-thumb {
  background: rgba(74, 158, 255, 0.25);
}

.group-accent .group-wall::-webkit-scrollbar-thumb {
  background: rgba(255, 193, 7, 0.3);
}

.group-other .group-wall::-webkit-scrollbar-thumb {
  background: rgba(158, 158, 158, 0.25);
}

.wall-card {
  flex-shrink: 0;
  width: 140px;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s;
}

.wall-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
}

.wall-card-primary:hover {
  box-shadow: 0 6px 16px rgba(255, 107, 157, 0.2);
}

.wall-card-secondary:hover {
  box-shadow: 0 6px 16px rgba(74, 158, 255, 0.2);
}

.wall-card-accent:hover {
  box-shadow: 0 6px 16px rgba(255, 193, 7, 0.25);
}

.wall-color {
  height: 72px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.wall-hex {
  font-size: 12px;
  font-weight: 700;
  font-family: 'SF Mono', 'Monaco', 'Menlo', monospace;
  letter-spacing: 0.5px;
  opacity: 0.85;
}

.wall-info {
  padding: 10px 12px;
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.wall-name {
  font-size: 13px;
  font-weight: 600;
  color: #333;
}

.wall-purpose {
  font-size: 11px;
  color: #999;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

@media (max-width: 600px) {
  .wall-card {
    width: 120px;
  }

  .wall-color {
    height: 60px;
  }

  .overview-strip {
    height: 44px;
  }

  .palette-group {
    padding: 12px;
  }
}
</style>
