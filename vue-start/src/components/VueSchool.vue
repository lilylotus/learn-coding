<template>
    <div class="school">
        <h2 self="sKeyValue" ref="sn">学校名称： {{name}}</h2>
        <h2 ref="sd">学校地址： {{address}}</h2>
        <button @click="tipName">点我提示学校名称</button>
        <Student ref="st" sex='女' :score='88'></Student>
    </div>
</template>

<script>
import Student from './Student.vue'
export default {
    name: 'VueSchool',
    data() {
        return {
            name: 'schoolName',
            address: 'schoolAddress'
        }
    },
    methods: {
        tipName() {
            console.log("@ $refs", this.$refs)
            console.log("@ $refs", this.$refs.sn.innerText)
            console.log("@ $refs", this.$refs.sn.attributes.self.value)
            console.log('@ $refs', this.$refs.st)
            window.alert(this.name);
        }
    },
    components: {
        Student
    }
}
</script>

<style scoped>
    .school {
        background-color: orange;
    }
</style>