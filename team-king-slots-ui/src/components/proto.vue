<!-- The prototype slot that just demonstrates the functionality of the backend. -->
<template>
    <div id="container">
        <!--Contains the reels, buttons and other controls.-->
        <div id="reel-container">
            <!--The container with the reels.-->
            <div id="reels">
                <!-- Reels -->
            </div>
        </div>
        <div id="control-container">
            <!--The bottom of the container with the controls etc.-->
            <div class="spin-controls">
                <input type="text" placeholder="Good luck...!" name="result" disabled/>
                <i class="fas fa-sync fa-3x"/>
            </div>
            <!--<button id="spin" />-->
            <!--<input id="result" type="text"/>-->

        </div>

        <b-alert show>Default Alert</b-alert>

    </div>
</template>

<script>
    export default {
        name: 'proto'
    }
</script>

<style scoped>
    @import "https://cdn.jsdeliver.net/npm/animate.css@3.5.1";
    /*@import "https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css";*/
    @import "https://use.fontawesome.com/releases/v5.0.12/css/all.css";
    @import "~bootstrap-vue";

    #container {
        position: fixed;
        top: 30%;
        left: 35%;
        width: 60em;
        height: 38em;
        margin-top: -9em;
        margin-left: -15em;
        border: 2px solid #F4791A;

    }

    #reel-container {
        position: relative;
        height: 60%;
    }

    #reels {
        position: absolute;
        left: 10%;
        top: 5%;
        width: 50em;
        height: 20em;
        /*border: 2px solid #7FC9FF;*/
        background-color: white;
    }

    #control-container {
        position: relative;
        height: 30%;
    }

    .spin-controls {
        float: right;
        position: relative;
        right: 75px;
        top: 10px;
    }

    input {
        width: calc(80% - 40px);
        margin-right: 10px;
        border: 0;
        padding: 10px;
        font-size: 1.3em;
        /*background-color: #323333;*/
        background-color: #FFF;
        color: #687F7F;
    }

    i {
        cursor: pointer;
        color: white;
        vertical-align: top;
    }
</style>