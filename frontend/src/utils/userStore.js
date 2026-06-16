const USER_KEY = 'user_info'

export const setCurrentUser = (user) => {
  localStorage.setItem(USER_KEY, JSON.stringify(user))
}

export const getCurrentUser = () => {
  const userStr = localStorage.getItem(USER_KEY)
  if (userStr) {
    try {
      return JSON.parse(userStr)
    } catch (e) {
      return null
    }
  }
  return null
}

export const getCurrentUserId = () => {
  const user = getCurrentUser()
  if (user && user.id) {
    return user.id
  }
  return 1
}

export const clearCurrentUser = () => {
  localStorage.removeItem(USER_KEY)
}
