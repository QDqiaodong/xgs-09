import request from '@/utils/request'

export const getColorPaletteList = (categoryIds) => {
  return request({
    url: '/color-palettes',
    method: 'get',
    params: { categoryIds: categoryIds ? categoryIds.join(',') : undefined }
  })
}

export const getColorPaletteDetail = (id) => {
  return request({
    url: `/color-palettes/${id}`,
    method: 'get'
  })
}

export const useColorPalette = (id) => {
  return request({
    url: `/color-palettes/${id}/use`,
    method: 'post'
  })
}
