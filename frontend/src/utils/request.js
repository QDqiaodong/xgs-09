import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getCurrentUserId } from './userStore'

const service = axios.create({
  baseURL: '/api',
  timeout: 30000
})

service.interceptors.request.use(
  config => {
    const userId = getCurrentUserId()
    if (userId) {
      config.headers['X-User-Id'] = userId
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      if (res.code !== 404) {
        ElMessage.error(res.message || '请求失败')
      }
      const err = new Error(res.message || '请求失败')
      err.code = res.code
      err.data = res.data
      return Promise.reject(err)
    }
    return res
  },
  error => {
    if (!error.__silent) {
      ElMessage.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default service
