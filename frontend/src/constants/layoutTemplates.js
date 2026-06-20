export const layoutTemplates = {
  twoColumnLeftImage: {
    columns: 2,
    columnGap: '16px',
    padding: '24px',
    areas: [
      { type: 'image', label: '图片区', columnSpan: 1, rowSpan: 2 },
      { type: 'text', label: '主文字区', columnSpan: 1, rowSpan: 1 },
      { type: 'sticker', label: '装饰贴纸', columnSpan: 1, rowSpan: 1, stickerCount: 4 }
    ]
  },

  twoColumnRightImage: {
    columns: 2,
    columnGap: '16px',
    padding: '24px',
    areas: [
      { type: 'text', label: '主文字区', columnSpan: 1, rowSpan: 2 },
      { type: 'image', label: '图片区', columnSpan: 1, rowSpan: 1 },
      { type: 'sticker', label: '贴纸区', columnSpan: 1, rowSpan: 1, stickerCount: 3 }
    ]
  },

  timelineVertical: {
    columns: 1,
    columnGap: '0',
    padding: '20px',
    areas: [
      { type: 'tape', label: '日期胶带', columnSpan: 1, rowSpan: 1, tapeCount: 1 },
      { type: 'handwriting', label: '标题手写字', columnSpan: 1, rowSpan: 1 },
      { type: 'sticker', label: '便签贴纸', columnSpan: 1, rowSpan: 1, stickerCount: 3 },
      { type: 'text', label: '记录区', columnSpan: 1, rowSpan: 2 },
      { type: 'stamp', label: '打卡印章', columnSpan: 1, rowSpan: 1, stampCount: 2 }
    ]
  },

  centerFocus: {
    columns: 2,
    columnGap: '12px',
    padding: '28px',
    areas: [
      { type: 'tape', label: '顶部装饰胶带', columnSpan: 2, rowSpan: 1, tapeCount: 2 },
      { type: 'sticker', label: '顶部装饰', columnSpan: 2, rowSpan: 1, stickerCount: 5 },
      { type: 'handwriting', label: '手写字标题', columnSpan: 2, rowSpan: 1 },
      { type: 'text', label: '主内容区', columnSpan: 2, rowSpan: 2 },
      { type: 'image', label: '配图区', columnSpan: 1, rowSpan: 1 },
      { type: 'sticker', label: '小贴纸', columnSpan: 1, rowSpan: 1, stickerCount: 4 },
      { type: 'stamp', label: '装饰印章', columnSpan: 2, rowSpan: 1, stampCount: 3 }
    ]
  },

  collageStyle: {
    columns: 3,
    columnGap: '8px',
    padding: '16px',
    areas: [
      { type: 'image', label: '照片1', columnSpan: 1, rowSpan: 1 },
      { type: 'text', label: '文字', columnSpan: 1, rowSpan: 1 },
      { type: 'sticker', label: '票根', columnSpan: 1, rowSpan: 1, stickerCount: 2 },
      { type: 'tape', label: '胶带装饰', columnSpan: 2, rowSpan: 1, tapeCount: 2 },
      { type: 'text', label: '记录', columnSpan: 2, rowSpan: 1 },
      { type: 'image', label: '照片2', columnSpan: 1, rowSpan: 2 },
      { type: 'sticker', label: '贴纸', columnSpan: 2, rowSpan: 1, stickerCount: 4 },
      { type: 'stamp', label: '纪念印章', columnSpan: 1, rowSpan: 1, stampCount: 2 },
      { type: 'handwriting', label: '手写备注', columnSpan: 2, rowSpan: 1 }
    ]
  },

  minimalist: {
    columns: 1,
    columnGap: '0',
    padding: '40px',
    areas: [
      { type: 'text', label: '标题', columnSpan: 1, rowSpan: 1 },
      { type: 'handwriting', label: '手写日期', columnSpan: 1, rowSpan: 1 },
      { type: 'text', label: '正文', columnSpan: 1, rowSpan: 3 },
      { type: 'sticker', label: '小装饰', columnSpan: 1, rowSpan: 1, stickerCount: 2 },
      { type: 'stamp', label: '日期印章', columnSpan: 1, rowSpan: 1, stampCount: 1 }
    ]
  },

  naturalStyle: {
    columns: 2,
    columnGap: '20px',
    padding: '24px',
    areas: [
      { type: 'image', label: '干花区', columnSpan: 1, rowSpan: 2 },
      { type: 'handwriting', label: '手写字', columnSpan: 1, rowSpan: 1 },
      { type: 'text', label: '主文字', columnSpan: 1, rowSpan: 1 },
      { type: 'sticker', label: '树叶装饰', columnSpan: 1, rowSpan: 1, stickerCount: 3 },
      { type: 'tape', label: '纸胶带', columnSpan: 2, rowSpan: 1, tapeCount: 2 },
      { type: 'stamp', label: '植物印章', columnSpan: 1, rowSpan: 1, stampCount: 2 }
    ]
  },

  threeColumnMagazine: {
    columns: 3,
    columnGap: '10px',
    padding: '20px',
    areas: [
      { type: 'image', label: '大图', columnSpan: 2, rowSpan: 2 },
      { type: 'text', label: '侧栏文字', columnSpan: 1, rowSpan: 2 },
      { type: 'handwriting', label: '手写标题', columnSpan: 2, rowSpan: 1 },
      { type: 'sticker', label: '装饰', columnSpan: 1, rowSpan: 1, stickerCount: 3 },
      { type: 'tape', label: '侧边胶带', columnSpan: 1, rowSpan: 1, tapeCount: 1 },
      { type: 'stamp', label: '刊号印章', columnSpan: 2, rowSpan: 1, stampCount: 2 }
    ]
  }
}

export const styleLayoutMap = {
  '复古风': 'twoColumnRightImage',
  '简约风': 'minimalist',
  '可爱风': 'centerFocus',
  '森系': 'naturalStyle',
  '盐系': 'minimalist',
  '甜系': 'centerFocus',
  '暗黑系': 'threeColumnMagazine',
  'ins风': 'twoColumnLeftImage'
}

export const sceneLayoutMap = {
  '日常记录': 'timelineVertical',
  '节日主题': 'centerFocus',
  '旅行手账': 'collageStyle',
  '读书摘抄': 'twoColumnLeftImage',
  '美食记录': 'collageStyle',
  '观影心得': 'twoColumnRightImage',
  '工作计划': 'timelineVertical',
  '习惯打卡': 'threeColumnMagazine'
}

export const getDefaultLayout = (categories = []) => {
  if (!categories || categories.length === 0) {
    return layoutTemplates.twoColumnLeftImage
  }

  for (const cat of categories) {
    if (cat.type === 'style' && styleLayoutMap[cat.name]) {
      return layoutTemplates[styleLayoutMap[cat.name]]
    }
  }

  for (const cat of categories) {
    if (cat.type === 'scene' && sceneLayoutMap[cat.name]) {
      return layoutTemplates[sceneLayoutMap[cat.name]]
    }
  }

  return layoutTemplates.twoColumnLeftImage
}
