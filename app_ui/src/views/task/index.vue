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
  const taskdesc = ref('')
  const  taskstatus = ref('')
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
  const dialogVisibleassin = ref(false)

  // Inside your script setup
  const getUserNickname = (userId) => {
    const user = userList.value.find(user => user.id.toString() === userId.toString());
    return user ? user.nickname : '';
  };





  // 表单数据定义
  const form = reactive({
    id:undefined,
    name:undefined,
    taskdesc:undefined,
    relatedorg:undefined,
    joinedtesters:undefined,
    taskstatus:'未开始',
    taskvul:undefined,
    starttime:undefined,
    endtime:undefined,
  })

  // 表单样式
  const formSize = ref('default')
  // 表单ref标识数据
  const ruleFormRef = ref()

  const assetorgList=ref([])

  const userList=ref([])
  // 自定义校验规则

  // 表单校验规则
  const rules = reactive({

    id:[{required: true, message: '必选项不能为空', trigger: 'blur'}],
    name:[{required: true, message: '必选项不能为空', trigger: 'blur'}],
    taskdesc:[{required: true, message: '必选项不能为空', trigger: 'blur'}],
    relatedorg:[{required: true, message: '必选项不能为空', trigger: 'blur'}],
  })

  //新增方法
  const handleAdd = async () => {
    headerTitle.value = reactive('新增数据')
    dialogVisible.value = true
  }
  // // 修改方法
  // const handleUpdate = async (id) => {
  //   dialogVisible.value = true
  //   headerTitle.value = reactive('编辑数据')
  //   const res = await request.get(`/task/${id}`)
  //   console.log(res.data.joinedtesters)
  //
  //   res.data.joinedtesters=res.data.joinedtesters.split(',')
  //   console.log(res.data.joinedtesters)
  //   Object.assign(form, res.data)
  // }


  const handleAssign = async (id) => {
    dialogVisibleassin.value = true
    headerTitle.value = reactive('指派人员')
    const res = await request.get(`/task/${id}`)
    console.log(res.data.joinedtesters)
    if(res.data.joinedtesters != undefined ){
      res.data.joinedtesters=res.data.joinedtesters.split(',');
    }
    console.log(res.data.joinedtesters)
    Object.assign(form, res.data)
  }
  const handleStart = async (row) => {
    const entity = row
    entity.taskstatus='进行中'
    console.log(entity)
    // await request({
    //   method: 'put',
    //   url: `/task/${entity.id}`,
    //   data: entity
    // })
    await  request.put('/task/'+entity.id,entity)

    ElMessage({
      showClose: true,
      message: '开始任务成功',
      type: 'success'
    })
    load()
  }

  const handleStop = async (row) => {
    const entity = row
    entity.taskstatus='已完成'
    console.log(entity)
    await request({
      method: 'put',
      url: `/task/${entity.id}`,
      data: entity
    })

    ElMessage({
      showClose: true,
      message: '完成任务',
      type: 'success'
    })
    load()
  }

  // const hadnleAssign = async (id) => {
  //   dialogVisible.value = true
  //   headerTitle.value = reactive('指派数据')
  //   const res = await request.get(`/task/${id}`)
  //   console.log(res.data.joinedtesters)
  //
  //   res.data.joinedtesters=res.data.joinedtesters.split(',')
  //   console.log(res.data.joinedtesters)
  //   Object.assign(form, res.data)
  // }
  // 单个删除方法
  // const handleDel = async (id) => {
  //   await request.delete(`/task/${id}`)
  //   ElMessage({
  //     showClose: true,
  //     message: '删除成功',
  //     type: 'success'
  //   })
  //   await load()
  // }
  // 批量删除方法
  const handleBatchDel = async () => {
    const ids = []
    multipleSelection.value.forEach((row) => {
      ids.push(row.id)
    })
    await request.delete(`/task/batch/${ids}`)
    ElMessage({
      showClose: true,
      message: '批量删除成功',
      type: 'success'
    })
    await load()
  }

  // 分页查询方法（初始化方法，页面加载成功以后就调用的方法）
  const load = async () => {
    const res = await request.get('/task/page', {
      params: {
        name: name.value,
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        taskdesc: taskdesc.value,
        taskstatus: taskstatus.value,
      }
    })
    pageNum.value = res.data.current
    pageSize.value = res.data.size
    total.value = res.data.total
    tableData.value = res.data.records
    const req = await request.get('/assetorg')
    assetorgList.value = req.data
    const req1 = await request.get('/user/tester')
    userList.value = req1.data
    // const req2 = await request.get('/userrole')

  }
  // 加载页面初始化调用load方法
  load()

  // 清空查询数据重置方法
  const handleReset = () => {
    taskdesc.value = ''
    name.value = ''
    taskstatus.value = ''
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
        console.log(form.joinedtesters)
        if(form.joinedtesters != undefined ){
          form.joinedtesters = form.joinedtesters.join(',');
        }
        console.log(form.joinedtesters)
        await request({
          method: form.id ? 'put' : 'post',
          url: form.id ? `/task/${form.id}` : '/task',
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
    await request.post('/task/batch/upload', fd, {
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
              url: `/task/batch/export/${ids}`,
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
    dialogVisibleassin.value = false
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


  <el-dialog
      :modelValue="dialogVisibleassin"

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

      <el-form-item label="参与人员" prop="joinedtesters">
        <!--          <el-input v-model="form.joinedtesters"/>-->
        <el-select   v-model="form.joinedtesters"  multiple
                     collapse-tags
                     fit-input-width
                     placeholder="请选择">
          <el-option
              v-for="(item,index) in userList"
              :key = "index.toString()"
              :value = "item.id.toString()"
              :label = "item.nickname"
          >
          </el-option>
        </el-select>

      </el-form-item>


    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleResetForm(ruleFormRef)">取消</el-button>
        <el-button type="primary" @click="handleSubmitForm(ruleFormRef)"> 提交 </el-button>
      </span>
    </template>
  </el-dialog>



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

        <el-form-item label="任务名称" prop="name">
          <el-input v-model="form.name"/>
        </el-form-item>
        <el-form-item label="任务描述" prop="taskdesc">
          <el-input v-model="form.taskdesc"/>
        </el-form-item>
        <el-form-item label="组织结构" prop="relatedorg">
<!--          <el-input v-model="form.relatedorg"/>-->
          <el-select v-model="form.relatedorg"  prop="belongOrg">
            <el-option
                v-for="(item,index) in assetorgList"
                :key = "index.toString()"
                :value = "item.id.toString()"
                :label = "item.orgname"
            >
            </el-option>
          </el-select>
        </el-form-item>
<!--        <el-form-item label="参与人员" prop="joinedtesters">-->
<!--&lt;!&ndash;          <el-input v-model="form.joinedtesters"/>&ndash;&gt;-->
<!--          <el-select   v-model="form.joinedtesters"  multiple-->
<!--              collapse-tags-->
<!--                       fit-input-width-->
<!--              placeholder="请选择">-->
<!--            <el-option-->
<!--                v-for="(item,index) in userList"-->
<!--                :key = "index.toString()"-->
<!--                :value = "item.id.toString()"-->
<!--                :label = "item.nickname"-->
<!--            >-->
<!--            </el-option>-->
<!--          </el-select>-->

<!--        </el-form-item>-->
<!--        <el-form-item label="任务状态" prop="taskstatus">-->
<!--&lt;!&ndash;          <el-input v-model="form.taskstatus"/>&ndash;&gt;-->
<!--          <el-select v-model="form.taskstatus"  prop="belongOrg">-->
<!--            <el-option-->
<!--                v-for="(item,index) in ['未开始','进行中','已完成']"-->
<!--                :key = "index.toString()"-->
<!--                :value = "item"-->
<!--                :label = "item"-->
<!--            >-->
<!--            </el-option>-->
<!--          </el-select>-->
<!--        </el-form-item>-->
        <el-form-item label="任务漏洞" prop="taskvul">
          <el-input v-model="form.taskvul" type="number"/>
        </el-form-item>
      <el-form-item label="开始时间" prop="starttime">
        <el-date-picker
                v-model="form.starttime"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择日期"
                size="default"
        />
      </el-form-item>
      <el-form-item label="结束时间" prop="endtime">
        <el-date-picker
                v-model="form.endtime"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择日期"
                size="default"
        />
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
          <el-input v-model="name" placeholder="演练名称查询"></el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="taskdesc" placeholder="演练描述查询"></el-input>
        </el-form-item>
        <el-form-item>
          <el-select v-model="taskstatus" placeholder="选中所属标签">
            <el-option
                v-for="(item,index) in ['未开始','进行中','已完成']"
                :key = "index.toString()"
                :label = "item"
                :value = "item"
            >
            </el-option>
          </el-select>
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
      <div v-permission="'sys:task:add'">
        <el-button type="primary" :icon="Plus" @click="handleAdd" size="small" plain
        >新增
        </el-button>
      </div>
      <!--   批量导入   -->
      <div v-permission="'sys:task:batch:export'">
        <el-upload action="" :before-upload="beforeBatchUpload" :show-file-list="true">
          <el-button type="success" :icon="Upload" size="small" plain>批量导入</el-button>
        </el-upload>
      </div>
      <!--   批量删除   -->
      <div v-permission="'sys:task:batch:delete'">
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

      <div v-permission="'sys:task:batch:export'">
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
        <!--        <template #default="scope">-->
        <!--          <el-tag>{{scope.row.user.nickname}}</el-tag>-->
        <!--        </template>-->
        <el-table-column type="selection" width="25"/>
<!--        <el-table-column prop="id" label="ID" align="center"/>-->
        <el-table-column prop="name" label="任务名称"  align="center"/>
        <el-table-column prop="taskdesc" label="任务描述"  align="center"/>
        <el-table-column prop="relatedorg" label="组织结构"  align="center">
          <template #default="scope">
            <el-tag>{{scope.row.assetorg.orgname}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="joinedtesters" label="参与人员" align="center">
          <template #default="scope">
            <div>
              <template v-if="scope.row.joinedtesters">
                <template v-for="userid in scope.row.joinedtesters.split(',')" :key="userid">
                  <el-tag>{{ getUserNickname(userid) }}</el-tag>
                </template>
              </template>
            </div>
          </template>
        </el-table-column>





        <el-table-column prop="taskstatus" label="任务状态"  align="center"/>
        <el-table-column prop="taskvul" label="任务漏洞"  align="center"/>
        <el-table-column prop="starttime" label="开始时间"  align="center"/>
        <el-table-column prop="endtime" label="结束时间"  align="center"/>

        <el-table-column label="操作" align="center" width="200" >
          <template #default="scope">
            <div style="display: flex; justify-content: center; gap: 3px">
              <div v-if="scope.row.taskstatus == '未开始' ">
                <el-button
                        type="primary"
                        :icon="Edit"
                        size="small"
                        @click="handleStart(scope.row)"
                        v-permission="'sys:task:update'"
                >开始
                </el-button>
              </div>
              <div v-if="scope.row.taskstatus !== '已完成' && scope.row.taskstatus =='进行中'">
                <el-button
                    type="primary"
                    :icon="Edit"
                    size="small"
                    @click="handleAssign(scope.row.id)"
                    v-permission="'sys:task:update'"
                >
                  指派
                </el-button>
              </div>
              <div v-if="scope.row.taskstatus !== '已完成'">
                <el-button
                    type="primary"
                    :icon="Edit"
                    size="small"
                    @click="handleStop(scope.row)"
                    v-permission="'sys:task:update'"
                >结束
                </el-button>
              </div>
<!--              <div v-permission="'sys:task:delete'">-->
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
