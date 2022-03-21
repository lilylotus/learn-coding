<template>
    <div>
        <h2>Home组件内容</h2>
        <div>
            <ul class="nav nav-tabs">
                <li>
                    <router-link class="list-group-item" active-class="active" to="/home/news">News</router-link>
                </li>
                <li>
                    <router-link class="list-group-item" active-class="active" to="/home/message">Message</router-link>
                </li>
            </ul>
            
            <!-- keep-alive 默认不会销毁路过产生的所有组件 -->
            <!-- include 指定仅某些组件不被销毁，这里的是组件的名称 -->
            <!-- <keep-alive include="News"> -->
            <!-- 操作多个 -->
            <keep-alive :include="['News']">
                <router-view></router-view>
            </keep-alive>
        </div>
    </div>
</template>

<script>
export default {
    name: 'Home',
    // beforeDestroy() {
    //     console.log("Home 组件即将被销毁");
    // },
    // mounted() {
    //     console.log("Home 组件挂载完成", this);
    //     window.homeRoute = this.$route
    //     window.homeRouter = this.$router
    // }
}
</script>

<style>

</style>