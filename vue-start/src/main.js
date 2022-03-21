import Vue from 'vue';
import App from './App.vue';
import store from './store/index';
import VueRouter from 'vue-router';
import router from './router';

// 引入 ElementUI
// import ElementUI from 'element-ui';
// 按需引入
// import { Button, Row, TimePicker } from 'element-ui'
// import 'element-ui/lib/theme-chalk/index.css';

Vue.config.productionTip = false

Vue.use(VueRouter);
// Vue.use(ElementUI);
// 引入组件
// Vue.component(Button.name, Button)
// Vue.component(Row.name, Row)
// Vue.component(TimePicker.name, TimePicker)

// 解决 ElementUI 导航栏中的 vue-router 在 3.0 版本以上重复点菜单报错问题
const originalPush = VueRouter.prototype.push
const originalReplace = VueRouter.prototype.replace
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => err)
}
VueRouter.prototype.replace = function replace(location) {
  return originalReplace.call(this, location).catch(err => err)
}

new Vue({
  render: h => h(App),
  store,
  router: router
  // render(createElement) {
  //   return createElement('h1', '你好 Render')
  // }
  // =  render: h => h('h1', '你好');
}).$mount('#app')
