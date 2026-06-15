export const WORK_STATUS = {
  PUBLIC: {
    code: 1,
    desc: '可展示'
  },
  PRIVATE: {
    code: 2,
    desc: '仅本人可见'
  },
  ARCHIVED: {
    code: 3,
    desc: '已归档'
  }
}

export const getWorkStatusDesc = (code) => {
  for (const key in WORK_STATUS) {
    if (WORK_STATUS[key].code === code) {
      return WORK_STATUS[key].desc
    }
  }
  return '未知'
}

export const isPublicStatus = (code) => {
  return WORK_STATUS.PUBLIC.code === code
}

export const isPrivateStatus = (code) => {
  return WORK_STATUS.PRIVATE.code === code
}

export const isArchivedStatus = (code) => {
  return WORK_STATUS.ARCHIVED.code === code
}
