<script setup>
import { AiEditor } from 'aieditor'
import 'aieditor/dist/style.css'
import { onMounted, onUnmounted, ref } from 'vue'
const divRef = ref()
let aiEditor = null

onMounted(() => {
  aiEditor = new AiEditor({
    element: divRef.value,
    ai: {
      model: {
        xinghuo: {
          appId: 'ae357409',
          apiKey: '003f7d52cf31273ff7dd9c32be4c7a62',
          apiSecret: 'MGQwY2MwYmM5MmZkMTkxM2RmZTQ3NmEy'
        }
      },
      bubblePanelEnable: true,
      bubblePanelModel: 'xinghuo'
    },
    placeholder: '点击输入内容...',
    content: 'AiEditor 是一个面向 AI 的开源富文本编辑器。 '
  })
})

onUnmounted(() => {
  aiEditor && aiEditor.destroy()
})
</script>
<template>
  <div ref="divRef" style="height: 600px" />
</template>

<style scoped></style>
