# 课程状态管理系统

## 🎯 状态流程设计

您说得完全正确！课程状态应该有三个主要阶段：**草稿 → 审核中 → 已发布**，而不是简单的两个状态。

### 📊 状态定义

| 状态 | 英文标识 | 说明 | 颜色标识 |
|------|----------|------|----------|
| 草稿 | `draft` | 教师正在编辑课程内容 | 蓝色 (info) |
| 审核中 | `pending` | 已提交审核，等待管理员审核 | 橙色 (warning) |
| 已发布 | `published` | 审核通过，课程已发布 | 绿色 (success) |
| 已拒绝 | `rejected` | 审核未通过，需要修改 | 红色 (danger) |

## 🔄 状态流转

```
草稿 (draft) 
    ↓ 教师提交审核
审核中 (pending)
    ↓ 管理员审核
    ├─ 通过 → 已发布 (published)
    └─ 拒绝 → 已拒绝 (rejected)
        ↓ 教师修改后重新提交
    草稿 (draft)
```

## 🛠️ 技术实现

### 1. 数据库结构更新

#### 修改前
```sql
status TINYINT DEFAULT 1 COMMENT '状态：1-发布，0-草稿'
```

#### 修改后
```sql
status ENUM('draft', 'pending', 'published', 'rejected') DEFAULT 'draft' 
COMMENT '状态：draft-草稿，pending-审核中，published-已发布，rejected-已拒绝'
```

### 2. 后端API更新

#### 教师操作
- **创建课程**：默认状态为 `draft`
- **提交审核**：状态从 `draft` 变为 `pending`
- **撤回课程**：状态从 `pending` 变为 `draft`

#### 管理员操作
- **审核通过**：状态从 `pending` 变为 `published`
- **审核拒绝**：状态从 `pending` 变为 `rejected`

### 3. 前端界面更新

#### 状态显示
```javascript
// 状态文本映射
const statusMap = {
  'draft': '草稿',
  'pending': '审核中', 
  'published': '已发布',
  'rejected': '已拒绝'
}

// 状态颜色映射
const typeMap = {
  'draft': 'info',      // 蓝色
  'pending': 'warning', // 橙色
  'published': 'success', // 绿色
  'rejected': 'danger'  // 红色
}
```

## 📁 修改的文件

### 数据库文件
1. `database_schema.sql` - 更新表结构定义
2. `update_course_status.sql` - 数据库迁移脚本

### 后端文件
1. `Course.java` - 实体类状态字段类型
2. `TeacherServiceImpl.java` - 教师服务状态逻辑
3. `AdminController.java` - 管理员审核控制器
4. `AdminService.java` - 管理员服务接口
5. `AdminServiceImpl.java` - 管理员服务实现
6. `CourseMapper.java` - 数据访问层接口
7. `CourseMapper.xml` - SQL映射文件

### 前端文件
1. `TeacherDashboard.vue` - 教师工作台状态显示

## 🚀 部署步骤

### 1. 数据库迁移
```sql
-- 执行迁移脚本
source update_course_status.sql
```

### 2. 重启后端服务
- 让数据库修改生效
- 新的状态管理逻辑生效

### 3. 测试状态流转
- 创建课程 → 草稿状态
- 提交审核 → 审核中状态
- 管理员审核 → 已发布/已拒绝状态

## 🎨 用户体验

### 教师视角
- **草稿状态**：可以继续编辑课程内容
- **审核中状态**：等待管理员审核，无法编辑
- **已发布状态**：课程已上线，学生可以购买
- **已拒绝状态**：查看拒绝原因，修改后重新提交

### 管理员视角
- **待审核列表**：查看所有待审核课程
- **审核操作**：通过/拒绝课程
- **拒绝原因**：可以填写拒绝原因

## 🔒 权限控制

### 状态操作权限
- **教师**：只能操作自己的课程
- **管理员**：可以审核所有课程
- **学生**：只能看到已发布的课程

### 状态验证
- 只有 `draft` 状态的课程可以编辑
- 只有 `pending` 状态的课程可以审核
- 只有 `published` 状态的课程可以购买

## 📈 未来扩展

### 计划功能
1. **审核历史**：记录审核过程和原因
2. **批量审核**：支持批量通过/拒绝
3. **审核通知**：审核结果邮件通知
4. **审核统计**：审核效率统计报表

### 技术优化
1. **状态机**：使用状态机模式管理状态流转
2. **事件驱动**：状态变更时触发相应事件
3. **审计日志**：记录所有状态变更操作
4. **消息队列**：异步处理审核通知

现在课程状态管理更加完善了！支持完整的审核流程，确保课程质量。🎉
