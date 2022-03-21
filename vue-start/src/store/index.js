// 创建 Vuex 的核心 store

import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

// vuex store 的操作流程 $store.dispatch('action', value)
// -> context.commit('mutations', value) -> state 操作数据
// 或者直接 $store.commit('mutations', value) -> state 操作数据，不走 action

// 用于响应组件中的动作
const actions = {
    // context 上下文，去调用 commit 执行到操作 mutations
    // context 中包含 dispatch / commit / state /
    actionIncrement(context, value) {
        // console.log(context, value)
        context.commit('mutationsIncrement', value);
    },
    mapActionIncrement(context, value) {
        console.log('mapActionIncrement invocked', context, value)
        context.commit('mutationsIncrement', value);
    },
    actionDecrement(context, value) {
        context.commit('mutationsDecrement', value);
    },
    actionIncrementOdd(context, value) {
        console.log('actionIncrementOdd', context);
        if (context.state.vuexSum % 2) {
            context.commit('mutationsIncrementOdd', value);
        }
    },
    actionIncrementWait(context, value) {
        setTimeout(() => {
            context.commit('mutationsIncrement', value);
        }, 500);
    }
}

// 用于操作数据 state
const mutations = {
    // state - vuex 保存的数据
    mutationsIncrement(state, value) {
        // console.log('mutations Increment', state, value)
        state.vuexSum += value
    },
    mapMutationsIncrement(state, value) {
        console.log('mapMutationsIncrement invocked', state, value)
        state.vuexSum += value
    },
    mutationsDecrement(state, value) {
        state.vuexSum -= value;
    },
    mutationsIncrementOdd(state, value) {
        state.vuexSum += value;
    }
}

// 用于存储数据
const state = {
    vuexSum: 0
}

const getters = {
    vuexCalcSum(state) {
        return state.vuexSum * 10
    }
}

// vuex store 模块化， 可以拆分到多个 store.js 当中书写
// const calcSumOptions = {
//     namespaced: true, // 开启命名空间
//     actions: {},
//     mutations: {},
//     state: {},
//     getters: {}
// }
// 在 new Vuex 时使用模块化
// new Vuex.Store({
//     modules: {
//         calcSum: calcSumOptions
//     }
// })
// 在使用的时候 
// actions   - this.$store.calcSum.dispatch('calcSum/actionIncrement', this.n)
// mutations - this.$store.commit('calcSum/mutationsIncrement', value)
// state     - this.$store.state.calcSum.vuexSum
// getters   - this.$store.getters['calcSum/vuexCalcSum']
// 命名空间名称，[要获取的值 key] 
// ...mapState('calcSum', ['vuexSum'])
// ...mapMutations('calcSum', {mapIncrement: 'mapMutationsIncrement'})
// ...mapActions('calcSum', ['mapActionIncrement'])
// 按照配置的模块获取

//创建并暴露，vuex 使用到的配置项需要加入
export default new Vuex.Store({
    actions,
    mutations,
    state,
    getters
})

// 导出 store，暴露
// export default store