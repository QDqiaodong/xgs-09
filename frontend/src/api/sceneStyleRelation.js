import request from '@/utils/request'

export const getSceneStyleRelationsByScene = (sceneCategoryId) => {
  return request({
    url: '/scene-style-relations/by-scene',
    method: 'get',
    params: { sceneCategoryId }
  })
}

export const getSceneStyleRelationsByStyle = (styleCategoryId) => {
  return request({
    url: '/scene-style-relations/by-style',
    method: 'get',
    params: { styleCategoryId }
  })
}

export const getPrimaryStyleByScene = (sceneCategoryId) => {
  return request({
    url: '/scene-style-relations/primary-style',
    method: 'get',
    params: { sceneCategoryId }
  })
}
