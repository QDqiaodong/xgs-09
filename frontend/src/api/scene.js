import request from '@/utils/request'

export const getSceneTaskList = (sceneCategoryId) => {
  return request({
    url: '/scene-tasks',
    method: 'get',
    params: { sceneCategoryId }
  })
}
