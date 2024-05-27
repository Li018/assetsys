<template>
  <div class="items rowCC" ref="itemRef">
    <div class="item rowCC" v-for="(item, index) in ip" :key="index">
      <el-input
          v-model="item.value"
          @input="onChange($event, index)"
          @keyup="onControl($event, index)"
          @keydown="onControl($event, index)"
          @keypress.prevent.enter="initAddress"
          :ref="(el) => setItemRef(el, 'ipRef' + index)"
      />
      <div class="point" v-if="!(index === ip.length - 1)">.</div>
    </div>
  </div>
</template>

<script lang="ts">
import { nextTick, ref, defineProps, defineEmits } from "vue";

export default {
  props: {
    modelValue: String,
  },
  emits: ['update:modelValue'],

  setup(props, { emit }) {
    let ip = ref([
      { value: '' },
      { value: '' },
      { value: '' },
      { value: '' },
    ]);
    let ipAddress = ref('');
    let refs = ref({});

    const setItemRef = (el, key) => {
      refs.value[key] = el;
    };

    const initAddress = () => {
      ipAddress.value = ip.value
          .map((item) => item.value)
          .join('.');
    };

    const onControl = (e, index) => {
      switch (e.code) {
        case 'Backspace':
          if (ip.value[index].value === '') {
            initAddress();
            if (index === 0) {
              return;
            }
            nextTick(() => {
              refs.value['ipRef' + (index - 1)].focus();
            });
          }
          break;
        case 'ArrowLeft':
          if (index === 0) return;
          if (e.srcElement.selectionStart === 0) {
            nextTick(() => {
              refs.value['ipRef' + (index - 1)].focus();
            });
          }
          break;
        case 'ArrowRight':
          if (index === 3) {
            return;
          }
          if (e.srcElement.selectionStart === e.srcElement.value.length) {
            nextTick(() => {
              refs.value['ipRef' + (index + 1)].focus();
            });
          }
          break;
      }
    };

    const onChange = (e, index) => {
      if (e[e.length - 1] === '.' || e[e.length - 1] === '。') {
        e = e.substring(0, e.length - 1);
        if (index === 3) {
          ip.value[index].value = e;
          return;
        }
        refs.value['ipRef' + (index + 1)].focus();
      }

      if (isNaN(parseInt(e)) || parseInt(e) < 0) {
        ip.value[index].value = '';
        return;
      }

      if (Number(e) >= 255) {
        ip.value[index].value = 255;
        nextTick(() => {
          if (index + 1 === 4) {
            initAddress();
            return;
          }
          refs.value['ipRef' + (index + 1)].focus();
        });
        return;
      } else if (Number(e) >= 100) {
        ip.value[index].value = e;
        nextTick(() => {
          if (index + 1 === 4) {
            initAddress();
            return;
          }
        });
        return;
      } else {
        ip.value[index].value = e;
      }
      initAddress();
      emit('update:modelValue', ipAddress.value);
    };

    return {
      ip,
      setItemRef,
      initAddress,
      onControl,
      onChange,
    };
  },
};
</script>

<style scoped lang="scss">
.items {
  border: 1px solid #dcdfe6;
  border-radius: var(--el-input-border-radius, var(--el-border-radius-base));
  display: flex; /* Make items flex container to align items in the same row */

  .item {
    display: flex; /* Make each item flex container to align input and point in the same row */
    align-items: center; /* Align items vertically in the center */
    margin-right: 5px; /* Add margin between items for spacing */
  }

  .point {
    user-select: none;
  }

  :deep(.el-input--large .el-input__wrapper) {
    border: none !important;
    box-shadow: none !important;
    padding: 1px 10px !important;
  }

  :deep(.el-input__inner) {
    width: 100%;
    padding: 0 !important;
  }
}
</style>
