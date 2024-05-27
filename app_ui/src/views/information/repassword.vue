<script setup>
import { reactive, ref } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

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
  password: [{ validator: validatePass, trigger: 'blur' }],
  checkPass: [{ validator: validatePass2, trigger: 'blur' }],
  oldpass: [{ validator: checkPass, trigger: 'blur' }]
})

const submitForm = (formEl) => {
  if (!formEl) return
  formEl.validate(async (valid) => {
    if (valid) {
      await request.post('/auth/repassword', ruleForm)
      ElMessage({
        showClose: true,
        message: '操作成功',
        type: 'success'
      })
      window.location.reload(true)
    } else {
      return false
    }
  })
}

const resetForm = (formEl) => {
  if (!formEl) return
  formEl.resetFields()
}
</script>
<script>
export default {
  name: 'RepassWord'
}
</script>
<template>
  <el-row>
    <el-col :sm="24" :xs="24" :md="8" :lg="8" :xl="8">
      <el-form
        ref="ruleFormRef"
        :model="ruleForm"
        status-icon
        :rules="rules"
        label-width="80px"
        class="demo-ruleForm"
      >
        <el-form-item label="原始密码" prop="oldpass">
          <el-input v-model="ruleForm.oldpass" type="password" autocomplete="off" />
        </el-form-item>
        <el-form-item label="新的密码" prop="password">
          <el-input v-model="ruleForm.password" type="password" autocomplete="off" />
        </el-form-item>
        <el-form-item label="确认密码" prop="checkPass">
          <el-input v-model="ruleForm.checkPass" type="password" autocomplete="off" />
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
