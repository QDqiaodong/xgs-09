export const MATERIAL_TYPES = {
  tape: {
    key: 'tape',
    name: '胶带',
    icon: '📎',
    color: '#FFD700',
    bgColor: '#FFFBEB',
    borderColor: '#F5D76E',
    description: '装饰胶带，用于分区和点缀'
  },
  sticker: {
    key: 'sticker',
    name: '贴纸',
    icon: '⭐',
    color: '#FF6B9D',
    bgColor: '#FFF0F5',
    borderColor: '#FFADD2',
    description: '各式贴纸，增添趣味感'
  },
  stamp: {
    key: 'stamp',
    name: '印章',
    icon: '🔖',
    color: '#E91E63',
    bgColor: '#FCE4EC',
    borderColor: '#F48FB1',
    description: '印章图案，营造复古感'
  },
  handwriting: {
    key: 'handwriting',
    name: '手写字',
    icon: '✍️',
    color: '#4A90D9',
    bgColor: '#E8F4FD',
    borderColor: '#91D5FF',
    description: '手写文字，记录心情'
  },
  photo: {
    key: 'photo',
    name: '照片',
    icon: '📷',
    color: '#52C41A',
    bgColor: '#F6FFED',
    borderColor: '#B7EB8F',
    description: '照片区域，珍藏回忆'
  }
}

export const MATERIAL_TYPE_LIST = Object.values(MATERIAL_TYPES)

export const getMaterialConfig = (type) => {
  return MATERIAL_TYPES[type] || null
}

export const extractMaterialStats = (layoutConfig) => {
  const stats = {
    tape: { count: 0, areas: [] },
    sticker: { count: 0, areas: [] },
    stamp: { count: 0, areas: [] },
    handwriting: { count: 0, areas: [] },
    photo: { count: 0, areas: [] }
  }

  if (!layoutConfig || !layoutConfig.areas) {
    return stats
  }

  for (const area of layoutConfig.areas) {
    const type = area.type
    if (type === 'image' || type === 'photo') {
      stats.photo.count += 1
      stats.photo.areas.push(area)
    } else if (type === 'text' || type === 'handwriting') {
      stats.handwriting.count += 1
      stats.handwriting.areas.push(area)
    } else if (type === 'sticker') {
      stats.sticker.count += area.stickerCount || 1
      stats.sticker.areas.push(area)
    } else if (type === 'tape') {
      stats.tape.count += area.tapeCount || 1
      stats.tape.areas.push(area)
    } else if (type === 'stamp') {
      stats.stamp.count += area.stampCount || 1
      stats.stamp.areas.push(area)
    }
  }

  return stats
}
