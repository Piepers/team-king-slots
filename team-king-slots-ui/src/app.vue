<template>
    <div id="app">
        <router-view/>
    </div>
    <!--<start/>-->
    <!--<div id="app">-->

    <!--<nav>-->
    <!--<router-link to="/">Play for real</router-link>-->
    <!--<router-link to="/about">Play for free</router-link>-->
    <!--</nav>-->
    <!--<router-view/>-->
    <!--</div>-->
</template>

<script>
    import start from './components/start.vue'
    import proto from './components/proto.vue'

    export default {
        name: 'app',
        components: {
            start,
            proto
        }
    }
</script>

<style>
    @import url('https://fonts.googleapis.com/css?family=Montserrat:400,700');

    body {
        /*background-color: #F5C518;*/
        /*background: url("./assets/background.png") no-repeat;*/
        /*background-size: auto;*/
        background-color: #F97E18;
        font-family: 'Montserrat', sans-serif;
        display: grid;
        grid-template-rows: auto;
        justify-items: center;
        padding-top: 50px;
    }

    body, html {
        margin: 0;
        height: 100%;
    }

    #app {
        width: 90%;
    }


</style>