<template>
  <div>
      <h2>事件绑定学习，共享数据= {{shareData}}</h2>
      <button @click="incrementShareData(1)">共享数据 + 1</button>
      <hr>
      <!-- 事件 - 子组件和父组件通信，给谁绑定的就去那个组件中去触发，事件的回调保存在绑定的方法中（父组件中） -->
      <!-- 全局事件总线才能在任意组件间通信 $bus -->
      <!-- 通过父组件给子组件传递函数类型 props 实现，子给父传递数据 -->
      <!-- v-on: 或 @ 绑定事件，当事件被触发就会去调用回调函数，绑定到组件实例 vc 上去，绑定给那个组件就去那个组件去触发该事件 -->
      <!-- 此方式 仅允许触发一次写法 v-on:event.once='callback' -->
      <!-- 若是给组件绑定原生的 html 事件写法, @click.native="show" ，添加 .native 修饰，不然都会当成自定义事件 -->
      <SiblingEvent1 :shareData="shareData"
            :incrementShareData="incrementShareData"
            v-on:sibling1IncrementEvent="sibling1Increment"
        />
      <hr>
      <!-- 通过 ref 获取到组件 vc 实例，在挂载完成时候绑定事件，这种方式灵活性强 -->
      <SiblingEvent2 :shareData="shareData" 
        :incrementShareData="incrementShareData"
        ref="sibling2"
        />
  </div>
</template>

<script>
import SiblingEvent1 from '../components/SiblingEvent1.vue'
import SiblingEvent2 from '../components/SiblingEvent2.vue'

export default {
    name: 'EventUsage',
    data() {
        return {
            shareData: 10
        }
    },
    components: {SiblingEvent1, SiblingEvent2},
    methods: {
        incrementShareData(step) {
            this.shareData += step;
        },
        sibling1Increment(value) {
            console.log('sibling1Increment 被调用了');
            this.shareData += value;
        },
        sibling2Increment(value) {
            console.log('sibling2Increment 被调用了');
            this.shareData += value;
        }
    },
    mounted() {
        // this.$refs.sibling2 获取到组件对象 vc 实例， $on 绑定事件
        console.log('通过 ref 挂载时绑定事件');
        this.$refs.sibling2.$on('sibling2IncrementEvent', this.sibling2Increment);
        // 仅允许触发一次
        // this.$refs.sibling2.$once('sibling2IncrementEvent', this.sibling2Increment);

        // this.$refs.sibling2.$on('sibling2IncrementEvent', function(value, ...params) {
        //     console.log('sibling2 绑定了事件', this);
        //     // 注意：这里的这个 this 不是当前组件的实例对象，而是绑定到组件的实例对象
        // });

        // this.$refs.sibling2.$on('sibling2IncrementEvent', (value, ...params) => {
        //     console.log('sibling2 绑定了事件', this);
        //     // 注意：这里的这个 this 就是是当前组件的实例对象，而不是上面绑定到组件的实例对象，使用的是箭头函数的方式
        // });
    }
}
</script>

<style>

</style>