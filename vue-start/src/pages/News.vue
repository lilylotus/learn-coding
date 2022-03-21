<template>
    <div>
        <h4 :style="{opacity}">欢迎，欢迎</h4>
        <ul>
            <li v-for="n in newsList" :key="n.id">
                {{n.msg}} <input type="text">
            </li>
        </ul>
    </div>
</template>

<script>
export default {
    name: 'News',
    data() {
        return {
            opacity: 1,
            newsList: [
                {id: '001', msg: 'news001'},
                {id: '002', msg: 'news002'},
                {id: '003', msg: 'news003'}
            ]
        }
    },
    beforeDestroy() {
        console.log('news 即将被销毁了')
        // clearInterval(this.timer)
    },
    mounted() {
        // console.log('mounted', this)
    },
    // vue-router 新加的两个钩子函数，路由页面被激活和失活的时候调用
    activated() {
        console.log('News 被激活')
        this.timer = setInterval(() => {
            // console.log('@')
            this.opacity -= 0.01;
            if (this.opacity <= 0) {
                this.opacity = 1
            }
        }, 16)
    },
    deactivated() {
        console.log('News 未被激活')
        clearInterval(this.timer)
    }
}
</script>

<style>

</style>