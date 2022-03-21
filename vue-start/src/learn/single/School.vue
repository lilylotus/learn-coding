<template>
    <div class="school">
        <h2>学校名称： {{name}}</h2>
        <h2>学校地址： {{address}}</h2>
        <button @click="tipName">点我提示学校名称</button>
    </div>
</template>

<script>
export default {
    name: 'School',
    data() {
        return {
            name: 'schoolName',
            address: 'schoolAddress'
        }
    },
    methods: {
        tipName() {
            window.alert(this.name);
        }
    }
}
</script>

<style>
    .school {
        background-color: orange;
    }
</style>