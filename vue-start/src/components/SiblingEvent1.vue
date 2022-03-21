<template>
    <div>
        <span>兄弟组件 1 共享数据 {{shareData}} </span><br>
        <button @click="incrementShareData(3)">共享数据 + 3</button>
        <button @click="triggerIncrementEvent">触发 v-on/@ 绑定事件</button>
        <button @click="unbindIncrementEvent">解绑定事件</button>
        <button @click="selfFree">自我销毁 destroy</button>
    </div>
</template>

<script>
export default {
    name: 'SiblingEvent1',
    props: ['shareData', 'incrementShareData'],
    methods: {
        triggerIncrementEvent() {
            // 触发该组件绑定的事件 this.$emit('事件', 参数); ('name', ...params)
            this.$emit('sibling1IncrementEvent', 3);
        },
        unbindIncrementEvent() {
            console.log('完成指定事件解绑')
            this.$off('sibling1IncrementEvent');
            // 解绑多个事件 this.$off(['event1', 'event2']);
            // 解绑所有绑定的事件 this.$off() 不传递参数
        },
        selfFree() {
            console.log('销毁当前实例对象');
            // 销毁时会把所有的(自定义绑定事件)给解绑掉，包括其所有的子组件的绑定事件
            this.$destroy();
        },
        invokeGlobalEvent(data, ...params) {
            this.triggerIncrementEvent();
            console.log('全局事件被触发调用', data, params);
        }
    },
    mounted() {
        // 在全局事件总线上绑定事件
        // 注意在组件实例销毁的时候要取消绑定的事件
        console.log('在全局事件总线上绑定事件');
        this.$bus.$on('globalEvent', this.invokeGlobalEvent);
    },
    beforeDestroy() {
        console.log('解绑在全局事件总线上绑定的事件');
        // 注意在组件实例销毁的时候要取消绑定的事件
        this.$bus.$off(['globalEvent']);
    }
}
</script>

<style>

</style>