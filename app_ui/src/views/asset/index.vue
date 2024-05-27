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
  import IpAddressInput from "@/components/ipaddressinput/index.vue";
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

  const dialogVisiblecategory = ref(false)

  const  dialogVisiblelabel =  ref(false)




  // 表单数据定义
  const form = reactive({
    id:undefined,
    assetno:undefined,
    assettypeno:undefined,
    assetname:undefined,
    assetlabel:undefined,
    assetdesc:undefined,
    assetcategory:undefined,
    assetStatus:undefined,
    registerTime:undefined,
    lastUpdatedTime:undefined,
    belongOrg:undefined,
    assetUser:undefined,
    ip:undefined,
    name:undefined, //分类或者名称
  })

  // 表单样式
  const formSize = ref('default')
  // 表单ref标识数据
  const ruleFormRef = ref()
  // 自定义校验规则
  const assetorgList=ref([])

  const categoryList=ref([])

  const assetlabel=ref()
  const  assetcategory = ref()
  const assetorg = ref()

  const labelList=ref([])

  const UserList=ref([])

  // 表单校验规则
  const rules = reactive({

    id:[{required: true, message: '必选项不能为空', trigger: 'blur'}],
    assetno:[{required: true, message: '必选项不能为空', trigger: 'blur'}],
    assetname:[{required: true, message: '必选项不能为空', trigger: 'blur'}],
    assetlabel:[{required: true, message: '必选项不能为空', trigger: 'blur'}],
    assetcategory:[{required: true, message: '必选项不能为空', trigger: 'blur'}],
    belongOrg:[{required: true, message: '必选项不能为空', trigger: 'blur'}],

  })
  const getUserNickname = (userId) => {
    const user = UserList.value.find(user => user.id.toString() === userId.toString());
    return user ? user.nickname : '';
  };

  const getLabelName = (labelId) =>{
    const label = labelList.value.find(item => item.id.toString() === labelId.toString());
    return label ? label.name : '';
  };
  //新增方法
  const handleAdd = async () => {
    headerTitle.value = reactive('新增数据')
    dialogVisible.value = true
  }

  const handleAdd1 = async () => {
        headerTitle.value = reactive('新增数据')
        dialogVisiblecategory.value = true
      }

  const handleAdd2 = async () => {
    headerTitle.value = reactive('新增数据')
    dialogVisiblelabel.value = true
  }

  // 修改方法
  const handleUpdate = async (id) => {
    dialogVisible.value = true
    headerTitle.value = reactive('编辑数据')
    const res = await request.get(`/asset/${id}`)
    if(res.data.assetUser != undefined ){
      res.data.assetUser=res.data.assetUser.split(',');
    }
    if(res.data.assetlabel != undefined){
      res.data.assetlabel = res.data.assetlabel.split(',');
    }
    Object.assign(form, res.data)
  }

  // 单个删除方法
  const handleDel = async (id) => {
    await request.delete(`/asset/${id}`)
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
    await request.delete(`/asset/batch/${ids}`)
    ElMessage({
      showClose: true,
      message: '批量删除成功',
      type: 'success'
    })
    await load()
  }

  // 分页查询方法（初始化方法，页面加载成功以后就调用的方法）
  const load = async () => {
    const res = await request.get('/asset/page', {
      params: {
        name: name.value,
        pageNum: pageNum.value,
        assetlabel: Array.isArray(assetlabel.value) ? assetlabel.value.join(',') : '',
        assetcategory:assetcategory.value,
        assetorg:assetorg.value
      }
    })
    pageNum.value = res.data.current
    pageSize.value = res.data.size
    total.value = res.data.total
    tableData.value = res.data.records
    const req = await request.get('/assetorg')
    assetorgList.value = req.data
    const req1 = await request.get('/label')
    labelList.value = req1.data
    const req2 = await request.get('/category')
    categoryList.value = req2.data
    const req3 = await request.get('/user/asset')
    UserList.value = req3.data
  }
  // 加载页面初始化调用load方法
  load()


  const handleSubmitForm1 = async (formEl) => {
    if (!formEl) return
    await formEl.validate(async (valid, fields) => {
      if (valid) {
        await request({
          method: form.id ? 'put' : 'post',
          url: form.id ? `/category/${form.id}` : '/category',
          data: form
        })
        ElMessage({
          showClose: true,
          message: '操作成功',
          type: 'success'
        })
        await handleResetcategoryForm(formEl)
      } else {
        console.log('error submit!', fields)
      }
    })
  }

  const handleSubmitForm2 = async (formEl) => {
    if (!formEl) return
    await formEl.validate(async (valid, fields) => {
      if (valid) {
        await request({
          method: form.id ? 'put' : 'post',
          url: form.id ? `/label/${form.id}` : '/label',
          data: form
        })
        ElMessage({
          showClose: true,
          message: '操作成功',
          type: 'success'
        })
        await handleResetlabelForm(formEl)
      } else {
        console.log('error submit!', fields)
      }
    })
  }
  // 清空查询数据重置方法
  const handleReset = () => {
    name.value = ''
    assetlabel.value=''
    assetcategory.value=''
    assetorg.value=''
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
              handleResetcategoryForm(ruleFormRef.value)
            })
            .then(() => {
              handleResetlabelForm(ruleFormRef.value)
              // catch error
            })
          .catch(() => {
          })

  }
  // 提交表单校验方法
  const handleSubmitForm = async (formEl) => {
    if (!formEl) return
    await formEl.validate(async (valid, fields) => {
      if (valid) {
        if(form.assetUser != undefined ){
          form.assetUser = form.assetUser.join(',');
        }
        if(form.assetlabel != undefined){
          form.assetlabel = form.assetlabel.join(',');
        }
        await request({
          method: form.id ? 'put' : 'post',
          url: form.id ? `/asset/${form.id}` : '/asset',
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
    await request.post('/asset/batch/upload', fd, {
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
              url: `/asset/batch/export/${ids}`,
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
  const handleResetcategoryForm = async (formEl) => {
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
    dialogVisiblecategory.value = false
    await load()
  }

  const handleResetlabelForm = async (formEl) => {
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
    dialogVisiblelabel.value = false
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

<!--        <el-form-item label="资产编号" prop="assetno">-->
<!--          <el-input v-model="form.assetno"/>-->
<!--        </el-form-item>-->
<!--        <el-form-item label="资产型号" prop="assettypeno">-->
<!--          <el-input v-model="form.assettypeno"/>-->
<!--        </el-form-item>-->
        <el-form-item label="资产名" prop="assetname">
          <el-input v-model="form.assetname"/>
        </el-form-item>
        <el-form-item label="资产标签" prop="assetlabel">
          <el-select   v-model="form.assetlabel"  multiple
                       collapse-tags
                       fit-input-width
                       placeholder="请选择">
            <el-option
                v-for="(item,index) in labelList"
                :key = "index.toString()"
                :label = "item.name"
                :value = "item.id.toString()"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="资产描述" prop="assetdesc">
          <el-input v-model="form.assetdesc"/>
        </el-form-item>
        <el-form-item label="资产类别" prop="assetcategory">
          <el-select v-model="form.assetcategory" placeholder="选中所属分类">
            <el-option
                v-for="(item,index) in categoryList"
                :key= "index.toString()"
                :label = "item.name"
                :value = "item.id.toString()"

            >

            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="资产状态" prop="assetStatus">
          <el-select v-model="form.assetStatus">
            <el-option
                v-for="(item,index) in ['使用中','维修中','已报废']"
                :key = "index.toString()"
                :value = "item"
                :label = "item"
            >
            </el-option>
          </el-select>
        </el-form-item>
<!--        <el-form-item label="加入时间" prop="registerTime">-->
<!--          <el-input v-model="form.registerTime"/>-->
<!--        </el-form-item>-->
<!--        <el-form-item label="最近更新时间" prop="lastUpdatedTime">-->
<!--          <el-input v-model="form.lastUpdatedTime"/>-->
<!--        </el-form-item>-->
        <el-form-item label="资产所属组织" prop="belongOrg">
<!--          <el-input v-model="form.belongOrg"/>-->
          <el-select v-model="form.belongOrg"  prop="belongOrg">
            <el-option
            v-for="(item,index) in assetorgList"
            :key = "index.toString()"
            :value = "item.id.toString()"
            :label = "item.orgname"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="资产负责人" prop="assetUser">
<!--          <el-input v-model="form.assetUser"/>-->
          <el-select   v-model="form.assetUser"  multiple
                       collapse-tags
                       fit-input-width
                       placeholder="请选择">
            <el-option
                v-for="(item,index) in UserList"
                :key = "index.toString()"
                :value = "item.id.toString()"
                :label = "item.nickname"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="资产网络ip" prop="ip">
          <IpAddressInput v-model="form.ip"/>

<!--          <el-input v-model="form.ip"/>-->
        </el-form-item>

    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleResetForm(ruleFormRef)">取消</el-button>
        <el-button type="primary" @click="handleSubmitForm(ruleFormRef)"> 提交 </el-button>
      </span>
    </template>
  </el-dialog>

  <el-dialog
      :modelValue="dialogVisiblecategory"
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

      <el-form-item label="分类名称" prop="name">
        <el-input v-model="form.name"/>
      </el-form-item>

    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleResetcategoryForm(ruleFormRef)">取消</el-button>
        <el-button type="primary" @click="handleSubmitForm1(ruleFormRef)"> 提交 </el-button>
      </span>
    </template>
  </el-dialog>



  <el-dialog
      :modelValue="dialogVisiblelabel"

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

      <el-form-item label="标签名字" prop="name">
        <el-input v-model="form.name"/>
      </el-form-item>

    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleResetlabelForm(ruleFormRef)">取消</el-button>
        <el-button type="primary" @click="handleSubmitForm2(ruleFormRef)"> 提交 </el-button>
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
        <el-form-item>
<!--          <el-select v-model="assetlabel" placeholder="选中所属标签">-->
            <el-select   v-model="assetlabel"  multiple
                         collapse-tags
                         fit-input-width
                         placeholder="选中所属标签">
              <el-option
                  v-for="(item,index) in labelList"
                  :key = "index.toString()"
                  :label = "item.name"
                  :value = "item.id.toString()"
              >
              </el-option>
            </el-select>
<!--          </el-select>-->
        </el-form-item>
        <el-form-item>
          <el-select v-model="assetcategory" placeholder="选中所属分类">
            <el-option
                v-for="(item,index) in categoryList"
                :key= "index.toString()"
                :label = "item.name"
                :value = "item.id.toString()"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="assetorg"  placeholder="选中所属组织">
            <el-option
                v-for="(item,index) in assetorgList"
                :key = "index.toString()"
                :value = "item.id.toString()"
                :label = "item.orgname"
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
      <div v-permission="'sys:asset:add'">
        <el-button type="primary" :icon="Plus" @click="handleAdd" size="small" plain
        >新增
        </el-button>
      </div>
      <!--   批量导入   -->
      <div v-permission="'sys:asset:batch:export'">
        <el-upload action="" :before-upload="beforeBatchUpload" :show-file-list="true">
          <el-button type="success" :icon="Upload" size="small" plain>批量导入</el-button>
        </el-upload>
      </div>
      <!--   批量删除   -->
      <div v-permission="'sys:asset:batch:delete'">
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

      <div v-permission="'sys:asset:batch:export'">
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
      <div v-permission="'sys:category:add'">
        <el-button type="primary" :icon="Plus" @click="handleAdd1" size="small" plain
        >新增分类
        </el-button>
      </div>
      <div v-permission="'sys:category:add'">
        <el-button type="primary" :icon="Plus" @click="handleAdd2" size="small" plain
        >新增标签
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
        <el-table-column type="selection" width="25"/>
<!--        <el-table-column prop="id" label="ID" align="center"/>-->
        <el-table-column prop="assetno" label="资产编号"  align="center"/>
        <el-table-column prop="assettypeno" label="资产型号"  align="center"/>
        <el-table-column prop="assetname" label="资产名"  align="center"/>
        <el-table-column prop="assetlabel" label="资产标签" align="center">
          <template #default="scope">
            <div>
              <template v-if="scope.row.assetlabel">
                <template v-for="labelId in scope.row.assetlabel.split(',')" :key="labelId">
                  <el-tag>{{ getLabelName(labelId) }}</el-tag>
                </template>
              </template>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="assetdesc" label="资产描述"  align="center"/>
        <el-table-column prop="assetcategory" label="资产类别"  align="center">
        <template #default="scope">
          <el-tag>{{scope.row.category.name}}</el-tag>
        </template>
        </el-table-column>
        <el-table-column prop="assetStatus" label="资产状态"  align="center"/>
        <el-table-column prop="registerTime" label="加入时间"  align="center"/>
        <el-table-column prop="lastUpdatedTime" label="最近更新时间"  align="center"/>
        <el-table-column prop="belongOrg" label="资产所属组织"  align="center">
        <template #default="scope">
          <el-tag>{{scope.row.assetorg.orgname}}</el-tag>
        </template>
        </el-table-column>
        <el-table-column prop="assetUser" label="资产负责人"  align="center">
          <template #default="scope">
            <div>
              <template v-if="scope.row.assetUser">
                <template v-for="userid in scope.row.assetUser.split(',')" :key="userid">
                  <el-tag>{{ getUserNickname(userid) }}</el-tag>
                </template>
              </template>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="ip" label="资产网络ip"  align="center"/>

        <el-table-column label="操作" width="140" align="center">
          <template #default="scope">
            <div style="display: flex; justify-content: center; gap: 10px">
              <div>
                <el-button
                        type="primary"
                        :icon="Edit"
                        size="small"
                        @click="handleUpdate(scope.row.id)"
                        v-permission="'sys:asset:update'"
                >编辑
                </el-button>
              </div>
              <div v-permission="'sys:asset:delete'">
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
