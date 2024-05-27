<script setup>
import { reactive, ref } from 'vue'
import useSystemStore from '@/stores/system'
import pinia from '@/stores/store'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const systemStore = useSystemStore(pinia)
const userInfo = systemStore.userInfo

const formSize = ref('default')
const ruleFormRef = ref()
const ruleForm = reactive(userInfo)

const rules = reactive({
  nickname: [
    { required: true, message: '输入用户昵称', trigger: 'blur' },
    { min: 2, max: 5, message: '长度在2到5位之间', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    {
      pattern: /^1[3456789]\d{9}$/,
      message: '手机号格式不正确',
      trigger: 'blur'
    }
  ],
  email: [
    { required: true, message: '请输入邮箱号', trigger: 'blur' },
    {
      type: 'email',
      message: '邮箱地址格式不正确',
      trigger: ['blur']
    }
  ],
  content: [
    { required: true, message: '请输个人简介', trigger: 'blur' },
    { min: 5, max: 100, message: '长度在5到100位之间', trigger: 'blur' }
  ]
})

const submitForm = async (formEl) => {
  if (!formEl) return
  await formEl.validate(async (valid) => {
    if (valid) {
      await request.put('/user', ruleForm)
      ElMessage({
        showClose: true,
        message: '操作成功',
        type: 'success'
      })
    }
  })
}

const resetForm = (formEl) => {
  if (!formEl) return
  formEl.resetFields()
}

const options = Array.from({ length: 10000 }).map((_, idx) => ({
  value: `${idx + 1}`,
  label: `${idx + 1}`
}))
// 上传成功以后
const handleAvatarSuccess = async (file) => {
  let fd = new FormData()
  fd.append('file', file)
  const res = await request.post('/file/upload', fd, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
  ElMessage({
    showClose: true,
    message: '上传成功',
    type: 'success'
  })
  ruleForm.avatar = res.data
  ruleFormRef.value.clearValidate('avatar')
}
</script>
<script>
export default {
  name: 'Info'
}
</script>
<template>
  <el-row>
    <el-col :sm="8">
      <el-form
        ref="ruleFormRef"
        :model="ruleForm"
        :rules="rules"
        label-width="80px"
        class="demo-ruleForm"
        :size="formSize"
        status-icon
      >
        <el-form-item label="用户头像" prop="avatar">
          <el-upload
            class="avatar-uploader"
            :show-file-list="false"
            :before-upload="handleAvatarSuccess"
          >
            <el-avatar :size="30" v-if="ruleForm.avatar" :src="ruleForm.avatar" />
            <el-icon v-else class="avatar-uploader-icon">
              <Plus />
            </el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="账户名称" prop="username">
          <el-input v-model="ruleForm.username" disabled />
        </el-form-item>
        <el-form-item label="用户昵称" prop="nickname">
          <el-input v-model="ruleForm.nickname" />
        </el-form-item>
        <el-form-item label="手机号码" prop="phone">
          <el-input v-model="ruleForm.phone" />
        </el-form-item>
        <el-form-item label="个人邮箱" prop="email">
          <el-input v-model="ruleForm.email" />
        </el-form-item>
        <el-form-item label="个人简介" prop="content">
          <el-input v-model="ruleForm.content" type="textarea" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm(ruleFormRef)"> 确认修改 </el-button>
        </el-form-item>
      </el-form>
    </el-col>
  </el-row>
</template>

<style scoped lang="scss"></style>
