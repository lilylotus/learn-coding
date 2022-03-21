<template>
  <div class="st">
      <h3>学生姓名 {{name}}</h3>
      <h3>学生年龄 {{age}}</h3>
      <h3>学生性别 {{sex}}</h3>
      <h3>学生分数 {{score + 1}}</h3>
  </div>
</template>

<script>
export default {
    name: 'Student',
    data() {
        return {
            name: 'SutdentName',
            age: 20
        }
    },
    // 接受外面传递的值, 简单接受
    // props: ['sex', 'score']
    props: {
        sex: String,
        score: Number
    },
    computed: {
        // ...mapState({calcSum:'sum'})
        // ...mapState(['calcSum'])
        // ...mapGetters('calcSum')
    }
}
</script>

<style scoped>
    .st {
        background-color: blue;
    }
</style>>