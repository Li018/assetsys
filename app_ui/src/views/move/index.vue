<script setup name="user">
import {computed, reactive, ref} from 'vue'
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

  // 表单数据定义
  const form = reactive({
    id:undefined,
    name:undefined,
    tester:undefined,
    attacktime:undefined,
    targetip:undefined,
    exploitvuls:undefined,
    movepath:undefined,
    moveresult:undefined,
    movetime:undefined,
  })



  // 表单样式
  const formSize = ref('default')
  // 表单ref标识数据
  const ruleFormRef = ref()
  const exploitlist = ref([])
  const VulList=ref([])
  // 自定义校验规则


  // 表单校验规则
  const rules = reactive({

    id:[{required: true, message: '必选项不能为空', trigger: 'blur'}],
    name:[{required: true, message: '必选项不能为空', trigger: 'blur'}],
    exploitvuls:[{required: true, message: '必选项不能为空', trigger: 'blur'}],
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
    const res = await request.get(`/move/${id}`)
    Object.assign(form, res.data)
  }

  const convertedTimeInMinutes = computed(() => {
    return form.movetime ? form.movetime / 60 : 0;
  });

  // 单个删除方法
  const handleDel = async (id) => {
    await request.delete(`/move/${id}`)
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
    await request.delete(`/move/batch/${ids}`)
    ElMessage({
      showClose: true,
      message: '批量删除成功',
      type: 'success'
    })
    await load()
  }

  // 分页查询方法（初始化方法，页面加载成功以后就调用的方法）
  const load = async () => {
    const res = await request.get('/move/page', {
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
    const req = await request.get('/exploit')
    exploitlist.value = req.data
    const res2 = await request.get('/vulscan')
    VulList.value = res2.data
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

  const getVulNo = (ad) => {
    console.log(ad)
    // 通过id查找对应的vulno
    console.log(VulList.value)
    for(let i=0;i<VulList.value.length;i++){
      if(VulList.value[i].id==ad){
        return VulList.value[i].vulno
      }
    }
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
          url: form.id ? `/move/${form.id}` : '/move',
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
    await request.post('/move/batch/upload', fd, {
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
              url: `/move/batch/export/${ids}`,
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

        <el-form-item label="移动名称" prop="name">
          <el-input v-model="form.name"/>
        </el-form-item>
<!--        <el-form-item label="测试员" prop="tester">-->
<!--          <el-input v-model="form.tester"/>-->
<!--        </el-form-item>-->
<!--        <el-form-item label="发生时间" prop="attacktime">-->
<!--          <el-input v-model="form.attacktime"/>-->
<!--        </el-form-item>-->
        <el-form-item label="目标IP" prop="targetip">
<!--          <el-input v-model="form.targetip"/>-->
          <IpAddressInput v-model="form.targetip"/>
        </el-form-item>
        <el-form-item label="漏洞利用列表" prop="exploitvuls">
<!--          <el-input v-model="form.exploitvuls"/>-->
          <el-select v-model="form.exploitvuls"  placeholder="请在漏洞利用列表中选择">
<!--            我要将里面的label改为    exploitlist中的vu值等于VulList的对应id的   vulno数值  -->
            <el-option
                v-for="(item,index) in  exploitlist"
                :key = "index.toString()"
                :label="'利用漏洞名称为:'  + item.name + '   漏洞编号:' + getVulNo(item.vul)"
                :value = "item.id.toString()"
            >
            </el-option>
          </el-select>
        </el-form-item>
<!--        <el-form-item label="横移路径" prop="movepath">-->
<!--          <el-input v-model="form.movepath"/>-->
<!--        </el-form-item>-->
        <el-form-item label="移动结果" prop="moveresult">
          <el-input v-model="form.moveresult"/>
<!--          <IpAddressInput v-model="form.moveresult"/>-->
        </el-form-item>
        <el-form-item label="执行时间" prop="movetime" >
          <el-input v-model="form.movetime" type="number"/>
          <div>
            {{ convertedTimeInMinutes }} 分钟
          </div>
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
    <!-- 新增和批量按钮 -->
    <el-col style="margin-bottom: 10px; display: flex; gap: 10px">
      <!--   新增   -->
      <div v-permission="'sys:move:add'">
        <el-button type="primary" :icon="Plus" @click="handleAdd" size="small" plain
        >新增
        </el-button>
      </div>
      <!--   批量导入   -->
      <div v-permission="'sys:move:batch:export'">
        <el-upload action="" :before-upload="beforeBatchUpload" :show-file-list="true">
          <el-button type="success" :icon="Upload" size="small" plain>批量导入</el-button>
        </el-upload>
      </div>
      <!--   批量删除   -->
      <div v-permission="'sys:move:batch:delete'">
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

      <div v-permission="'sys:move:batch:export'">
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
        <el-table-column type="selection" width="55"/>
<!--        <el-table-column prop="id" label="ID" align="center"/>-->
        <el-table-column prop="name" label="移动名称"  align="center"/>
<!--        <el-table-column prop="tester" label="测试员"  align="center"/>-->
        <el-table-column prop="attacktime" label="发生时间"  align="center"/>
        <el-table-column prop="targetip" label="目标IP"  align="center"/>
        <el-table-column prop="exploitvuls" label="利用的漏洞"  align="center">
        <template #default="scope">
          <el-tag>{{getVulNo(scope.row.exploit.vul)}}</el-tag>
        </template>
        </el-table-column>
        <el-table-column prop="movepath" label="横移路径"  align="center"/>
        <el-table-column prop="moveresult" label="移动结果"  align="center"/>
        <el-table-column prop="movetime" label="执行时间(单位为秒)"  align="center"/>

<!--        <el-table-column label="操作" align="center">-->
<!--          <template #default="scope">-->
<!--            <div style="display: flex; justify-content: center; gap: 10px">-->
<!--              <div>-->
<!--                <el-button-->
<!--                        type="primary"-->
<!--                        :icon="Edit"-->
<!--                        size="small"-->
<!--                        @click="handleUpdate(scope.row.id)"-->
<!--                        v-permission="'sys:move:update'"-->
<!--                >编辑-->
<!--                </el-button>-->
<!--              </div>-->
<!--              <div v-permission="'sys:move:delete'">-->
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
<!--            </div>-->
<!--          </template>-->
<!--        </el-table-column>-->
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
