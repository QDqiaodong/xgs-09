import request from '@/utils/request'

export const getLatestWorks = (pageNum = 1, pageSize = 10) => {
  return request({
    url: '/works/latest',
    method: 'get',
    params: { pageNum, pageSize }
  })
}

export const getHotWorks = (pageNum = 1, pageSize = 10) => {
  return request({
    url: '/works/hot',
    method: 'get',
    params: { pageNum, pageSize }
  })
}

export const getWorkDetail = (id, userId) => {
  return request({
    url: `/works/${id}`,
    method: 'get',
    params: { userId }
  })
}

export const getWorkList = (params) => {
  return request({
    url: '/works/list',
    method: 'get',
    params
  })
}

export const publishWork = (data) => {
  return request({
    url: '/works',
    method: 'post',
    data
  })
}

export const updateWorkStatus = (id, status) => {
  return request({
    url: `/works/${id}/status`,
    method: 'put',
    params: { status }
  })
}

export const getUserWorks = (userId, pageNum = 1, pageSize = 10) => {
  return request({
    url: `/works/user/${userId}`,
    method: 'get',
    params: { pageNum, pageSize }
  })
}

export const getUserWorkStats = (userId) => {
  return request({
    url: `/works/user/${userId}/stats`,
    method: 'get'
  })
}
