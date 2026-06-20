import request from '@/utils/request'

export const getSceneTaskList = (sceneCategoryId) => {
  return request({
    url: '/scene-tasks',
    method: 'get',
    params: { sceneCategoryId }
  })
}

export const getSceneTaskCheckList = (workId, sceneCategoryId) => {
  return request({
    url: '/work-scene-task-check/list',
    method: 'get',
    params: { workId, sceneCategoryId }
  })
}
