<script setup>
import { reactive, ref } from 'vue'
import request from '@/utils/request'
import { ElNotification } from 'element-plus'
import useSystemStore from '@/stores/system'
import router from '@/router'

import Register from '@/views/login/register.vue'
import Forget from '@/views/login/forgetpassword.vue'

const $route = router
const systemStore = useSystemStore()
const formSize = ref('default')
const ruleFormRef = ref()
const captchaImg = ref()
const activeName = ref('first')
const ruleForm = reactive({
  username: '',
  password: '',
  code: '',
  token: ''
})
// 校验规则
const rules = reactive({
  username: [
    { required: true, message: '请输入账户名', trigger: 'blur' },
    { min: 5, max: 10, message: '账户名在5到10位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 5, max: 10, message: '密码在5到10位', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { min: 5, max: 5, message: '验证码为长度为5', trigger: 'blur' }
  ]
})
//登录方法
const submitForm = async (formEl) => {
  if (!formEl) return
  formEl.validate(async (valid, fields) => {
    if (valid) {
      try {
        const res = await request.post('/auth/login', ruleForm)
        systemStore.setToken(res.data.tokenValue)
        ElNotification({
          title: '登录成功',
          message: `${ruleForm.username} 欢迎您!`,
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
//获取验证码
const getCaptcha = async () => {
  const res = await request.get('/auth/captcha')

  captchaImg.value = res.data.captchaImg
  ruleForm.token = res.data.token
  ruleForm.code = ''
}
getCaptcha()
</script>

<template>
  <div class="app_login">
    <div class="login_top">
      <img class="top_logo" src="/public/logo.png" />
      <p class="top_title">assetsys</p>
    </div>
    <div class="login_main">
      <el-row align="middle">
        <el-col :xs="0" :sm="0" :md="0" :lg="4" :xl="6">
          <div></div>
        </el-col>

        <el-col :xs="0" :sm="9" :md="12" :lg="10" :xl="11">
          <div></div>
        </el-col>
        <el-col :xs="24" :sm="14" :md="12" :lg="9" :xl="6">
          <div class="main_form">
            <el-card>
              <el-tabs v-model="activeName">
                <el-tab-pane label="登录" name="first">
                  <el-form
                    ref="ruleFormRef"
                    :model="ruleForm"
                    :rules="rules"
                    label-width="100px"
                    class="demo-ruleForm"
                    :size="formSize"
                    status-icon
                  >
                    <el-form-item label="账户名" prop="username">
                      <el-input v-model="ruleForm.username" prefix-icon="User" />
                    </el-form-item>
                    <el-form-item label="密码" prop="password">
                      <el-input
                        v-model="ruleForm.password"
                        prefix-icon="Lock"
                        type="password"
                        show-password
                      />
                    </el-form-item>
                    <el-form-item label="验证码" prop="code">
                      <el-row align="middle">
                        <el-col :span="12">
                          <el-input v-model="ruleForm.code" prefix-icon="Crop" />
                        </el-col>
                        <el-col :span="10" :offset="1" style="display: flex; align-items: center">
                          <img
                            :src="captchaImg"
                            @click="getCaptcha"
                            style="border-radius: 3px 3px"
                          />
                        </el-col>
                      </el-row>
                    </el-form-item>

                    <br />
                    <div style="display: flex; justify-content: center">
                      <el-button type="primary" @click="submitForm(ruleFormRef)"> 登录</el-button>
                      <el-button @click="resetForm(ruleFormRef)">重置</el-button>
                    </div>
                  </el-form>
                </el-tab-pane>

                <el-tab-pane label="注册" name="second">
                  <Register></Register>
                </el-tab-pane>
                <el-tab-pane label="忘记密码" name="third">
                  <Forget></Forget>
                </el-tab-pane>
              </el-tabs>

              <br />
              <br />
            </el-card>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<style scoped lang="scss">
.app_login {
  width: 100vw;
  height: 100vh;
  background: linear-gradient(
    135deg,
    rgba(0, 255, 204, 0.85),
    rgba(255, 153, 204, 0.85),
    rgba(153, 0, 204, 0.85)
  );

  .login_top {
    padding-left: 10px;
    width: 100%;
    height: 40px; /* 调整合适的高度 */
    display: flex;
    align-items: center; /* 在垂直方向上居中 */
    background: linear-gradient(135deg, rgba(0, 0, 0, 0.1), rgba(0, 0, 0, 0.3));

    .top_logo {
      width: 40px;
      height: 40px;
    }

    .top_title {
      font-size: 24px; /* 调整字号 */
      font-family: '黑体'; /* 使用更好看的字体 */
      color: #ffffff; /* 设置字体颜色 */
      text-align: center;
      line-height: 1.2; /* 调整行高，使文本更清晰易读 */
      letter-spacing: 1px; /* 调整字符间距，增加可读性 */
    }
  }

  .login_main {
    width: 100vw;
    height: calc(100vh - 40px);

    .el-row {
      .main_form {
        margin-top: 220px;
      }
    }
  }
}
</style>
