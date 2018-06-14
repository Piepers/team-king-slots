<!-- The prototype slot that just demonstrates the functionality of the backend. -->
<template>
    <div>
        <h1>King Slots Prototype</h1>
        <b-container>
            <!--<b-card>-->
            <b-row no-gutters>
                <b-col>
                    <!-- Start of the grid with reel items -->
                    <b-row no-gutters align-h="center">
                        <b-col class="reel-col">1</b-col>
                        <b-col class="reel-col">4</b-col>
                        <b-col class="reel-col">7</b-col>
                        <b-col class="reel-col">10</b-col>
                        <b-col class="reel-col">13</b-col>
                    </b-row>
                    <b-row no-gutters align-h="center">
                        <b-col class="reel-col">2</b-col>
                        <b-col class="reel-col">5</b-col>
                        <b-col class="reel-col">8</b-col>
                        <b-col class="reel-col">11</b-col>
                        <b-col class="reel-col">14</b-col>
                    </b-row>
                    <b-row no-gutters align-h="center">
                        <b-col class="reel-col">3</b-col>
                        <b-col class="reel-col">6</b-col>
                        <b-col class="reel-col">9</b-col>
                        <b-col class="reel-col">12</b-col>
                        <b-col class="reel-col">15</b-col>
                    </b-row>
                    <!-- End of the grid reel items -->
                </b-col>
            </b-row>
            <b-row align-v="end">
                <b-col>
                    <div class="input-group">
                        <div class="input-group-prepend">
                            <span class="input-group-text">coins</span>
                        </div>
                        <input id="cs" aria-describedby="coins-addon" type="text" class="form-control" readonly
                               v-model="slot.coins"/>
                    </div>
                    <!--<b-form-group id="coinsGroup" label="Coins:" label-for="cs">-->
                    <!--<b-form-input id="cs" readonly v-model="slot.coins"/>-->
                    <!--</b-form-group>-->
                </b-col>
                <b-col>
                    <div class="input-group">
                            <div class="input-group-prepend">
                                <button class="btn btn-success" type="button"
                                        v-on:click="decrementCoinValue(slot)">-</button>
                                <!--<b-button class="btn btn-secondary" v-on:click="decrementCoinValue(slot)">-</b-button>-->
                            </div>
                        <input type="text" class="form-control" readonly v-model="slot.coinValue"/>
                        <!--<b-form-input id="coinValue" class="form-control" readonly v-model="slot.coinValue"/>-->
                        <div class="input-group-append">
                                <button class="btn btn-success" type="button"
                                        v-on:click="incrementCoinValue(slot)">+</button>
                            <!--<b-button variant="secondary" v-on:click="incrementCoinValue(slot)">+</b-button>-->
                            </div>
                    </div>
                    <!--<b-form-group id="coinValueGroup" label="Coinvalue:" label-for="coinValue">-->
                    <!--<b-form-input id="coinValue" readonly v-model="slot.coinValue"/>-->
                    <!--<b-button-group>-->
                    <!--<b-button variant="success" v-on:click="incrementCoinValue(slot)">+</b-button>-->
                    <!--<b-button variant="success" v-on:click="decrementCoinValue(slot)">-</b-button>-->
                    <!--</b-button-group>-->
                    <!--</b-form-group>-->
                </b-col>
                <b-col cols="5">
                    <b-form-input readonly v-model="slot.userMessage"/>
                </b-col>
                <b-col>
                    <b-button variant="success" size="lg">Spin!</b-button>
                </b-col>
            </b-row>
            <!--</b-card>-->
        </b-container>
    </div>
</template>

<script>
    export default {
        name: 'proto',
//        props: ['slot'],
        data() {
            return {
                slot: {
                    id: "",
                    userMessage: "Welcome to this slot!",
                    coinValue: 1,
                    lines: 10,
                    coins: 2500
                }
            }
        },
        // mounted() {
        //     fetch("http://localhost:8080/api/start")
        //         .then(response => response.json())
        //         .then((data) => {
        //             this.slot.id = data.id;
        //         })

        //     this.$http.get("http://localhost:8080/start").then(result => {
        //         console.log('Result: ' + result.data);
        //         this.slotId = result.body.id;
        //     }, error => {
        //         console.error(error);
        //     });
        // },
        methods: {
            spinSlot() {
                this.$http.post("http://localhost:8080/spin/", this.slotId, {headers: {"content-type": "application/json"}}).then(result => {
                    console.log('Result: ' + result.data);
                    this.response = result.data;
                }, error => {
                    console.error(error);
                });
            },
            incrementCoinValue(slot) {
                console.log(slot);
                slot.coinValue++
            },
            decrementCoinValue(slot) {
                console.log(slot);
                if (slot.coinValue > 0) {
                    slot.coinValue--;
                }
            }
        }
    }
</script>

<style scoped>
    .reel-col {
        -ms-flex: 0 0 50px;
        flex: 0 0 50px;
        background-color: orange;
    }
</style>