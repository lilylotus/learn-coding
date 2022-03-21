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

// 注意：原型，VueComponent.prototype.__proto__ === Vue.prototype
// const component = Vue.extend({}) // -> 创建一个 VueComponente - vc， Vue - vm
// const vc = new component();
// Vue.prototype.x = vc;
// 于是乎在所有的 VueComponent 实例上就能访问到这个 x 变量, this.x

// 绑定事件到全局的 x vc 实例上
// mounted() {
//   this.x.$on('globalEvent', (data) => {
//     console.log('全局事件回调函数被调用', data)
//   })
// };

// 触发全局事件
// triget() {
//   this.x.$emit('globalEvent', 88);
// }

new Vue({
  render: h => h(App),
  store,
  router: router,
  // render(createElement) {
  //   return createElement('h1', '你好 Render')
  // }
  // =  render: h => h('h1', '你好');
  // 正确的创建全局事件总线
  beforeCreate() {
    Vue.prototype.$bus = this;
  }
}).$mount('#app')
