let buttons = [{text: "Restart Game",id: "btn_restart"}, {text: "Quit Game", id: "btn_quit"}, {text: "Save Game", id: "btn_save"}, {text: "Undo", id: "btn_undo"}, {text: "Redo", id: "btn_redo"}];

function row() {
    let rows = []
    for (let row = 0; row < 6; row++) {
        rows.push({row: row})
    }
    return rows
}

function col() {
    let cols = []
    for (let col = 0; col < 7; col++) {
        cols.push({col: col})

    }


    return cols;

}


$(document).ready(function () {

    let connect4Game = new Vue({
        el:'.container'
    })



});


Vue.component('buttongroup', {
    template: `
            <div class="list-group list-group-horizontal btn-group-above"> 
            <button v-for="button in this.buttons" v-bind:id="button.id" class="btn btn-primary list-group-item list-group-item-action flex-fill"> {{button.text }} </button>
            </div>
    `,
    data: function () {
        return {
            buttons:  [{text: "Restart Game",id: "btn_restart"}, {text: "Quit Game", id: "btn_quit"}, {text: "Save Game", id: "btn_save"}, {text: "Undo", id: "btn_undo"}, {text: "Redo", id: "btn_redo"}]
        }
    },

});




Vue.component('board', {
    template: `
            <div class="board">
                <div v-for="boardRow in rows" class="boardrow">
                    <div v-for="boardCol in cols" v-bind:id=" 'row-' + boardRow.row + '-' + 'col-' + boardCol.col" class="cell unset" ></div>
                </div>
            </div>
    `,
    data: function () {
        console.log("row:", row())
        console.log("col:", col())
        return {
            rows: row(),
            cols: col(),
            buttons: buttons
        }

    },

});

