export const layoutTemplates = {
  twoColumnLeftImage: {
    columns: 2,
    columnGap: '16px',
    rowGap: '12px',
    padding: '24px',
    whiteSpacePosition: 'bottom',
    whiteSpaceSize: '60px',
    partitionMode: 'fixed',
    partitionRatios: [0.4, 0.6],
    imageTextLayout: 'left-image-right-text',
    layoutDirection: 'vertical',
    gridStyle: 'dashed',
    areas: [
      { type: 'image', label: '图片区', columnSpan: 1, rowSpan: 2, areaRatio: 0.4, imageTextRelation: 'left-image-right-text' },
      { type: 'text', label: '主文字区', columnSpan: 1, rowSpan: 1, areaRatio: 0.3, imageTextRelation: 'left-image-right-text', textAlign: 'left', textVerticalAlign: 'top' },
      { type: 'sticker', label: '装饰贴纸', columnSpan: 1, rowSpan: 1, stickerCount: 4, areaRatio: 0.3, decorativeElements: [{ elementType: 'sticker', elementName: '装饰贴纸', quantity: 4, position: 'corner' }] }
    ]
  },

  twoColumnRightImage: {
    columns: 2,
    columnGap: '16px',
    rowGap: '12px',
    padding: '24px',
    whiteSpacePosition: 'top',
    whiteSpaceSize: '40px',
    partitionMode: 'fixed',
    partitionRatios: [0.55, 0.45],
    imageTextLayout: 'left-text-right-image',
    layoutDirection: 'vertical',
    gridStyle: 'solid',
    areas: [
      { type: 'text', label: '主文字区', columnSpan: 1, rowSpan: 2, areaRatio: 0.55, imageTextRelation: 'left-text-right-image', textAlign: 'left', textVerticalAlign: 'top' },
      { type: 'image', label: '图片区', columnSpan: 1, rowSpan: 1, areaRatio: 0.3, imageTextRelation: 'left-text-right-image' },
      { type: 'sticker', label: '贴纸区', columnSpan: 1, rowSpan: 1, stickerCount: 3, areaRatio: 0.15, decorativeElements: [{ elementType: 'sticker', elementName: '贴纸', quantity: 3, position: 'scattered' }] }
    ]
  },

  timelineVertical: {
    columns: 1,
    columnGap: '0',
    rowGap: '16px',
    padding: '20px',
    whiteSpacePosition: 'left',
    whiteSpaceSize: '30px',
    partitionMode: 'flow',
    partitionRatios: [0.15, 0.25, 0.6],
    imageTextLayout: 'timeline',
    layoutDirection: 'vertical',
    gridStyle: 'dotted',
    areas: [
      { type: 'tape', label: '日期胶带', columnSpan: 1, rowSpan: 1, tapeCount: 1, areaRatio: 0.05, decorativeElements: [{ elementType: 'tape', elementName: '日期胶带', quantity: 1, position: 'top' }] },
      { type: 'handwriting', label: '标题手写字', columnSpan: 1, rowSpan: 1, areaRatio: 0.15, imageTextRelation: 'timeline' },
      { type: 'sticker', label: '便签贴纸', columnSpan: 1, rowSpan: 1, stickerCount: 3, areaRatio: 0.25, decorativeElements: [{ elementType: 'sticker', elementName: '便签贴纸', quantity: 3, position: 'left' }] },
      { type: 'text', label: '记录区', columnSpan: 1, rowSpan: 2, areaRatio: 0.6, imageTextRelation: 'timeline', textAlign: 'left', textVerticalAlign: 'top' },
      { type: 'stamp', label: '打卡印章', columnSpan: 1, rowSpan: 1, stampCount: 2, areaRatio: 0.05, decorativeElements: [{ elementType: 'stamp', elementName: '打卡印章', quantity: 2, position: 'corner' }] }
    ]
  },

  centerFocus: {
    columns: 2,
    columnGap: '12px',
    rowGap: '14px',
    padding: '28px',
    whiteSpacePosition: 'around',
    whiteSpaceSize: '50px',
    partitionMode: 'center',
    partitionRatios: [0.2, 0.6, 0.2],
    imageTextLayout: 'center-text-with-decoration',
    layoutDirection: 'vertical',
    gridStyle: 'double',
    areas: [
      { type: 'tape', label: '顶部装饰胶带', columnSpan: 2, rowSpan: 1, tapeCount: 2, areaRatio: 0.1, decorativeElements: [{ elementType: 'tape', elementName: '装饰胶带', quantity: 2, position: 'top' }] },
      { type: 'sticker', label: '顶部装饰', columnSpan: 2, rowSpan: 1, stickerCount: 5, areaRatio: 0.15, imageTextRelation: 'center-text-with-decoration', decorativeElements: [{ elementType: 'sticker', elementName: '装饰贴纸', quantity: 5, position: 'scattered' }] },
      { type: 'handwriting', label: '手写字标题', columnSpan: 2, rowSpan: 1, areaRatio: 0.1, imageTextRelation: 'center-text-with-decoration' },
      { type: 'text', label: '主内容区', columnSpan: 2, rowSpan: 2, areaRatio: 0.4, imageTextRelation: 'center-text-with-decoration', textAlign: 'center', textVerticalAlign: 'middle' },
      { type: 'image', label: '配图区', columnSpan: 1, rowSpan: 1, areaRatio: 0.15, imageTextRelation: 'center-text-with-decoration' },
      { type: 'sticker', label: '小贴纸', columnSpan: 1, rowSpan: 1, stickerCount: 4, areaRatio: 0.15, decorativeElements: [{ elementType: 'sticker', elementName: '小贴纸', quantity: 4, position: 'corner' }] },
      { type: 'stamp', label: '装饰印章', columnSpan: 2, rowSpan: 1, stampCount: 3, areaRatio: 0.05, decorativeElements: [{ elementType: 'stamp', elementName: '装饰印章', quantity: 3, position: 'bottom' }] }
    ]
  },

  collageStyle: {
    columns: 3,
    columnGap: '8px',
    rowGap: '8px',
    padding: '16px',
    whiteSpacePosition: 'scattered',
    whiteSpaceSize: '20px',
    partitionMode: 'masonry',
    partitionRatios: [0.3, 0.35, 0.35],
    imageTextLayout: 'mixed-collage',
    layoutDirection: 'mixed',
    gridStyle: 'none',
    areas: [
      { type: 'image', label: '照片1', columnSpan: 1, rowSpan: 1, areaRatio: 0.2, imageTextRelation: 'mixed-collage' },
      { type: 'text', label: '文字', columnSpan: 1, rowSpan: 1, areaRatio: 0.15, imageTextRelation: 'mixed-collage', textAlign: 'left', textVerticalAlign: 'top' },
      { type: 'sticker', label: '票根', columnSpan: 1, rowSpan: 1, stickerCount: 2, areaRatio: 0.1, decorativeElements: [{ elementType: 'sticker', elementName: '票根贴纸', quantity: 2, position: 'scattered' }] },
      { type: 'tape', label: '胶带装饰', columnSpan: 2, rowSpan: 1, tapeCount: 2, areaRatio: 0.05, decorativeElements: [{ elementType: 'tape', elementName: '复古胶带', quantity: 2, position: 'border' }] },
      { type: 'text', label: '记录', columnSpan: 2, rowSpan: 1, areaRatio: 0.2, imageTextRelation: 'mixed-collage', textAlign: 'left', textVerticalAlign: 'top' },
      { type: 'image', label: '照片2', columnSpan: 1, rowSpan: 2, areaRatio: 0.25, imageTextRelation: 'mixed-collage' },
      { type: 'sticker', label: '贴纸', columnSpan: 2, rowSpan: 1, stickerCount: 4, areaRatio: 0.1, decorativeElements: [{ elementType: 'sticker', elementName: '纪念贴纸', quantity: 4, position: 'scattered' }] },
      { type: 'stamp', label: '纪念印章', columnSpan: 1, rowSpan: 1, stampCount: 2, areaRatio: 0.05, decorativeElements: [{ elementType: 'stamp', elementName: '景点印章', quantity: 2, position: 'corner' }] },
      { type: 'handwriting', label: '手写备注', columnSpan: 2, rowSpan: 1, areaRatio: 0.1, imageTextRelation: 'mixed-collage' }
    ]
  },

  minimalist: {
    columns: 1,
    columnGap: '0',
    rowGap: '20px',
    padding: '40px',
    whiteSpacePosition: 'all-around',
    whiteSpaceSize: '80px',
    partitionMode: 'minimal',
    partitionRatios: [0.15, 0.75, 0.1],
    imageTextLayout: 'text-only-with-minimal-decoration',
    layoutDirection: 'vertical',
    gridStyle: 'none',
    areas: [
      { type: 'text', label: '标题', columnSpan: 1, rowSpan: 1, areaRatio: 0.15, imageTextRelation: 'text-only-with-minimal-decoration', textAlign: 'left', textVerticalAlign: 'top' },
      { type: 'handwriting', label: '手写日期', columnSpan: 1, rowSpan: 1, areaRatio: 0.1, imageTextRelation: 'text-only-with-minimal-decoration' },
      { type: 'text', label: '正文', columnSpan: 1, rowSpan: 3, areaRatio: 0.75, imageTextRelation: 'text-only-with-minimal-decoration', textAlign: 'left', textVerticalAlign: 'top' },
      { type: 'sticker', label: '小装饰', columnSpan: 1, rowSpan: 1, stickerCount: 2, areaRatio: 0.05, decorativeElements: [{ elementType: 'sticker', elementName: '简约贴纸', quantity: 2, position: 'corner' }] },
      { type: 'stamp', label: '日期印章', columnSpan: 1, rowSpan: 1, stampCount: 1, areaRatio: 0.05, decorativeElements: [{ elementType: 'stamp', elementName: '日期印章', quantity: 1, position: 'corner' }] }
    ]
  },

  naturalStyle: {
    columns: 2,
    columnGap: '20px',
    rowGap: '16px',
    padding: '24px',
    whiteSpacePosition: 'organic',
    whiteSpaceSize: '50px',
    partitionMode: 'asymmetric',
    partitionRatios: [0.45, 0.55],
    imageTextLayout: 'left-nature-right-text',
    layoutDirection: 'vertical',
    gridStyle: 'wavy',
    areas: [
      { type: 'image', label: '干花区', columnSpan: 1, rowSpan: 2, areaRatio: 0.45, imageTextRelation: 'left-nature-right-text' },
      { type: 'handwriting', label: '手写字', columnSpan: 1, rowSpan: 1, areaRatio: 0.2, imageTextRelation: 'left-nature-right-text' },
      { type: 'text', label: '主文字', columnSpan: 1, rowSpan: 1, areaRatio: 0.35, imageTextRelation: 'left-nature-right-text', textAlign: 'left', textVerticalAlign: 'top' },
      { type: 'sticker', label: '树叶装饰', columnSpan: 1, rowSpan: 1, stickerCount: 3, areaRatio: 0.15, decorativeElements: [{ elementType: 'sticker', elementName: '树叶贴纸', quantity: 3, position: 'scattered' }] },
      { type: 'tape', label: '纸胶带', columnSpan: 2, rowSpan: 1, tapeCount: 2, areaRatio: 0.05, decorativeElements: [{ elementType: 'tape', elementName: '复古胶带', quantity: 2, position: 'border' }] },
      { type: 'stamp', label: '植物印章', columnSpan: 1, rowSpan: 1, stampCount: 2, areaRatio: 0.05, decorativeElements: [{ elementType: 'stamp', elementName: '植物印章', quantity: 2, position: 'corner' }] }
    ]
  },

  threeColumnMagazine: {
    columns: 3,
    columnGap: '10px',
    rowGap: '10px',
    padding: '20px',
    whiteSpacePosition: 'editorial',
    whiteSpaceSize: '30px',
    partitionMode: 'editorial',
    partitionRatios: [0.25, 0.5, 0.25],
    imageTextLayout: 'magazine-layout',
    layoutDirection: 'mixed',
    gridStyle: 'solid',
    areas: [
      { type: 'image', label: '大图', columnSpan: 2, rowSpan: 2, areaRatio: 0.5, imageTextRelation: 'magazine-layout' },
      { type: 'text', label: '侧栏文字', columnSpan: 1, rowSpan: 2, areaRatio: 0.25, imageTextRelation: 'magazine-layout', textAlign: 'left', textVerticalAlign: 'top' },
      { type: 'handwriting', label: '手写标题', columnSpan: 2, rowSpan: 1, areaRatio: 0.2, imageTextRelation: 'magazine-layout' },
      { type: 'sticker', label: '装饰', columnSpan: 1, rowSpan: 1, stickerCount: 3, areaRatio: 0.05, decorativeElements: [{ elementType: 'sticker', elementName: '装饰贴纸', quantity: 3, position: 'scattered' }] },
      { type: 'tape', label: '侧边胶带', columnSpan: 1, rowSpan: 1, tapeCount: 1, areaRatio: 0.05, decorativeElements: [{ elementType: 'tape', elementName: '侧边胶带', quantity: 1, position: 'divider' }] },
      { type: 'stamp', label: '刊号印章', columnSpan: 2, rowSpan: 1, stampCount: 2, areaRatio: 0.05, decorativeElements: [{ elementType: 'stamp', elementName: '刊号印章', quantity: 2, position: 'corner' }] }
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
