import request from '@/utils/request'

export const getLayoutTemplateList = (templateType, categoryIds) => {
  return request({
    url: '/layout-templates',
    method: 'get',
    params: { templateType, categoryIds }
  })
}

export const getLayoutTemplateById = (id) => {
  return request({
    url: `/layout-templates/${id}`,
    method: 'get'
  })
}

export const getLayoutTemplateByCode = (templateCode) => {
  return request({
    url: `/layout-templates/code/${templateCode}`,
    method: 'get'
  })
}

export const incrementTemplateUseCount = (id) => {
  return request({
    url: `/layout-templates/${id}/use`,
    method: 'post'
  })
}
