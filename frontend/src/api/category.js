import request from '@/utils/request'

export const getCategoryList = (type) => {
  return request({
    url: '/categories',
    method: 'get',
    params: { type }
  })
}

export const getWorkCategories = (workId) => {
  return request({
    url: `/categories/work/${workId}`,
    method: 'get'
  })
}
