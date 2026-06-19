export const ELEMENT_CATEGORIES = [
  {
    code: 1,
    key: 'sticker',
    name: '贴纸',
    icon: '✨',
    desc: '装饰贴纸、文字贴纸、插画贴纸等',
    color: '#ff6b9d',
    bgGradient: 'linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%)',
    borderColor: '#ffb6c1'
  },
  {
    code: 2,
    key: 'washi_tape',
    name: '胶带',
    icon: '🎀',
    desc: '和纸胶带、装饰胶带、分割线胶带',
    color: '#9b59b6',
    bgGradient: 'linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%)',
    borderColor: '#d4a5e8'
  },
  {
    code: 3,
    key: 'photo',
    name: '照片',
    icon: '📷',
    desc: '打印照片、拍立得、冲印相片',
    color: '#3498db',
    bgGradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    borderColor: '#b3d9ff'
  },
  {
    code: 4,
    key: 'handwriting',
    name: '手写字',
    icon: '✍️',
    desc: '手写文字、艺术字、标题字',
    color: '#27ae60',
    bgGradient: 'linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%)',
    borderColor: '#b2f0bb'
  },
  {
    code: 5,
    key: 'border',
    name: '边框',
    icon: '🖼️',
    desc: '装饰边框、花边、分割线装饰',
    color: '#f39c12',
    bgGradient: 'linear-gradient(135deg, #fddb92 0%, #d1fdff 100%)',
    borderColor: '#ffe0b3'
  }
]

export const getElementCategoryByCode = (code) => {
  return ELEMENT_CATEGORIES.find(cat => cat.code === code) || ELEMENT_CATEGORIES[0]
}
