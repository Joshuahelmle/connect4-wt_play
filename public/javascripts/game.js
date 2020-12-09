var SOCKET;

function turn(id, col) {
    let turn = {
        _type : "playTurn",
        _msg : "",
        _col : col
    }
    SOCKET.send(JSON.stringify(turn))
}





function createEvent(type){
    console.log(`Clicked on ${type}`)
    let event = {
        _type :type,
        _msg : ""
    }
    SOCKET.send(JSON.stringify(event))
}



function redrawBoard(id) {
    $.get(`/games/${id}/json`).then(data => {
        data.board.cells.forEach(cell => {
                if (cell.cell.isSet) {
                    let span = $(`#cell-row-${cell.row}-col-${cell.col}`);
                    span.addClass(cell.cell.color.color);
                    span.removeClass('unset');
                }
                else {
                    let span = $(`#cell-row-${cell.row}-col-${cell.col}`);
                    span.addClass('unset');
                    span.removeClass('red').removeClass('yellow');
                }
            }
        );

    });
}

function zoomIn(col, id) {
    $.get(`/games/${id}/json`).then(json => {
        let board = json.board;
        let cells = board.cells;
        let idx = 6;
        cells.forEach( c => {
            if(c.col === col){
                $(`#cell-row-${c.row}-col-${col}`).addClass('scaled');
              if(c.cell.isSet){
                  idx = c.row < idx ? c.row :idx;
              }
            }
        })
        if(idx != 0){
            $(`#cell-row-${idx-1}-col-${col}`).addClass('preview').addClass(json.currentPlayerIndex === 0 ? "red" : "yellow");
        }

    })
}

function zoomOut(id) {
    $('.scaled').removeClass('scaled');
    $('.preview').removeClass('preview').removeClass('red').removeClass('yellow').addClass('unset');
    redrawBoard(id)
}
function onload() {
    console.log(ID);
    SOCKET = new WebSocket(`ws://localhost:9000/games/${ID}/websocket`);
    console.log(SOCKET)
    SOCKET.onopen = () => {
        //SOCKET.send("Hey SERVER. IM HERE")
    }
    SOCKET.onmessage = (e) => {
        if (typeof e.data === "string" && e.data === "done") {
            redrawBoard(ID)

        }
        if (typeof e.data === "string" && e.data === "quitGame") {
            SOCKET.close();
            location.href = "/games"
        }
    }
    SOCKET.onerror = (error) => console.log(error)
    SOCKET.onclose = () => console.log("Websocket closed!")

    $('#btn_quit').on('click', () => createEvent("quit"))
    $('#btn_redo').on('click',() => createEvent("redo"))
    $('#btn_restart').on('click', () => createEvent("restart"))
    $('#btn_save').on('click', () => createEvent("save"))
    $('#btn_undo').on('click', () => createEvent("undo"))


}

window.onload = onload()

