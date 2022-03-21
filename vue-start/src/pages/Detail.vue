<template>
    <ul>
        <!-- query 参数传递方式 /home/message/detail?id=5555&msg=你好 -->
        <!-- <li>消息编号 {{$route.query.id}} </li>
        <li>消息信息 {{$route.query.msg}} </li> -->

        <!-- params 方式传递 -->
        <li>消息编号 {{$route.params.id}} </li>
        <li>消息信息 {{$route.params.msg}} </li>

        <hr>
        <!-- 以 props 方式传递 -->
        <li>消息编号 {{id}} </li>
        <li>消息信息 {{msg}} </li>
    </ul>
</template>

<script>
export default {
    name: 'Detail',
    props: ["id", "msg"]
    // mounted() {
    //     console.log('detail mounted', this.$route)
    // }
}
</script>

<style>

</style>