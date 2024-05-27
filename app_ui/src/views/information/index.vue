<script setup>
import useSystemStore from '@/stores/system'
import pinia from '@/stores/store'
import Info from '@/views/information/userInfo.vue'
import RepassWord from '@/views/information/repassword.vue'
import { ref } from 'vue'
const systemStore = useSystemStore(pinia)
const userInfo = systemStore.userInfo
const activeName = ref('first')
</script>

<template>
  <!--  一个大盒子里面两个盒子，第一个盒子展示自己的身份信息，第二个盒子修改自己的个人信息和密码-->
  <el-row :gutter="20" :justify="space - around">
    <el-col :span="24">
      <el-descriptions :column="2" title="个人信息" size="default" border>
        <el-descriptions-item label="账户名">{{ userInfo.username }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ userInfo.nickname }}</el-descriptions-item>
        <el-descriptions-item label="当前状态">
          <el-tag type="success" v-if="userInfo.statu == 1">正常</el-tag>
          <el-tag type="danger" v-if="userInfo.statu == 2">封禁</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="手机号">{{ userInfo.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ userInfo.email }}</el-descriptions-item>
        <el-descriptions-item label="个人简介">{{ userInfo.content }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ userInfo.createTime }}
        </el-descriptions-item>
        <el-descriptions-item label="修改时间">
          {{ userInfo.updateTime }}
        </el-descriptions-item>
      </el-descriptions>
    </el-col>
    <el-divider></el-divider>
    <el-col :span="24">
      <el-tabs v-model="activeName" class="demo-tabs" @tab-click="handleClick">
        <el-tab-pane label="个人信息" name="first">
          <Info></Info>
        </el-tab-pane>
        <el-tab-pane label="修改密码" name="second">
          <RepassWord></RepassWord>
        </el-tab-pane>
      </el-tabs>
    </el-col>
  </el-row>
</template>

<style scoped lang="scss"></style>
