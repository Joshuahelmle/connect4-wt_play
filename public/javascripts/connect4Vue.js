let boardRows = [row(0), row(1), row(2), row(3), row(4), row(5)];
let boardCols = [col(0), col(1), col(2), col(3), col(4), col(5), col(6)]

let buttons = [{text: "Restart Game",id: "btn_restart"}, {text: "Quit Game", id: "btn_quit"}, {text: "Save Game", id: "btn_save"}, {text: "Undo", id: "btn_undo"}, {text: "Redo", id: "btn_redo"}];

function row(rowNumber) {
    let rows = [];
        for (let col = 0; col < 7; col++) {
            rows.push({row: rowNumber, col: col})
        }

    return rows
}

function col(colNumber) {
    let cols = [];
    for (let row = 0; row < 6; row++) {
        cols.push({row: row, column: colNumber})
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
            <button v-for="button in buttons" v-bind:id="button.id" class="btn btn-primary list-group-item list-group-item-action flex-fill"> {{button.text }} </button>
            </div>
    `,
    data: function () {
        return {
            buttons: buttons
        }
    },

});




Vue.component('board', {
    template: `
            <div class="board">
                <div v-for="boardRow in rows" class="boardrow">
                    <div v-for="colRow in cols" class="cell unset" v-bind:id="boardRow.row - boardRow.col"></div>
                </div>
            </div>
    `,
    data: function () {
        return {
            rows: boardRows,
            cols: boardCols
        }
    },

});

