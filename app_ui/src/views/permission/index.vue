<script setup>
import { nextTick, ref } from 'vue'
import { Delete, Edit, InfoFilled, Plus, Sort } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import PermissionForm from '@/views/permission/form/index.vue'

// 表格数据
const tableData = ref([])
// 批量选择的数组
const multipleSelection = ref([])
// 是否禁用按钮
const disabled = ref(true)
// 是否展示弹框
const dialogVisible = ref(false)
// 获取子组件对象
const childComp = ref(null)
const refreshTree = ref(true)
const isExpandALl = ref(false)

//新增角色
const handleAdd = async () => {
  dialogVisible.value = true
  await childComp.value.handleGetTrees()
}
// 修改角色
const handleUpdate = async (id) => {
  dialogVisible.value = true
  await childComp.value.handleGetForm(id)
}
// 删除角色
const handleDel = async (id) => {
  await request.delete(`/permission/${id}`)
  ElMessage({
    showClose: true,
    message: '删除成功',
    type: 'success'
  })
  await load()
}

// 分页查询角色
const load = async () => {
  const res = await request.get('/permission/page')
  tableData.value = res.data
}
// 加载页面初始化调用load方法
load()
// 新增子级路由
const handleAddChildren = (id) => {
  dialogVisible.value = true
  childComp.value.handleAddChildren(id)
}
//自定义方法接收子级路由传递过来的值
const handlechangeDialog = (value) => {
  dialogVisible.value = value
  load()
}
// 展开折叠处理方案
const handleDefaultExpandAll = () => {
  isExpandALl.value = !isExpandALl.value
  refreshTree.value = false
  nextTick(() => {
    refreshTree.value = true
  })
}
</script>

<template>
  <el-row>
    <!--  编辑弹框-->
    <PermissionForm
      :dialogVisible="dialogVisible"
      @changeDialog="handlechangeDialog"
      ref="childComp"
    ></PermissionForm>
    <!-- 批量按钮 -->
    <el-col style="margin-bottom: 10px; display: flex; gap: 10px">
      <div v-permission="'sys:permission:add'">
        <el-button type="primary" plain size="small" :icon="Plus" @click="handleAdd">
          新增
        </el-button>
      </div>

      <div>
        <el-button type="info" :icon="Sort" @click="handleDefaultExpandAll" size="small" plain
          >展开/折叠
        </el-button>
      </div>
    </el-col>
    <!--  分页页面-->
    <el-col>
      <el-table
        :data="tableData"
        style="width: 100%"
        row-key="id"
        :header-cell-style="{ background: '#eef1f6', color: '#606266' }"
        v-if="refreshTree"
        :default-expand-all="isExpandALl"
        :tree-props="{ children: 'children' }"
      >
        <el-table-column prop="title" label="菜单名称" align="center" />
        <el-table-column prop="icon" label="图标" width="55" align="center">
          <template #default="scope">
            <el-icon v-if="scope.row.icon">
              <component :is="scope.row.icon"></component>
            </el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="orderNum" label="排序" width="55" align="center" />
        <el-table-column prop="perms" label="权限标识" width="240" />
        <el-table-column prop="component" label="组件名称" />
        <el-table-column prop="path" label="访问路径" />
        <el-table-column prop="createTime" label="创建时间" width="180" align="center" />

        <el-table-column prop="hidden" label="显示状态" align="center">
          <template #default="scope">
            <div v-if="scope.row.menuType != 3">
              <el-tag class="ml-2" type="success" v-if="scope.row.hidden === false">正常</el-tag>
              <el-tag class="ml-2" type="danger" v-if="scope.row.hidden === true">隐藏</el-tag>
            </div>
            <div v-else style="display: none">
              <el-tag class="ml-2" type="success" v-if="scope.row.hidden === false">正常</el-tag>
              <el-tag class="ml-2" type="danger" v-if="scope.row.hidden === true">隐藏</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="statu" label="菜单状态" align="center">
          <template #default="scope">
            <el-tag type="success" v-if="scope.row.statu === 1">正常</el-tag>
            <el-tag type="danger" v-if="scope.row.statu === 0">禁用</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="menuType" label="菜单类型" align="center">
          <template #default="scope">
            <el-tag type="success" v-if="scope.row.menuType === 1">目录</el-tag>
            <el-tag type="warning" v-if="scope.row.menuType === 2">菜单</el-tag>
            <el-tag type="info" v-if="scope.row.menuType === 3">按钮</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300">
          <template #default="scope">
            <div style="display: flex; gap: 10px">
              <div v-permission="'sys:permission:update'">
                <el-button
                  type="primary"
                  :icon="Edit"
                  size="small"
                  @click="handleUpdate(scope.row.id)"
                  >编辑
                </el-button>
              </div>
              <div v-permission="'sys:permission:update'">
                <el-button
                  type="warning"
                  v-if="scope.row.menuType != 3"
                  :icon="Plus"
                  size="small"
                  @click="handleAddChildren(scope.row.id)"
                  >新增
                </el-button>
              </div>
              <div v-permission="'sys:permission:delete'">
                <el-popconfirm
                  confirm-button-text="确定"
                  cancel-button-text="取消"
                  :icon="InfoFilled"
                  icon-color="#626AEF"
                  title="确认要删除吗？"
                  @confirm="handleDel(scope.row.id)"
                  @cancel="load"
                >
                  <template #reference>
                    <el-button type="danger" :icon="Delete" size="small">删除</el-button>
                  </template>
                </el-popconfirm>
              </div>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-col>
  </el-row>
</template>

<style scoped lang="scss">
.el-form--inline .el-form-item {
  margin-right: 10px;
}

.form_header {
  background-color: #666666;
}

.pagination {
  margin-top: 10px;
}

.el-button {
  height: 28px;
  border-radius: 3px;
}
</style>
