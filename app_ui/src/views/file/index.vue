<script setup name="user">
import { ref } from 'vue'
import { Delete, Download, Edit, InfoFilled, Plus, Refresh, Search } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import UserForm from '@/views/user/form/index.vue'
import downloadExcel from '@/utils/downloads'
import downloads from '@/utils/downloads'
// 名称
const name = ref('')
// 当前页数
const pageNum = ref(1)
// 每页展示量
const pageSize = ref(10)
// 分页数据总数
const total = ref(0)
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
//新增
const handleAdd = async () => {
  dialogVisible.value = true
  await childComp.value.handleGetTrees()
}
// 修改
const handleUpdate = async (id) => {
  dialogVisible.value = true
  await childComp.value.handleGetForm(id)
}
// 单个删除
const handleDel = async (id) => {
  await request.delete(`/file/${id}`)
  ElMessage({
    showClose: true,
    message: '删除成功',
    type: 'success'
  })
  await load()
}
// 批量删除
const handleBatchDel = async () => {
  const ids = []
  multipleSelection.value.forEach((row) => {
    ids.push(row.id)
  })
  await request.delete(`/file/batch/${ids}`)
  ElMessage({
    showClose: true,
    message: '批量删除成功',
    type: 'success'
  })
  await load()
}

// 分页查询
const load = async () => {
  const res = await request.get('/file/page', {
    params: {
      name: name.value,
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
  })
  pageNum.value = res.data.current
  pageSize.value = res.data.size
  total.value = res.data.total
  tableData.value = res.data.records
}
// 加载页面初始化调用load方法
load()
// 清空查询数据重置
const handleReset = () => {
  name.value = ''
  load()
}
// 修改每页展示的数据量
const handleSizeChange = (size) => {
  pageSize.value = size
  load()
}
// 翻页方法
const handleCurrentChange = (current) => {
  pageNum.value = current
  load()
}
// 多选按钮处理
const handleSelectionChange = (val) => {
  multipleSelection.value = val
  disabled.value = val.length === 0
}
//子组件传值给父组件,更改显示状态
const handlechangeDialog = (value) => {
  dialogVisible.value = value
  load()
}
// 文件下载
const download = (url) => {
  window.open(url)
}
// 使用上传文件之前的钩子来进行自定义文件上传
const beforeUpload = async (file) => {
  let fd = new FormData()
  fd.append('file', file)

  await request.post('/file/upload', fd, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
  // 上传成功，返回图片完整路径
  ElMessage({
    showClose: true,
    message: '文件上传成功',
    type: 'success'
  })
  await load()

  // 阻止默认的上传操作, 就不会产生如下图action为空报404错误的情况
  return false
}
</script>
<template>
  <el-row>
    <!--  分页查询表单按钮-->
    <el-col>
      <el-form :inline="true">
        <el-form-item>
          <el-input v-model="name" placeholder="点击输入文件名称"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="load">查询</el-button>
          <el-button type="warning" :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-col>
    <!-- 批量按钮 -->
    <el-col style="margin-bottom: 10px; display: flex; gap: 10px">
      <div v-permission="'sys:user:add'">
        <el-upload action="" :before-upload="beforeUpload" :show-file-list="false">
          <el-button type="primary" :icon="Plus" size="small" plain>上传文件 </el-button>
        </el-upload>
      </div>
      <div v-permission="'sys:user:batch:delete'">
        <el-popconfirm
          confirm-button-text="确定"
          cancel-button-text="取消"
          :icon="InfoFilled"
          icon-color="#626AEF"
          title="确认要批量删除吗？"
          @confirm="handleBatchDel"
          @cancel="load"
        >
          <template #reference>
            <el-button type="danger" :disabled="disabled" :icon="Delete" size="small" plain
              >批量删除
            </el-button>
          </template>
        </el-popconfirm>
      </div>
    </el-col>
    <!--  分页页面-->
    <el-col>
      <el-table
        :data="tableData"
        style="width: 100%"
        :header-cell-style="{ background: '#eef1f6', color: '#606266' }"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />

        <el-table-column prop="name" label="文件名称" align="center" />
        <el-table-column prop="type" label="文件类型" align="center" />
        <el-table-column prop="size" label="文件大小(kb)" align="center" />

        <el-table-column prop="md5" label="md5" align="center"> </el-table-column>
        <el-table-column label="操作" align="center">
          <template #default="scope">
            <div style="margin-bottom: 10px; display: flex; gap: 10px">
              <el-button type="primary" :icon="Download" @click="download(scope.row.url)"
                >下载</el-button
              >
              <div v-permission="'sys:user:delete'">
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
      <div class="pagination">
        <el-pagination
          :current-page="pageNum"
          :page-size="pageSize"
          :page-sizes="[10, 20, 30, 50, 100, 500, 1000]"
          small="small"
          layout="total, sizes, prev, pager, next, jumper"
          :total="Number(total)"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
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
