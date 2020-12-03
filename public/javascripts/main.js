
function turn(id, col) {
    $.post(`/games/${id}/${col}`).then(() => {
        redrawBoard(id)
    })
}

function redrawBoard(id) {
    $.get(`/games/${id}/json`).then(data => {
        console.log(data);
        data.board.cells.forEach(cell => {
                if (cell.cell.isSet) {
                    let span = $(`#cell-row-${cell.row}-col-${cell.col}`);
                    span.addClass(cell.cell.color.color);
                    span.removeClass('unset');
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