<script setup name="user">
  import {reactive, ref} from 'vue'
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
  import {ElMessage, ElMessageBox} from 'element-plus'

  import downloadExcel from '@/utils/downloads'
  import {MdEditor} from 'md-editor-v3';
  import 'md-editor-v3/lib/style.css';
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

  const tasklist = ref([])





  // 表单数据定义
  const form = reactive({
    id:undefined,
    userid:undefined,
    taskid:undefined,
    finishstatus:undefined,
    finishtime:undefined,
    submitscannum:undefined,
    submitexploitnum:undefined,
    submitmovenum:undefined,
    taskstatus:undefined,
    taskname:undefined,
  })

  // 表单样式
  const formSize = ref('default')
  // 表单ref标识数据
  const ruleFormRef = ref()
  // 自定义校验规则

  // 表单校验规则
  const rules = reactive({

    id:[{required: true, message: '必选项不能为空', trigger: 'blur'}],
    userid:[{required: true, message: '必选项不能为空', trigger: 'blur'}],
    taskid:[{required: true, message: '必选项不能为空', trigger: 'blur'}],
  })

  //新增方法
  const handleAdd = async () => {
    headerTitle.value = reactive('新增任务')
    dialogVisible.value = true
  }


  const handleFinish = async (row) => {
    const entity = row
    entity.finishstatus='已完成'
    console.log(entity)

    await  request.put('/mytask/'+entity.id,entity)

    ElMessage({
      showClose: true,
      message: '完成任务',
      type: 'success'
    })
    load()
  }
  const handleAccept = async (row) => {
    const entity = row
    entity.finishstatus='已接受'
    console.log(entity)

    await  request.put('/mytask/'+entity.id,entity)

    ElMessage({
      showClose: true,
      message: '成功接受任务',
      type: 'success'
    })
  }

  const handleRefuse = async (row) => {
    const entity = row
    entity.finishstatus='已拒绝'
    console.log(entity)

    await  request.put('/mytask/'+entity.id,entity)

    ElMessage({
      showClose: true,
      message: '已拒绝该任务',
      type: 'success'
    })
  }

  // 分页查询方法（初始化方法，页面加载成功以后就调用的方法）
  const load = async () => {
    const res = await request.get('/mytask/page', {
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
    const req1 = await request.get('/task')
    tasklist.value = req1.data
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
  const handleSubmitForm = async (formEl) => {
    if (!formEl) return
    await formEl.validate(async (valid, fields) => {
      if (valid) {
        await request({
          method: form.id ? 'put' : 'post',
          url: form.id ? `/mytask/${form.id}` : '/mytask',
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
    await request.post('/mytask/batch/upload', fd, {
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
              url: `/mytask/batch/export/${ids}`,
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
    Object.assign(form,
            {
              id:undefined,
              name:undefined,
              phone:undefined,
              email:undefined,}

    )
    // formEl.clearValidate("img")
    dialogVisible.value = false
    await load()
  }

  // 文件上传方法


  // 文件下载
  const dowload = async (url) => {
    window.open(url)
  }
  // 富文本文件上传
  const onUploadImg = async (files, callback) => {
    let i = 0;
    const res = await Promise.all(
            files.map((file) => {

              if (i > 0) {

                return false
              }
              return new Promise((rev, rej) => {
                const formdata = new FormData();
                formdata.append('file', file);

                request
                        .post('/file/upload', formdata, {
                          headers: {
                            'Content-Type': 'multipart/form-data',
                          }
                        })
                        .then((res) => {
                          i++
                          rev(res.data)
                        })
                        .catch((error) => rej(error));
              });

            })
    );
    callback(res);
  };

</script>
<template>
  <!--  编辑弹框-->
  <el-dialog
          :modelValue="dialogVisible"

          :show-close="false"
          :before-close="handleClose"
          width=30%


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

<!--        <el-form-item label="用户id" prop="userid">-->
<!--          <el-input v-model="form.userid"/>-->
<!--        </el-form-item>-->
        <el-form-item label="任务id" prop="taskid">
          <el-input v-model="form.taskid"/>
        </el-form-item>
        <el-form-item label="任务状态" prop="finishstatus">
          <el-input v-model="form.finishstatus"/>
        </el-form-item>
        <el-form-item label="完成时间" prop="finishtime">
          <el-input v-model="form.finishtime"/>
        </el-form-item>
        <el-form-item label="漏洞扫描个数" prop="submitscannum">
          <el-input v-model="form.submitscannum"/>
        </el-form-item>
        <el-form-item label="漏洞利用个数" prop="submitexploitnum">
          <el-input v-model="form.submitexploitnum"/>
        </el-form-item>
        <el-form-item label="横向移动个数" prop="submitmovenum">
          <el-input v-model="form.submitmovenum"/>
        </el-form-item>

    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleResetForm(ruleFormRef)">取消</el-button>
        <el-button type="primary" @click="handleSubmitForm(ruleFormRef)"> 提交 </el-button>
      </span>
    </template>
  </el-dialog>

  <el-row>
    <!--  分页查询表单按钮-->
    <el-col>
      <el-form :inline="true">
        <!--        查询输入框-->
        <el-form-item>
          <el-input v-model="name" placeholder="点击输入名称"></el-input>
        </el-form-item>
        <!--        查询按钮 and 重置按钮-->
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="load">查询</el-button>
          <el-button type="warning" :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-col>

    <el-col>
      <el-table
              :data="tableData"
              style="width: 100%"
              :header-cell-style="{ background: '#eef1f6', color: '#606266' }"
              @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="25"/>
<!--        <el-table-column prop="id" label="ID" align="center"/>-->
<!--        <el-table-column prop="userid" label="用户id"  align="center"/>-->
        <el-table-column prop="taskid" label="任务id"  align="center"/>
        <el-table-column prop="taskname" label="任务名称"  align="center"/>
        <el-table-column prop="taskstatus" label="任务状态"  align="center"/>
        <el-table-column prop="finishstatus" label="完成状态"  align="center"/>
        <el-table-column prop="finishtime" label="完成时间"  align="center"/>
        <el-table-column prop="submitscannum" label="漏洞扫描个数"  align="center"/>
        <el-table-column prop="submitexploitnum" label="漏洞利用个数"  align="center"/>
        <el-table-column prop="submitmovenum" label="横向移动个数"  align="center"/>

        <el-table-column label="操作" align="center" width="200" >
          <template #default="scope">
            <div style="display: flex; justify-content: center; gap: 5px">

              <div v-if="scope.row.taskstatus=='进行中' && scope.row.finishstatus!='已接受'">
                <el-button
                    type="primary"
                    :icon="Edit"
                    size="small"
                    @click="handleAccept(scope.row)"
                    v-permission="'sys:mytask:update'"
                >接受
                </el-button>
              </div>
              <div v-if="scope.row.taskstatus=='进行中' && scope.row.finishstatus!='已完成'">
                <el-button
                    type="primary"
                    :icon="Edit"
                    size="small"
                    @click="handleRefuse(scope.row)"
                    v-permission="'sys:mytask:update'"
                >拒绝
                </el-button>
              </div>
              <div v-if="scope.row.taskstatus =='进行中' && scope.row.finishstatus!='已完成' && scope.row.finishstatus != '已拒绝'">
                <el-button
                    type="primary"
                    :icon="Edit"
                    size="small"
                    @click="handleFinish(scope.row)"
                    v-permission="'sys:mytask:update'"
                >完成
                </el-button>
              </div>
<!--              <div v-permission="'sys:mytask:delete'">-->
<!--                <el-popconfirm-->
<!--                        confirm-button-text="确定"-->
<!--                        cancel-button-text="取消"-->
<!--                        :icon="InfoFilled"-->
<!--                        icon-color="#626AEF"-->
<!--                        title="确认要删除吗？"-->
<!--                        @confirm="handleDel(scope.row.id)"-->
<!--                        @cancel="load"-->
<!--                >-->
<!--                  <template #reference>-->
<!--                    <el-button type="danger" :icon="Delete" size="small">删除</el-button>-->
<!--                  </template>-->
<!--                </el-popconfirm>-->
<!--              </div>-->
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
