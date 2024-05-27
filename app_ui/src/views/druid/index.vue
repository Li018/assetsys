<script setup></script>

<template>
  <iframe
    id="druidframe"
    src="http://127.0.0.1:9090/api/druid/login.html"
    width="100%"
    height="1000"
    sandbox="allow-forms allow-modals allow-popups allow-scripts allow-same-origin"
  >
  </iframe>
</template>

<style scoped lang="scss">
#druidframe {
  width: 100%;
  height: calc(100vh - 30px);
}
</style>
