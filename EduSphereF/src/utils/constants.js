// 角色常量定义
export const ROLES = {
  ADMIN: 'admin',
  TEACHER: 'teacher', 
  STUDENT: 'student'
}

// 角色显示名称映射
export const ROLE_NAMES = {
  [ROLES.ADMIN]: '管理员',
  [ROLES.TEACHER]: '教师',
  [ROLES.STUDENT]: '学生'
}

// 课程难度映射
export const COURSE_LEVELS = {
  BEGINNER: 'beginner',
  INTERMEDIATE: 'intermediate',
  ADVANCED: 'advanced'
}

// 课程难度显示名称
export const COURSE_LEVEL_NAMES = {
  [COURSE_LEVELS.BEGINNER]: '入门',
  [COURSE_LEVELS.INTERMEDIATE]: '进阶',
  [COURSE_LEVELS.ADVANCED]: '高级'
}

// 订单状态
export const ORDER_STATUS = {
  PENDING: 'pending',
  PAID: 'paid',
  CANCELLED: 'cancelled',
  REFUNDED: 'refunded'
}

// 订单状态显示名称
export const ORDER_STATUS_NAMES = {
  [ORDER_STATUS.PENDING]: '待支付',
  [ORDER_STATUS.PAID]: '已支付',
  [ORDER_STATUS.CANCELLED]: '已取消',
  [ORDER_STATUS.REFUNDED]: '已退款'
}
