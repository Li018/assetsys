<script setup>
import { reactive, ref } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import router from '../../router'

const ruleFormRef = ref()
// 校验原密码
const checkPass = (rule, value, callback) => {
  if (!value) {
    return callback(new Error('请输入原密码'))
  }

  if (value.toString().length < 5 || value.toString().length > 10) {
    return callback(new Error('密码长度在5到10位之间'))
  }
  callback()
}
// 校验新密码
const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入新密码'))
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

const ruleForm = reactive({
  password: '',
  checkPass: '',
  oldpass: ''
})

const rules = reactive({
  password: [
    { required: true, message: '输入新密码', trigger: 'blur' },
    { validator: validatePass, trigger: 'blur' }
  ],
  checkPass: [
    { required: true, message: '确认新密码', trigger: 'blur' },
    { validator: validatePass2, trigger: 'blur' }
  ],
  username: [
    { required: true, message: '输入用户账户名', trigger: 'blur' },
    { min: 5, max: 10, message: '长度在5到10位之间', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '输入邮箱验证码', trigger: 'blur' },
    { min: 5, max: 5, message: '验证码为为五位字符串', trigger: 'blur' }
  ]
})

const submitForm = (formEl) => {
  if (!formEl) return
  formEl.validate(async (valid) => {
    if (valid) {
      await request.post('/auth/findpassword', ruleForm)
      ElMessage({
        showClose: true,
        message: '修改密码成功！',
        type: 'success'
      })
      router.to('/login')
    } else {
      return false
    }
  })
}

const resetForm = (formEl) => {
  if (!formEl) return
  formEl.resetFields()
}
// 获取验证码
const handleGetEmailCode = async () => {
  if (!ruleForm.username) {
    ElMessage({
      showClose: true,
      message: '账户名不能为空',
      type: 'error'
    })
    return false
  } else {
    await request.get(`/auth/email/${ruleForm.username} `)
    ElMessage({
      showClose: true,
      message: '验证码已经发送到您的邮箱，有效时间为5分钟,请及时填写！',
      type: 'success'
    })
  }
}
</script>
<script>
export default {
  name: 'Forget'
}
</script>
<template>
  <el-row>
    <el-col>
      <el-form
        ref="ruleFormRef"
        :model="ruleForm"
        status-icon
        :rules="rules"
        label-width="80px"
        class="demo-ruleForm"
      >
        <el-form-item label="账户名" prop="username">
          <el-input v-model="ruleForm.username" autocomplete="off" />
        </el-form-item>
        <el-form-item label="新的密码" prop="password">
          <el-input v-model="ruleForm.password" type="password" autocomplete="off" />
        </el-form-item>
        <el-form-item label="确认密码" prop="checkPass">
          <el-input v-model="ruleForm.checkPass" type="password" autocomplete="off" />
        </el-form-item>
        <el-form-item label="验证码" prop="code">
          <el-row :gutter="10">
            <el-col :span="15">
              <el-input v-model="ruleForm.code" autocomplete="off" />
            </el-col>
            <el-col :span="6">
              <el-button type="warning" @click="handleGetEmailCode">获取邮箱验证码</el-button>
            </el-col>
          </el-row>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm(ruleFormRef)">确认修改 </el-button>
          <el-button @click="resetForm(ruleFormRef)">重置</el-button>
        </el-form-item>
      </el-form>
    </el-col>
  </el-row>
</template>

<style scoped lang="scss"></style>
