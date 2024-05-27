<script setup>
import { reactive, ref } from 'vue'
import useSystemStore from '@/stores/system'
import pinia from '@/stores/store'
import request from '@/utils/request'
import { ElMessage, ElNotification } from 'element-plus'
import router from '@/router'
const $route = router
const systemStore = useSystemStore(pinia)
const userInfo = systemStore.userInfo
const captchaImg = ref('')

const formSize = ref('default')
const ruleFormRef = ref()
const ruleForm = reactive({
  avatar: 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif',
  nickname: undefined,
  username: undefined,
  phone: undefined,
  email: undefined,
  content: undefined,
  password: undefined,
  checkPass: undefined
})
// 校验新密码
const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else if (value.toString().length < 5 || value.toString().length > 10) {
    return callback(new Error('密码长度在5到10位之间'))
  } else {
    if (ruleForm.checkPass !== '') {
      if (!ruleFormRef.value) return
      ruleFormRef.value.validateField('checkPass', () => null)
    }
    callback()
  }
}
// 确认密码
const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请确认密码'))
  } else if (value.toString().length < 5 || value.toString().length > 10) {
    return callback(new Error('密码长度在5到10位之间'))
  } else if (!ruleForm.password) {
    return ruleFormRef.value.validateField('password', () => null)
  } else if (value !== ruleForm.password) {
    callback(new Error('两次密码输入不一致'))
  } else {
    callback()
  }
}
const rules = reactive({
  username: [
    { required: true, message: '输入账户名', trigger: 'blur' },
    { min: 5, max: 10, message: '长度在5到10位之间', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '输入用户昵称', trigger: 'blur' },
    { min: 2, max: 5, message: '长度在5到10位之间', trigger: 'blur' }
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
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { min: 5, max: 5, message: '验证码为长度为5', trigger: 'blur' }
  ],
  password: [{ required: true, message: '请输入密码', validator: validatePass, trigger: 'blur' }],
  checkPass: [{ required: true, message: '请确认密码', validator: validatePass2, trigger: 'blur' }]
})

const submitForm = async (formEl) => {
  if (!formEl) return
  formEl.validate(async (valid, fields) => {
    if (valid) {
      try {
        await request.post('/auth/register', ruleForm)

        ElNotification({
          title: '注册成功',
          message: `${ruleForm.nickname} 欢迎您!`,
          type: 'success'
        })

        $route.replace('/')
      } catch (error) {
        getCaptcha()
      }
    }
  })
}

//重置方法
const resetForm = (formEl) => {
  if (!formEl) return
  formEl.resetFields()
  getCaptcha()
}

const options = Array.from({ length: 10000 }).map((_, idx) => ({
  value: `${idx + 1}`,
  label: `${idx + 1}`
}))
const handleAvatarSuccess = (response, uploadFile) => {
  ruleForm.avatar = URL.createObjectURL(uploadFile.raw)
}

//获取验证码
const getCaptcha = async () => {
  const res = await request.get('/auth/captcha')

  captchaImg.value = res.data.captchaImg
  ruleForm.token = res.data.token
  ruleForm.code = ''
}
getCaptcha()
</script>
<script>
export default {
  name: 'Register'
}
</script>
<template>
  <el-row>
    <el-col>
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
            action="http://localhost:9090/api/file/upload"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
          >
            <el-avatar :size="30" v-if="ruleForm.avatar" :src="ruleForm.avatar" />
            <el-icon v-else class="avatar-uploader-icon">
              <Plus />
            </el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="账户名称" prop="username">
          <el-input v-model="ruleForm.username" />
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
        <el-form-item label="新的密码" prop="password">
          <el-input v-model="ruleForm.password" type="password" autocomplete="off" />
        </el-form-item>
        <el-form-item label="确认密码" prop="checkPass">
          <el-input v-model="ruleForm.checkPass" type="password" autocomplete="off" />
        </el-form-item>

        <el-form-item label="验证码" prop="code">
          <el-row align="middle">
            <el-col :span="12">
              <el-input v-model="ruleForm.code" prefix-icon="Crop" />
            </el-col>
            <el-col :span="10" :offset="1" style="display: flex; align-items: center">
              <img :src="captchaImg" @click="getCaptcha" style="border-radius: 3px 3px" />
            </el-col>
          </el-row>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm(ruleFormRef)"> 确认注册 </el-button>
          <el-button @click="resetForm(ruleFormRef)">重置</el-button>
        </el-form-item>
      </el-form>
    </el-col>
  </el-row>
</template>

<style scoped lang="scss"></style>
