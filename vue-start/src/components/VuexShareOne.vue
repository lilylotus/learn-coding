<template>
  <div>
      <h2>vuex data share one</h2>
      <!-- <h4>当前求和为 {{$store.state.vuexSum}}</h4> -->
      <h4>当前求和为 {{mapStateSum}}</h4>
      <!-- <h4>当前求和 * 10 = {{storeVuexCalcSum}}</h4> -->
      <h4>当前求和 * 10 = {{vuexCalcSum}}</h4>
      <select v-model.number="n">
          <option value="1">1</option>
          <option value="2">2</option>
          <option value="3">3</option>
      </select>
      <button @click="increment">+</button>
      <button @click="mapIncrement(n)">mapMutations+</button>
      <button @click="mapActionIncrement(n)">mapAction+</button>
      <button @click="decrement">-</button>
      <button @click="incrementOdd">和为奇数加</button>
      <button @click="incrementWait">等一等在加</button>
  </div>
</template>

<script>
// 自动生成属性的 getter
import {mapState, mapGetters, mapMutations, mapActions} from 'vuex'

export default {
    name: 'VuexShareOne',
    data() {
        return {
            n: 1
        }
    },
    methods: {
        increment() {
            // this.$store -> vuex
            console.log(this.$store)
            // this.sum += this.n
            // 去调度 vuex 中的 action actionIncrement
            this.$store.dispatch('actionIncrement', this.n)
        },
        decrement() {
            // this.sum -= this.n
            this.$store.dispatch('actionDecrement', this.n);
        },
        incrementOdd() {
            // if (this.sum % 2 !== 0) {
            //     this.sum += 1
            // }
            this.$store.dispatch('actionIncrementOdd', this.n);
        },
        incrementWait() {
            // setTimeout(() => {
            //     this.sum += this.n
            // }, 500);
            this.$store.dispatch('actionIncrementWait', this.n);
        },
        /* 
        increment() {
            this.$store.commit('mutationsIncrement', this.n)
        }

        increment(value) {
            this.$store.commit('mutationsIncrement', value)
        }
        */
        ...mapMutations({mapIncrement: 'mapMutationsIncrement'}),
        // 也有简写数组形式, key:value 都是一致的
        // ...mapMutations(['mapMutationsIncrement']),
        ...mapActions(['mapActionIncrement'])
    },
    computed: {
        storeVuexCalcSum() {
            return this.$store.getters.vuexCalcSum;
        },
        // 靠 vuex 的 mapState 自动生成属性的计算属性
        // key : 当前组件使用的计算属性 - sum ， value ：vuex 中对应的 state 数据属性
        ...mapState({mapStateSum:'vuexSum'}),
        // ...mapGetters({mapGettersVuexCalcSum: 'vuexCalcSum'})
        // 数组写法，在当前组件使用的 key 和  vuex store 中的一致，key:value 一致 {vuexCalcSum: 'vuexCalcSum'}
        ...mapGetters(['vuexCalcSum'])

    }
}
</script>

<style lang="css" scoped>
    button {
        margin-left: 6px;
    }
</style>