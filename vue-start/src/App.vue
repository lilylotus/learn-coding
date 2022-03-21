<template>
  <div id="app">

  <!-- <el-row>
    <el-button>默认按钮</el-button>
    <el-button type="primary">主要按钮</el-button>
    <el-button type="success">成功按钮</el-button>
    <el-button type="info">信息按钮</el-button>
    <el-button type="warning">警告按钮</el-button>
    <el-button type="danger">危险按钮</el-button>
  </el-row> -->

  <hr>
  
  <Banner/>

    <div class="row">
      <div class="col-xs-2 col-xs-offset-2">
        <div class="list-group">
          <!-- 原始使用 a 标签路由跳转 -->
          <!-- <a class="list-group-item" href="./about.html">About</a> -->
          <!-- <a class="list-group-item active" href="./home.html">Home</a> -->

          <!-- 采用 router 管理 router-link -->
          <!-- replace 开启路由 浏览器历史记录替换模式 -->
          <router-link class="list-group-item" active-class="active" :to="{name: 'about'}">About</router-link>
          <router-link class="list-group-item" active-class="active" to="/home">Home</router-link>
          <router-link class="list-group-item" active-class="active" to="/vuex">Vuex</router-link>
          <router-link class="list-group-item" active-class="active" to="/axios">Axios</router-link>
        </div>
      </div>

      <div class="col-xs-6">
        <div class="panel">
          <div class="panel-body">
            <router-view></router-view>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
import Banner from './pages/Banner.vue';

export default {
  name: 'App',
  components: {
    Banner
  }
}
</script>

<style>
/* #app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
} */
</style>
