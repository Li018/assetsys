<script setup>
import { ElMessage, ElMessageBox } from 'element-plus'
import { nextTick, reactive, ref } from 'vue'
import request from '@/utils/request'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { CircleCloseFilled } from '@element-plus/icons-vue'

// 组件传值
const props = defineProps(['dialogVisible', 'id'])
// 子组件向父组件传递数据
const emit = defineEmits(['changeDialog'])
// 表单数据
const form = reactive({
  menuType: 1,
  orderNum: 1,
  parentId: 0,
  icon: 'Menu',
  component: '',
  statu: 1,
  hidden: false
})
// 表单规则
const rules = reactive({
  orderNum: [{ required: true, message: '必选项不能为空', trigger: 'blur' }],
  // icon: [{required: true, message: '必选项不能为空', trigger: 'blur'}],
  perms: [{ required: true, message: '必选项不能为空', trigger: 'blur' }],
  menuType: [{ required: true, message: '必选项不能为空', trigger: 'change' }],
  parentId: [{ required: true, message: '必选项不能为空', trigger: 'change' }],
  title: [{ required: true, message: '必选项不能为空', trigger: 'blur' }],
  path: [{ required: true, message: '必选项不能为空', trigger: 'blur' }],
  component: [{ required: true, message: '必选项不能为空', trigger: 'blur' }]
})
// 定义树形组件ref标识
const treeRef = ref()

// 定义树形组件数据
const treeData = ref([])
// 表单样式
const formSize = ref('default')
// 表单ref标识数据
const ruleFormRef = ref()
// 是否展开
const isExpandAll = ref(false)
// 刷新trees
const refreshTree = ref(true)
// 是否全选
const isCheckAllKeys = ref(false)

// 定义树形组件结构数据
const defaultProps = {
  children: 'children',
  value: 'id',
  label: 'title'
}

// 关闭弹框提示
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
// 提交表单
const handleSumbmitForm = async (formEl) => {
  if (!formEl) return
  await formEl.validate(async (valid, fields) => {
    if (valid) {
      if (form.menuType === 1) {
        form.component = ''
      }
      if (form.menuType === 3) {
        form.icon = ''
        form.path = ''
        form.hidden = false
        form.statu = 1
        form.component = ''
      }
      await request({
        method: form.id ? 'put' : 'post',
        url: form.id ? `/permission/${form.id}` : '/permission',
        data: form
      })
      handleResetForm(formEl)
      ElMessage({
        showClose: true,
        message: '操作成功',
        type: 'success'
      })
    }
  })
}

// 取消弹框
const handleResetForm = (formEl) => {
  if (!formEl) return
  formEl.resetFields()
  Object.assign(form, {
    menuType: 1,
    orderNum: 1,
    parentId: 0,
    icon: 'Menu',
    component: '',
    statu: 1,
    hidden: false,
    id: undefined
  })

  emit('changeDialog', false)
}
// 初始化数据
const handleGetForm = async (id) => {
  const res = await request.get(`/permission/${id}`)
  await handleGetTrees()
  Object.assign(form, res.data)
}
// 权限树形组件数据的获取
const handleGetTrees = async () => {
  const res = await request.get('/permission/page')
  treeData.value = [
    {
      id: 0,
      title: '主目录',
      children: res.data
    }
  ]
}

