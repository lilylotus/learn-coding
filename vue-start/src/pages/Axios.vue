<template>
  <div>
      <h1>Axios</h1>
      <textarea name="resultContent" :value="resultContent" id="" cols="30" rows="10"></textarea>
      <hr>
      <button @click="axiosGet">点击我发送 Get 请求</button>
      <button @click="axiosPost">点击我发送 Post 请求</button>
      <button @click="axiosPut">点击我发送 Put 请求</button>
  </div>
</template>

<script>
import axios from 'axios'

export default {
    name: 'AxiosUsage',
    data() {
      return {
        resultContent: 'init'
      }
    },
    methods: {
      // vue.config.js 中由服务器代理配置
      axiosGet() {
          axios.get('/person')
            .then(response => {
              console.log('axios get result', response)
              this.resultContent = JSON.stringify(response.data);
            }).catch(function(error) {
              console.log('axios get catch error' + error)
            });
      },
      axiosPost() {
        axios({
          method: 'POST',
          url: '/person',
          headers: {
            'Content-Type': 'application/json'
          },
          data: {
            name: 'axiosPostName',
            age: 20
          }
        }).then(response => {
          console.log('axios post result', response)
          this.resultContent = JSON.stringify(response.data);
        }).catch(error => {
          console.log('axios catch error', error)
        })
      },
      axiosPut() {
        axios.put('/person', {name: 'putName', age: 20})
          .then(response => {
            console.log('axios put result', response)
            this.resultContent = JSON.stringify(response.data);
          }).catch(error => {
            console.log('axios put catch error', error)
          });
      }
    }
}
</script>

<style>

</style>