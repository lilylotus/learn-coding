<template>
   <div>
        <ul>
            <li v-for="m in messageList" :key="m.id">
                <!-- 路由传递 query 参数 -->
                <!-- <router-link :to="`/home/message/detail?id=${m.id}&msg=${m.msg}`">{{m.msg}}</router-link> -->
                <!-- <router-link :to="{
                    path: '/home/message/detail',
                    query: {
                        id: m.id,
                        msg: m.msg
                    }
                }">
                    {{m.msg}}
                </router-link> -->

                <!-- 路由 params 参数 - /detail/5555/你好呀 -->
                <!-- <router-link :to="`/home/message/detail/${m.id}/${m.msg}`">{{m.msg}}</router-link> -->
                <router-link :to="{
                    // params 方式这里仅能使用 name ，不能使用 path
                    name: 'detail',
                    params: {
                        id: m.id,
                        msg: m.msg
                    }
                }">{{m.msg}}</router-link>
                <button @click="pushShow(m)">push查看</button>
                <button @click="replaceShow(m)">replace查看</button>
            </li>
        </ul>
        <hr>
        <router-view></router-view>
    </div>
</template>

<script>
export default {
    name: 'Message',
    data() {
        return {
            messageList: [
                {id: '001', msg: 'message001'},
                {id: '002', msg: 'message002'},
                {id: '003', msg: 'message003'}
            ]
        }
    },
    methods: {
        pushShow(data) {
            // console.log(this.$router)
            this.$router.push({
                name: 'detail',
                params: {
                    id: data.id,
                    msg: data.msg
                }
            });
        },
        replaceShow(data) {
          this.$router.replace({
                name: 'detail',
                params: {
                    id: data.id,
                    msg: data.msg
                }
            });
        }
    }
}
</script>

<style>

</style>