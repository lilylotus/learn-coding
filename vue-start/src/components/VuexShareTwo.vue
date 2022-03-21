<template>
  <div>
      <h2>vuex data share two</h2>
      <h4 style="color: brown;">多组件共享数据：当前求和为 {{vuexSum}}</h4>
  </div>
</template>

<script>
import {mapState} from 'vuex'

export default {
    name: 'VuexShareTwo',
    computed: {
      ...mapState(['vuexSum'])
    }
}
</script>

<style>

</style>