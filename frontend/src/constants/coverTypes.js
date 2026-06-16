export const COVER_TYPES = {
  HORIZONTAL_COLLAGE: {
    code: 1,
    key: 'HORIZONTAL_COLLAGE',
    name: '横向拼贴',
    widthRatio: 3,
    heightRatio: 2,
    aspectRatio: 3 / 2,
    tip: '适合展示多图拼贴风格手账'
  },
  VERTICAL_FULL: {
    code: 2,
    key: 'VERTICAL_FULL',
    name: '竖向整页',
    widthRatio: 3,
    heightRatio: 4,
    aspectRatio: 3 / 4,
    tip: '适合展示竖向整页手账内容'
  },
  CLOSEUP: {
    code: 3,
    key: 'CLOSEUP',
    name: '局部特写',
    widthRatio: 1,
    heightRatio: 1,
    aspectRatio: 1 / 1,
    tip: '适合展示细节特写'
  }
}

export const COVER_TYPE_LIST = Object.values(COVER_TYPES)

export const DEFAULT_COVER_TYPE = COVER_TYPES.HORIZONTAL_COLLAGE

export function getCoverTypeByCode(code) {
  if (!code) return DEFAULT_COVER_TYPE
  const found = COVER_TYPE_LIST.find(item => item.code === code)
  return found || DEFAULT_COVER_TYPE
}

export function getCoverAspectRatio(code) {
  const coverType = getCoverTypeByCode(code)
  return coverType.aspectRatio
}

export function getCardHeightByCoverType(code, cardWidth) {
  const aspectRatio = getCoverAspectRatio(code)
  const coverHeight = cardWidth / aspectRatio
  const contentHeight = 160
  return coverHeight + contentHeight
}
