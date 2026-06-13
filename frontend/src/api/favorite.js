import request from '@/utils/request'

export const toggleFavorite = (userId, workId) => {
  return request({
    url: '/favorites/toggle',
    method: 'post',
    params: { userId, workId }
  })
}

export const checkFavorite = (userId, workId) => {
  return request({
    url: '/favorites/check',
    method: 'get',
    params: { userId, workId }
  })
}

export const getUserFavorites = (userId, pageNum = 1, pageSize = 10) => {
  return request({
    url: `/favorites/user/${userId}`,
    method: 'get',
    params: { pageNum, pageSize }
  })
}