// 获取树形ids
const getCheckedKeys = () => {
  return treeRef.value.getCheckedKeys(false)
}
//设置树形ids
const setCheckedKeys = (ids) => {
  treeRef.value.setCheckedKeys(ids, false)
}
// 重置选择
const resetChecked = () => {
  treeRef.value.setCheckedKeys([], false)
}
// 展开折叠处理方案
const handleDefaultExpandAll = () => {
  refreshTree.value = false
  nextTick(() => {
    refreshTree.value = true
  })
}
// 确定全选/不全选
const handleCheckedTreesAll = () => {
  if (isCheckAllKeys.value) {
    setCheckedKeys(treeData.value.map((row) => row.id))
  } else {
    resetChecked()
  }
}
// 添加children
const handleAddChildren = async (id) => {
  await handleGetTrees()
  form.parentId = id
}
// 暴露方法
defineExpose({
  handleGetForm,
  handleGetTrees,
  handleAddChildren
})
</script>
<script>
export default {
  name: 'RoleForm'
}
</script>
<template>
  <el-dialog
    :modelValue="dialogVisible"
    width="35%"
    :show-close="false"
    :before-close="handleClose"
  >
    <template #header="{ close, titleId, titleClass }">
      <div class="my-header">
        <h4 :id="titleId" :class="titleClass">编辑数据</h4>
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
      <!--      parentId-->
      <el-form-item label="上级目录" prop="parentId">
        <el-tree-select
          v-model="form.parentId"
          :data="treeData"
          node-key="id"
          :props="defaultProps"
          check-strictly
          :render-after-expand="false"
        />
      </el-form-item>
      <!--      menuType -->
      <el-form-item label="菜单类型" prop="menuType">
        <el-radio-group v-model="form.menuType">
          <el-radio-button :label="1">目录</el-radio-button>
          <el-radio-button :label="2">菜单</el-radio-button>
          <el-radio-button :label="3">按钮</el-radio-button>
        </el-radio-group>
      </el-form-item>
      <!--path and icon-->
      <div v-if="form.menuType === 1 || form.menuType === 2">
        <el-form-item label="菜单图标" prop="icon">
          <el-select v-model="form.icon" placeholder="选择合适的图标" style="width: 175px">
            <el-option v-for="item in ElementPlusIconsVue" :key="item.name" :value="item.name">
              <el-icon>
                <component :is="item.name"></component>
              </el-icon>
              &nbsp &nbsp &nbsp
              <span>{{ item.name }}</span>
            </el-option>
          </el-select>
          <el-icon style="margin-left: 4px" size="25">
            <component :is="form.icon"></component>
          </el-icon>
        </el-form-item>

        <el-form-item label="访问路径" prop="path">
          <el-input v-model="form.path" />
        </el-form-item>
      </div>
      <!--      title -->
      <el-row>
        <el-col :span="12">
          <el-form-item label="菜单名称" prop="title">
            <el-input v-model="form.title" />
          </el-form-item>
        </el-col>

        <el-col :span="12" v-if="form.menuType === 2">
          <el-form-item label="组件路径" prop="component">
            <el-input v-model="form.component" />
          </el-form-item>
        </el-col>
      </el-row>
      <!--      perms-->
      <el-row></el-row>

      <el-row>
        <el-col :span="12">
          <el-form-item label="权限编码" prop="perms">
            <el-input v-model="form.perms" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="菜单排序" prop="orderNum">
            <el-input-number v-model="form.orderNum" :min="1" :max="999" />
          </el-form-item>
        </el-col>
      </el-row>
      <!--      status and hidden and orderNum-->
      <el-row v-if="form.menuType === 1 || form.menuType === 2">
        <el-col :span="6">
          <el-form-item label="菜单状态" prop="statu">
            <el-switch
              v-model="form.statu"
              :active-value="1"
              :inactive-value="0"
              inline-prompt
              active-text="正常"
              inactive-text="禁用"
              style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949"
            />
          </el-form-item>
        </el-col>
        <el-col :span="5">
          <el-form-item label="显示状态" prop="hidden">
            <el-switch
              v-model="form.hidden"
              :active-value="false"
              :inactive-value="true"
              inline-prompt
              active-text="正常"
              inactive-text="隐藏"
              style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949"
            />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleResetForm(ruleFormRef)">取消</el-button>
        <el-button type="primary" @click="handleSumbmitForm(ruleFormRef)"> 提交 </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style scoped lang="scss">
.my-header {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}
</style>
