<template>
    <div id="start">
        <div class="navigate-group">
            <router-link to="/proto">Play for real</router-link>
            <router-link to="/proto">Play for free</router-link>
        </div>
        <!--<nav>-->
        <!--<router-link to="/proto">Play for real</router-link>-->
        <!--<router-link to="/proto">Play for free</router-link>-->
        <!--</nav>-->
        <!--<router-view/>-->
    </div>
</template>

<script>
    export default {
        name: "start"
    }
</script>

<style scoped>
    .navigate-group {
        width: 300px;
        height: 100px;
        /*background-color: red;*/
        padding: 20px 20px 20px 0;
        position: absolute;
        top: 0;
        bottom: 0;
        left: 0;
        right: 0;
        margin: auto;
    }
    .navigate-group a {
        padding: 10px;
        text-decoration: none;
        background: #111E6C;
        border-radius: 3px;
        /*color: rgb(0, 110, 255);*/
        color: #F5C518;
        font-weight: bold;
        margin-right: 15px;
    }
    nav {
        width: 300px;
        height: 100px;
        /*background-color: red;*/
        padding: 20px 20px 20px 0;
        position: absolute;
        top: 0;
        bottom: 0;
        left: 0;
        right: 0;
        margin: auto;
    }
    nav a {
        padding: 10px;
        text-decoration: none;
        background: #111E6C;
        border-radius: 3px;
        /*color: rgb(0, 110, 255);*/
        color: #F5C518;
        font-weight: bold;
        margin-right: 15px;
    }
</style>