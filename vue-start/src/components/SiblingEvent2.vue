<template>
    <div>
        <span>兄弟组件 2 共享数据 {{shareData}}</span><br>
        <button @click="incrementShareData(2)">共享数据 + 2</button>
        <button @click="triggerIncrementEvent">触发 ref - $on 绑定事件</button>
        <button @click="triggerGlobalEvent">触发全局绑定事件</button>
    </div>
</template>

<script>
export default {
    name: 'SiblingEvent2',
    props: ["shareData", 'incrementShareData'],
    methods: {
        triggerIncrementEvent() {
            this.$emit('sibling2IncrementEvent', 2);
        },
        triggerGlobalEvent() {
            this.$bus.$emit('globalEvent', 88, 99);
        }
    }
}
</script>

<style>

</style>