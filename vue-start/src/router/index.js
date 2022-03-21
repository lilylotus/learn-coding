// 用于创建整个应用的路由器

import VueRouter from "vue-router";

import About from '../pages/About'
import Home from '../pages/Home'
import Message from '../pages/Message'
import News from '../pages/News'
import Detail from '../pages/Detail'
import AxiosUsage from '../pages/Axios'
import VuexUsage from '../pages/VuexUsage'
import EventUsage from '../pages/EventUsage'

// 创建并暴露一个路由器
// 添加路由守卫
// 默认 hash 工作模式， # hash 值，# hash 值部分不会传给后端， /about#/student/msg，历史模式 history （兼容性差）
const router = new VueRouter({
    mode: 'hash',
    routes: [
        {
            name: 'about',
            path: '/about',
            component: About,
            meta: {
                check: false,
                title: '关于'
            }
        },
        {
            name: 'axios',
            path: '/axios',
            component: AxiosUsage,
            meta: {
                check: false,
                title: 'Axios 示例'
            }
        },
        {
            name: 'vuex',
            path: '/vuex',
            component: VuexUsage,
            meta: {
                check: false,
                title: 'Vuex 示例'
            }
        },
        {
            name: 'event',
            path: '/event',
            component: EventUsage,
            meta: {
                check: false,
                title: 'Event 事件绑定'
            }
        },
        {
            name: 'home',
            path: '/home',
            component: Home,
            meta: {
                check: false,
                title: '主页'
            },
            children: [
                {
                    name: 'news',
                    path: 'news',
                    component: News,
                    meta: {
                        check: true,
                        title: '新闻'
                    },
                    // 单独给某个路由配置
                    beforeEnter: (to, from, next) => {
                        // console.log('独享路由守卫', from, to)
                        next()
                    }
                },
                {
                    name: 'message',
                    path: 'message',
                    component: Message,
                    meta: {
                        check: true,
                        title: '消息'
                    },
                    children: [
                        {
                            // 添加唯一标识的名称，参数传递只能使用名称
                            name: 'detail',
                            // params 参数传递，- /detail/555/你好
                            path: 'detail/:id/:msg',
                            component: Detail,

                            // props 第一种写法：值为对象，以 key-value 方式传递给路由过去的组件，但是仅能传固定值
                            // props: {id: '001', msg: 'message-001'},

                            // props 第二种写法，值为布尔类型，true 时，
                            // 会把路由收到的所有 params 参数以 props 形式传给接受组件
                            // props: true,

                            // props 第三种写法，值为函数， 推荐使用
                            // props($route) {
                            //     return {
                            //         id: $route.params.id,
                            //         msg: $route.params.msg
                            //     }
                            // }
                            // 结构赋值连续写法
                            props({params:{id,msg}}) {
                                return {id, msg}
                            }

                        }
                    ]
                }
            ]
        }
    ]
});

// 每次路由前都会调用一个函数，全局前置守卫，每次路由切换之前调用
// 切换失败，url 不会改变
// to 去哪里， from -> 从哪里来， next ->
router.beforeEach((to, from, next) => {
    // console.log('全局前置路由守卫被调用', from, to)
    if (to.meta.check) {
        // console.log('全局前置路由守卫 - 权限校验', to.meta.check)
        next()
    } else {
        next()
    }
})

// 全局后置路由守卫 - 在每次路由切换后被调用
router.afterEach((to, from) => {
    // console.log('全局后置路由守卫被调用', from, to)
    document.title = to.meta.title || '默认-title'
})

export default router