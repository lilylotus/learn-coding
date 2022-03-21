<template>
    <div>
        <h1>About 的内容</h1>
        <hr>
        <!-- :picker-options="{selectableRange: '18:30:00 - 20:30:00'}" -->
        <!-- <el-time-picker v-model="value1"
            :editable="false"
            align="center"
            format="HH:mm:ss"
            value-format="HH:mm:ss"
            placeholder="任意时间点">
        </el-time-picker>
        <hr>
        <span> 所选时间 {{value1}} </span> -->
    </div>
</template>

<script>
export default {
    name: 'About',
    data() {
        return {
            value1: new Date()
        }
    },
    // beforeDestroy() {
    //     console.log("About 组件即将被销毁");
    // },
    // mounted() {
    //     console.log("About 组件挂载完成", this);
    //     // this.$route 是自己的 route， this.$router 所有的都一个对象
    //     window.aboutRoute = this.$route
    //     window.aboutRouter = this.$router
    // },
    // 通过路由规则进入该组件的时候被调用
    beforeRouteEnter(to, from, next) {
        console.log('About - beforeRouteEnter')
        next()
    },
    // 通过路由规则，离开该组件时被调用
    beforeRouteLeave(to, from, next) {
        console.log('About - beforeRouteLeave')
        next()
    },
    mounted() {
        // console.log(this.$store)
        console.log('About Page mounted')
        // window.vuexStore = this.$store;
    }

}
</script>

<style>

</style>