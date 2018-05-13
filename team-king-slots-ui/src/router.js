import Vue from 'vue'
import Router from 'vue-router'
import start from './components/start.vue'
import proto from './components/proto.vue'

Vue.use(Router)

export default new Router({
    routes: [
        {
            path: '/',
            redirect: {
                name: 'start'
            }
        },
        {
            path: '/start',
            name: 'start',
            component: start
        },
        {
            path: '/proto',
            name: 'proto',
            component: proto
        }
    ]
})