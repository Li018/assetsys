<script setup name="user">
import { reactive, ref } from 'vue'
import {
  CircleCloseFilled,
  Delete,
  Download,
  Edit,
  InfoFilled,
  Plus,
  Refresh,
  Search,
  Upload
} from '@element-plus/icons-vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

import downloadExcel from '@/utils/downloads'

// 名称
const name = ref('')
// 弹框头部名称
const headerTitle = ref()
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

// 表单数据定义
const form = reactive({
  id: undefined,
  name: undefined,
  content: undefined,
  createTime: undefined
})

// 表单样式
const formSize = ref('default')
// 表单ref标识数据
const ruleFormRef = ref()
// 自定义校验规则

// 表单校验规则
const rules = reactive({
  id: [{ required: true, message: '必选项不能为空', trigger: 'blur' }],
  name: [{ required: true, message: '必选项不能为空', trigger: 'blur' }],
  content: [{ required: true, message: '必选项不能为空', trigger: 'blur' }],
  createTime: [{ required: true, message: '必选项不能为空', trigger: 'blur' }]
})

//新增方法
const handleAdd = async () => {
  headerTitle.value = reactive('新增数据')
  dialogVisible.value = true
}
// 修改方法
const handleUpdate = async (id) => {
  dialogVisible.value = true
  headerTitle.value = reactive('编辑数据')
  const res = await request.get(`/notice/${id}`)
  Object.assign(form, res.data)
}
// 单个删除方法
const handleDel = async (id) => {
  await request.delete(`/notice/${id}`)
  ElMessage({
    showClose: true,
    message: '删除成功',
    type: 'success'
  })
  await load()
}
// 批量删除方法
const handleBatchDel = async () => {
  const ids = []
  multipleSelection.value.forEach((row) => {
    ids.push(row.id)
  })
  await request.delete(`/notice/batch/${ids}`)
  ElMessage({
    showClose: true,
    message: '批量删除成功',
    type: 'success'
  })
  await load()
}

// 分页查询方法（初始化方法，页面加载成功以后就调用的方法）
const load = async () => {
  const res = await request.get('/notice/page', {
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

// 清空查询数据重置方法
const handleReset = () => {
  name.value = ''
  load()
}
// 修改每页展示的数据量方法
const handleSizeChange = (size) => {
  pageSize.value = size
  load()
}
// 翻页方法
const handleCurrentChange = (current) => {
  pageNum.value = current
  load()
}
// 多选按钮处理方法
const handleSelectionChange = (val) => {
  multipleSelection.value = val
  disabled.value = val.length === 0
}

// 关闭弹框提示方法
const handleClose = (done) => {
  ElMessageBox.confirm('确定关闭窗口?')
    .then(() => {
      handleResetForm(ruleFormRef.value)
    })
    .then(() => {
      done()
    })
    .catch(() => {
      // catch error
    })
}
// 提交表单校验方法
const handleSumbmitForm = async (formEl) => {
  if (!formEl) return
  await formEl.validate(async (valid, fields) => {
    if (valid) {
      await request({
        method: form.id ? 'put' : 'post',
        url: form.id ? `/notice/${form.id}` : '/notice',
        data: form
      })
      ElMessage({
        showClose: true,
        message: '操作成功',
        type: 'success'
      })
      await handleResetForm(formEl)
    } else {
      console.log('error submit!', fields)
    }
  })
}
// 批量导入读数据写到后端数据库中
const beforeBatchUpload = async (file) => {
  let fd = new FormData()
  fd.append('file', file)
  await request.post('/notice/batch/upload', fd, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
  ElMessage({
    showClose: true,
    message: '上传成功',
    type: 'success'
  })
  await load()
}
// 批量导出方法
const handleBatchExport = async () => {
  const ids = multipleSelection.value.map((row) => row.id)
  const res = await request(
    {
      url: `/notice/batch/export/${ids}`,
      method: 'get',
      responseType: 'blob'
    } //在请求中加上这一行，特别重要
  )
  downloadExcel(res, '导出数据表')
}
// 取消弹框方法
const handleResetForm = async (formEl) => {
  if (!formEl) return
  formEl.resetFields()

  dialogVisible.value = false
  await load()
}

// 文件下载
const dowload = async (url) => {
  window.open(url)
}
</script>
<template>
  <!--  编辑弹框-->
  <el-dialog
    :modelValue="dialogVisible"
    width="30%"
    :show-close="false"
    :before-close="handleClose"
  >
    <template #header="{ close, titleId, titleClass }">
      <div class="my-header">
        <h4 :id="titleId" :class="titleClass">{{ headerTitle }}</h4>
        <el-button type="danger" plain size="small" :icon="CircleCloseFilled" @click="handleClose">
          关闭弹框
        </el-button>
      </div>
    </template>
    <el-form
      ref="ruleFormRef"
      :model="form"
      :rules="rules"
      label-width="120px"
      class="ruleForm"
      :size="formSize"
      status-icon
    >
      <el-form-item label="标题" prop="name">
        <el-input v-model="form.name" />
      </el-form-item>
      <el-form-item label="内容" prop="content">
        <el-input v-model="form.content" type="textarea" />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleResetForm(ruleFormRef)">取消</el-button>
        <el-button type="primary" @click="handleSumbmitForm(ruleFormRef)"> 提交 </el-button>
      </span>
    </template>
  </el-dialog>

  <el-row>
    <!--  分页查询表单按钮-->
    <el-col>
      <el-form :inline="true">
        <!--        查询输入框-->
        <el-form-item>
          <el-input v-model="name" placeholder="点击输入用户名称"></el-input>
        </el-form-item>
        <!--        查询按钮 and 重置按钮-->
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="load">查询</el-button>
          <el-button type="warning" :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-col>
    <!-- 新增和批量按钮 -->
    <el-col style="margin-bottom: 10px; display: flex; gap: 10px">
      <!--   新增   -->
      <div v-permission="'sys:notice:add'">
        <el-button type="primary" :icon="Plus" @click="handleAdd" size="small" plain
          >新增
        </el-button>
      </div>
      <!--   批量导入   -->
      <div v-permission="'sys:notice:batch:export'">
        <el-upload action="" :before-upload="beforeBatchUpload" :show-file-list="true">
          <el-button type="success" :icon="Upload" size="small" plain>批量导入</el-button>
        </el-upload>
      </div>
      <!--   批量删除   -->
      <div v-permission="'sys:notice:batch:delete'">
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

      <div v-permission="'sys:notice:batch:export'">
        <el-button
          type="warning"
          :icon="Download"
          :disabled="disabled"
          size="small"
          @click="handleBatchExport"
          plain
          >批量导出
        </el-button>
      </div>
    </el-col>
    <!--  表格页面-->
    <el-col>
      <el-table
        :data="tableData"
        style="width: 100%"
        :header-cell-style="{ background: '#eef1f6', color: '#606266' }"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" align="center" />
        <el-table-column prop="name" label="标题" align="center" />
        <el-table-column prop="content" label="内容" align="center" />
        <el-table-column prop="createTime" label="创建时间" align="center" />

        <el-table-column label="操作" align="center">
          <template #default="scope">
            <div style="display: flex; justify-content: center; gap: 10px">
              <div>
                <el-button
                  type="primary"
                  :icon="Edit"
                  size="small"
                  @click="handleUpdate(scope.row.id)"
                  v-permission="'sys:notice:update'"
                  >编辑
                </el-button>
              </div>
              <div v-permission="'sys:notice:delete'">
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
      <!--      分页按钮-->
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

.my-header {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}
</style>
