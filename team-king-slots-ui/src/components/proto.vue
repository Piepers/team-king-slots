<!-- The prototype slot that just demonstrates the functionality of the backend. -->
<template>
    <div>
        <h1>King Slots Prototype</h1>
        <b-container>
            <b-card>
                <b-row align-h="center" class="mb-2" no-gutters>
                    <b-col>
                        <!-- Start of the grid with reel items -->
                        <b-row no-gutters>
                            <b-col class="reel-col">1</b-col>
                            <b-col class="reel-col">4</b-col>
                            <b-col class="reel-col">7</b-col>
                            <b-col class="reel-col">10</b-col>
                            <b-col class="reel-col">13</b-col>
                        </b-row>
                        <b-row no-gutters>
                            <b-col class="reel-col">2</b-col>
                            <b-col class="reel-col">5</b-col>
                            <b-col class="reel-col">8</b-col>
                            <b-col class="reel-col">11</b-col>
                            <b-col class="reel-col">14</b-col>
                        </b-row>
                        <b-row no-gutters>
                            <b-col class="reel-col">3</b-col>
                            <b-col class="reel-col">6</b-col>
                            <b-col class="reel-col">9</b-col>
                            <b-col class="reel-col">12</b-col>
                            <b-col class="reel-col">15</b-col>
                        </b-row>
                        <!-- End of the grid reel items -->
                    </b-col>
                </b-row>
                <b-row>
                    <b-col>
                        <b-form-input readonly v-model="coins"/>
                    </b-col>
                    <b-col cols="5">
                        <b-form-input readonly v-model="usermessage"/>
                    </b-col>
                    <b-col>
                        <b-button variant="success" size="lg">Spin!</b-button>
                    </b-col>
                </b-row>
            </b-card>
        </b-container>
    </div>
</template>

<script>
    export default {
        name: 'proto',
        data() {
            return {
                slotId: "",
                usermessage: "Welcome to this slot!",
                coins: 2500
            }
        },
        mounted() {
            this.$http.get("http://localhost:8080/start").then(result => {
                console.log('Result: ' + result.data);
                this.slotId = result.body.id;
            }, error => {
                console.error(error);
            });
        },
        methods: {
            spinSlot() {
                this.$http.post("http://localhost:8080/spin/", this.slotId, { headers: { "content-type": "application/json" } }).then(result => {
                    console.log('Result: ' + result.data);
                    this.response = result.data;
                }, error => {
                    console.error(error);
                });
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