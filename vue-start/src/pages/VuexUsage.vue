<template>
  <div>
      <h2>vuex store</h2>
      <hr>
      <vuex-share-one></vuex-share-one>
      <hr>
      <vuex-share-two></vuex-share-two>
  </div>
</template>

<script>
import VuexShareOne from '../components/VuexShareOne.vue'
import VuexShareTwo from '../components/VuexShareTwo.vue'

export default {
    name: 'VuexUsage',
    components: { VuexShareOne, VuexShareTwo }
}
</script>

<style>

</style>