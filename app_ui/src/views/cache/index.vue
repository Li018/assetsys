<script setup>
import request from '@/utils/request'
import useSystemStore from '@/stores/system'
import { reactive } from 'vue'
// const systemStore = useSystemStore()
const systemInformation = reactive({})
const handlerGetSystemInformation = async () => {
  const res = await request.get('/system')
  Object.assign(systemInformation, res.data)
}
handlerGetSystemInformation()
</script>

<template>
  <el-row :gutter="10">
    <el-col>
      <el-descriptions class="margin-top" title="系统参数" :column="1" size="default" border>
        <el-descriptions-item>
          <template #label>
            <div class="cell-item">
              <el-icon><Menu /></el-icon>
              系统名称
            </div>
          </template>
          {{ systemInformation.osName }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <div class="cell-item">
              <el-icon><DataLine /></el-icon>
              系统架构
            </div>
          </template>
          {{ systemInformation.osArch }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <div class="cell-item">
              <el-icon><Star /></el-icon>
              系统版本
            </div>
          </template>
          {{ systemInformation.osVersion }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <div class="cell-item">
              <el-icon><Collection /></el-icon>
              文件分割符
            </div>
          </template>
          {{ systemInformation.fileSeparator }}
        </el-descriptions-item>

        <el-descriptions-item>
          <template #label>
            <div class="cell-item">
              <el-icon><Cpu /></el-icon>
              CPU核心数
            </div>
          </template>
          {{ systemInformation.availableProcessors }}
        </el-descriptions-item>

        <el-descriptions-item>
          <template #label>
            <div class="cell-item">
              <el-icon><Coffee /></el-icon>
              Java版本
            </div>
          </template>
          {{ systemInformation.javaVersion }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <div class="cell-item">
              <el-icon><Refrigerator /></el-icon>
              虚拟机名称
            </div>
          </template>
          {{ systemInformation.javaVmName }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <div class="cell-item">
              <el-icon><OfficeBuilding /></el-icon>
              Java供应商
            </div>
          </template>
          {{ systemInformation.javaVendor }}
        </el-descriptions-item>

        <el-descriptions-item>
          <template #label>
            <div class="cell-item">
              <el-icon><Stopwatch /></el-icon>
              供应商官网
            </div>
          </template>
          {{ systemInformation.javaVenDorUrl }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <div class="cell-item">
              <el-icon><Coffee /></el-icon>
              最大内存
            </div>
          </template>
          {{ systemInformation.maxMemoryGB }} GB
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <div class="cell-item">
              <el-icon><Coffee /></el-icon>
              空闲内存
            </div>
          </template>
          {{ systemInformation.freeMemoryGB }} GB
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <div class="cell-item">
              <el-icon><Document /></el-icon>
              磁盘总容量
            </div>
          </template>
          {{ systemInformation.totalDiskSpaceGB }} GB
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <div class="cell-item">
              <el-icon><DocumentChecked /></el-icon>
              磁盘剩余容量
            </div>
          </template>
          {{ systemInformation.freeDiskSpaceGB }} GB
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <div class="cell-item">
              <el-icon><Folder /></el-icon>
              项目目录
            </div>
          </template>
          {{ systemInformation.userDir }}
        </el-descriptions-item>
      </el-descriptions>
    </el-col>
  </el-row>
</template>

<style scoped lang="scss">
.el-descriptions {
  margin-top: 20px;
}
.cell-item {
  display: flex;
  align-items: center;
}
.margin-top {
  margin-top: 20px;
}
:deep(.my-label) {
  background: none !important;
}
.el-row {
  .el-col {
    margin-top: 3px;
  }
}
</style>
